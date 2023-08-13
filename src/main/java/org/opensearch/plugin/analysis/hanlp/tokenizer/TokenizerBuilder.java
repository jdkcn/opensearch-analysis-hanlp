/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.tokenizer;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.lucene.analysis.Tokenizer;
import org.opensearch.plugin.analysis.hanlp.config.Configuration;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

/**
 * The tokenizer builder.
 *
 * @author Rory Ye
 */
public class TokenizerBuilder {

    public static Tokenizer tokenizer(Segment segment, Configuration configuration) {
        Segment seg = segment(segment, configuration);
        return AccessController.doPrivileged((PrivilegedAction<HanLPTokenizer>)() -> new HanLPTokenizer(seg, configuration));
    }

    private static Segment segment(Segment segment, Configuration configuration) {
        if (!configuration.isEnableCustomConfig()) {
            return segment.enableOffset(true);
        }
        segment.enableIndexMode(configuration.isEnableIndexMode())
                .enableNumberQuantifierRecognize(configuration.isEnableNumberQuantifierRecognize())
                .enableCustomDictionary(configuration.isEnableCustomDictionary())
                .enableTranslatedNameRecognize(configuration.isEnableTranslatedNameRecognize())
                .enableJapaneseNameRecognize(configuration.isEnableJapaneseNameRecognize())
                .enableOrganizationRecognize(configuration.isEnableOrganizationRecognize())
                .enablePlaceRecognize(configuration.isEnablePlaceRecognize())
                .enableNameRecognize(configuration.isEnableNameRecognize())
                .enablePartOfSpeechTagging(configuration.isEnablePartOfSpeechTagging())
                .enableOffset(configuration.isEnableOffset());
        if (configuration.isEnableTraditionalChineseMode()) {
            return new Segment() {
                @Override
                protected List<Term> segSentence(char[] sentence) {
                    return segment.seg(HanLP.convertToSimplifiedChinese(new String(sentence)));
                }
            };
        }
        return segment;
    }
}
