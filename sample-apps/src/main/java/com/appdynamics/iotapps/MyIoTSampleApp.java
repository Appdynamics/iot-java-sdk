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

package com.appdynamics.iotapps;

import com.appdynamics.iot.AgentConfiguration;
import com.appdynamics.iot.DeviceInfo;
import com.appdynamics.iot.Instrumentation;
import com.appdynamics.iot.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class MyIoTSampleApp {

    public static final String APP_KEY = "<YOUR-APP-KEY>";
    public static final String COLLECTOR_URL = "<YOUR-COLLECTOR-URL>";

    // Update the value of LOOP_COUNT variable to simulate long running application.
    private static final int LOOP_COUNT = 1;

    static final Logger LOGGER = LoggerFactory.getLogger(MyIoTSampleApp.class);
    private static MyAppKeyStateChangeListener listener = new MyAppKeyStateChangeListener() {};

    public static void main(String[] args) {
        LOGGER.info("Initializing AppDynamics Instrumentation");
        initInstrumentation();

        int count = 0;
        while (count < LOOP_COUNT) {
            count++;
            LOGGER.info("Sending a Custom Event");
            createAndAddSingleCustomEvents();
            sendEventNonBlocking();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LOGGER.info("Sending Network Events thru Tracker");
            createAndAddNetworkEventsThruTracker();
            sendEventBlocking();

            LOGGER.info("Sending Network Events");
            createAndAddNetworkEvents();
            sendEventBlocking();

            LOGGER.info("Sending Network Events w/ Correlation Headers");
            createAndAddNetworkEventsThruTrackerWithCorrelation();
            sendEventNonBlocking();

            LOGGER.info("Sending Throwable Events");
            createAndAddThrowable();
            sendEventBlocking();

            LOGGER.info("Sending Error Events");
            createAndAddErrorEvent();
            sendEventNonBlocking();
        }
    }

    private static void initInstrumentation() {
        AgentConfiguration.Builder agentBuilder = AgentConfiguration.builder();
        AgentConfiguration agent = agentBuilder
                .withAppKey(APP_KEY)
                .withCollectorUrl(COLLECTOR_URL)
                .withAppKeyEnabledStatusChangeListener(listener)
                .build();

        DeviceInfo.Builder deviceInfoBuilder = DeviceInfo.builder("Smart Shelf", UUID.randomUUID().toString());
        DeviceInfo deviceInfo = deviceInfoBuilder.withDeviceName("Cheetah").build();

        VersionInfo.Builder versionInfoBuilder = VersionInfo.builder();
        VersionInfo versionInfo = versionInfoBuilder
                .withFirmwareVersion("2.3.4")
                .withHardwareVersion("1.6.7")
                .withOsVersion("8.9.9")
                .withSoftwareVersion("3.1.1").build();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                listener.shutdownAndAwaitTermination();
                LOGGER.debug("Shutting down Scheduler for IoT Monitoring");
            }
        });

        Instrumentation.start(agent, deviceInfo, versionInfo);
        LOGGER.info("Instrumentation initialized");
        return;
    }

    private static void createAndAddSingleCustomEvents() {
        Instrumentation.addEvent(CustomEventCreator.create(5));
    }

    private static void createAndAddNetworkEventsThruTracker() {
        NetworkEventCreator.sendPostAndAddEvent();
    }

    private static void createAndAddNetworkEventsThruTrackerWithCorrelation() {
        NetworkEventCreator.sendPostAndAddEventWithHeaders();
    }

    private static void createAndAddNetworkEvents() {
        Instrumentation.addEvent(NetworkEventCreator.create());
    }

    private static void createAndAddThrowable() {
        try {
            String s = null;
            s.charAt(0);
        } catch (Throwable t) {
            Instrumentation.addErrorEvent(t, Instrumentation.Severity.ALERT);
        }

        try {
            float f = 5 / 0;
        } catch (Throwable t) {
            Instrumentation.addErrorEvent(t, Instrumentation.Severity.CRITICAL);
        }
    }

    private static void createAndAddErrorEvent() {
        Instrumentation.addEvent(ErrorEventCreator.create());
    }

    public static void sendEventNonBlocking() {
        Thread t = new Thread(new NonBlockingSender());
        t.start();
    }

    public static void sendEventBlocking() {
        LOGGER.info("Events send initiated. Blocking ");
        Instrumentation.sendAllEvents();
        LOGGER.info("...done ");
    }

    private static class NonBlockingSender implements Runnable {
        @Override
        public void run() {
            Instrumentation.sendAllEvents();
            LOGGER.info("Events send initiated. Non Blocking");
        }
    }
}
