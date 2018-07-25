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

import com.appdynamics.iot.events.CustomEvent;
import com.appdynamics.iot.events.ErrorEvent;
import com.appdynamics.iot.events.Event;
import com.appdynamics.iot.events.NetworkRequestEvent;
import com.appdynamics.iot.utils.Constants;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BeaconBufferTest {
    IMocksControl control;

    @Before
    public void initializeBufferBeacon() throws Exception {
        BeaconManager.currentBeacon = null;
        BeaconManager.beaconBuffer.clear();
        control = EasyMock.createControl();
    }

    @Test
    public void testNewBeacon() throws Exception {
        DeviceInfo mockDeviceInfo = control.createMock(DeviceInfo.class);
        VersionInfo mockVersionInfo = control.createMock(VersionInfo.class);
        BeaconManager manager = new BeaconManager(mockDeviceInfo, mockVersionInfo);
        assertNotNull(BeaconManager.currentBeacon);

        CustomEvent mockCustomEvent = control.createMock(CustomEvent.class);
        expect(mockCustomEvent.getType()).andReturn(Event.Type.CUSTOM_EVENT);
        control.replay();

        manager.addEvent(mockCustomEvent);
        assertEquals(1, BeaconManager.beaconBuffer.size());
        control.verify();
    }

    @Test
    public void testAddMaxCustomEventPlusOne() throws Exception {
        DeviceInfo mockDeviceInfo = control.createMock(DeviceInfo.class);
        VersionInfo mockVersionInfo = control.createMock(VersionInfo.class);
        BeaconManager manager = new BeaconManager(mockDeviceInfo, mockVersionInfo);
        assertNotNull(BeaconManager.currentBeacon);

        // Create an array of mock events
        CustomEvent[] mockCustomEventArray = new CustomEvent[Constants.CUSTOM_EVENTS_MAX];
        for (int i = 0; i < Constants.CUSTOM_EVENTS_MAX; i++) {
            mockCustomEventArray[i] = control.createMock(CustomEvent.class);
            expect(mockCustomEventArray[i].getType()).andReturn(Event.Type.CUSTOM_EVENT).anyTimes();
        }
        // one more
        CustomEvent mockCustomEvent = control.createMock(CustomEvent.class);
        expect(mockCustomEvent.getType()).andReturn(Event.Type.CUSTOM_EVENT).anyTimes();

        control.replay();

        for (int i = 0; i < Constants.CUSTOM_EVENTS_MAX; i++) {
            manager.addEvent(mockCustomEventArray[i]);
        }
        assertEquals(1, BeaconManager.beaconBuffer.size());
        manager.addEvent(mockCustomEvent);
        assertEquals(2, BeaconManager.beaconBuffer.size());
        control.verify();
    }

    @Test
    public void testAddMaxErrorEventPlusOne() throws Exception {
        DeviceInfo mockDeviceInfo = control.createMock(DeviceInfo.class);
        VersionInfo mockVersionInfo = control.createMock(VersionInfo.class);
        BeaconManager manager = new BeaconManager(mockDeviceInfo, mockVersionInfo);

        // Create an array of mock events
        ErrorEvent[] mockErrorEventArray = new ErrorEvent[Constants.ERROR_EVENTS_MAX];
        for (int i = 0; i < Constants.ERROR_EVENTS_MAX; i++) {
            mockErrorEventArray[i] = control.createMock(ErrorEvent.class);
            expect(mockErrorEventArray[i].getType()).andReturn(Event.Type.ERROR_EVENT).anyTimes();
        }
        // one more
        ErrorEvent mockErrorEvent = control.createMock(ErrorEvent.class);
        expect(mockErrorEvent.getType()).andReturn(Event.Type.ERROR_EVENT).anyTimes();

        control.replay();

        for (int i = 0; i < Constants.ERROR_EVENTS_MAX; i++) {
            manager.addEvent(mockErrorEventArray[i]);
        }
        assertEquals(1, BeaconManager.beaconBuffer.size());
        manager.addEvent(mockErrorEvent);
        assertEquals(2, BeaconManager.beaconBuffer.size());
        control.verify();
    }

    @Test
    public void testAddMaxNetworkRequestEventPlusOne() throws Exception {
        DeviceInfo mockDeviceInfo = control.createMock(DeviceInfo.class);
        VersionInfo mockVersionInfo = control.createMock(VersionInfo.class);
        BeaconManager manager = new BeaconManager(mockDeviceInfo, mockVersionInfo);
        assertNotNull(BeaconManager.currentBeacon);

        // Create an array of mock events
        NetworkRequestEvent[] mockNetworlRequestEventArray = new NetworkRequestEvent[Constants.CUSTOM_EVENTS_MAX];

        for (int i = 0; i < Constants.CUSTOM_EVENTS_MAX; i++) {
            mockNetworlRequestEventArray[i] = control.createMock(NetworkRequestEvent.class);
            expect(mockNetworlRequestEventArray[i].getType()).andReturn(Event.Type.NETWORK_EVENT).anyTimes();
        }
        // one more
        NetworkRequestEvent networkRequestEvent = control.createMock(NetworkRequestEvent.class);
        expect(networkRequestEvent.getType()).andReturn(Event.Type.NETWORK_EVENT).anyTimes();

        control.replay();

        for (int i = 0; i < Constants.NETWORK_REQUEST_EVENTS_MAX; i++) {
            manager.addEvent(mockNetworlRequestEventArray[i]);
        }
        assertEquals(1, BeaconManager.beaconBuffer.size());

        manager.addEvent(networkRequestEvent);
        assertEquals(2, BeaconManager.beaconBuffer.size());
        control.verify();
    }

    @Test
    public void testAddMaxBeaconsPlusOne() throws Exception {
        DeviceInfo mockDeviceInfo = control.createMock(DeviceInfo.class);
        VersionInfo mockVersionInfo = control.createMock(VersionInfo.class);
        BeaconManager manager = new BeaconManager(mockDeviceInfo, mockVersionInfo);
        assertNotNull(BeaconManager.currentBeacon);

        CustomEvent[] firstMockEventArray = new CustomEvent[Constants.CUSTOM_EVENTS_MAX];

        for (int i = 0; i < Constants.CUSTOM_EVENTS_MAX; i++) {
            firstMockEventArray[i] = control.createMock(CustomEvent.class);
            expect(firstMockEventArray[i].getType()).andReturn(Event.Type.CUSTOM_EVENT).anyTimes();
        }

        CustomEvent oneMoreCustomEvent = control.createMock(CustomEvent.class);
        expect(oneMoreCustomEvent.getType()).andReturn(Event.Type.CUSTOM_EVENT).anyTimes();

        control.replay();

        for (int j = 0; j < Constants.BEACONS_IN_MEMORY_MAX; j++) {
            for (int i = 0; i < Constants.CUSTOM_EVENTS_MAX; i++) {
                manager.addEvent(firstMockEventArray[i]);
            }
            assertEquals(j + 1, BeaconManager.beaconBuffer.size());
        }
        assertEquals(Constants.BEACONS_IN_MEMORY_MAX, BeaconManager.beaconBuffer.size());
        manager.addEvent(oneMoreCustomEvent);
        assertEquals(Constants.BEACONS_IN_MEMORY_MAX, BeaconManager.beaconBuffer.size());
        control.verify();
    }

    @Test
    public void testInitializeTransport() throws Exception {

        final String collectorUrl = "http://www.test.com";
        final String appKey = "AAA-BBB-CCC";

        AgentConfiguration mockAgent = control.createMock(AgentConfiguration.class);
        CollectorChannelFactory mockCollectorChannelFactory = control.createMock(CollectorChannelFactory.class);
        expect(mockCollectorChannelFactory.getCollectorChannel()).andReturn(new DefaultCollectorChannel());
        expect(mockAgent.getCollectorUrl()).andReturn(collectorUrl);
        expect(mockAgent.getAppKey()).andReturn(appKey);
        expect(mockAgent.getCollectorChannelFactory()).andReturn(mockCollectorChannelFactory);
        control.replay();

        CollectorChannel channel = BeaconManager.initializeTransport(mockAgent);

        assertEquals("POST", channel.getRequestMethod());
        assertEquals("application/json; charset=UTF-8", channel.getRequestProperties().get("Content-Type").get(0));
        assertEquals("gzip", channel.getRequestProperties().get("Content-Encoding").get(0));
        assertEquals(collectorUrl + AgentConfiguration.COLLECTOR_URL_PREFIX_APPKEY +
                        appKey + AgentConfiguration.COLLECTOR_URL_SUFFIX_BEACONS,
                channel.getURL().toString());
        control.verify();
    }

    @Test(expected = MalformedURLException.class)
    public void testInitializeTransportMalformedUrl() throws Exception {
        final String collectorUrl = "http//:www.test.com";
        final String appKey = "AAA-BBB-CCC";

        AgentConfiguration mockAgent = control.createMock(AgentConfiguration.class);
        CollectorChannelFactory mockCollectorChannelFactory = control.createMock(CollectorChannelFactory.class);
        expect(mockCollectorChannelFactory.getCollectorChannel()).andReturn(new DefaultCollectorChannel());
        expect(mockAgent.getCollectorUrl()).andReturn(collectorUrl);
        expect(mockAgent.getAppKey()).andReturn(appKey);
        expect(mockAgent.getCollectorChannelFactory()).andReturn(mockCollectorChannelFactory);
        control.replay();

        BeaconManager.initializeTransport(mockAgent);
        control.verify();
    }
}