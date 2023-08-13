/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.tokenizer;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.seg.Dijkstra.DijkstraSegment;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.Other.DoubleArrayTrieSegment;
import com.hankcs.hanlp.seg.Segment;
import org.apache.lucene.analysis.Tokenizer;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.index.IndexSettings;
import org.opensearch.index.analysis.AbstractTokenizerFactory;
import org.opensearch.plugin.analysis.hanlp.config.Configuration;
import org.opensearch.plugin.analysis.hanlp.model.*;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author Rory Ye
 */
public class HanLPTokenizerFactory extends AbstractTokenizerFactory {

    private final TokenizerType tokenizerType;

    private final Configuration configuration;

    public HanLPTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings, TokenizerType tokenizerType) {
        super(indexSettings, settings, name);
        this.tokenizerType = tokenizerType;
        this.configuration = new Configuration(env, settings);
    }


    public static HanLPTokenizerFactory getHanLPTokenizerFactory(IndexSettings indexSettings, Environment env, String name,
                                                                 Settings settings) {
        return new HanLPTokenizerFactory(indexSettings, env, name, settings, TokenizerType.HANLP);
    }

    public static HanLPTokenizerFactory getHanLPStandardTokenizerFactory(IndexSettings indexSettings, Environment env, String name,
                                                                         Settings settings) {
        return new HanLPTokenizerFactory(indexSettings, env, name, settings, TokenizerType.STANDARD);
    }

    public static HanLPTokenizerFactory getHanLPIndexTokenizerFactory(IndexSettings indexSettings, Environment env, String name,
                                                                      Settings settings) {
        return new HanLPTokenizerFactory(indexSettings, env, name, settings, TokenizerType.INDEX);
    }

    public static HanLPTokenizerFactory getHanLPNLPTokenizerFactory(IndexSettings indexSettings, Environment env, String name,
                                                                    Settings settings) {
        return new HanLPTokenizerFactory(indexSettings, env, name, settings, TokenizerType.NLP);
    }

    public static HanLPTokenizerFactory getHanLPCRFTokenizerFactory(IndexSettings indexSettings, Environment env, String name,
                                                                    Settings settings) {
        return new HanLPTokenizerFactory(indexSettings, env, name, settings, TokenizerType.CRF);
    }

    public static HanLPTokenizerFactory getHanLPNShortTokenizerFactory(IndexSettings indexSettings, Environment env, String name,
                                                                       Settings settings) {
        return new HanLPTokenizerFactory(indexSettings, env, name, settings, TokenizerType.N_SHORT);
    }

    public static HanLPTokenizerFactory getHanLPDijkstraTokenizerFactory(IndexSettings indexSettings, Environment env, String name,
                                                                         Settings settings) {
        return new HanLPTokenizerFactory(indexSettings, env, name, settings, TokenizerType.DIJKSTRA);
    }

    public static HanLPTokenizerFactory getHanLPSpeedTokenizerFactory(IndexSettings indexSettings, Environment env, String name,
                                                                      Settings settings) {
        return new HanLPTokenizerFactory(indexSettings, env, name, settings, TokenizerType.SPEED);
    }

    @Override
    public Tokenizer create() {
        switch (this.tokenizerType) {
            case INDEX:
                configuration.enableIndexMode(true);
                return TokenizerBuilder.tokenizer(AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                HanLP.newSegment().enableIndexMode(true)),
                        configuration);
            case NLP:
                return TokenizerBuilder.tokenizer(AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                new PerceptronLexicalAnalyzer(
                                        PerceptronCWSInstance.getInstance().getLinearModel(),
                                        PerceptronPOSInstance.getInstance().getLinearModel(),
                                        PerceptronNERInstance.getInstance().getLinearModel())
                        ),
                        configuration);
            case CRF:
                if (CRFPOSTaggerInstance.getInstance().getTagger() == null) {
                    return TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter()
                                    )),
                            configuration);
                } else if (CRFNERecognizerInstance.getInstance().getRecognizer() == null) {
                    return TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter(),
                                            CRFPOSTaggerInstance.getInstance().getTagger()
                                    )),
                            configuration);
                } else {
                    return TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter(),
                                            CRFPOSTaggerInstance.getInstance().getTagger(),
                                            CRFNERecognizerInstance.getInstance().getRecognizer()
                                    )),
                            configuration);
                }
            case N_SHORT:
                configuration.enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
                return TokenizerBuilder.tokenizer(
                        AccessController.doPrivileged(
                                (PrivilegedAction<Segment>) () -> new NShortSegment()
                                        .enableCustomDictionary(false)
                                        .enablePlaceRecognize(true)
                                        .enableOrganizationRecognize(true)),
                        configuration);
            case DIJKSTRA:
                configuration.enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
                return TokenizerBuilder.tokenizer(
                        AccessController.doPrivileged(
                                (PrivilegedAction<Segment>) () -> new DijkstraSegment()
                                        .enableCustomDictionary(false)
                                        .enablePlaceRecognize(true)
                                        .enableOrganizationRecognize(true)),
                        configuration);
            case SPEED:
                configuration.enableCustomDictionary(false);
                return TokenizerBuilder.tokenizer(
                        AccessController.doPrivileged(
                                (PrivilegedAction<Segment>) () -> new DoubleArrayTrieSegment().enableCustomDictionary(false)
                        ),
                        configuration);
            case HANLP:
            case STANDARD:
            default:
                return TokenizerBuilder.tokenizer(
                        AccessController.doPrivileged((PrivilegedAction<Segment>) HanLP::newSegment),
                        configuration);
        }
    }
}
