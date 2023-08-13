/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.dictionary;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.other.CharTable;
import com.hankcs.hanlp.utility.LexiconUtility;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.opensearch.SpecialPermission;
import org.opensearch.common.collect.Tuple;
import org.opensearch.common.util.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * The remote dictionary monitor.
 *
 * @author Rory Ye
 */
public class RemoteDictionaryMonitor implements Runnable {

    private static final Logger logger = LogManager.getLogger(RemoteDictionaryMonitor.class);

    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    private String lastModified;

    private String eTags;

    private final String location;

    private final String type;

    private static final String SPLITTER = "\\s";

    public RemoteDictionaryMonitor(String location, String type) {
        this.location = location;
        this.type = type;
        this.lastModified = null;
        this.eTags = null;
    }

    @Override
    public void run() {
        SpecialPermission.check();
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            runPrivileged();
            return null;
        });
    }

    private void runPrivileged() {
        String path = location.split(SPLITTER)[0];

        HttpHead head = new HttpHead(path);
        head.setConfig(buildRequestConfig());

        // 设置请求头
        if (lastModified != null) {
            head.setHeader(HttpHeaders.IF_MODIFIED_SINCE, lastModified);
        }
        if (eTags != null) {
            head.setHeader(HttpHeaders.IF_NONE_MATCH, eTags);
        }

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(head);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if ((response.getLastHeader(HttpHeaders.LAST_MODIFIED) != null)
                        && !response.getLastHeader(HttpHeaders.LAST_MODIFIED).getValue().equalsIgnoreCase(lastModified)) {
                    loadRemoteCustomWords(response);
                } else if ((response.getLastHeader(HttpHeaders.ETAG) != null)
                        && !response.getLastHeader(HttpHeaders.ETAG).getValue().equalsIgnoreCase(eTags)) {
                    loadRemoteCustomWords(response);
                }
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
                logger.info("remote_ext_dict {} is without modified", location);
            } else {
                logger.info("remote_ext_dict {} return bad code {}", location, response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            logger.error(() -> new ParameterizedMessage("remote_ext_dict load from [{}] error", location), e);
        } finally {
            IOUtils.closeWhileHandlingException(response);
        }
    }

    private void loadRemoteCustomWords(CloseableHttpResponse response) {
        switch (type) {
            case "custom":
                logger.info("load hanlp remote custom dict path: {}", location);
                loadRemoteWordsUnprivileged(location);
                logger.info("finish load hanlp remote custom dict path: {}", location);
                break;
            case "stop":
                logger.info("load hanlp remote stop words path: {}", location);
                loadRemoteStopWordsUnprivileged(location);
                logger.info("finish load hanlp remote stop words path: {}", location);
                break;
            default:
                return;
        }
        lastModified = response.getLastHeader(HttpHeaders.LAST_MODIFIED) == null
                ? null
                : response.getLastHeader(HttpHeaders.LAST_MODIFIED).getValue();
        eTags = response.getLastHeader(HttpHeaders.ETAG) == null ? null : response.getLastHeader(HttpHeaders.ETAG).getValue();
    }


    private void loadRemoteWordsUnprivileged(String location) {
        Tuple<String, Nature> defaultInfo = analysisDefaultInfo(location);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        HttpGet get = new HttpGet(defaultInfo.v1());
        get.setConfig(buildRequestConfig());
        try {
            response = httpclient.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), analysisDefaultCharset(response)));
                String line;
                boolean firstLine = true;
                while ((line = in.readLine()) != null) {
                    if (firstLine) {
                        line = IOUtil.removeUTF8BOM(line);
                        firstLine = false;
                    }

                    // 切分
                    String[] param = line.split(SPLITTER);
                    String word = param[0];

                    // 排除空行
                    if (word.length() == 0) {
                        continue;
                    }

                    // 正规化
                    if (HanLP.Config.Normalization) {
                        word = CharTable.convert(word);
                    }
                    logger.debug("hanlp remote custom word: {}", word);
                    CustomDictionary.insert(word, analysisNatureWithFrequency(defaultInfo.v2(), param));
                }
                in.close();
                response.close();
            }
            response.close();
        } catch (IllegalStateException | IOException e) {
            logger.error(() -> new ParameterizedMessage("get remote words from [{}] error", location), e);
        } finally {
            IOUtils.closeWhileHandlingException(in, response);
        }
    }

    private void loadRemoteStopWordsUnprivileged(String location) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        HttpGet get = new HttpGet(location);
        get.setConfig(buildRequestConfig());
        try {
            response = httpclient.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), analysisDefaultCharset(response)));
                String line;
                boolean firstLine = true;
                while ((line = in.readLine()) != null) {
                    if (firstLine) {
                        line = IOUtil.removeUTF8BOM(line);
                        firstLine = false;
                    }
                    logger.debug("hanlp remote stop word: {}", line);
                    CustomStopWordDictionary.add(line);
                }
                in.close();
                response.close();
            }
            response.close();
        } catch (IllegalStateException | IOException e) {
            logger.error(() -> new ParameterizedMessage("get remote words from [{}] error", location), e);
        } finally {
            IOUtils.closeWhileHandlingException(in, response);
        }
    }

    private RequestConfig buildRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(10 * 1000)
                .setConnectTimeout(10 * 1000)
                .setSocketTimeout(60 * 1000)
                .build();
    }

    private Charset analysisDefaultCharset(CloseableHttpResponse response) {
        Charset charset = StandardCharsets.UTF_8;
        // 获取编码，默认为utf-8
        if (response.getEntity().getContentType().getValue().contains("charset=")) {
            String contentType = response.getEntity().getContentType().getValue();
            charset = Charset.forName(contentType.substring(contentType.lastIndexOf("=") + 1));
        }
        return charset;
    }

    private Tuple<String, Nature> analysisDefaultInfo(String location) {
        Nature defaultNature = Nature.n;
        String path = location;
        int cut = location.indexOf(' ');
        if (cut > 0) {
            // 有默认词性
            String nature = location.substring(cut + 1);
            path = location.substring(0, cut);
            defaultNature = LexiconUtility.convertStringToNature(nature);
        }
        return Tuple.tuple(path, defaultNature);
    }

    private String analysisNatureWithFrequency(Nature defaultNature, String[] param) {
        int natureCount = (param.length - 1) / 2;
        StringBuilder builder = new StringBuilder();
        if (natureCount == 0) {
            builder.append(defaultNature).append(" ").append(1000);
        } else {
            for (int i = 0; i < natureCount; ++i) {
                Nature nature = LexiconUtility.convertStringToNature(param[1 + 2 * i]);
                int frequency = Integer.parseInt(param[2 + 2 * i]);
                builder.append(nature).append(" ").append(frequency);
                if (i != natureCount - 1) {
                    builder.append(" ");
                }
            }
        }
        return builder.toString();
    }
}
