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
import com.appdynamics.iot.utils.StringUtilsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class EventTest {

    private static final String CUSTOM_EVENT_TYPE = "Custom Event Type";
    private static final String CUSTOM_EVENT_SUMMARY = "Custom Event Summary";
    private static final String KEY = "Key";
    private static final String VALUE = "value";

    private CustomEvent.Builder builder;

    @Before
    public void createCustomEventBuilder() {
        builder = CustomEvent.builder(CUSTOM_EVENT_TYPE, CUSTOM_EVENT_SUMMARY);
    }

    @Test
    public void testWithTimestamp() throws Exception {
        final long thisTimestamp = System.currentTimeMillis() + 33L;
        CustomEvent c = builder.withTimestamp(thisTimestamp).build();
        assertEquals(thisTimestamp, c.timestamp);
    }

    @Test
    public void testWithDuration() throws Exception {
        final long duration = 3000L;
        CustomEvent c = builder.withDuration(duration).build();
        assertEquals(duration, c.duration);
    }

    @Test
    public void testWithDurationInvalid() throws Exception {
        CustomEvent c = builder.withDuration(-1).build();
        assertNotEquals(-1, c.duration);
    }

    @Test
    public void testAddRemoveAddStringProperty() throws Exception {
        builder.addStringProperty(KEY, VALUE);
        assertTrue(builder.stringProperties.containsKey(KEY));
        assertEquals(VALUE, builder.stringProperties.get(KEY));

        builder.removeStringProperty((KEY));
        assertFalse(builder.stringProperties.containsKey(KEY));

        CustomEvent c = builder.addStringProperty(KEY, VALUE).build();
        assertTrue(c.stringProperties.containsKey(KEY));
        assertEquals(VALUE, c.stringProperties.get(KEY));
    }

    @Test
    public void testRemoveStringProperty() throws Exception {
        builder.addStringProperty((KEY), VALUE);
        CustomEvent c = builder.removeStringProperty(KEY).build();
        assertFalse(c.stringProperties.containsKey(KEY));
    }

    @Test
    public void testClearStringProperties() throws Exception {
        final String key1 = "key1";
        final String value1 = "value1";
        final String key2 = "key2";
        final String value2 = "value2";

        builder.addStringProperty(key1, value1);
        builder.addStringProperty(key2, value2);
        assertEquals(value1, builder.stringProperties.get(key1));
        assertEquals(value2, builder.stringProperties.get(key2));

        builder.clearStringProperties();
        CustomEvent c = builder.build();
        assertFalse(c.stringProperties.containsKey(key1));
        assertFalse(c.stringProperties.containsKey(key2));
    }

    @Test
    public void testStringPropertyWithLongValues() throws Exception {
        final String longStringValue = StringUtilsTest.repeat('z', Constants.EVENT_STRING_PROPERTIES_VALUE_MAX + 1);
        CustomEvent c = builder.addStringProperty(KEY, longStringValue).build();
        assertEquals(Constants.EVENT_STRING_PROPERTIES_VALUE_MAX, c.stringProperties.get(KEY).length());

    }

    @Test
    public void testAddRemoveAddLongProperty() throws Exception {
        final long longValue = Long.MAX_VALUE;

        builder.addLongProperty(KEY, longValue);
        assertTrue(builder.longProperties.containsKey(KEY));
        assertEquals(longValue, (long) builder.longProperties.get(KEY));

        builder.removeLongProperty((KEY));
        assertFalse(builder.longProperties.containsKey(KEY));

        CustomEvent c = builder.addLongProperty(KEY, longValue).build();
        assertTrue(c.longProperties.containsKey(KEY));
        assertEquals(longValue, (long) c.longProperties.get(KEY));
    }

    @Test
    public void testRemoveLongProperty() throws Exception {
        final long longValue = Long.MAX_VALUE;
        builder.addLongProperty((KEY), longValue);
        CustomEvent c = builder.removeLongProperty(KEY).build();
        assertFalse(c.stringProperties.containsKey(KEY));
    }

    @Test
    public void testClearLongProperties() throws Exception {
        final String key1 = "key1";
        final long value1 = Long.MAX_VALUE;
        final String key2 = "key2";
        final long value2 = Long.MIN_VALUE;

        builder.addLongProperty(key1, value1);
        builder.addLongProperty(key2, value2);
        assertEquals(value1, (long) builder.longProperties.get(key1));
        assertEquals(value2, (long) builder.longProperties.get(key2));

        builder.clearLongProperties();
        CustomEvent c = builder.build();
        assertFalse(c.longProperties.containsKey(key1));
        assertFalse(c.longProperties.containsKey(key2));
    }

    @Test
    public void testAddRemoveAddDoubleProperty() throws Exception {
        final double doubleValue = Double.MAX_VALUE;

        builder.addDoubleProperty(KEY, doubleValue);
        assertTrue(builder.doubleProperties.containsKey(KEY));
        assertEquals(Double.doubleToLongBits(doubleValue), Double.doubleToLongBits(builder.doubleProperties.get(KEY)));

        builder.removeDoubleProperty((KEY));
        assertFalse(builder.doubleProperties.containsKey(KEY));

        CustomEvent c = builder.addDoubleProperty(KEY, doubleValue).build();
        assertTrue(c.doubleProperties.containsKey(KEY));
        assertEquals(Double.doubleToLongBits(doubleValue), Double.doubleToLongBits(builder.doubleProperties.get(KEY)));
    }

    @Test
    public void testRemoveDoubleProperty() throws Exception {
        final double doubleValue = Double.MAX_VALUE;
        builder.addDoubleProperty((KEY), doubleValue);
        CustomEvent c = builder.removeDoubleProperty(KEY).build();
        assertFalse(c.doubleProperties.containsKey(KEY));
    }

    @Test
    public void testClearDoubleProperties() throws Exception {
        final String key1 = "key1";
        final Double value1 = Double.MIN_VALUE;
        final String key2 = "key2";
        final Double value2 = Double.MAX_VALUE;

        builder.addDoubleProperty(key1, value1);
        builder.addDoubleProperty(key2, value2);
        assertEquals(value1, builder.doubleProperties.get(key1));
        assertEquals(value2, builder.doubleProperties.get(key2));

        builder.clearDoubleProperties();
        CustomEvent c = builder.build();
        assertFalse(c.doubleProperties.containsKey(key1));
        assertFalse(c.doubleProperties.containsKey(key2));
    }

    @Test
    public void testAddRemoveAddDateProperty() throws Exception {
        final Date dateValue = new Date();

        builder.addDateProperty(KEY, dateValue);
        assertTrue(builder.dateProperties.containsKey(KEY));
        assertEquals(dateValue, builder.dateProperties.get(KEY));

        builder.removeDateProperty(KEY);
        assertFalse(builder.dateProperties.containsKey(KEY));

        CustomEvent c = builder.addDateProperty(KEY, dateValue).build();
        assertTrue(c.dateProperties.containsKey(KEY));
        assertEquals(dateValue, c.dateProperties.get(KEY));
    }

    @Test
    public void testRemoveDateProperty() throws Exception {
        final Date dateValue = new Date();
        builder.addDateProperty((KEY), dateValue);
        CustomEvent c = builder.removeDateProperty(KEY).build();
        assertFalse(c.dateProperties.containsKey(KEY));
    }

    @Test
    public void testClearDateProperties() throws Exception {
        final String key1 = "key1";
        final Date value1 = new Date();
        final String key2 = "key2";
        final Date value2 = new Date();

        builder.addDateProperty(key1, value1);
        builder.addDateProperty(key2, value2);
        assertEquals(value1, builder.dateProperties.get(key1));
        assertEquals(value2, builder.dateProperties.get(key2));

        builder.clearDateProperties();
        CustomEvent c = builder.build();
        assertFalse(c.dateProperties.containsKey(key1));
        assertFalse(c.dateProperties.containsKey(key2));
    }

    @Test
    public void testAddBooleanProperty() throws Exception {
        final boolean value = false;
        builder.addBooleanProperty(KEY, value);
        assertTrue(builder.booleanProperties.containsKey(KEY));
        assertEquals(builder.booleanProperties.get(KEY), value);

        builder.removeBooleanProperty(KEY);
        assertFalse(builder.booleanProperties.containsKey(KEY));

        CustomEvent c = builder.addBooleanProperty(KEY, value).build();
        assertTrue(c.booleanProperties.containsKey(KEY));
        assertEquals(c.booleanProperties.get(KEY), value);
    }

    @Test
    public void testRemoveBooleanProperty() throws Exception {
        final boolean value = false;
        builder.addBooleanProperty((KEY), value);
        CustomEvent c = builder.removeBooleanProperty(KEY).build();
        assertFalse(c.booleanProperties.containsKey(KEY));
    }

    @Test
    public void testClearBooleanProperties() throws Exception {
        final String key1 = "key1";
        final boolean value1 = true;
        final String key2 = "key2";
        final boolean value2 = false;

        builder.addBooleanProperty(key1, value1);
        builder.addBooleanProperty(key2, value2);
        assertEquals(builder.booleanProperties.get(key1), value1);
        assertEquals(builder.booleanProperties.get(key2), value2);

        builder.clearBooleanProperties();
        CustomEvent c = builder.build();
        assertFalse(c.booleanProperties.containsKey(key1));
        assertFalse(c.booleanProperties.containsKey(key2));
    }

    @Test
    public void testAddPropertiesWithPipedKeys() throws Exception {
        final String pipedKey = "Key|";
        final Long longValue = Long.MAX_VALUE;
        final Date dateValue = new Date();
        final Double doubleValue = Double.MAX_VALUE;
        final Boolean booleanValue = false;

        CustomEvent c = builder.addStringProperty(pipedKey, VALUE)
                .addBooleanProperty(pipedKey, booleanValue)
                .addDateProperty(pipedKey, dateValue)
                .addDoubleProperty(pipedKey, doubleValue)
                .addLongProperty(pipedKey, longValue).build();
        assertFalse(c.stringProperties.containsKey(pipedKey));
        assertTrue(c.stringProperties.containsKey(KEY));
        assertFalse(c.booleanProperties.containsKey(pipedKey));
        assertTrue(c.booleanProperties.containsKey(KEY));
        assertFalse(c.doubleProperties.containsKey(pipedKey));
        assertTrue(c.doubleProperties.containsKey(KEY));
        assertFalse(c.dateProperties.containsKey(pipedKey));
        assertTrue(c.dateProperties.containsKey(KEY));
        assertFalse(c.longProperties.containsKey(pipedKey));
        assertTrue(c.longProperties.containsKey(KEY));
    }

    @Test
    public void testRemovePropertiesWithPipedKeys() throws Exception {
        final String pipedKey = "Key|";
        final Long longValue = Long.MAX_VALUE;
        final Date dateValue = new Date();
        final Double doubleValue = Double.MAX_VALUE;
        final Boolean booleanValue = false;

        builder.addStringProperty(pipedKey, VALUE)
                .addBooleanProperty(pipedKey, booleanValue)
                .addDateProperty(pipedKey, dateValue)
                .addDoubleProperty(pipedKey, doubleValue)
                .addLongProperty(pipedKey, longValue);
        CustomEvent c = builder.removeStringProperty(pipedKey)
                .removeBooleanProperty(pipedKey)
                .removeDateProperty(pipedKey)
                .removeDoubleProperty(pipedKey)
                .removeLongProperty(pipedKey).build();
        assertFalse(c.stringProperties.containsKey(pipedKey));
        assertFalse(c.stringProperties.containsKey(KEY));
        assertFalse(c.booleanProperties.containsKey(pipedKey));
        assertFalse(c.booleanProperties.containsKey(KEY));
        assertFalse(c.doubleProperties.containsKey(pipedKey));
        assertFalse(c.doubleProperties.containsKey(KEY));
        assertFalse(c.dateProperties.containsKey(pipedKey));
        assertFalse(c.dateProperties.containsKey(KEY));
        assertFalse(c.longProperties.containsKey(pipedKey));
        assertFalse(c.longProperties.containsKey(KEY));
    }

    @Test
    public void testAddPropertiesWithLongKeys() throws Exception {
        final String longKey = StringUtilsTest.repeat('2', Constants.EVENT_PROPERTIES_KEY_MAX + 1);
        final String abbreviatedKey = StringUtils.abbreviate(longKey, Constants.EVENT_PROPERTIES_KEY_MAX);
        final Long longValue = Long.MAX_VALUE;
        final Date dateValue = new Date();
        final Double doubleValue = Double.MAX_VALUE;
        final Boolean booleanValue = false;

        CustomEvent c = builder.addStringProperty(longKey, VALUE)
                .addBooleanProperty(longKey, booleanValue)
                .addDateProperty(longKey, dateValue)
                .addDoubleProperty(longKey, doubleValue)
                .addLongProperty(longKey, longValue).build();
        assertFalse(c.stringProperties.containsKey(longKey));
        assertTrue(c.stringProperties.containsKey(abbreviatedKey));
        assertFalse(c.booleanProperties.containsKey(longKey));
        assertTrue(c.booleanProperties.containsKey(abbreviatedKey));
        assertFalse(c.doubleProperties.containsKey(longKey));
        assertTrue(c.doubleProperties.containsKey(abbreviatedKey));
        assertFalse(c.dateProperties.containsKey(longKey));
        assertTrue(c.dateProperties.containsKey(abbreviatedKey));
        assertFalse(c.longProperties.containsKey(longKey));
        assertTrue(c.longProperties.containsKey(abbreviatedKey));
    }

    @Test
    public void testRemovePropertiesWithLongKeys() throws Exception {
        final String longKey = StringUtilsTest.repeat('2', Constants.EVENT_PROPERTIES_KEY_MAX + 1);
        final String abbreviatedKey = StringUtils.abbreviate(longKey, Constants.EVENT_PROPERTIES_KEY_MAX);
        final Long longValue = Long.MAX_VALUE;
        final Date dateValue = new Date();
        final Double doubleValue = Double.MAX_VALUE;
        final Boolean booleanValue = false;

        builder.addStringProperty(longKey, VALUE)
                .addBooleanProperty(longKey, booleanValue)
                .addDateProperty(longKey, dateValue)
                .addDoubleProperty(longKey, doubleValue)
                .addLongProperty(longKey, longValue);
        CustomEvent c = builder.removeStringProperty(longKey)
                .removeBooleanProperty(longKey)
                .removeDateProperty(longKey)
                .removeDoubleProperty(longKey)
                .removeLongProperty(longKey).build();
        assertFalse(c.stringProperties.containsKey(longKey));
        assertFalse(c.stringProperties.containsKey(abbreviatedKey));
        assertFalse(c.booleanProperties.containsKey(longKey));
        assertFalse(c.booleanProperties.containsKey(abbreviatedKey));
        assertFalse(c.doubleProperties.containsKey(longKey));
        assertFalse(c.doubleProperties.containsKey(abbreviatedKey));
        assertFalse(c.dateProperties.containsKey(longKey));
        assertFalse(c.dateProperties.containsKey(abbreviatedKey));
        assertFalse(c.longProperties.containsKey(longKey));
        assertFalse(c.longProperties.containsKey(abbreviatedKey));
    }

}