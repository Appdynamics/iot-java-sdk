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
import com.appdynamics.iot.utils.StringUtilsTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VersionInfoTest {

    @Test
    public void testWithHardwareVersion() throws Exception {
        final String hardwareVersion = "1.5.89";
        VersionInfo versionInfo = VersionInfo.builder()
                .withHardwareVersion(hardwareVersion).build();
        assertEquals(hardwareVersion, versionInfo.hwVersion);
    }

    @Test
    public void testWithHardwareVersionNull() throws Exception {
        VersionInfo versionInfo = VersionInfo.builder()
                .withHardwareVersion(null).build();
        assertNull(versionInfo.hwVersion);
    }

    @Test
    public void testWithHardwareVersionLong() throws Exception {
        final String longHardwareVersion = StringUtilsTest.repeat('b', Constants.VERSION_INFO_HWVERSION_MAX + 1);
        VersionInfo versionInfo = VersionInfo.builder()
                .withHardwareVersion(longHardwareVersion).build();
        assertEquals(Constants.VERSION_INFO_HWVERSION_MAX, versionInfo.hwVersion.length());
    }

    @Test
    public void testWithFirmwareVersion() throws Exception {
        final String firmwareVersion = "22.34.50";
        VersionInfo versionInfo = VersionInfo.builder()
                .withFirmwareVersion(firmwareVersion).build();
        assertEquals(firmwareVersion, versionInfo.fwVersion);
    }

    @Test
    public void testWithFirmwareVersionNull() throws Exception {
        VersionInfo versionInfo = VersionInfo.builder()
                .withFirmwareVersion(null).build();
        assertNull(versionInfo.fwVersion);
    }

    @Test
    public void testWithFirmwareVersionLong() throws Exception {
        final String longFirmwareVersion = StringUtilsTest.repeat('a', Constants.VERSION_INFO_FWVERSION_MAX + 1);
        VersionInfo versionInfo = VersionInfo.builder()
                .withFirmwareVersion(longFirmwareVersion).build();
        assertEquals(Constants.VERSION_INFO_FWVERSION_MAX, versionInfo.fwVersion.length());
    }

    @Test
    public void testWithOsVersion() throws Exception {
        final String osVersion = "UbuntuCore";
        VersionInfo versionInfo = VersionInfo.builder()
                .withOsVersion(osVersion).build();
        assertEquals(osVersion, versionInfo.osVersion);
    }

    @Test
    public void testWithOsVersionNull() throws Exception {
        VersionInfo versionInfo = VersionInfo.builder()
                .withOsVersion(null).build();
        assertNull(versionInfo.osVersion);
    }

    @Test
    public void testWithOsVersionLong() throws Exception {
        final String longOsVersion = StringUtilsTest.repeat('r', Constants.VERSION_INFO_OSVERSION_MAX + 1);
        VersionInfo versionInfo = VersionInfo.builder()
                .withOsVersion(longOsVersion).build();
        assertEquals(Constants.VERSION_INFO_OSVERSION_MAX, versionInfo.osVersion.length());
    }

    @Test
    public void testWithSoftwareVersion() throws Exception {
        final String swVersion = "2.3";
        VersionInfo versionInfo = VersionInfo.builder()
                .withSoftwareVersion(swVersion).build();
        assertEquals(swVersion, versionInfo.swVersion);
    }

    @Test
    public void testWithSoftwareVersionNull() throws Exception {
        VersionInfo versionInfo = VersionInfo.builder()
                .withSoftwareVersion(null).build();
        assertNull(versionInfo.swVersion);
    }

    @Test
    public void testWithSoftwareVersionLong() throws Exception {
        final String longSwVersion = StringUtilsTest.repeat('m', Constants.VERSION_INFO_SWVERSION_MAX + 1);
        VersionInfo versionInfo = VersionInfo.builder()
                .withSoftwareVersion(longSwVersion).build();
        assertEquals(Constants.VERSION_INFO_SWVERSION_MAX, versionInfo.swVersion.length());
    }

    @Test
    public void testBuild() throws Exception {
        final String swVersion = "2.3";
        final String hardwareVersion = "1.5.89";
        final String firmwareVersion = "22.34.50";
        final String osVersion = "Ubuntu Core";

        VersionInfo versionInfo = VersionInfo.builder()
                .withSoftwareVersion(swVersion)
                .withHardwareVersion(hardwareVersion)
                .withFirmwareVersion(firmwareVersion)
                .withOsVersion(osVersion)
                .build();

        assertEquals(swVersion, versionInfo.swVersion);
        assertEquals(hardwareVersion, versionInfo.hwVersion);
        assertEquals(firmwareVersion, versionInfo.fwVersion);
        assertEquals(osVersion, versionInfo.osVersion);
    }
}
