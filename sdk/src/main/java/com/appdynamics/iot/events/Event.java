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
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Base class of the Event</p>
 * <p>Each event has a timestamp and a duration</p>
 * <p>And maps of name-value pairs</p>
 * <p>Known Direct Classes</p>
 * <ul>
 * <li>{@link CustomEvent}</li>
 * <li>{@link NetworkRequestEvent}</li>
 * <li>{@link ErrorEvent}</li>
 * </ul>
 */
public abstract class Event {
    static final String TIMESTAMP = "timestamp";
    static final String DURATION = "duration";
    static final String STRING_PROPERTIES = "stringProperties";
    static final String LONG_PROPERTIES = "longProperties";
    static final String BOOLEAN_PROPERTIES = "booleanProperties";
    static final String DOUBLE_PROPERTIES = "doubleProperties";
    static final String DATE_PROPERTIES = "datetimeProperties";

    protected long timestamp;
    protected long duration;
    protected Type identifier;

    final Map<String, String> stringProperties;
    final Map<String, Long> longProperties;
    final Map<String, Double> doubleProperties;
    final Map<String, Boolean> booleanProperties;
    final Map<String, Date> dateProperties;

    public enum Type {
        CUSTOM_EVENT,
        NETWORK_EVENT,
        ERROR_EVENT
    }

    public Event(Type type,
                 long timestamp,
                 long duration,
                 Map<String, String> stringProperties,
                 Map<String, Long> longProperties,
                 Map<String, Double> doubleProperties,
                 Map<String, Boolean> booleanProperties,
                 Map<String, Date> dateProperties) {
        this.timestamp = timestamp;
        this.duration = duration;
        this.stringProperties = stringProperties;
        this.longProperties = longProperties;
        this.doubleProperties = doubleProperties;
        this.booleanProperties = booleanProperties;
        this.dateProperties = dateProperties;
        this.identifier = type;
    }

    public void toJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name(TIMESTAMP).value(this.timestamp);
        writer.name(DURATION).value(this.duration);
        writeProperties(writer);
        eventSpecificFields(writer);
        writer.endObject();
    }

    private void writeProperties(JsonWriter writer) throws IOException {
        writer.name(STRING_PROPERTIES);
        writer.beginObject();
        if (stringProperties.size() > 0) {
            Set<Map.Entry<String, String>> set = stringProperties.entrySet();
            Iterator<Map.Entry<String, String>> i = set.iterator();
            Map.Entry<String, String> entry;
            while (i.hasNext()) {
                entry = i.next();
                writer.name(entry.getKey()).value(entry.getValue());
            }
        }
        writer.endObject();

        writer.name(LONG_PROPERTIES);
        writer.beginObject();
        if (longProperties.size() > 0) {
            Set<Map.Entry<String, Long>> set = longProperties.entrySet();
            Iterator<Map.Entry<String, Long>> i = set.iterator();
            Map.Entry<String, Long> entry;
            while (i.hasNext()) {
                entry = i.next();
                writer.name(entry.getKey()).value(entry.getValue());
            }
        }
        writer.endObject();

        writer.name(DATE_PROPERTIES);
        writer.beginObject();
        if (dateProperties.size() > 0) {
            Set<Map.Entry<String, Date>> set = dateProperties.entrySet();
            Iterator<Map.Entry<String, Date>> i = set.iterator();
            Map.Entry<String, Date> entry;
            while (i.hasNext()) {
                entry = i.next();
                writer.name(entry.getKey()).value(entry.getValue().getTime());
            }
        }
        writer.endObject();

        writer.name(DOUBLE_PROPERTIES);
        writer.beginObject();
        if (doubleProperties.size() > 0) {
            Set<Map.Entry<String, Double>> set = doubleProperties.entrySet();
            Iterator<Map.Entry<String, Double>> i = set.iterator();
            Map.Entry<String, Double> entry;
            while (i.hasNext()) {
                entry = i.next();
                writer.name(entry.getKey()).value(entry.getValue());
            }
        }
        writer.endObject();

        writer.name(BOOLEAN_PROPERTIES);
        writer.beginObject();
        if (booleanProperties.size() > 0) {
            Set<Map.Entry<String, Boolean>> set = booleanProperties.entrySet();
            Iterator<Map.Entry<String, Boolean>> i = set.iterator();
            Map.Entry<String, Boolean> entry;
            while (i.hasNext()) {
                entry = i.next();
                writer.name(entry.getKey()).value(entry.getValue());
            }
        }
        writer.endObject();
    }

    public abstract void eventSpecificFields(JsonWriter writer) throws IOException;

    public abstract Type getType();

    public static class BaseBuilder<T> {
        protected T thisObj;
        protected long timestamp;
        protected long duration = 0L;
        protected final Map<String, String> stringProperties = new ConcurrentHashMap<String, String>();
        protected final Map<String, Long> longProperties = new ConcurrentHashMap<String, Long>();
        protected final Map<String, Double> doubleProperties = new ConcurrentHashMap<String, Double>();
        protected final Map<String, Boolean> booleanProperties = new ConcurrentHashMap<String, Boolean>();
        protected final Map<String, Date> dateProperties = new ConcurrentHashMap<String, Date>();

        /**
         * @param timestamp Timestamp when the event occurred or started
         * @return current Builder Object
         */
        public T withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this.thisObj;
        }

        /**
         * @param duration Must be later than the timestamp
         * @return current Builder Object
         */
        public T withDuration(long duration) {
            if (duration >= 0L) {
                this.duration = duration;
            }
            return this.thisObj;
        }

        /**
         * @param key   an identifier for the event property. Keys cannot contain the pipe character '|'.
         *              e.g. "url"
         * @param value a String value for this key. e.g. "http://www.appdynamics.com"
         * @return current Builder Object
         */
        public T addStringProperty(String key, String value) {
            stringProperties.put(getValidKey(key), StringUtils.abbreviate(value, Constants.EVENT_STRING_PROPERTIES_VALUE_MAX));
            return this.thisObj;
        }

        /**
         * @param key an identifier for the event property.
         *            If a key is not found, the call is a no-op
         * @return current Builder Object
         */
        public T removeStringProperty(String key) {
            stringProperties.remove(getValidKey(key));
            return this.thisObj;
        }

        /**
         * Clears all existing string properties
         *
         * @return current Builder Object
         */
        public T clearStringProperties() {
            stringProperties.clear();
            return this.thisObj;
        }

        /**
         * @param key   an identifier for the event property. Keys cannot contain the pipe character '|'.
         *              e.g. "responseCode"
         * @param value an int or long value for this key e.g. 200
         * @return current Builder Object
         */
        public T addLongProperty(String key, long value) {
            longProperties.put(getValidKey(key), value);
            return this.thisObj;
        }

        /**
         * @param key an identifier for a long property
         * @return current Builder Object
         */
        public T removeLongProperty(String key) {
            longProperties.remove(getValidKey(key));
            return this.thisObj;
        }

        /**
         * Clears all existing long properties
         *
         * @return current Builder Object
         */
        public T clearLongProperties() {
            longProperties.clear();
            return this.thisObj;
        }

        /**
         * @param key   an identifier for the event property. Keys cannot contain the pipe character '|'.
         *              e.g. "temperatureReading"
         * @param value a float value for this key e.g. 98.2
         * @return current Builder Object
         */
        public T addDoubleProperty(String key, double value) {
            doubleProperties.put(getValidKey(key), value);
            return this.thisObj;
        }

        /**
         * @param key an identifier for the a float property
         * @return current Builder Object
         */
        public T removeDoubleProperty(String key) {
            doubleProperties.remove(getValidKey(key));
            return this.thisObj;
        }

        /**
         * Clears all existing Double Properties
         *
         * @return current Builder Object
         */
        public T clearDoubleProperties() {
            doubleProperties.clear();
            return this.thisObj;
        }

        /**
         * @param key   an identifier for the event property. Keys cannot contain the pipe character '|'.
         *              e.g. "deviceBootUpTime"
         * @param value a Date value for this key e.g. 1487119625012
         * @return current Builder Object
         */
        public T addDateProperty(String key, Date value) {
            dateProperties.put(getValidKey(key), value);
            return this.thisObj;
        }

        /**
         * @param key an identifier for the DataProperty
         * @return current Builder Object
         */
        public T removeDateProperty(String key) {
            dateProperties.remove(getValidKey(key));
            return this.thisObj;
        }

        /**
         * Clear all existing boolean properties
         *
         * @return current Builder Object
         */
        public T clearDateProperties() {
            dateProperties.clear();
            return this.thisObj;
        }

        /**
         * @param key   an identifier for the event property. Keys cannot contain the pipe character '|'.
         *              e.g. "boilerOn"
         * @param value a boolean value for this key e.g. false
         * @return current Builder Object
         */
        public T addBooleanProperty(String key, boolean value) {
            booleanProperties.put(getValidKey(key), value);
            return this.thisObj;
        }

        /**
         * @param key a key for a boolean property
         * @return current Builder Object
         */
        public T removeBooleanProperty(String key) {
            booleanProperties.remove(getValidKey(key));
            return this.thisObj;
        }

        /**
         * Clear all existing boolean properties
         *
         * @return current Builder Object
         */
        public T clearBooleanProperties() {
            booleanProperties.clear();
            return this.thisObj;
        }

        private String getValidKey(String key) {
            String validKey = StringUtils.remove(key, Constants.PIPE_CHARACTER);
            return StringUtils.abbreviate(validKey, Constants.EVENT_PROPERTIES_KEY_MAX);
        }
    }
}
