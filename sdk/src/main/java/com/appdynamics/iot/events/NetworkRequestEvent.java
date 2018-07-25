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

package com.appdynamics.iot.events;

import com.appdynamics.iot.utils.Constants;
import com.appdynamics.iot.utils.StringUtils;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.appdynamics.iot.Instrumentation.LOGGER;

public class NetworkRequestEvent extends Event {
    public static final String NETWORK_EVENT_TYPE_LABEL = "networkRequestEvents";
    private static final String URL = "url";
    private static final String STATUS_CODE = "statusCode";
    private static final String NETWORK_ERROR = "networkError";
    private static final String REQUEST_CONTENT_LENGTH = "requestContentLength";
    private static final String RESPONSE_CONTENT_LENGTH = "responseContentLength";
    private static final String SERVER_RESPONSE_HEADERS = "responseHeaders";

    final String url;
    final Integer statusCode;
    final String networkError;
    final Integer requestContentLength;
    final Integer responseContentLength;
    final Map<String, List<String>> responseHeaders;

    public NetworkRequestEvent(URL url,
                               Integer statusCode,
                               String networkError,
                               Integer requestContentLength,
                               Integer responseContentLength,
                               Map<String, List<String>> responseHeaders,
                               long timestamp,
                               long duration,
                               Map<String, String> stringProperties,
                               Map<String, Long> longProperties,
                               Map<String, Double> doubleProperties,
                               Map<String, Boolean> booleanProperties,
                               Map<String, Date> dateProperties) {
        super(Type.NETWORK_EVENT,
                timestamp,
                duration,
                stringProperties,
                longProperties,
                doubleProperties,
                booleanProperties,
                dateProperties);

        if (url != null) {
            this.url = StringUtils.abbreviate(url.toString(), Constants.NETWORK_REQUEST_EVENT_URL_MAX);
        } else {
            this.url = null;
        }
        this.statusCode = statusCode;
        this.networkError = StringUtils.abbreviate(networkError, Constants.NETWORK_REQUEST_EVENT_ERROR_MAX);
        this.requestContentLength = requestContentLength;
        this.responseContentLength = responseContentLength;
        if (responseHeaders != null) {
            this.responseHeaders = Collections.unmodifiableMap(responseHeaders);
        } else {
            this.responseHeaders = null;
        }
    }

    @Override
    public void eventSpecificFields(JsonWriter writer) throws IOException {
        if (this.url != null) {
            writer.name(URL).value(url);
        }
        if (this.statusCode != null) {
            writer.name(STATUS_CODE).value(this.statusCode);
        }

        if (this.networkError != null) {
            writer.name(NETWORK_ERROR).value(this.networkError);
        }

        if (this.requestContentLength != null) {
            writer.name(REQUEST_CONTENT_LENGTH).value(this.requestContentLength);
        }

        if (this.responseContentLength != null) {
            writer.name(RESPONSE_CONTENT_LENGTH).value(this.responseContentLength);
        }

        if (this.responseHeaders != null) {
            writer.name(SERVER_RESPONSE_HEADERS);
            writer.beginObject();
            if (responseHeaders.size() > 0) {
                Set<Map.Entry<String, List<String>>> set = responseHeaders.entrySet();
                Iterator<Map.Entry<String, List<String>>> i = set.iterator();
                Map.Entry<String, List<String>> entry;
                List<String> values;
                while (i.hasNext()) {
                    entry = i.next();
                    writer.name(entry.getKey());
                    values = entry.getValue();
                    writer.beginArray();
                    for (String s : values) {
                        writer.value(s);
                    }
                    writer.endArray();
                }
            }
            writer.endObject();
        }
    }

    @Override
    public Type getType() {
        return Type.NETWORK_EVENT;
    }

    public static Builder builder(URL url) {
        return new Builder(url);
    }

    public String getUrl() {
        return this.url;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public Integer getRequestContentLength() {
        return this.requestContentLength;
    }

    public Integer getResponseContentLength() {
        return this.responseContentLength;
    }

    public String getNetworkError() {
        return this.networkError;
    }

    public static final class Builder extends BaseBuilder<Builder> {

        private final URL url;
        private Integer statusCode;
        private String networkError;
        private Integer requestContentLength;
        private Integer responseContentLength;
        private Map<String, List<String>> responseHeaders;

        private Builder(URL url) {
            this.thisObj = this;
            this.timestamp = System.currentTimeMillis();
            this.url = url;
        }

        public Builder withStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder withNetworkError(String error) {
            this.networkError = error;
            return this;
        }

        public Builder withRequestContentLength(Integer requestContentLength) {
            if (requestContentLength != null) {
                this.requestContentLength = requestContentLength;
            }
            return this;
        }

        public Builder withResponseContentLength(Integer responseContentLength) {
            if (responseContentLength != null) {
                this.responseContentLength = responseContentLength;
            }
            return this;
        }

        public synchronized Builder withServerResponseHeaders(Map<String, List<String>> responseHeaderFields) {
            if (responseHeaderFields != null && responseHeaderFields.size() > 0) {
                this.responseHeaders = responseHeaderFields;
            }
            return this;
        }

        public NetworkRequestEvent build() {
            Map<String, List<String>> adrumHeaders = createAdrumHeaderMap();
            return new NetworkRequestEvent(url,
                    statusCode,
                    networkError,
                    requestContentLength,
                    responseContentLength,
                    adrumHeaders,
                    timestamp,
                    duration,
                    Collections.unmodifiableMap(new HashMap<String, String>(stringProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Long>(longProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Double>(doubleProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Boolean>(booleanProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Date>(dateProperties)));
        }

        private synchronized Map<String, List<String>> createAdrumHeaderMap() {
            Map<String, List<String>> result = null;
            if (this.responseHeaders != null) {
                result = new HashMap<String, List<String>>();
                Iterator<String> iterator;
                String key;
                List<String> entry;
                iterator = this.responseHeaders.keySet().iterator();
                while (iterator.hasNext()) {
                    key = iterator.next();
                    if (key != null && key.regionMatches(true, 0, "ADRUM", 0, 5)) {
                        entry = this.responseHeaders.get(key);
                        result.put(key, entry);
                        LOGGER.debug("Received ADRUM Headers: {} : {}", key, entry);
                    }
                }
            }
            return result;
        }
    }
}
