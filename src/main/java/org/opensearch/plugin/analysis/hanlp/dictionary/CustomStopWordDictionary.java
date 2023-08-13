/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.dictionary;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.ByteArray;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.dictionary.stopword.Filter;
import com.hankcs.hanlp.dictionary.stopword.StopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.utility.Predefine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.opensearch.common.util.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

/**
 * The stop word dictionary, Modified the original stop word filter, deleted the part of speech filter, and only filtered the words in the stop word stopWordDictionary.
 *
 * @author Rory Ye
 */
public class CustomStopWordDictionary {
    
    private static final Logger logger = LogManager.getLogger(CustomStopWordDictionary.class);
    
    private static final StopWordDictionary stopWordDictionary;

    private static final Filter FILTER = term -> {
        // 除掉停用词
        String nature = term.nature != null ? term.nature.toString() : "空";
        char firstChar = nature.charAt(0);
        if (firstChar == 'w') {
            return false;
        } else {
            return !CustomStopWordDictionary.contains(term.word);
        }
    };


    private static boolean contains(String key) {
        return stopWordDictionary.contains(key);
    }

    private static boolean shouldInclude(Term term) {
        return FILTER.shouldInclude(term);
    }

    public static boolean shouldRemove(Term term) {
        return !shouldInclude(term);
    }

    public static void add(String stopWord) {
        stopWordDictionary.add(stopWord);
    }

    public static void remove(String stopWord) {
        stopWordDictionary.remove(stopWord);
    }

    public static void apply(List<Term> termList) {
        termList.removeIf(CustomStopWordDictionary::shouldRemove);
    }

    static {
        ByteArray byteArray = isNeedUpdate() ? null :
                AccessController.doPrivileged((PrivilegedAction<ByteArray>) () ->
                        ByteArray.createByteArray(HanLP.Config.CoreStopWordDictionaryPath + Predefine.BIN_EXT));
        if (byteArray == null) {
            try {
                stopWordDictionary = AccessController.doPrivileged((PrivilegedAction<StopWordDictionary>) () -> {
                    try {
                        return new StopWordDictionary(HanLP.Config.CoreStopWordDictionaryPath);
                    } catch (IOException e) {
                        logger.error(() -> new ParameterizedMessage("load stop word dictionary from [{}] error",
                                HanLP.Config.CoreStopWordDictionaryPath), e);
                        return null;
                    }
                });
                if (stopWordDictionary != null) {
                    AccessController.doPrivileged((PrivilegedAction<Boolean>) CustomStopWordDictionary::save);
                }
            } catch (Exception e) {
                logger.error(() ->
                        new ParameterizedMessage("load stop word dictionary from [{}] error", HanLP.Config.CoreStopWordDictionaryPath), e);
                throw new RuntimeException("load stop word dictionary from [" + HanLP.Config.CoreStopWordDictionaryPath + "] error");
            }
        } else {
            stopWordDictionary = new StopWordDictionary();
            stopWordDictionary.load(byteArray);
        }
    }

    private static boolean save() {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new BufferedOutputStream(IOUtil.newOutputStream(HanLP.Config.CoreStopWordDictionaryPath + ".bin")));
            stopWordDictionary.save(out);
            return true;
        } catch (Exception e) {
            logger.error(() ->
                    new ParameterizedMessage("can not save stop word dictionary to [{}] error", HanLP.Config.CoreStopWordDictionaryPath), e);
            return false;
        } finally {
            IOUtils.closeWhileHandlingException(out);
        }
    }

    private static boolean isNeedUpdate() {
        File binFile = new File(HanLP.Config.CoreStopWordDictionaryPath + Predefine.BIN_EXT);
        if (!AccessController.doPrivileged((PrivilegedAction<Boolean>) binFile::exists)) {
            return true;
        }
        File txtFile = new File(HanLP.Config.CoreStopWordDictionaryPath);
        if (!AccessController.doPrivileged((PrivilegedAction<Boolean>) txtFile::exists)) {
            logger.error("can not find stop word dictionary from [{}]", HanLP.Config.CoreStopWordDictionaryPath);
            throw new IllegalArgumentException("can not find stop word dictionary from [" + HanLP.Config.CoreStopWordDictionaryPath + "]");
        }
        long binLastModified = binFile.lastModified();
        long txtLastModified = txtFile.lastModified();
        if (txtLastModified >= binLastModified) {
            AccessController.doPrivileged((PrivilegedAction<Boolean>) binFile::delete);
            return true;
        }
        return false;
    }
}
