/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.model;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFPOSTagger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.opensearch.common.io.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * The CRF TaggerInstance.
 *
 * @author Rory Ye
 */
public class CRFPOSTaggerInstance {

    private static final Logger logger = LogManager.getLogger(CRFPOSTaggerInstance.class);

    private static volatile CRFPOSTaggerInstance instance = null;

    public static CRFPOSTaggerInstance getInstance() {
        if (instance == null) {
            synchronized (CRFPOSTaggerInstance.class) {
                if (instance == null) {//二次检查
                    instance = new CRFPOSTaggerInstance();
                }
            }
        }
        return instance;
    }

    private final CRFPOSTagger tagger;

    private CRFPOSTaggerInstance() {
        if (FileSystemUtils.exists(Paths.get(
                AccessController.doPrivileged((PrivilegedAction<String>) () -> HanLP.Config.CRFPOSModelPath)
        ).toAbsolutePath())) {
            tagger = AccessController.doPrivileged((PrivilegedAction<CRFPOSTagger>) () -> {
                try {
                    return new CRFPOSTagger(HanLP.Config.CRFPOSModelPath);
                } catch (IOException e) {
                    logger.error(() -> new ParameterizedMessage("load crf pos model from [{}] error", HanLP.Config.CRFPOSModelPath), e);
                    return null;
                }
            });
        } else {
            logger.warn("can not find crf pos model from [{}]", HanLP.Config.CRFPOSModelPath);
            tagger = null;
        }
    }

    /**
     * Get the CRFNERecognizer.
     *
     * @return the tagger instance
     */
    public CRFPOSTagger getTagger() {
        return tagger;
    }
}
