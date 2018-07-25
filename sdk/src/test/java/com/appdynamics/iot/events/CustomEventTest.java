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
import com.appdynamics.iot.utils.StringUtilsTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CustomEventTest {

    private static final String CUSTOM_EVENT_TYPE = "Custom Event Type";
    private static final String CUSTOM_EVENT_SUMMARY = "Custom Event Summary";

    @Test
    public void testGetType() throws Exception {
        CustomEvent c = CustomEvent.builder(CUSTOM_EVENT_TYPE, CUSTOM_EVENT_SUMMARY).build();
        assertEquals(Event.Type.CUSTOM_EVENT, c.getType());
    }

    @Test
    public void testCustomEventDefault() throws Exception {
        CustomEvent c = CustomEvent.builder(CUSTOM_EVENT_TYPE, CUSTOM_EVENT_SUMMARY).build();
        assertEquals(CUSTOM_EVENT_TYPE, c.getEventType());
        assertEquals(CUSTOM_EVENT_SUMMARY, c.getEventSummary());
        assertNotNull(c.timestamp);

    }

    @Test
    public void testCustomEventDefaultNull() throws Exception {
        CustomEvent c = CustomEvent.builder(null, null).build();
        assertNull(c.getEventType());
        assertNull(c.getEventSummary());
    }

    @Test
    public void testCustomEventDefaultInvalid() throws Exception {
        final String longType = StringUtilsTest.repeat('p', Constants.CUSTOM_EVENT_TYPE_MAX + 1);
        final String longSummary = StringUtilsTest.repeat('q', Constants.CUSTOM_EVENT_SUMMARY_MAX + 1);
        CustomEvent c = CustomEvent.builder(longType, longSummary).build();
        assertEquals(Constants.CUSTOM_EVENT_TYPE_MAX, c.getEventType().length());
        assertEquals(Constants.CUSTOM_EVENT_SUMMARY_MAX, c.getEventSummary().length());
    }
}