/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.model;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFNERecognizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.opensearch.common.io.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * The CRF RecognizerInstance.
 *
 * @author Rory Ye
 */
public class CRFNERecognizerInstance {

    private static final Logger logger = LogManager.getLogger(CRFNERecognizerInstance.class);

    private static volatile CRFNERecognizerInstance instance = null;

    /**
     * Get CRFNERecognizerInstance.
     *
     * @return the singleton instance
     */
    public static CRFNERecognizerInstance getInstance() {
        if (instance == null) {
            synchronized (CRFNERecognizerInstance.class) {
                if (instance == null) {//二次检查
                    instance = new CRFNERecognizerInstance();
                }
            }
        }
        return instance;
    }

    private final CRFNERecognizer recognizer;

    private CRFNERecognizerInstance() {
        if (FileSystemUtils.exists(Paths.get(
                AccessController.doPrivileged((PrivilegedAction<String>) () -> HanLP.Config.CRFNERModelPath)
        ).toAbsolutePath())) {
            recognizer = AccessController.doPrivileged((PrivilegedAction<CRFNERecognizer>) () -> {
                try {
                    return new CRFNERecognizer(HanLP.Config.CRFNERModelPath);
                } catch (IOException e) {
                    logger.error(() -> new ParameterizedMessage("load crf ner model from [{}] error", HanLP.Config.CRFNERModelPath), e);
                    return null;
                }
            });
        } else {
            logger.warn("can not find crf ner model from [{}]", HanLP.Config.CRFNERModelPath);
            recognizer = null;
        }
    }

    /**
     * Get the CRFNERecognizer.
     *
     * @return the CRFNERecognizer instance
     */
    public CRFNERecognizer getRecognizer() {
        return recognizer;
    }
}
