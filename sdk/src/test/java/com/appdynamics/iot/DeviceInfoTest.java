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

public class DeviceInfoTest {
    private static final String DEVICE_TYPE = "CAR_MODEL_A";
    private static final String DEVICE_ID = "1FMHK7F8XCGA67893";

    @Test
    public void testDefaultContext() throws Exception {
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID).build();
        assertEquals(DEVICE_TYPE, deviceInfo.deviceType);
        assertEquals(DEVICE_ID, deviceInfo.deviceId);
    }

    @Test
    public void testDefaultContextNull() throws Exception {
        DeviceInfo deviceInfo = DeviceInfo.builder(null, null).build();
        assertNull(deviceInfo.deviceType);
        assertNull(deviceInfo.deviceId);
    }

    @Test
    public void testDefaultContextWithPipe() throws Exception {
        final String pipedDeviceType = "Device| Type";
        final String pipedDeviceTypeNoPipe = "Device Type";
        final String pipedDeviceId = "Device| Id";
        DeviceInfo deviceInfo = DeviceInfo.builder(pipedDeviceType, pipedDeviceId).build();
        assertEquals(deviceInfo.deviceType, pipedDeviceTypeNoPipe);
        assertEquals(deviceInfo.deviceId, pipedDeviceId);
    }

    @Test
    public void testDefaultContextInvalidInput() throws Exception {
        final String longDeviceType = StringUtilsTest.repeat('w', Constants.DEVICE_INFO_DEVICETYPE_MAX + 1);
        final String longDeviceId = StringUtilsTest.repeat('x', Constants.DEVICE_INFO_DEVICEID_MAX + 1);
        DeviceInfo deviceInfo = DeviceInfo.builder(longDeviceType, longDeviceId).build();
        assertEquals(Constants.DEVICE_INFO_DEVICETYPE_MAX, deviceInfo.deviceType.length());
        assertEquals(Constants.DEVICE_INFO_DEVICEID_MAX, deviceInfo.deviceId.length());
    }

    @Test
    public void testWithDeviceName() throws Exception {
        final String deviceName = "Peter_s_car";
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID)
                .withDeviceName(deviceName).build();
        assertEquals(DEVICE_TYPE, deviceInfo.deviceType);
        assertEquals(DEVICE_ID, deviceInfo.deviceId);
        assertEquals(deviceName, deviceInfo.deviceName);
    }

    @Test
    public void testWithDeviceNameNull() throws Exception {
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID)
                .withDeviceName(null).build();
        assertEquals(DEVICE_TYPE, deviceInfo.deviceType);
        assertEquals(DEVICE_ID, deviceInfo.deviceId);
        assertNull(deviceInfo.deviceName);
    }

    @Test
    public void testWithDeviceNameLong() throws Exception {
        final String longDeviceName = StringUtilsTest.repeat('y', Constants.DEVICE_INFO_DEVICENAME_MAX + 1);
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID)
                .withDeviceName(longDeviceName).build();
        assertEquals(Constants.DEVICE_INFO_DEVICENAME_MAX, deviceInfo.deviceName.length());
    }


    @Test
    public void testBuild() throws Exception {
        final String deviceName = "Peter_s_car";

        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, DEVICE_ID)
                .withDeviceName(deviceName)
                .build();
        assertEquals(DEVICE_TYPE, deviceInfo.deviceType);
        assertEquals(DEVICE_ID, deviceInfo.deviceId);
        assertEquals(deviceName, deviceInfo.deviceName);
    }
}
