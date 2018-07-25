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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AgentConfigurationTest {

    @Test
    public void testAppKey() throws Exception {
        final String appKey = "AAA-BBB-CCC";
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(appKey).build();
        assertEquals(appKey, agentConfiguration.getAppKey());
    }

    @Test
    public void testCollectionUrl() throws Exception {
        final String collectorUrl = "http://127.0.0.1:9001/eumcollector";
        final String collectorUrl1 = "https://yourdomain.com:9001/eumcollector";
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withCollectorUrl(collectorUrl).build();
        assertEquals(collectorUrl, agentConfiguration.getCollectorUrl());
        agentConfiguration = AgentConfiguration.builder()
                .withCollectorUrl(collectorUrl1).build();
        assertEquals(collectorUrl1, agentConfiguration.getCollectorUrl());
    }

    @Test
    public void testCollectorUrlDefault() throws Exception {
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .build();
        assertEquals(AgentConfiguration.DEFAULT_COLLECTOR_URL, agentConfiguration.getCollectorUrl());
    }

    @Test
    public void testCollectorUrlNull() throws Exception {
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withCollectorUrl(null)
                .build();
        assertEquals(AgentConfiguration.DEFAULT_COLLECTOR_URL, agentConfiguration.getCollectorUrl());
    }

    @Test
    public void testCollectorChannelFactoryDefault() throws Exception {
        final String appKey = "AAA-BBB-CCC";
        final String collectorUrl = "https://yourdomain.com:9001/eumcollector";
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(appKey).withCollectorUrl(collectorUrl).build();
        assertEquals(appKey, agentConfiguration.getAppKey());
        assertEquals(collectorUrl, agentConfiguration.getCollectorUrl());
        assertTrue(agentConfiguration.getCollectorChannelFactory().getCollectorChannel() instanceof DefaultCollectorChannel);
    }

}
