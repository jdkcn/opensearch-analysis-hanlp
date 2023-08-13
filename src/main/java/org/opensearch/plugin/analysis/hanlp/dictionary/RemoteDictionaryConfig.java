/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.dictionary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.common.util.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The remote dictionary config.
 *
 * @author Rory Ye
 */
public class RemoteDictionaryConfig {
    private static final String REMOTE_EXT_DICT = "remote_ext_dict";

    private static final String REMOTE_EXT_STOP = "remote_ext_stopwords";

    private static final Logger logger = LogManager.getLogger(RemoteDictionaryConfig.class);

    private final Properties properties;

    private String configFile;

    private RemoteDictionaryConfig() {
        this.properties = new Properties();
    }

    private static class RemoteDictionaryConfigHolder {
        private static final RemoteDictionaryConfig INSTANCE = new RemoteDictionaryConfig();
    }

    private String getProperty(String key) {
        return properties.getProperty(key);
    }

    private void loadConfig() {
        InputStream input = null;
        try {
            logger.info("try load remote hanlp config from {}", configFile);
            input = new FileInputStream(configFile);
            properties.loadFromXML(input);
        } catch (FileNotFoundException e) {
            logger.error("remote hanlp config isn't exist", e);
        } catch (Exception e) {
            logger.error("can not load remote hanlp config", e);
        } finally {
            IOUtils.closeWhileHandlingException(input);
        }
    }

    private List<String> getRemoteExtFiles(String key) {
        List<String> remoteExtFiles = new ArrayList<>(2);
        String remoteExtStopWordDictCfg = getProperty(key);
        if (remoteExtStopWordDictCfg != null) {

            String[] filePaths = remoteExtStopWordDictCfg.split(";");
            for (String filePath : filePaths) {
                if (filePath != null && !"".equals(filePath.trim())) {
                    remoteExtFiles.add(filePath);

                }
            }
        }
        return remoteExtFiles;
    }

    public synchronized void initial(String configFile) {
        this.configFile = configFile;
        loadConfig();
    }
    public List<String> getRemoteExtDictionaries() {
        return getRemoteExtFiles(REMOTE_EXT_DICT);
    }

    public List<String> getRemoteExtStopWordDictionaries() {
        return getRemoteExtFiles(REMOTE_EXT_STOP);
    }

    public static RemoteDictionaryConfig getInstance() {
        return RemoteDictionaryConfigHolder.INSTANCE;
    }
}
