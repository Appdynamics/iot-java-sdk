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

package com.appdynamics.iotapps;

import com.appdynamics.iot.events.ErrorEvent;

import java.util.ArrayList;

public class ErrorEventCreator {

    public static ErrorEvent create() {
        ErrorEvent.Builder builder = ErrorEvent.builder("Error Test1");
        builder.withMessage("Error Test Message 1")
                .withDuration(5000L)
                .addBooleanProperty("BooleanKey", true)
                .addDoubleProperty("DoubleKey", 50.0D)
                .addLongProperty("LongKey", 999L)
                .addStringProperty("StringKey", "StringValue");
        try {
            float f = (5 / 0);
        } catch (Throwable t) {
            if (t.getStackTrace().length > 5) {
                StackTraceElement[] ste = new StackTraceElement[5];
                ste[0] = t.getStackTrace()[0];
                ste[1] = t.getStackTrace()[1];
                ste[2] = t.getStackTrace()[2];
                ste[3] = t.getStackTrace()[3];
                ste[4] = t.getStackTrace()[4];
                ErrorEvent.StackTrace s = new ErrorEvent.StackTrace("Thread-1", ste);
                ArrayList<ErrorEvent.StackTrace> st = new ArrayList<ErrorEvent.StackTrace>();
                st.add(s);
                s = new ErrorEvent.StackTrace("Thread-2", ste);
                st.add(s);
                builder.withStackTraces(st);
            }
        }
        return builder.build();
    }
}
