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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomEvent extends Event {
    public static final String CUSTOM_EVENT_LABEL = "customEvents";
    private static final String EVENT_TYPE = "eventType";
    private static final String EVENT_SUMMARY = "eventSummary";
    private final String eventType;
    private final String eventSummary;

    public CustomEvent(String type, String eventSummary,
                       long timestamp,
                       long duration,
                       Map<String, String> stringProperties,
                       Map<String, Long> longProperties,
                       Map<String, Double> doubleProperties,
                       Map<String, Boolean> booleanProperties,
                       Map<String, Date> dateProperties) {
        super(Type.CUSTOM_EVENT,
                timestamp,
                duration,
                stringProperties,
                longProperties,
                doubleProperties,
                booleanProperties,
                dateProperties);
        this.eventType = StringUtils.abbreviate(type, Constants.CUSTOM_EVENT_TYPE_MAX);
        this.eventSummary = StringUtils.abbreviate(eventSummary, Constants.CUSTOM_EVENT_SUMMARY_MAX);

    }

    @Override
    public String toString() {
        return "Custom Event";
    }

    @Override
    public void eventSpecificFields(JsonWriter writer) throws IOException {
        writer.name(EVENT_TYPE).value(this.eventType);
        writer.name(EVENT_SUMMARY).value(this.eventSummary);
    }

    @Override
    public Type getType() {
        return Type.CUSTOM_EVENT;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getEventSummary() {
        return this.eventSummary;
    }

    public static Builder builder(String type, String summary) {
        return new Builder(type, summary);
    }

    public static final class Builder extends BaseBuilder<Builder> {
        private final String summary;
        private final String type;

        private Builder(String type, String summary) {
            this.thisObj = this;
            this.timestamp = System.currentTimeMillis();
            this.type = type;
            this.summary = summary;
        }

        public CustomEvent build() {
            return new CustomEvent(type,
                    summary,
                    timestamp,
                    duration,
                    Collections.unmodifiableMap(new HashMap<String, String>(stringProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Long>(longProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Double>(doubleProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Boolean>(booleanProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Date>(dateProperties)));
        }
    }
}
