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

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class BeaconManagerFactoryTest {

    private IMocksControl control;

    @Before
    public void initializeFields() {
        BeaconManagerFactory.beaconManager = null;
        control = EasyMock.createControl();
    }

    @Test
    public void testCreateBeaconManager() throws Exception {
        DeviceInfo mockDeviceInfo = control.createMock(DeviceInfo.class);
        VersionInfo mockVersionInfo = control.createMock(VersionInfo.class);
        BeaconManagerFactory.createBeaconManager(mockDeviceInfo, mockVersionInfo);

        BeaconManager beaconManager = BeaconManagerFactory.getBeaconManager();
        assertNotNull(beaconManager);
    }

    @Test
    public void testGetBeaconManagerNoCreation() throws Exception {
        BeaconManager beaconManager = BeaconManagerFactory.getBeaconManager();
        assertNull(beaconManager);
    }

    @Test
    public void testGetBeaconManagerCreateOnlyOnce() throws Exception {
        DeviceInfo mockDeviceInfo = control.createMock(DeviceInfo.class);
        VersionInfo mockVersionInfo = control.createMock(VersionInfo.class);
        BeaconManager beaconManager = BeaconManagerFactory.createBeaconManager(mockDeviceInfo, mockVersionInfo);
        assertNotNull(beaconManager);

        BeaconManager beaconManager1 = BeaconManagerFactory.getBeaconManager();
        assertSame(beaconManager, beaconManager1);


    }
}