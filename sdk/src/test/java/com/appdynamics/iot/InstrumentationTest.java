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
import junit.framework.AssertionFailedError;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


public class InstrumentationTest {

    private final String appKey = "AAA-BBB-CCC";
    private static final String DEVICE_TYPE = "CAR_MODEL_A";
    private static final String DEVICE_ID = "1FMHK7F8XCGA67893";
    IMocksControl control;

    @Before
    public void initializeInstrumentation() throws Exception {
        Instrumentation.isInitialized = false;
        Instrumentation.isDisabled = false;
        Instrumentation.beaconManager = null;
        control = EasyMock.createControl();
    }

    @Test
    public void testStart() throws Exception {
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(appKey).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);
        assertTrue(Instrumentation.isInitialized);
        assertFalse(Instrumentation.isDisabled());
        assertNotNull(Instrumentation.beaconManager);
        assertNotNull(Instrumentation.eventBus);
    }

    @Test
    public void testStartNotCalled() throws Exception {
        CustomEvent mockCustomEvent = EasyMock.createNiceMock(CustomEvent.class);
        replay(mockCustomEvent);

        Instrumentation.addEvent(mockCustomEvent);
        assertFalse(Instrumentation.isInitialized);
        assertFalse(Instrumentation.isDisabled());
        assertNull(Instrumentation.beaconManager);
        assertNotNull(Instrumentation.eventBus);

        verify(mockCustomEvent);
    }

    @Test
    public void testBeginHttpRequest() throws Exception {
        final URL testUrl = new URL("http://www.test.com");

        EventBus mockEventBus = EasyMock.createStrictMock(EventBus.class);
        Instrumentation.eventBus = mockEventBus;

        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(appKey).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();

        Capture<NetworkRequestEvent> capturer = new Capture<NetworkRequestEvent>();
        mockEventBus.post(capture(capturer));
        expectLastCall();

        replay(mockEventBus);

        Instrumentation.start(agentConfiguration, deviceInfo, null);
        HttpRequestTracker tracker = new HttpRequestTrackerImpl(mockEventBus, testUrl);
        tracker.withResponseCode(200).reportDone();

        verify(mockEventBus);

        NetworkRequestEvent capturedEvent = capturer.getValue();
        assertNotNull(capturedEvent);
        assertEquals(testUrl.toString(), capturedEvent.getUrl());
        assertEquals(200, capturedEvent.getStatusCode());
    }

    @Test
    public void testAddErrorEvent() throws Exception {
        final String myExceptionMessage = "Exception Message";

        Throwable mockThrowable = control.createMock(Throwable.class);
        expect(mockThrowable.getStackTrace()).andReturn(null);
        expect(mockThrowable.getMessage()).andReturn(myExceptionMessage);
        expect(mockThrowable.getCause()).andReturn(null);

        EventBus mockEventBus = control.createMock(EventBus.class);
        Instrumentation.eventBus = mockEventBus;

        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(appKey).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);

        Capture<ErrorEvent> capturer = new Capture<ErrorEvent>();
        mockEventBus.post(capture(capturer));
        expectLastCall();

        control.replay();

        Instrumentation.addErrorEvent(mockThrowable, Instrumentation.Severity.CRITICAL);
        ErrorEvent capturedEvent = capturer.getValue();
        assertNotNull(capturedEvent);
        assertEquals(myExceptionMessage, capturedEvent.getMessage());
        assertEquals(0, capturedEvent.getErrorStackTraceIndex());
        assertNotNull(capturedEvent.getStackTraces().get(0));
        assertEquals(Event.Type.ERROR_EVENT, capturedEvent.getType());
        control.verify();
    }

    @Test
    public void testAddErrorEventWithCauseByThrowable() throws Exception {
        final String myExceptionMessage = "Exception Message";

        Throwable mockThrowable = EasyMock.createNiceMock(Throwable.class);
        Throwable mockCausedByThrowable = EasyMock.createNiceMock(Throwable.class);
        expect(mockThrowable.getMessage()).andReturn(myExceptionMessage);
        expect(mockThrowable.getCause()).andReturn(mockCausedByThrowable);
        replay(mockThrowable);
        replay(mockCausedByThrowable);

        EventBus mockEventBus = EasyMock.createStrictMock(EventBus.class);
        Instrumentation.eventBus = mockEventBus;

        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(appKey).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);

        Capture<ErrorEvent> capturer = new Capture<ErrorEvent>();
        mockEventBus.post(capture(capturer));
        expectLastCall();
        replay(mockEventBus);

        Instrumentation.addErrorEvent(mockThrowable, Instrumentation.Severity.CRITICAL);
        verify(mockEventBus);
        ErrorEvent capturedEvent = capturer.getValue();
        assertNotNull(capturedEvent);
        assertEquals(myExceptionMessage, capturedEvent.getMessage());
        assertEquals(0, capturedEvent.getErrorStackTraceIndex());
        assertNotNull(capturedEvent.getStackTraces().get(0));
        assertNotNull(capturedEvent.getStackTraces().get(1));
        assertEquals(Event.Type.ERROR_EVENT, capturedEvent.getType());
    }

    @Test
    public void testAddEvent() throws Exception {

        final String myEventType = "My Type";
        final String myEventSummary = "My Event Summary";

        EventBus mockEventBus = EasyMock.createStrictMock(EventBus.class);
        Instrumentation.eventBus = mockEventBus;

        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(appKey).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);

        Capture<CustomEvent> capturer = new Capture<CustomEvent>();
        mockEventBus.post(capture(capturer));

        expectLastCall();

        replay(mockEventBus);

        CustomEvent customEvent = CustomEvent.builder(myEventType, myEventSummary).build();
        Instrumentation.addEvent(customEvent);

        verify(mockEventBus);

        CustomEvent capturedEvent = capturer.getValue();
        assertNotNull(capturedEvent);
        assertEquals(myEventType, capturedEvent.getEventType());
        assertEquals(myEventSummary, capturedEvent.getEventSummary());
        assertEquals(Event.Type.CUSTOM_EVENT, capturedEvent.getType());
    }

    @Test
    public void testSendAllEvents() throws Exception {

        EventBus mockEventBus = EasyMock.createStrictMock(EventBus.class);
        Instrumentation.eventBus = mockEventBus;

        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(appKey).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);

        Capture<CustomEvent> customEventCapturer = new Capture<CustomEvent>();
        mockEventBus.post(capture(customEventCapturer));
        expectLastCall();

        Capture<ErrorEvent> errorEventCapturer = new Capture<ErrorEvent>();
        mockEventBus.post(capture(errorEventCapturer));
        expectLastCall();

        Capture<NetworkRequestEvent> networkEventCapturer = new Capture<NetworkRequestEvent>();
        mockEventBus.post(capture(networkEventCapturer));
        expectLastCall();

        Capture<AgentConfiguration> agentConfigurationCapture = new Capture<AgentConfiguration>();
        mockEventBus.post(capture(agentConfigurationCapture));
        expectLastCall();

        replay(mockEventBus);

        CustomEvent customEvent = CustomEvent.builder("My Custom Device", "Device that reads brain waves and causes Tsunamis").build();
        Instrumentation.addEvent(customEvent);

        ErrorEvent errorEvent = ErrorEvent.builder("My Error").build();
        Instrumentation.addEvent(errorEvent);

        NetworkRequestEvent networkEvent = NetworkRequestEvent.builder(new URL("http://no.tsunami.org")).build();
        Instrumentation.addEvent(networkEvent);

        Instrumentation.sendAllEvents();

        verify(mockEventBus);

        AgentConfiguration capturedEvent = agentConfigurationCapture.getValue();
        assertNotNull(capturedEvent);
        assertEquals(appKey, capturedEvent.getAppKey());
    }

    @Test
    public void testDisabledStatus() throws Exception {
        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(appKey).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);
        Instrumentation.disable(true);
        assertTrue(Instrumentation.isDisabled());
    }

    @Test
    public void testDisabledStatusWithAddEvent() throws Exception {
        final String myEventType = "My Type";
        final String myEventSummary = "My Event Summary";

        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(appKey).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);
        Instrumentation.disable(true);
        EventBus mockEventBus = control.createMock(EventBus.class);
        Instrumentation.eventBus = mockEventBus;
        Capture<CustomEvent> capturer = new Capture<CustomEvent>();
        mockEventBus.post(capture(capturer));
        // Since the instrumentation is disabled, post should not be called
        expectLastCall().andThrow(new AssertionFailedError()).anyTimes();

        control.replay();

        CustomEvent customEvent = CustomEvent.builder(myEventType, myEventSummary).build();
        Instrumentation.addEvent(customEvent);

        control.verify();
        assertTrue(Instrumentation.isDisabled());
    }

    @Test
    public void testDisabledStatusWithErrorEvent() throws Exception {
        final String myExceptionMessage = "Exception Message";

        AgentConfiguration agentConfiguration = AgentConfiguration.builder()
                .withAppKey(appKey).build();
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        Instrumentation.start(agentConfiguration, deviceInfo, null);
        Instrumentation.disable(true);
        EventBus mockEventBus = control.createMock(EventBus.class);
        Instrumentation.eventBus = mockEventBus;
        Capture<CustomEvent> capturer = new Capture<CustomEvent>();
        mockEventBus.post(capture(capturer));
        // Since the instrumentation is disabled, post should not be called
        expectLastCall().andThrow(new AssertionFailedError()).anyTimes();

        Throwable mockThrowable = control.createMock(Throwable.class);
        control.replay();

        Instrumentation.addErrorEvent(mockThrowable, Instrumentation.Severity.CRITICAL);

        control.verify();
        assertTrue(Instrumentation.isDisabled());
    }

    @Test
    public void testSeverityAlert() throws Exception {
        Instrumentation.Severity s = Instrumentation.Severity.ALERT;
        // This string is defined by the swagger spec for the RESTFul API, so should not be changed.
        assertEquals("alert", s.toString());
    }

    @Test
    public void testSeverityCritical() throws Exception {
        Instrumentation.Severity s = Instrumentation.Severity.CRITICAL;
        // This string is defined by the swagger spec for the RESTFul API, so should not be changed.
        assertEquals("critical", s.toString());
    }

    @Test
    public void testSeverityFatal() throws Exception {
        Instrumentation.Severity s = Instrumentation.Severity.FATAL;
        // This string is defined by the swagger spec for the RESTFul API, so should not be changed.
        assertEquals("fatal", s.toString());
    }
}
