/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.config;

import org.opensearch.common.inject.Inject;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.plugin.analysis.hanlp.dictionary.Dictionary;

/**
 * The configuration for hanLP.
 *
 * @author Rory Ye
 */
public class Configuration {

    private final Environment environment;

    private final Settings settings;

    private boolean enablePorterStemming;

    private boolean enableIndexMode;

    private boolean enableNumberQuantifierRecognize;

    private boolean enableCustomDictionary;

    private boolean enableTranslatedNameRecognize;

    private boolean enableJapaneseNameRecognize;

    private boolean enableOrganizationRecognize;

    private boolean enablePlaceRecognize;

    private boolean enableNameRecognize;

    private boolean enableTraditionalChineseMode;

    private boolean enableStopDictionary;

    private boolean enablePartOfSpeechTagging;

    private boolean enableRemoteDict;

    private boolean enableNormalization;

    private boolean enableOffset;

    private boolean enableCustomConfig;

    /**
     * The plugin's configuration.
     *
     * @param env the opensearch environment
     * @param settings the opensearch settings
     */
    @Inject
    public Configuration(Environment env, Settings settings) {
        this.environment = env;
        this.settings = settings;
        this.enablePorterStemming = settings.get("enable_porter_stemming", "false").equals("true");
        this.enableIndexMode = settings.get("enable_index_mode", "false").equals("true");
        this.enableNumberQuantifierRecognize = settings.get("enable_number_quantifier_recognize", "false").equals("true");
        this.enableCustomDictionary = settings.get("enable_custom_dictionary", "true").equals("true");
        this.enableTranslatedNameRecognize = settings.get("enable_translated_name_recognize", "true").equals("true");
        this.enableJapaneseNameRecognize = settings.get("enable_japanese_name_recognize", "false").equals("true");
        this.enableOrganizationRecognize = settings.get("enable_organization_recognize", "false").equals("true");
        this.enablePlaceRecognize = settings.get("enable_place_recognize", "false").equals("true");
        this.enableNameRecognize = settings.get("enable_name_recognize", "true").equals("true");
        this.enableTraditionalChineseMode = settings.get("enable_traditional_chinese_mode", "false").equals("true");
        this.enableStopDictionary = settings.get("enable_stop_dictionary", "false").equals("true");
        this.enablePartOfSpeechTagging = settings.get("enable_part_of_speech_tagging", "false").equals("true");
        this.enableRemoteDict = settings.get("enable_remote_dict", "true").equals("true");
        this.enableNormalization = settings.get("enable_normalization", "false").equals("true");
        this.enableOffset = settings.get("enable_offset", "true").equals("true");
        this.enableCustomConfig = settings.get("enable_custom_config", "false").equals("true");
        Dictionary.initial(this);
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public boolean isEnablePorterStemming() {
        return this.enablePorterStemming;
    }

    public Configuration enablePorterStemming(boolean enablePorterStemming) {
        this.enablePorterStemming = enablePorterStemming;
        return this;
    }

    public boolean isEnableIndexMode() {
        return this.enableIndexMode;
    }

    /**
     * Enable the index mode.
     *
     * @param enableIndexMode enable or not
     * @return configuration instance
     */
    public Configuration enableIndexMode(boolean enableIndexMode) {
        this.enableIndexMode = enableIndexMode;
        return this;
    }

    public boolean isEnableNumberQuantifierRecognize() {
        return this.enableNumberQuantifierRecognize;
    }

    /**
     * Enable number quantifier recognize.
     *
     * @param enableNumberQuantifierRecognize enable or not
     * @return configuration instance
     */
    public Configuration enableNumberQuantifierRecognize(boolean enableNumberQuantifierRecognize) {
        this.enableNumberQuantifierRecognize = enableNumberQuantifierRecognize;
        return this;
    }

    public boolean isEnableCustomDictionary() {
        return this.enableCustomDictionary;
    }

    /**
     * Enable custom dictionary.
     *
     * @param enableCustomDictionary enable or not
     * @return configuration instance
     */
    public Configuration enableCustomDictionary(boolean enableCustomDictionary) {
        this.enableCustomDictionary = enableCustomDictionary;
        return this;
    }

    public boolean isEnableTranslatedNameRecognize() {
        return this.enableTranslatedNameRecognize;
    }

    public Configuration enableTranslatedNameRecognize(boolean enableTranslatedNameRecognize) {
        this.enableTranslatedNameRecognize = enableTranslatedNameRecognize;
        return this;
    }

    public boolean isEnableJapaneseNameRecognize() {
        return this.enableJapaneseNameRecognize;
    }

    /**
     * Enable japanese name recognize.
     *
     * @param enableJapaneseNameRecognize enable or not
     * @return configuration instance
     */
    public Configuration enableJapaneseNameRecognize(boolean enableJapaneseNameRecognize) {
        this.enableJapaneseNameRecognize = enableJapaneseNameRecognize;
        return this;
    }

    public boolean isEnableOrganizationRecognize() {
        return this.enableOrganizationRecognize;
    }

    /**
     * Enable organization recognize.
     *
     * @param enableOrganizationRecognize enable or not
     * @return configuration instance
     */
    public Configuration enableOrganizationRecognize(boolean enableOrganizationRecognize) {
        this.enableOrganizationRecognize = enableOrganizationRecognize;
        return this;
    }

    public boolean isEnablePlaceRecognize() {
        return this.enablePlaceRecognize;
    }

    /**
     * Enable place recognize.
     *
     * @param enablePlaceRecognize enable or not
     * @return configuration instance
     */
    public Configuration enablePlaceRecognize(boolean enablePlaceRecognize) {
        this.enablePlaceRecognize = enablePlaceRecognize;
        return this;
    }

    public boolean isEnableNameRecognize() {
        return this.enableNameRecognize;
    }

    /**
     * Enable name recognize.
     *
     * @param enableNameRecognize enable or not
     * @return configuration instance
     */
    public Configuration enableNameRecognize(boolean enableNameRecognize) {
        this.enableNameRecognize = enableNameRecognize;
        return this;
    }

    public boolean isEnableTraditionalChineseMode() {
        return this.enableTraditionalChineseMode;
    }

    /**
     * Enable traditional chinese mode.
     *
     * @param enableTraditionalChineseMode enable or not
     * @return configuration instance
     */
    public Configuration enableTraditionalChineseMode(boolean enableTraditionalChineseMode) {
        this.enableTraditionalChineseMode = enableTraditionalChineseMode;
        return this;
    }

    public boolean isEnableStopDictionary() {
        return this.enableStopDictionary;
    }

    /**
     * Enable stop dictionary.
     *
     * @param enableStopDictionary enable or not
     * @return configuration instance
     */
    public Configuration enableStopDictionary(boolean enableStopDictionary) {
        this.enableStopDictionary = enableStopDictionary;
        return this;
    }

    public boolean isEnablePartOfSpeechTagging() {
        return this.enablePartOfSpeechTagging;
    }

    /**
     * Enable part of speech tagging.
     *
     * @param enablePartOfSpeechTagging enable or not
     * @return configuration instance
     */
    public Configuration enablePartOfSpeechTagging(boolean enablePartOfSpeechTagging) {
        this.enablePartOfSpeechTagging = enablePartOfSpeechTagging;
        return this;
    }

    public boolean isEnableRemoteDict() {
        return enableRemoteDict;
    }

    /**
     * Enable remote dictionary.
     *
     * @param enableRemoteDict enable or not
     * @return configuration instance
     */
    public Configuration enableRemoteDict(boolean enableRemoteDict) {
        this.enableRemoteDict = enableRemoteDict;
        return this;
    }

    public boolean isEnableNormalization() {
        return enableNormalization;
    }

    /**
     * Enable normalization.
     *
     * @param enableNormalization enable or not
     * @return configuration instance
     */
    public Configuration enableNormalization(boolean enableNormalization) {
        this.enableNormalization = enableNormalization;
        return this;
    }

    public boolean isEnableOffset() {
        return enableOffset;
    }

    /**
     * Enable the offset.
     *
     * @param enableOffset enable or not
     * @return configuration instance
     */
    public Configuration enableOffset(boolean enableOffset) {
        this.enableOffset = enableOffset;
        return this;
    }

    public boolean isEnableCustomConfig() {
        return enableCustomConfig;
    }

    public Configuration enableCustomConfig(boolean enableCustomConfig) {
        this.enableCustomConfig = enableCustomConfig;
        return this;
    }
}
