/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.index.IndexSettings;
import org.opensearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.opensearch.plugin.analysis.hanlp.config.Configuration;
import org.opensearch.plugin.analysis.hanlp.tokenizer.TokenizerType;

/**
 * The hanLP analyzer provider.
 *
 * @author Rory Ye
 */
public class HanLPAnalyzerProvider extends AbstractIndexAnalyzerProvider<Analyzer> {

    private final Analyzer analyzer;

    public HanLPAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings, TokenizerType tokenizerType) {
        super(indexSettings, name, settings);
        Configuration configuration = new Configuration(env, settings);
        switch (tokenizerType) {
            case HANLP:
                analyzer = new HanLPAnalyzer(configuration);
                break;
            case STANDARD:
                analyzer = new HanLPStandardAnalyzer(configuration);
                break;
            case INDEX:
                analyzer = new HanLPIndexAnalyzer(configuration);
                break;
            case NLP:
                analyzer = new HanLPNLPAnalyzer(configuration);
                break;
            case CRF:
                analyzer = new HanLPCRFAnalyzer(configuration);
                break;
            case N_SHORT:
                analyzer = new HanLPNShortAnalyzer(configuration);
                break;
            case DIJKSTRA:
                analyzer = new HanLPDijkstraAnalyzer(configuration);
                break;
            case SPEED:
                analyzer = new HanLPSpeedAnalyzer(configuration);
                break;
            default:
                analyzer = null;
                break;
        }
    }


    public static HanLPAnalyzerProvider getHanLPAnalyzerProvider(IndexSettings indexSettings, Environment env, String name,
                                                                 Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, TokenizerType.HANLP);
    }

    public static HanLPAnalyzerProvider getHanLPStandardAnalyzerProvider(IndexSettings indexSettings, Environment env, String name,
                                                                         Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, TokenizerType.STANDARD);
    }

    public static HanLPAnalyzerProvider getHanLPIndexAnalyzerProvider(IndexSettings indexSettings, Environment env, String name,
                                                                      Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, TokenizerType.INDEX);
    }

    public static HanLPAnalyzerProvider getHanLPNLPAnalyzerProvider(IndexSettings indexSettings, Environment env, String name,
                                                                    Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, TokenizerType.NLP);
    }

    public static HanLPAnalyzerProvider getHanLPCRFAnalyzerProvider(IndexSettings indexSettings, Environment env, String name,
                                                                    Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, TokenizerType.CRF);
    }

    public static HanLPAnalyzerProvider getHanLPNShortAnalyzerProvider(IndexSettings indexSettings, Environment env, String name,
                                                                       Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, TokenizerType.N_SHORT);
    }

    public static HanLPAnalyzerProvider getHanLPDijkstraAnalyzerProvider(IndexSettings indexSettings, Environment env, String name,
                                                                         Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, TokenizerType.DIJKSTRA);
    }

    public static HanLPAnalyzerProvider getHanLPSpeedAnalyzerProvider(IndexSettings indexSettings, Environment env, String name,
                                                                      Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, TokenizerType.SPEED);
    }

    @Override
    public Analyzer get() {
        return this.analyzer;
    }
}
