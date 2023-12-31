/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.model;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFSegmenter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.opensearch.common.io.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * The CRF SegmenterInstance.
 *
 * @author Rory Ye
 */
public class CRFSegmenterInstance {

    private static final Logger logger = LogManager.getLogger(CRFSegmenterInstance.class);

    private static volatile CRFSegmenterInstance instance = null;

    public static CRFSegmenterInstance getInstance() {
        if (instance == null) {
            synchronized (CRFSegmenterInstance.class) {
                if (instance == null) {//二次检查
                    instance = new CRFSegmenterInstance();
                }
            }
        }
        return instance;
    }

    private final CRFSegmenter segmenter;

    private CRFSegmenterInstance() {
        if (FileSystemUtils.exists(Paths.get(
                AccessController.doPrivileged((PrivilegedAction<String>) () -> HanLP.Config.CRFCWSModelPath)
        ).toAbsolutePath())) {
            segmenter = AccessController.doPrivileged((PrivilegedAction<CRFSegmenter>) () -> {
                try {
                    return new CRFSegmenter(HanLP.Config.CRFCWSModelPath);
                } catch (IOException e) {
                    logger.error(() -> new ParameterizedMessage("load crf cws model from [{}] error", HanLP.Config.CRFCWSModelPath), e);
                    return null;
                }
            });
        } else {
            logger.warn("can not find crf cws model from [{}]", HanLP.Config.CRFCWSModelPath);
            segmenter = null;
        }
    }

    public CRFSegmenter getSegmenter() {
        return segmenter;
    }
}
