/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.analyzer;

import com.hankcs.hanlp.seg.Dijkstra.DijkstraSegment;
import com.hankcs.hanlp.seg.Segment;
import org.apache.lucene.analysis.Analyzer;
import org.opensearch.plugin.analysis.hanlp.config.Configuration;
import org.opensearch.plugin.analysis.hanlp.tokenizer.TokenizerBuilder;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * The Dijkstra's algorithm analyzer.
 *
 * @author Rory Ye
 */
public class HanLPDijkstraAnalyzer extends Analyzer {

    private final Configuration configuration;

    public HanLPDijkstraAnalyzer(Configuration configuration) {
        super();
        this.configuration = configuration;
        enableConfiguration();
    }
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        return new Analyzer.TokenStreamComponents(TokenizerBuilder.tokenizer(
                AccessController.doPrivileged(
                        (PrivilegedAction<Segment>) () -> new DijkstraSegment()
                                .enableCustomDictionary(false)
                                .enablePlaceRecognize(true)
                                .enableOrganizationRecognize(true)),
                configuration));
    }

    private void enableConfiguration() {
        this.configuration.enableCustomDictionary(false)
                .enablePlaceRecognize(true)
                .enableOrganizationRecognize(true);
    }
}
