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
 * <p>The Version Information for the instrumentation runtime to use.</p>
 * <p>The version info object can have one or many the following specified.</p>
 *
 * <ul>
 * <li>Hardware Version. See {@link VersionInfo.Builder#withHardwareVersion(String)}</li>
 * <li>Software Version. See {@link VersionInfo.Builder#withSoftwareVersion(String)} </li>
 * <li>Firmware Version. See {@link VersionInfo.Builder#withFirmwareVersion(String)} </li>
 * <li>Operating System Version. See {@link VersionInfo.Builder#withOsVersion(String)} </li>
 * </ul>
 */
public class VersionInfo {

    private static final String VERSION_INFO = "versionInfo";
    private static final String VERSION_HARDWARE = "hardwareVersion";
    private static final String VERSION_FIRMWARE = "firmwareVersion";
    private static final String VERSION_SOFTWARE = "softwareVersion";
    private static final String VERSION_OS = "operatingSystemVersion";

    final String hwVersion;
    final String osVersion;
    final String fwVersion;
    final String swVersion;

    VersionInfo(String hardwareVersion,
                String operatingSystemVersioin,
                String firmwareVersion,
                String softwareVersion) {
        this.hwVersion = hardwareVersion;
        this.osVersion = operatingSystemVersioin;
        this.fwVersion = firmwareVersion;
        this.swVersion = softwareVersion;
    }

    void toJson(JsonWriter writer) throws IOException {
        writer.name(VERSION_INFO);
        {
            writer.beginObject();
            writer.name(VERSION_HARDWARE).value(this.hwVersion);
            writer.name(VERSION_FIRMWARE).value(this.fwVersion);
            writer.name(VERSION_SOFTWARE).value(this.swVersion);
            writer.name(VERSION_OS).value(this.osVersion);
            writer.endObject();
        }
    }

    /**
     * Builder class for Version Information
     *
     * @return the current Version Info builder object
     */

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String hwVersion = "";
        private String osVersion = "";
        private String fwVersion = "";
        private String swVersion = "";

        private Builder() {
        }

        /**
         * @param hwversion The hardware version of this device.
         *                  Valid Example: "Board Rev. 13A"
         *                  maxLength: 80. Truncates anything longer than that.
         * @return current Builder Object
         */

        public Builder withHardwareVersion(String hwversion) {
            this.hwVersion = StringUtils.abbreviate(hwversion, Constants.VERSION_INFO_HWVERSION_MAX);
            return this;
        }

        /**
         * @param fwversion The firmware version of this device.
         *                  Valid Example: "123.5.31"
         *                  maxLength: 80. Truncates anything longer than that.
         * @return current Builder Object
         */

        public Builder withFirmwareVersion(String fwversion) {
            this.fwVersion = StringUtils.abbreviate(fwversion, Constants.VERSION_INFO_FWVERSION_MAX);
            return this;
        }

        /**
         * @param osversion The operating system version of this device.
         *                  Valid Example: "Linux 13.4"
         *                  maxLength: 80. Truncates anything longer than that.
         * @return current Builder Object
         * @throws IllegalArgumentException if fwversion contains the '|' character
         */
        public Builder withOsVersion(String osversion) {
            this.osVersion = StringUtils.abbreviate(osversion, Constants.VERSION_INFO_OSVERSION_MAX);
            return this;
        }

        /**
         * @param swversion The software version of the embedded application being monitored.
         *                  Valid Example: "9.1.3"
         *                  maxLength: 80. Truncates anything longer than that.
         * @return current builder object
         * @throws IllegalArgumentException if swversion contains the '|' character
         */
        public Builder withSoftwareVersion(String swversion) {
            this.swVersion = StringUtils.abbreviate(swversion, Constants.VERSION_INFO_SWVERSION_MAX);
            return this;
        }

        public VersionInfo build() {
            return new VersionInfo(hwVersion,
                    osVersion,
                    fwVersion,
                    swVersion);
        }
    }
}
