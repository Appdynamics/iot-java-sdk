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

import com.appdynamics.iot.BeaconManager.Beacon;
import com.appdynamics.iot.events.CustomEvent;
import com.appdynamics.iot.events.ErrorEvent;
import com.appdynamics.iot.events.Event;
import com.appdynamics.iot.events.NetworkRequestEvent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BeaconManagerTest {

    private static final String DEVICE_TYPE = "TEST_A";
    private static final String CUSTOM_EVENT_SUMMARY = "This is a custom test event";
    private static final String CUSTOM_EVENT_TYPE = "Custom Test Event";
    private static final String DEVICE_NAME = "Test_Device";
    private static final String HW_VERSION = "1.0.0";
    private static final String FW_VERSION = "2.0.0";
    private static final String OS_VERSION = "3.0.0";
    private static final String SW_VERSION = "4.0.0";
    private static final String URL = "https://example.com";
    private static final String ERROR_EVENT = "Test error event";
    private static final String ERROR_MESSAGE = "Test error message";

    @Test
    public void testCreateEncodedPayload() throws IOException {
        List<Beacon> beacons = createBeacons();
        StringWriter stringWriter = new StringWriter();
        BeaconManager.createEncodedPayload(beacons, stringWriter);
        String beaconJson = stringWriter.toString();
        System.out.println(beaconJson);

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(beaconJson).getAsJsonArray().get(0);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonElement agentVersion = jsonObject.get("agentVersion");
        Assert.assertEquals(Beacon.AGENT_VERSION, agentVersion.getAsString());

        JsonObject deviceInfo = jsonObject.get("deviceInfo").getAsJsonObject();
        Assert.assertEquals(DEVICE_TYPE, deviceInfo.get("deviceType").getAsString());
        Assert.assertEquals(DEVICE_NAME, deviceInfo.get("deviceName").getAsString());

        JsonObject versionInfo = jsonObject.get("versionInfo").getAsJsonObject();
        Assert.assertEquals(HW_VERSION, versionInfo.get("hardwareVersion").getAsString());
        Assert.assertEquals(FW_VERSION, versionInfo.get("firmwareVersion").getAsString());
        Assert.assertEquals(SW_VERSION, versionInfo.get("softwareVersion").getAsString());
        Assert.assertEquals(OS_VERSION, versionInfo.get("operatingSystemVersion").getAsString());

        JsonObject customEvent = jsonObject.get("customEvents").getAsJsonArray().get(0).getAsJsonObject();
        Assert.assertEquals(CUSTOM_EVENT_TYPE, customEvent.get("eventType").getAsString());
        Assert.assertEquals(CUSTOM_EVENT_SUMMARY, customEvent.get("eventSummary").getAsString());

        JsonObject requestEvents = jsonObject.get("networkRequestEvents").getAsJsonArray().get(0).getAsJsonObject();
        Assert.assertEquals(URL, requestEvents.get("url").getAsString());


    }

    private List<Beacon> createBeacons() throws MalformedURLException {
        Beacon beacon = createBeacon();
        List<Beacon> beacons = new ArrayList<>();
        beacons.add(beacon);
        return beacons;
    }

    private Beacon createBeacon() throws MalformedURLException {
        DeviceInfo deviceInfo = DeviceInfo.builder(DEVICE_TYPE, UUID.randomUUID().toString())
                .withDeviceName(DEVICE_NAME)
                .build();
        VersionInfo versionInfo = VersionInfo.builder()
                .withHardwareVersion(HW_VERSION)
                .withFirmwareVersion(FW_VERSION)
                .withOsVersion(OS_VERSION)
                .withSoftwareVersion(SW_VERSION)
                .build();
        Beacon beacon = new Beacon(deviceInfo, versionInfo);
        beacon.offer(createCustomEvent());
        beacon.offer(createNetworkEvent());
        beacon.offer(createErrorEvent());
        return beacon;
    }

    private Event createCustomEvent() {
        return CustomEvent.builder(CUSTOM_EVENT_TYPE, CUSTOM_EVENT_SUMMARY)
                .withDuration(1000)
                .build();
    }

    private Event createNetworkEvent() throws MalformedURLException {
        return NetworkRequestEvent.builder(new URL(URL))
                .withDuration(1000)
                .build();
    }

    private Event createErrorEvent() {
        return ErrorEvent.builder(ERROR_EVENT)
                .withMessage(ERROR_MESSAGE)
                .build();
    }
}