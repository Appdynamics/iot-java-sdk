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

import com.appdynamics.iot.events.CustomEvent;
import com.appdynamics.iot.events.ErrorEvent;
import com.appdynamics.iot.events.Event;
import com.appdynamics.iot.events.NetworkRequestEvent;
import com.google.common.eventbus.EventBus;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import java.net.URL;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class EventBusTest {

    @Test
    public void testPostCustomEvent() throws Exception {
        final String eventType = "Custom Event Type";
        final String eventSummary = "Custom Event Summary";
        EventBus eventBus = EasyMock.createStrictMock(EventBus.class);
        Capture<CustomEvent> capturer = new Capture<CustomEvent>();
        eventBus.post(capture(capturer));
        expectLastCall();

        replay(eventBus);

        CustomEvent c = CustomEvent.builder(eventType, eventSummary).build();
        eventBus.post(c);

        verify(eventBus);

        CustomEvent capturedEvent = capturer.getValue();
        assertNotNull(capturedEvent);
        assertEquals(eventType, capturedEvent.getEventType());
        assertEquals(eventSummary, capturedEvent.getEventSummary());
        assertEquals(Event.Type.CUSTOM_EVENT, capturedEvent.getType());
    }

    @Test
    public void testPostNetworkEvent() throws Exception {
        EventBus eventBus = EasyMock.createStrictMock(EventBus.class);
        Capture<NetworkRequestEvent> capturer = new Capture<NetworkRequestEvent>();
        eventBus.post(capture(capturer));
        expectLastCall();

        replay(eventBus);

        URL testUrl = new URL("https://myapp.mydomain.com/iot");
        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl).build();
        eventBus.post(n);

        verify(eventBus);

        NetworkRequestEvent capturedEvent = capturer.getValue();
        assertNotNull(capturedEvent);
        assertEquals(capturedEvent.getUrl(), testUrl.toString());
        assertEquals(capturedEvent.getType(), Event.Type.NETWORK_EVENT);
    }

    @Test
    public void testPostErrorEvent() throws Exception {
        EventBus eventBus = EasyMock.createStrictMock(EventBus.class);
        Capture<ErrorEvent> capturer = new Capture<ErrorEvent>();
        eventBus.post(capture(capturer));
        expectLastCall();

        replay(eventBus);

        ErrorEvent e = ErrorEvent.builder("Error Event").build();
        eventBus.post(e);

        verify(eventBus);

        ErrorEvent capturedEvent = capturer.getValue();
        assertNotNull(capturedEvent);
        assertEquals("Error Event", capturedEvent.getErrorName());
        assertEquals(Event.Type.ERROR_EVENT, capturedEvent.getType());
    }

    @Test
    public void testRegisterListener() throws Exception {
        EventBus eventBus = EasyMock.createStrictMock(EventBus.class);
        Capture<EventListener> capturer = new Capture<EventListener>();
        eventBus.register(capture(capturer));
        expectLastCall();

        replay(eventBus);

        EventListener listener = new EventListener();
        eventBus.register(listener);

        verify(eventBus);
        assertEquals(capturer.getValue(), listener);
    }

    @Test
    public void testEventListenerHandle() throws Exception {
        EventListener eventListener = createStrictMock(EventListener.class);
        Capture<Event> capturer = new Capture<Event>();
        eventListener.handleEvent(capture(capturer));
        expectLastCall();

        replay(eventListener);

        EventBus eventBus = new EventBus();
        eventBus.register(eventListener);

        ErrorEvent e = ErrorEvent.builder("Error Event").build();
        eventBus.post(e);

        verify(eventListener);
        assertEquals(Event.Type.ERROR_EVENT, capturer.getValue().getType());
    }
}
