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

package com.appdynamics.iot.utils;

public class Constants {

    public static final int CONFIG_MAX_EVENTS = 200;

    public static final int EVENT_PROPERTIES_KEY_MAX = 24;
    public static final int EVENT_STRING_PROPERTIES_VALUE_MAX = 128;

    public static final int CUSTOM_EVENT_TYPE_MAX = 24;
    public static final int CUSTOM_EVENT_SUMMARY_MAX = 64;

    public static final int NETWORK_REQUEST_EVENT_URL_MAX = 2048;
    public static final int NETWORK_REQUEST_EVENT_ERROR_MAX = 1024;

    public static final int ERROR_EVENT_STACK_TRACE_ELEMENTS_MAX = 4; // Swagger spec is 32, but we limit it to 4
    public static final int ERROR_EVENT_NAME_MAX_WIDTH = 64;
    public static final int ERROR_EVENT_MESSAGE_MAX_WIDTH = 2048;

    //Swagger spec is 256, but we have a smaller limit
    //so that the device doesn't go out of memory;
    public static final int ERROR_EVENT_STACK_FRAMES_MAX = 4;
    public static final int ERROR_EVENT_STACK_FRAME_SYMBOL_NAME_MAX = 128;
    public static final int ERROR_EVENT_STACK_FRAME_PACKAGE_NAME_MAX = 256;
    public static final int ERROR_EVENT_STACK_FRAME_FILE_PATH_MAX = 4096;

    public static final int CUSTOM_EVENTS_MAX = 200;
    public static final int NETWORK_REQUEST_EVENTS_MAX = CUSTOM_EVENTS_MAX;
    public static final int ERROR_EVENTS_MAX = CUSTOM_EVENTS_MAX;

    public static final int VERSION_INFO_HWVERSION_MAX = 80;
    public static final int VERSION_INFO_FWVERSION_MAX = 80;
    public static final int VERSION_INFO_SWVERSION_MAX = 80;
    public static final int VERSION_INFO_OSVERSION_MAX = 80;

    public static final int DEVICE_INFO_DEVICENAME_MAX = 80;
    public static final int DEVICE_INFO_DEVICETYPE_MAX = 24;
    public static final int DEVICE_INFO_DEVICEID_MAX = 128;

    public static final char PIPE_CHARACTER = '|';
    public static final int BEACONS_IN_MEMORY_MAX = 10;
}
