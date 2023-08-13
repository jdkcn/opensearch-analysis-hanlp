/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.hanlp.dictionary;

import org.opensearch.plugin.analysis.hanlp.AnalysisHanLPPlugin;
import org.opensearch.plugin.analysis.hanlp.config.Configuration;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The dictionary for hanlp.
 *
 * @author Rory Ye
 */
public class Dictionary {

    private static Dictionary singleton;

    private final Configuration configuration;

    private static final String REMOTE_CONFIG_FILE_NAME = "hanlp-remote.xml";

    private static final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1, new ThreadFactory() {
        private final AtomicInteger counter = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            String threadName = "remote-dict-monitor-" + counter.getAndIncrement();
            return new Thread(r, threadName);
        }
    });

    private Dictionary(Configuration configuration) {
        this.configuration = configuration;
    }

    private void setUp() {
        Path configDir = configuration.getEnvironment().configFile().resolve(AnalysisHanLPPlugin.PLUGIN_NAME);
        DictionaryFileCache.configCachePath(configuration);
        DictionaryFileCache.loadCache();
        RemoteDictionaryConfig.getInstance().initial(configDir.resolve(REMOTE_CONFIG_FILE_NAME).toString());
    }

    public static synchronized void initial(Configuration configuration) {
        if (singleton == null) {
            synchronized (Dictionary.class) {
                if (singleton == null) {
                    singleton = new Dictionary(configuration);
                    singleton.setUp();
                    pool.scheduleAtFixedRate(new CustomDictionaryMonitor(), 10, 60, TimeUnit.SECONDS);
                    if (configuration.isEnableRemoteDict()) {
                        for (String location : RemoteDictionaryConfig.getInstance().getRemoteExtDictionaries()) {
                            pool.scheduleAtFixedRate(new RemoteDictionaryMonitor(location, "custom"), 10, 60, TimeUnit.SECONDS);
                        }

                        for (String location : RemoteDictionaryConfig.getInstance().getRemoteExtStopWordDictionaries()) {
                            pool.scheduleAtFixedRate(new RemoteDictionaryMonitor(location, "stop"), 10, 60, TimeUnit.SECONDS);
                        }
                    }
                }
            }
        }
    }
}
