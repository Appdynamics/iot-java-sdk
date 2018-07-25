/**
 * Copyright (c) 2018 AppDynamics LLC and its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appdynamics.iot;

import com.appdynamics.iot.utils.Constants;

/**
 * <p>The Agent Configuration for the instrumentation runtime to use.</p>
 * <p>The Agent must have the following configured as part of the initialization.</p>
 *
 * <ol>
 * <li>The Application Key from the Connected Devices UI e.g. IOT-AAB-AVW</li>
 * <li>A Collector URL for onPremise installation. When not specified, it defaults to {@value #DEFAULT_COLLECTOR_URL} </li>
 * </ol>
 */
public class AgentConfiguration {
    public static final String DEFAULT_COLLECTOR_URL = "https://iot-col.eum-appdynamics.com";

    static final String COLLECTOR_URL_PREFIX_APPKEY = "/eumcollector/iot/v1/application/";
    static final String COLLECTOR_URL_SUFFIX_BEACONS = "/beacons";
    static final String COLLECTOR_URL_SUFFIX_ENABLED = "/enabled";

    private final String appKey;
    private final String collectorUrl;
    private final int eventQueueSize;
    private final CollectorChannelFactory collectorChannelFactory;
    private final AppKeyEnabledStateChangeListener listener;
    private final String appkeyEnabledCheckUrl;

    AgentConfiguration(String key,
                       String collectorUrl,
                       CollectorChannelFactory collectorChannelFactory,
                       AppKeyEnabledStateChangeListener listener,
                       int eventQueueSize) {
        this.appKey = key;
        this.collectorUrl = collectorUrl;
        this.collectorChannelFactory = collectorChannelFactory;
        this.eventQueueSize = eventQueueSize;
        this.appkeyEnabledCheckUrl = collectorUrl + COLLECTOR_URL_PREFIX_APPKEY + appKey + COLLECTOR_URL_SUFFIX_ENABLED;
        this.listener = listener;
    }

    /**
     * @return a builder for an {@link AgentConfiguration agent configuration}
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getAppKey() {
        return this.appKey;
    }

    public String getCollectorUrl() {
        return this.collectorUrl;
    }

    public CollectorChannelFactory getCollectorChannelFactory() {
        return this.collectorChannelFactory;
    }

    public AppKeyEnabledStateChangeListener getListener() {
        return this.listener;
    }

    String getAppKeyEnabledCheckUrl() {
        return this.appkeyEnabledCheckUrl;
    }

    public static final class Builder {

        private String appKey;
        private String collectorUrl;
        private CollectorChannelFactory collectorChannelFactory;
        private AppKeyEnabledStateChangeListener listener;
        private int eventQueueSize = Constants.CONFIG_MAX_EVENTS;


        private Builder() {

        }

        /**
         * @param key The AppDynamics APP KEY
         * @return the current agentConfiguration object
         */
        public Builder withAppKey(String key) {
            this.appKey = key;
            return this;
        }

        /**
         * @param url of the AppDynamics Collector collecting events <br>
         *            This method needs to be called only if you are connecting with an
         *            OnPremise version of the Collector.<br>
         *            It should be of the format <b>https://your-collector.com:your-port-number</b>.<br>
         *            If not set, it defaults to the SaaS Production Collector
         *            {@value #DEFAULT_COLLECTOR_URL}<br>
         * @return the current agentConfiguration object
         */
        public Builder withCollectorUrl(String url) {
            this.collectorUrl = url;
            return this;
        }

        /**
         * Sets the collector channel to use.
         *
         * A custom collector channel allows you to have more control over how the SDK
         * communicates with the collector. Most users will not use this mechanism.
         *
         * @param collectorChannelFactory custom collector channel
         * @return the current agentConfiguration object
         * @see CollectorChannelFactory
         */
        public Builder withCollectorChannelFactory(CollectorChannelFactory collectorChannelFactory) {
            this.collectorChannelFactory = collectorChannelFactory;
            return this;
        }

        /**
         * Set a custom AppKeyEnabledStateChangeListener.
         *
         * @param listener set your own AppKeyEnabledStateChangeListener
         * @return the current agentConfiguration object
         * @see AppKeyEnabledStateChangeListener
         */
        public Builder withAppKeyEnabledStatusChangeListener(AppKeyEnabledStateChangeListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * @return an instance of the agentConfiguration to be used with the Instrumentation class
         * @see Instrumentation
         */
        public AgentConfiguration build() {

            if (this.collectorChannelFactory == null) {
                this.collectorChannelFactory = new CollectorChannelFactory() {
                    @Override
                    public CollectorChannel getCollectorChannel() {
                        return new DefaultCollectorChannel();
                    }
                };
            }

            if (this.collectorUrl == null) {
                this.collectorUrl = DEFAULT_COLLECTOR_URL;
            }

            return new AgentConfiguration(appKey,
                    collectorUrl,
                    collectorChannelFactory,
                    listener,
                    eventQueueSize);
        }
    }

    @Override
    public String toString() {
        return "AgentConfiguration{" +
                "App Key =" + appKey + '\'' +
                ", Collector URL ='" + collectorUrl + '\'' +
                ", Collection Channel Factory ='" + collectorChannelFactory + '\'' +
                ", AppKey Enabled State Change Listener = " + listener + '\'' +
                ", Event Queue Size ='" + eventQueueSize + '\'' +
                '}';
    }
}
