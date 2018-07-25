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
import com.appdynamics.iot.utils.Utils;
import com.google.gson.stream.JsonWriter;


import java.io.IOException;
import java.util.*;

import static com.appdynamics.iot.Instrumentation.Severity;

public class ErrorEvent extends Event {

    public static final String ERROR_EVENT_LABEL = "errorEvents";
    private static final String ERROR_NAME = "name";
    private static final String ERROR_MESSAGE = "message";
    private static final String STACK_TRACES = "stackTraces";
    private static final String ERROR_STACK_TRACE_INDEX = "errorStackTraceIndex";
    private static final String SEVERITY = "severity";

    private static final String SYMBOL_NAME = "symbolName"; //Class name and method name combo
    private static final String PACKAGE_NAME = "packageName";
    private static final String FILE_PATH = "filePath";
    private static final String LINE_NUMBER = "lineNumber";

    private final String errorName;
    private final String errorMessage;
    private final ArrayList<StackTrace> stackTraces;
    private final int errorStackTraceIndex;
    private Severity severity = Severity.CRITICAL;

    public ErrorEvent(String name,
                      String message,
                      ArrayList<StackTrace> stackTraces,
                      int errorStackTraceIndex,
                      Severity severity,
                      long timestamp,
                      long duration,
                      Map<String, String> stringProperties,
                      Map<String, Long> longProperties,
                      Map<String, Double> doubleProperties,
                      Map<String, Boolean> booleanProperties,
                      Map<String, Date> dateProperties) {
        super(Type.ERROR_EVENT,
                timestamp,
                duration,
                stringProperties,
                longProperties,
                doubleProperties,
                booleanProperties,
                dateProperties);
        this.errorName = StringUtils.abbreviate(name, Constants.ERROR_EVENT_NAME_MAX_WIDTH);
        this.errorMessage = StringUtils.abbreviate(message, Constants.ERROR_EVENT_MESSAGE_MAX_WIDTH);
        this.stackTraces = Utils.truncateArrayList(stackTraces, Constants.ERROR_EVENT_STACK_TRACE_ELEMENTS_MAX);
        if (stackTraces != null &&
                errorStackTraceIndex > 0 &&
                errorStackTraceIndex < stackTraces.size()) {
            this.errorStackTraceIndex = errorStackTraceIndex;
        } else {
            this.errorStackTraceIndex = 0;
        }
        if (severity != null) {
            this.severity = severity;
        } else {
            this.severity = Severity.CRITICAL;
        }
    }

    @Override
    public void eventSpecificFields(JsonWriter writer) throws IOException {
        writer.name(ERROR_NAME).value(this.errorName);
        writer.name(ERROR_MESSAGE).value(this.errorMessage);
        if (this.stackTraces != null && this.stackTraces.size() > 0) {
            writeStackTraces(writer, this.stackTraces);
        }
        if (errorStackTraceIndex >= 0) {
            writer.name(ERROR_STACK_TRACE_INDEX).value(this.errorStackTraceIndex);
        }
        writer.name(SEVERITY).value(this.severity.toString());
    }

    /**
     * @return error Name associated with this event
     */
    public String getErrorName() {
        return this.errorName;
    }

    /**
     * @return error message associated with this event
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * @return stackTraces associated with this event
     */
    public ArrayList<StackTrace> getStackTraces() {
        return this.stackTraces;
    }

    /**
     * @return The index into the stackTracesElements array that caused the error.
     */
    public int getErrorStackTraceIndex() {
        return this.errorStackTraceIndex;
    }

    /**
     * @return Level of the Error @see {@link Severity}
     */
    public Severity getSeverity() {
        return this.severity;
    }

    private void writeStackTraces(JsonWriter writer, ArrayList<StackTrace> traces) throws IOException {
        writer.name(STACK_TRACES);
        writer.beginArray();
        for (StackTrace t : traces) {
            writer.beginObject();
            if (t.thread != null) {
                writer.name(StackTrace.THREAD).value(t.thread);
            }
            writer.name(StackTrace.RUNTIME).value(t.runtime);
            if (t.stackTraceElements != null && t.stackTraceElements.size() > 0) {
                writer.name(StackTrace.STACK_FRAMES);
                writer.beginArray();
                StackTraceElement e;
                for (int i = 0; i < t.stackTraceElements.size() && i < Constants.ERROR_EVENT_STACK_FRAMES_MAX; i++) {
                    e = t.stackTraceElements.get(i);
                    writeStackTraceElement(writer, e);
                }
                writer.endArray();
            }
            writer.endObject();
        }
        writer.endArray();
    }

    private void writeStackTraceElement(JsonWriter writer, StackTraceElement e) throws IOException {
        writer.beginObject();
        writer.name(SYMBOL_NAME).value(StringUtils.abbreviate(e.getClass().getSimpleName() + "." + e.getMethodName(), Constants.ERROR_EVENT_STACK_FRAME_SYMBOL_NAME_MAX));
        Package p = e.getClass().getPackage();
        if (p != null) {
            writer.name(PACKAGE_NAME).value(StringUtils.abbreviate(p.toString(), Constants.ERROR_EVENT_STACK_FRAME_PACKAGE_NAME_MAX));
        }
        if (e.getFileName() != null) {
            writer.name(FILE_PATH).value(StringUtils.abbreviate(e.getFileName(), Constants.ERROR_EVENT_STACK_FRAME_FILE_PATH_MAX));
        }
        if (e.getLineNumber() > 0) {
            writer.name(LINE_NUMBER).value(e.getLineNumber());
        }
        writer.endObject();
    }

    @Override
    public Type getType() {
        return Type.ERROR_EVENT;
    }

    @Override
    public String toString() {
        return "Error Event";
    }


    public static Builder builder(String name) {
        return new Builder(name);
    }

    public String getMessage() {
        return this.errorMessage;
    }

    public static final class Builder extends BaseBuilder<ErrorEvent.Builder> {
        private final String name;
        private String errorMessage;
        private ArrayList<StackTrace> traces = new ArrayList<StackTrace>();
        private int errorStackTraceIndex;
        private Severity severity;

        private Builder(String name) {
            this.thisObj = this;
            this.timestamp = System.currentTimeMillis();
            this.name = name;
        }

        /**
         * @param message Error Message.
         * @return current Builder Object
         */
        public Builder withMessage(String message) {
            if (message != null) {
                this.errorMessage = message;
            }
            return this;
        }

        /**
         * Add an Array of Stack Trace Elements.
         *
         * @param stackTraces Maximum number supported is {@value Constants#ERROR_EVENT_STACK_TRACE_ELEMENTS_MAX}. To remove the list, pass in null
         * @return current Builder Object
         */
        public Builder withStackTraces(ArrayList<StackTrace> stackTraces) {
            this.traces = stackTraces;
            return this;
        }


        /**
         * The index into the stackTracesElements array that caused the error.
         * This is not required, but if there is more than one stackTrace, then
         * it is highly recommended.
         *
         * @param errorStackTraceIndex Default is 0.
         * @return current Builder Object
         */
        public Builder withErrorStackTraceIndex(int errorStackTraceIndex) {
            this.errorStackTraceIndex = errorStackTraceIndex;
            return this;
        }

        /**
         * @param level Level of the Error
         *              <ol>
         *              <li>{@link Severity#ALERT} An error that didn't cause any issues</li>
         *              <li>{@link Severity#CRITICAL} An error that caused issues</li>
         *              <li>{@link Severity#FATAL} An error that killed the app</li>
         *              </ol>
         * @return current Builder Object
         */
        public Builder withSeverity(Severity level) {
            this.severity = level;
            return this;
        }

        public ErrorEvent build() {
            return new ErrorEvent(name,
                    errorMessage,
                    traces,
                    errorStackTraceIndex,
                    severity,
                    timestamp,
                    duration,
                    Collections.unmodifiableMap(new HashMap<String, String>(stringProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Long>(longProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Double>(doubleProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Boolean>(booleanProperties)),
                    Collections.unmodifiableMap(new HashMap<String, Date>(dateProperties)));
        }
    }

    public static class StackTrace {
        static final String THREAD = "thread";
        static final String RUNTIME = "runtime";
        static final String STACK_FRAMES = "stackFrames";

        final String thread;
        final String runtime = "java";
        final ArrayList<StackTraceElement> stackTraceElements;

        public StackTrace(StackTraceElement[] stackTraceElements) {
            this(null, stackTraceElements);
        }

        public StackTrace(String thread, StackTraceElement[] stackTraceElements) {
            this.thread = StringUtils.abbreviate(thread, Constants.ERROR_EVENT_STACK_TRACE_ELEMENTS_MAX);
            if (stackTraceElements != null && stackTraceElements.length > 0) {
                this.stackTraceElements = new ArrayList<StackTraceElement>(Arrays.asList(stackTraceElements));
            } else {
                this.stackTraceElements = null;
            }
        }
    }
}
