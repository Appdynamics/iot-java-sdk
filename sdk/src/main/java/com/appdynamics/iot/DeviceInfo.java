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

import com.appdynamics.iot.utils.Constants;
import com.appdynamics.iot.utils.StringUtils;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * <p>The Device Information for the instrumentation runtime to use.</p>
 * <p>The device info object must have the following specified.</p>
 *
 * <ul>
 * <li>A Device Type. This is the name that gets displayed on the Connected Devices UI landing page.
 * Valid examples include "SUV Model Y", "Thermostat", "Camera".</li>
 * <li>A Device UUID. A unique identifier for the device where the application is running.
 * {@link #builder(String, String)}</li>
 * </ul>
 */
public class DeviceInfo {
    private static final String DEVICE_INFO = "deviceInfo";
    private static final String DEVICE_NAME = "deviceName";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String DEVICE_ID = "deviceId";

    final String deviceName;
    final String deviceType;
    final String deviceId;

    DeviceInfo(String deviceName,
               String deviceType,
               String deviceId) {
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    void toJson(JsonWriter writer) throws IOException {
        writer.name(DEVICE_INFO);
        {
            writer.beginObject();
            writer.name(DEVICE_NAME).value(this.deviceName);
            writer.name(DEVICE_TYPE).value(this.deviceType);
            writer.name(DEVICE_ID).value(this.deviceId);
            writer.endObject();
        }
    }

    public static Builder builder(String deviceType, String deviceid) {
        return new Builder(deviceType, deviceid);
    }

    public static final class Builder {
        private String deviceName = "";
        private String deviceType;
        private String deviceId;

        /**
         * @param devicetype Type of the device.
         *                   Cannot contain the pipe character '|'.
         *                   Valid Examples: "Thermostat", "Wind Turbine", "Camera"
         *                   maxLength: 24
         * @param deviceid   deviceId: Unique ID for this device
         *                   Valid Examples: "1877837d-66a9-4bfb-b6e8-6dd4ac62ea38", "CA 5UMH719"
         *                   maxLength: 128
         */
        private Builder(String devicetype, String deviceid) {
            devicetype = StringUtils.remove(devicetype, Constants.PIPE_CHARACTER);
            this.deviceType = StringUtils.abbreviate(devicetype, Constants.DEVICE_INFO_DEVICETYPE_MAX);
            this.deviceId = StringUtils.abbreviate(deviceid, Constants.DEVICE_INFO_DEVICEID_MAX);
        }


        /**
         * @param devicename Human readable name of the device. This is not required and does not need to be unique.
         *                   Valid Examples: "Thermostat in room 1302", "Vehicle 1532312"
         *                   maxLength: 80. Truncates anything longer than that.
         * @return current Builder Object
         */
        public Builder withDeviceName(String devicename) {
            this.deviceName = StringUtils.abbreviate(devicename, Constants.DEVICE_INFO_DEVICENAME_MAX);
            return this;
        }

        public DeviceInfo build() {
            return new DeviceInfo(deviceName,
                    deviceType,
                    deviceId);
        }
    }
}
