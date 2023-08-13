/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.tokenizer;

/**
 * The tokenizer type for hanLP.
 *
 * @author Rory Ye
 */
public enum TokenizerType {

    /**
     * 默认分词
     */
    HANLP,
    /**
     * 标准分词
     */
    STANDARD,
    /**
     * 索引分词
     */
    INDEX,
    /**
     * NLP分词
     */
    NLP,
    /**
     * CRF分词
     */
    CRF,
    /**
     * N-最短路分词
     */
    N_SHORT,
    /**
     * 最短路分词
     */
    DIJKSTRA,
    /**
     * 极速词典分词
     */
    SPEED
}
