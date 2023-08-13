/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.analyzer;

import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.seg.Segment;
import org.apache.lucene.analysis.Analyzer;
import org.opensearch.plugin.analysis.hanlp.config.Configuration;
import org.opensearch.plugin.analysis.hanlp.model.CRFNERecognizerInstance;
import org.opensearch.plugin.analysis.hanlp.model.CRFPOSTaggerInstance;
import org.opensearch.plugin.analysis.hanlp.model.CRFSegmenterInstance;
import org.opensearch.plugin.analysis.hanlp.tokenizer.TokenizerBuilder;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * The hanLP CRF analyzer.
 *
 * @author Rory Ye
 */
public class HanLPCRFAnalyzer extends Analyzer {

    private final Configuration configuration;

    public HanLPCRFAnalyzer(Configuration configuration) {
        super();
        this.configuration = configuration;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        if (CRFPOSTaggerInstance.getInstance().getTagger() == null) {
            return new TokenStreamComponents(
                    TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter()
                                    )),
                            configuration));
        } else if (CRFNERecognizerInstance.getInstance().getRecognizer() == null) {
            return new TokenStreamComponents(
                    TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter(),
                                            CRFPOSTaggerInstance.getInstance().getTagger()
                                    )),
                            configuration));
        } else {
            return new TokenStreamComponents(
                    TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter(),
                                            CRFPOSTaggerInstance.getInstance().getTagger(),
                                            CRFNERecognizerInstance.getInstance().getRecognizer()
                                    )),
                            configuration));
        }
    }
}
