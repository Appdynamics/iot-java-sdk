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

import com.appdynamics.iot.events.ErrorEvent;
import com.appdynamics.iot.events.ErrorEvent.StackTrace;
import com.appdynamics.iot.events.Event;
import com.appdynamics.iot.utils.Constants;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * <p>This class provides the following functionality</p>
 * <ol>
 * <li>Registers with the AppDynamics EUM Cloud Collector</li>
 * <li>Provides methods to add different event types to the in-memory buffer</li>
 * <li> Event Support
 * <ul>
 * <li> Collect performance data for HTTP network requests <code>{@link com.appdynamics.iot.events.NetworkRequestEvent NetworkRequestEvent}</code></li>
 * <li> Collect error and exception events <code>{@link com.appdynamics.iot.events.ErrorEvent ErrorEvent}</code></li>
 * <li> Collect any business specific custom event <code>{@link com.appdynamics.iot.events.CustomEvent CustomEvent}</code></li>
 * <li> Send a collection of events at a given time</li>
 * </ul>
 * </li>
 * <li>Send the events over HTTPS. This call clears all beacons from the buffer</li>
 * <li>Manage the Instrumentation State</li>
 * </ol>
 */
public class Instrumentation {

    public static final Logger LOGGER = LoggerFactory.getLogger(Instrumentation.class);
    private static AgentConfiguration config;
    static boolean isDisabled = false;
    static boolean isInitialized = false;
    static EventBus eventBus;
    static BeaconManager beaconManager;

    static {
        eventBus = new EventBus();
        eventBus.register(new EventListener());
    }

    /**
     * @param configuration Configuration of the Agent
     * @param deviceInfo    information about the device
     * @param versionInfo   version info about the
     * @see AgentConfiguration
     * @see DeviceInfo
     * @see VersionInfo
     */
    public static void start(AgentConfiguration configuration, DeviceInfo deviceInfo, VersionInfo versionInfo) {
        Instrumentation.config = configuration;
        beaconManager = BeaconManagerFactory.createBeaconManager(deviceInfo, versionInfo);
        isInitialized = true;

        LOGGER.info("AppDynamics Instrumentation Started");
    }

    /**
     * If the controller sends a responseCode of 402, 403 or 429 to the app when it calls
     * {@link Instrumentation#sendAllEvents()}, the
     * instrumentation runtime is put in a disabled state and the following calls become no-ops.
     * {@link Instrumentation#addEvent(Event)},
     * {@link Instrumentation#addErrorEvent(Throwable, Severity)},
     * {@link Instrumentation#beginHttpRequest(URL)},
     * {@link Instrumentation#sendAllEvents()}
     * The current state of the APPKEY on the controller can be checked by calling
     * {@link Instrumentation#isAppKeyEnabledOnCloud()}
     *
     * @return true if the instrumentation runtime has been disabled
     */
    public static synchronized boolean isDisabled() {
        return isDisabled;
    }

    /**
     * @param url The URL of the Http Network Request being tracked
     * @return The tracking object
     */
    public static HttpRequestTracker beginHttpRequest(URL url) {
        if (!isDisabled()) {
            if (isInitialized && url != null) {
                LOGGER.debug("beginHttpRequest called for: {}", url);
                try {
                    return new HttpRequestTrackerImpl(eventBus, url);
                } catch (Throwable e) {
                    LOGGER.error("Exception while starting to track HTTP request", e);
                }
            }
        } else {
            LOGGER.debug("Instrumentation is disabled. No Network Request Events are being tracked.");
        }
        return new DummyHttpRequestTracker();
    }

    /**
     * @param throwable Report a throwable. If null, this method does nothing.
     * @param level     Level of the Error
     *                  <ol>
     *                  <li>{@link Severity#ALERT} An error that didn't cause any issues</li>
     *                  <li>{@link Severity#CRITICAL} An error that caused issues</li>
     *                  <li>{@link Severity#FATAL} An error that killed the app</li>
     *                  </ol>
     */
    public static void addErrorEvent(Throwable throwable, Severity level) {
        if (!isDisabled()) {
            if (isInitialized && throwable != null) {
                ErrorEvent event = getErrorEvent(throwable, level);
                eventBus.post(event);
            }
        } else {
            LOGGER.debug("Instrumentation is disabled. No Error Events will be collected.");
        }
    }

    private static ErrorEvent getErrorEvent(Throwable throwable, Severity level) {
        ErrorEvent.Builder builder = ErrorEvent.builder(throwable.getClass().getSimpleName());
        builder.withMessage(throwable.getMessage());
        builder.withSeverity(level);
        ArrayList<StackTrace> stackTraces = new ArrayList<StackTrace>();
        StackTrace s = new StackTrace((throwable.getStackTrace()));
        stackTraces.add(s);

        Throwable cause = throwable.getCause();
        while (cause != null && stackTraces.size() < Constants.ERROR_EVENT_STACK_TRACE_ELEMENTS_MAX) {
            stackTraces.add(new StackTrace(cause.getStackTrace()));
            cause = cause.getCause();
        }

        builder.withStackTraces(stackTraces);
        return builder.build();
    }

    /**
     * @param event a user defined event
     * @see Event
     */
    public static void addEvent(Event event) {
        if (!isDisabled()) {
            if (isInitialized && event != null) {
                eventBus.post(event);
            }
        } else {
            LOGGER.debug("Instrumentation is disabled. No Events will be collected.");
        }
    }

    /**
     * This method sends all collected beacons to the AppDynamics Collector
     * It flushes the in memory buffer after that
     * If a CollectorChannel is set, the Beacons are sent over that
     * If not, the default transport bundled with the SDK is used.
     * If the AppDynamics Collector returns an HTTP status code of 402, 403 or 429
     * the instrumentation runtime automatically gets disabled.
     * A status check to see if the current AppKey is enabled or not can be called at any time using
     * the call {@link Instrumentation#isAppKeyEnabledOnCloud()}
     *
     * @see CollectorChannelFactory
     * @see Instrumentation#isDisabled()
     */
    public static void sendAllEvents() {
        if (!isDisabled()) {
            if (isInitialized) {
                eventBus.post(config);
            }
        } else {
            LOGGER.debug("Instrumentation is disabled. No Events will be sent.");
        }
    }

    static synchronized void disable(boolean disableFlag) {
        isDisabled = disableFlag;
        if (config.getListener() != null && isDisabled) {
            config.getListener().onStateChanged(State.DISABLED);
            LOGGER.debug("Instrumentation is disabled");
        }
    }


    /**
     * This call syncs the instrumentation runtime with the cloud for the appkey being used and returns true if
     * the AppKey is enabled and the backend is ready to accept event data.
     *
     * @return false if the AppKey has been disabled for any reason. Some examples are turning off
     * IoT Monitoring from UI or License Expiration.
     * @see com.appdynamics.iot.Instrumentation.State
     * @see Instrumentation#isDisabled()
     */
    public static boolean isAppKeyEnabledOnCloud() {
        boolean result = false;
        if (isInitialized) {
            try {
                String url = config.getAppKeyEnabledCheckUrl();
                URL obj = new URL(config.getAppKeyEnabledCheckUrl());
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                int responseCode = con.getResponseCode();
                LOGGER.debug("Checking if AppKey is enabled from " + url);
                LOGGER.debug("AppKey Enabled Status change sync with cloud returned status code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    result = true;
                    disable(false);
                }
            } catch (MalformedURLException e) {
                LOGGER.error("Error with the endpoint to check enabled state for {}, {}",
                        config.getAppKey(), e.getStackTrace());
            } catch (IOException e) {
                LOGGER.error("IO Error with the endpoint to check enabled state for {}, {}",
                        config.getAppKey(), e.getStackTrace());
            }
        } else {
            LOGGER.debug("Instrumentation not initialized. Please call Instrumentation.start method first");
        }
        return result;
    }

    public enum Severity {
        ALERT("alert"),
        CRITICAL("critical"),
        FATAL("fatal");

        private String value;

        Severity(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    public enum State {
        ENABLED("enabled"),
        DISABLED("disabled");
        private String value;

        State(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }
}
