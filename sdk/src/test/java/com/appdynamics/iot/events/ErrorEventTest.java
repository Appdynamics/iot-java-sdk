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
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.ArrayList;

import static com.appdynamics.iot.Instrumentation.Severity;
import static com.appdynamics.iot.events.ErrorEvent.StackTrace;
import static org.junit.Assert.*;

public class ErrorEventTest {

    private static final String ERROR_NAME = "Test error event";

    @Test
    public void testDefaultErrorEvent() {
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME).build();
        assertEquals(ERROR_NAME, e.getErrorName());
    }

    @Test
    public void testDefaultErrorEventNull() {
        ErrorEvent e = ErrorEvent.builder(null).build();
        assertNull(e.getErrorName());
    }

    @Test
    public void testDefaultErrorEventLong() {
        final String longErrorName = StringUtilsTest.repeat('c', Constants.ERROR_EVENT_NAME_MAX_WIDTH + 1);
        ErrorEvent e = ErrorEvent.builder(longErrorName).build();
        assertEquals(Constants.ERROR_EVENT_NAME_MAX_WIDTH, e.getErrorName().length());
    }

    @Test
    public void testWithMessage() throws Exception {
        final String message = "This is an error message";
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME)
                .withMessage(message).build();
        assertEquals(message, e.getErrorMessage());
    }

    @Test
    public void testWithMessageNull() throws Exception {
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME)
                .withMessage(null).build();
        assertEquals(null, e.getErrorMessage());
    }

    @Test
    public void testWithMessageLong() throws Exception {
        final String longMessage = StringUtilsTest.repeat('n', Constants.ERROR_EVENT_MESSAGE_MAX_WIDTH + 1);
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME)
                .withMessage(longMessage).build();
        assertEquals(Constants.ERROR_EVENT_MESSAGE_MAX_WIDTH, e.getErrorMessage().length());
    }

    @Test
    public void testWithStackTraces() throws Exception {
        ArrayList<StackTrace> s = new ArrayList<StackTrace>();
        for (int i = 0; i < Constants.ERROR_EVENT_STACK_FRAMES_MAX; i++) {
            s.add(EasyMock.createNiceMock(ErrorEvent.StackTrace.class));
        }
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME)
                .withStackTraces(s).build();
        assertEquals(Constants.ERROR_EVENT_STACK_FRAMES_MAX, e.getStackTraces().size());
    }

    @Test
    public void testWithStackTracesInvalidArrayLength() throws Exception {
        ArrayList<StackTrace> s = new ArrayList<StackTrace>();
        for (int i = 0; i < Constants.ERROR_EVENT_STACK_FRAMES_MAX + 1; i++) {
            s.add(EasyMock.createNiceMock(ErrorEvent.StackTrace.class));
        }
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME)
                .withStackTraces(s).build();
        assertEquals(Constants.ERROR_EVENT_STACK_FRAMES_MAX, e.getStackTraces().size());
    }

    @Test
    public void testWithErrorStackTraceIndexDefault() throws Exception {
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME).build();
        assertEquals(0, e.getErrorStackTraceIndex());
    }

    @Test
    public void testWithErrorStackTraceIndex() throws Exception {
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME)
                .withErrorStackTraceIndex(3).build();
        assertEquals(0, e.getErrorStackTraceIndex());
    }

    @Test
    public void testWithErrorStackTraceIndexValid() throws Exception {
        final int STACK_TRACE_ARRAY_LEN = 5;
        ArrayList<StackTrace> s = new ArrayList<StackTrace>();
        for (int i = 0; i < STACK_TRACE_ARRAY_LEN; i++) {
            s.add(EasyMock.createNiceMock(ErrorEvent.StackTrace.class));
        }
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME)
                .withStackTraces(s).withErrorStackTraceIndex(STACK_TRACE_ARRAY_LEN - 2).build();
        assertEquals(STACK_TRACE_ARRAY_LEN - 2, e.getErrorStackTraceIndex());
    }

    @Test
    public void testWithErrorStackTraceIndexInvalid() throws Exception {
        final int STACK_TRACE_ARRAY_LEN = 5;
        ArrayList<StackTrace> s = new ArrayList<StackTrace>();
        for (int i = 0; i < STACK_TRACE_ARRAY_LEN; i++) {
            s.add(EasyMock.createNiceMock(ErrorEvent.StackTrace.class));
        }
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME)
                .withStackTraces(s).withErrorStackTraceIndex(STACK_TRACE_ARRAY_LEN + 1).build();
        assertEquals(0, e.getErrorStackTraceIndex());
    }

    @Test
    public void testWithSeverityDefault() throws Exception {
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME).build();
        assertEquals(Severity.CRITICAL, e.getSeverity());
    }

    @Test
    public void testWithSeverity() throws Exception {
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME)
                .withSeverity(Severity.FATAL).build();
        assertEquals(Severity.FATAL, e.getSeverity());
    }

    @Test
    public void testToString() throws Exception {
        ErrorEvent event = ErrorEvent.builder(ERROR_NAME).build();
        assertEquals("Error Event", event.toString());
    }

    @Test
    public void testGetType() throws Exception {
        ErrorEvent e = ErrorEvent.builder(ERROR_NAME).build();
        assertEquals(Event.Type.ERROR_EVENT, e.getType());
    }

    @Test
    public void testStackTraceDefault() throws Exception {
        StackTrace s = EasyMock.createNiceMock(StackTrace.class);
        assertEquals("java", s.runtime);
        assertNull(s.stackTraceElements);
        assertNull(s.thread);
    }

    @Test
    public void testStackTrace() throws Exception {
        Throwable mockedThrowable = EasyMock.createNiceMock(Throwable.class);
        StackTraceElement[] elements = mockedThrowable.getStackTrace();
        StackTrace stackTrace = new StackTrace(elements);
        assertEquals("java", stackTrace.runtime);
        assertSame(elements, stackTrace.stackTraceElements);
        assertNull(stackTrace.thread);
    }

    @Test
    public void testStackTraceWithThread() throws Exception {
        final String THREAD_NAME = "Thread - 1";
        Throwable mockedThrowable = EasyMock.createNiceMock(Throwable.class);
        Thread thisThread = new Thread(THREAD_NAME);
        StackTraceElement[] elements = mockedThrowable.getStackTrace();
        StackTrace s = new StackTrace(thisThread.getName(), elements);
        assertEquals("java", s.runtime);
        assertSame(elements, s.stackTraceElements);
        assertNotNull(THREAD_NAME, s.thread);
    }
}