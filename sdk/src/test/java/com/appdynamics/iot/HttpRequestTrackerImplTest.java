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
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static uk.org.lidalia.slf4jtest.LoggingEvent.debug;
import static uk.org.lidalia.slf4jtest.LoggingEvent.warn;

/**
 * Brought over from android/runtime/.../runtime/networkrequests in Bitbucket
 */

public class HttpRequestTrackerImplTest {

    private URL testUrl;
    private EventBus eventBus;

    @Before
    public void setUp() throws Exception {
        testUrl = new URL("http://test-url");
        eventBus = createStrictMock(EventBus.class);
    }

    @Test
    public void reportsNormalRequest() {
        Capture<NetworkRequestEvent> capturer = new Capture<NetworkRequestEvent>();
        eventBus.post(capture(capturer));
        expectLastCall();

        replay(eventBus);

        HttpRequestTracker tracker = new HttpRequestTrackerImpl(eventBus, testUrl);
        tracker.withResponseCode(200).reportDone();

        verify(eventBus);

        NetworkRequestEvent capturedEvent = capturer.getValue();
        assertNotNull(capturedEvent);
        assertEquals(testUrl.toString(), capturedEvent.getUrl());
        assertEquals(200, capturedEvent.getStatusCode());
    }

    @Test
    public void reportsErrorRequest() {

        Capture<NetworkRequestEvent> capturer = new Capture<NetworkRequestEvent>();
        eventBus.post(capture(capturer));
        expectLastCall();

        replay(eventBus);

        HttpRequestTracker tracker = new HttpRequestTrackerImpl(eventBus, testUrl);
        tracker.withError("test-error").reportDone();

        verify(eventBus);

        NetworkRequestEvent capturedEvent = capturer.getValue();
        assertEquals(testUrl.toString(), capturedEvent.getUrl());
        assertEquals("test-error", capturedEvent.getNetworkError());
    }

    @Test
    public void checksForContentLength() throws Exception {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("Content-Length", Collections.singletonList(Integer.toString(5000)));

        Capture<NetworkRequestEvent> capturer = new Capture<NetworkRequestEvent>();
        eventBus.post(capture(capturer));
        expectLastCall();

        replay(eventBus);

        HttpRequestTracker tracker = new HttpRequestTrackerImpl(eventBus, testUrl);
        tracker.withResponseCode(200)
                .withResponseHeaderFields(headers)
                .withRequestHeaderFields(headers).reportDone();

        verify(eventBus);

        NetworkRequestEvent event = capturer.getValue();
        assertEquals(new Integer(5000), event.getResponseContentLength());
        assertEquals(new Integer(5000), event.getRequestContentLength());
    }

    @Test
    public void checksForNoContentLength() throws Exception {
        Capture<NetworkRequestEvent> capturer = new Capture<NetworkRequestEvent>();
        eventBus.post(capture(capturer));
        expectLastCall();

        replay(eventBus);

        HttpRequestTracker tracker = new HttpRequestTrackerImpl(eventBus, testUrl);
        tracker.withResponseCode(200).reportDone();

        verify(eventBus);

        NetworkRequestEvent event = capturer.getValue();
        assertNull(event.getResponseContentLength());
        assertNull(event.getRequestContentLength());
    }

    @Test
    public void checksResponseIsNegOneWithInvalidInput() throws Exception {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("Content-Length", Collections.singletonList("foo"));

        Capture<NetworkRequestEvent> capturer = new Capture<NetworkRequestEvent>();
        eventBus.post(capture(capturer));
        expectLastCall();

        replay(eventBus);

        HttpRequestTracker tracker = new HttpRequestTrackerImpl(eventBus, testUrl);
        tracker.withResponseCode(200).withResponseHeaderFields(headers).reportDone();

        verify(eventBus);

        NetworkRequestEvent event = capturer.getValue();
        assertNull(event.getResponseContentLength());
    }

    @Test
    public void doesNotReportIncompleteRequest() {
        replay(eventBus);

        HttpRequestTracker tracker = new HttpRequestTrackerImpl(eventBus, testUrl);
        tracker.reportDone();

        verify(eventBus);
    }

    @Test
    public void doesNotReportRequestTwice() {
        Capture<NetworkRequestEvent> capturer = new Capture<NetworkRequestEvent>();
        eventBus.post(capture(capturer));
        expectLastCall();

        replay(eventBus);

        HttpRequestTracker tracker = new HttpRequestTrackerImpl(eventBus, testUrl);
        tracker.withResponseCode(200).reportDone();
        tracker.reportDone();

        verify(eventBus);

        NetworkRequestEvent capturedEvent = capturer.getValue();
        assertNotNull(capturedEvent);
        assertEquals(testUrl.toString(), capturedEvent.getUrl());
        assertEquals(capturedEvent.getStatusCode(), 200);
    }

    @Test
    public void testTrackerFailingDoesNotCauseCrash() {
        eventBus.post(isA(NetworkRequestEvent.class));
        expectLastCall().andThrow(new RuntimeException());

        replay(eventBus);

        HttpRequestTracker tracker = new HttpRequestTrackerImpl(eventBus, testUrl);
        tracker.withResponseCode(200).reportDone();

        verify(eventBus);
    }

    @Test
    public void testLoggerReportRequestTwice() {
        TestLoggerFactory.clear();
        TestLogger testLogger = TestLoggerFactory.getTestLogger(Instrumentation.class);
        testLogger.setEnabledLevels(Level.WARN);

        Capture<NetworkRequestEvent> capturer = new Capture<NetworkRequestEvent>();
        eventBus.post(capture(capturer));
        expectLastCall();

        replay(eventBus);

        HttpRequestTracker tracker = new HttpRequestTrackerImpl(eventBus, testUrl);
        tracker.withResponseCode(200).reportDone();
        tracker.reportDone();

        assertThat(testLogger.getLoggingEvents(), is(asList(warn("Request already reported. This will not be reported to the AppDynamics collector."),
                warn("Do not reuse instances of HttpRequestTracker."))));
    }

    @Test
    public void testLoggerReportRequestTwiceDebug() {
        TestLoggerFactory.clear();
        TestLogger testLogger = TestLoggerFactory.getTestLogger(Instrumentation.class);
        testLogger.setEnabledLevels(Level.WARN, Level.DEBUG);

        Capture<NetworkRequestEvent> capturer = new Capture<NetworkRequestEvent>();
        eventBus.post(capture(capturer));
        expectLastCall();

        replay(eventBus);

        HttpRequestTracker tracker = new HttpRequestTrackerImpl(eventBus, testUrl);
        tracker.withResponseCode(200).reportDone();
        tracker.reportDone();

        String nullErrorString = null;

        assertThat(testLogger.getLoggingEvents(), is(asList(warn("Request already reported. This will not be reported to the AppDynamics collector."),
                warn("Do not reuse instances of HttpRequestTracker."),
                debug("Request Details"),
                debug("URL: {}", testUrl),
                debug("Response Code: {}", 200),
                debug("Error Message: {}", nullErrorString),
                debug("Throwable: "))));
    }
}
