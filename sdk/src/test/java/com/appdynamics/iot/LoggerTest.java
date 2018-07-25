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

import org.junit.Before;
import org.junit.Test;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.org.lidalia.slf4jext.Level.*;
import static uk.org.lidalia.slf4jtest.LoggingEvent.info;

public class LoggerTest {

    private static final String APP_KEY = "AAA-BBB-CCC";
    private static final String DEVICE_TYPE = "CAR_MODEL_A";
    private static final String DEVICE_ID = "1FMHK7F8XCGA67893";
    private TestLogger testLogger = TestLoggerFactory.getTestLogger(Instrumentation.class);

    @Before
    public void clearPriorLoggers() {
        TestLoggerFactory.clear();
    }

    @Test
    public void testInstrumentationStartLoggingDefault() {
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(APP_KEY).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);
        assertThat(testLogger.getLoggingEvents(), is(asList(info("AppDynamics Instrumentation Started"))));
    }

    @Test
    public void testInstrumentationStartLoggingInfo() {
        testLogger.setEnabledLevels(INFO);
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(APP_KEY).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);
        assertThat(testLogger.getLoggingEvents(), is(asList(info("AppDynamics Instrumentation Started"))));
    }

    @Test
    public void testInstrumentationStartLoggingDisabled() {
        testLogger.setEnabledLevels(OFF);
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(APP_KEY).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);
        assertThat(TestLoggerFactory.getLoggingEvents(), is(Collections.<LoggingEvent>emptyList()));
    }

    @Test
    public void testInstrumentationStartLoggingWarn() {
        testLogger.setEnabledLevels(WARN);
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(APP_KEY).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);
        assertThat(TestLoggerFactory.getLoggingEvents(), is(Collections.<LoggingEvent>emptyList()));
    }

    @Test
    public void testInstrumentationStartLoggingError() {
        testLogger.setEnabledLevels(ERROR);
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(APP_KEY).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);
        assertThat(TestLoggerFactory.getLoggingEvents(), is(Collections.<LoggingEvent>emptyList()));
    }

    @Test
    public void testInstrumentationStartLoggingALL() {
        testLogger.setEnabledLevels(INFO, DEBUG, WARN, ERROR);

        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(APP_KEY).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();

        Instrumentation.start(agentConfiguration, deviceInfo, null);
        assertThat(testLogger.getLoggingEvents(), is(asList(info("AppDynamics Instrumentation Started"))));
    }

}
