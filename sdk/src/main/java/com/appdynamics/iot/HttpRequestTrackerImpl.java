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

import com.appdynamics.iot.events.NetworkRequestEvent;
import com.google.common.eventbus.EventBus;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.appdynamics.iot.Instrumentation.LOGGER;

public class HttpRequestTrackerImpl extends AbstractHttpRequestTracker implements HttpRequestTracker {
    private final EventBus eventBus;
    private final URL url;
    private final long startTime;

    private boolean alreadyReported;

    public HttpRequestTrackerImpl(EventBus eventBus, URL url) {
        this.eventBus = eventBus;
        this.url = url;
        this.startTime = System.currentTimeMillis();
        this.alreadyReported = false;
    }

    @Override
    public void reportDone() {
        try {
            if (isReportable()) {
                report();
            } else if (alreadyReported) {
                LOGGER.warn("Request already reported. This will not be reported to the AppDynamics collector.");
                LOGGER.warn("Do not reuse instances of HttpRequestTracker.");
                logRequestDetails();
            } else {
                LOGGER.warn("Not enough information provided");
                LOGGER.warn("HTTP request will not be reported to the AppDynamics collector.");
                logRequestDetails();
            }
        } catch (Throwable e) {
            LOGGER.error("Exception while reporting HTTP request {}", e.toString());
        }
    }

    private void logRequestDetails() {
        LOGGER.debug("Request Details");
        LOGGER.debug("URL: {}", url);
        LOGGER.debug("Response Code: {}", responseCode);
        LOGGER.debug("Error Message: {}", error);
        LOGGER.debug("Throwable: ", throwable);
    }

    private boolean isReportable() {
        return !alreadyReported && url != null && (responseCode != null || error != null || throwable != null);
    }

    private void report() {
        final long timeNow = System.currentTimeMillis();
        NetworkRequestEvent.Builder builder = NetworkRequestEvent.builder(this.url)
                .withTimestamp(startTime)
                .withDuration(timeNow - startTime)
                .withStatusCode(responseCode)
                .withServerResponseHeaders(responseHeaderFields);
        NetworkRequestEvent event;
        if (throwable != null) {
            builder.withNetworkError(throwable.getMessage());
        } else if (error != null) {
            builder.withNetworkError(error);
        } else {
            builder.withRequestContentLength(getContentLength(requestHeaderFields))
                    .withResponseContentLength(getContentLength(responseHeaderFields));
        }
        event = builder.build();
        eventBus.post(event);
        alreadyReported = true;
    }

    private Integer getContentLength(Map<String, List<String>> headerFields) {
        if (headerFields != null) {
            for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                // HTTP Headers are case-insensitive
                if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("content-length")) {
                    List<String> cls = entry.getValue();
                    if (cls != null && !cls.isEmpty()) {
                        try {
                            return Integer.valueOf(cls.get(0));
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    }
                    break;
                }
            }
        }
        return null;
    }
}
