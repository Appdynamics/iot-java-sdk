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
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.zip.GZIPOutputStream;

import static com.appdynamics.iot.Instrumentation.LOGGER;

class BeaconManager {
    static Beacon currentBeacon;
    static Queue<Beacon> beaconBuffer;

    static {
        EvictingQueue<Beacon> buf = EvictingQueue.create(Constants.BEACONS_IN_MEMORY_MAX);
        beaconBuffer = Queues.synchronizedQueue(buf);
    }

    BeaconManager(DeviceInfo deviceInfo, VersionInfo versionInfo) {
        currentBeacon = new Beacon(deviceInfo, versionInfo);
        beaconBuffer.add(currentBeacon);
    }

    static synchronized void addEvent(Event event) {
        addEvent(event, false);
    }

    private static synchronized void addEvent(Event event, boolean isRetry) {
        if (!currentBeacon.offer(event)) {
            if (!isRetry) {
                createNewBeacon(currentBeacon);
                addEvent(event, true);
            } else {
                LOGGER.error("Unable to add Event {}", event.toString());
            }
        }
    }

    private static void createNewBeacon(Beacon b) {
        currentBeacon = new Beacon(b.deviceInfo, b.versionInfo);
        beaconBuffer.add(currentBeacon);
    }

    static synchronized void sendAllBeacons(AgentConfiguration agent) {
        CollectorChannel channel = null;
        try {
            channel = initializeTransport(agent);
        } catch (MalformedURLException e) {
            LOGGER.error("Cannot send events. Malformed URL Exception received.", e);
            return;
        }

        GZIPOutputStream gzipOutputStream = null;
        OutputStreamWriter writer = null;
        List<Beacon> beaconsToSend = null;
        InputStream responseStream = null;

        try {
            gzipOutputStream = new GZIPOutputStream(channel.getOutputStream());
            writer = new OutputStreamWriter(gzipOutputStream);
            beaconsToSend = drainBeacons();
            createEncodedPayload(beaconsToSend, writer);
            writer.flush();
            gzipOutputStream.close();
            gzipOutputStream = null;

            //get Response Code & Response Message
            int responseCode = channel.getResponseCode();
            String responseMessage = channel.getResponseMessage();
            LOGGER.debug("Received response code: {}", responseCode);
            LOGGER.debug("Received response message: {}", responseMessage);

            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN ||
                    responseCode == HttpURLConnection.HTTP_PAYMENT_REQUIRED ||
                    responseCode == 429) { // Too many requests
                Instrumentation.disable(true);
                LOGGER.info("Collector is unable to accept beacons at this time");
                LOGGER.info("SDK is disabled. No event information is being collected or sent");
                beaconBuffer.clear();
                return;
            } else if (200 > responseCode || responseCode > 299) {
                putBeaconsBack(beaconsToSend);
            } else {
                String responseBody = "None received";
                if (200 <= responseCode && responseCode <= 299) {
                    responseStream = channel.getInputStream();
                    responseBody = readResponseBody(responseStream);
                } else {
                    responseStream = channel.getErrorStream();
                    responseBody = readResponseBody(responseStream);
                }
                LOGGER.debug("Received response body: {}", responseBody);
            }
        } catch (IOException e) {
            LOGGER.error("Encountered IOException. Aborting Sending of events.", e);
            putBeaconsBack(beaconsToSend);
        } finally {
            if (responseStream != null) {
                try {
                    responseStream.close();
                } catch (IOException e) {
                    LOGGER.error("Error closing response Stream", e);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOGGER.error("Error closing JSON writer stream", e);
                }
            }
            if (gzipOutputStream != null) {
                try {
                    gzipOutputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Error closing Gzip output stream", e);
                }
            }
        }
    }

    static CollectorChannel initializeTransport(AgentConfiguration agent) throws MalformedURLException {
        String collectorUrl = agent.getCollectorUrl();
        String appkey = agent.getAppKey();
        CollectorChannelFactory collectorChannelFactory = agent.getCollectorChannelFactory();
        CollectorChannel channel = collectorChannelFactory.getCollectorChannel();
        channel.setRequestMethod("POST");
        channel.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
        channel.addRequestProperty("Content-Encoding", "gzip");

        URL url = new URL(collectorUrl + AgentConfiguration.COLLECTOR_URL_PREFIX_APPKEY + appkey + AgentConfiguration.COLLECTOR_URL_SUFFIX_BEACONS);
        channel.setURL(url);

        return channel;
    }

    private static String readResponseBody(InputStream ins) {
        if (ins == null) {
            LOGGER.error("Input Stream is null, cannot read response body");
            return null;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(ins));
        String inputLine = null;
        StringBuffer sbuf = new StringBuffer();
        try {
            while ((inputLine = in.readLine()) != null) {
                sbuf.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            LOGGER.error("Error getting response Body", e);
        }
        sbuf.trimToSize();
        return sbuf.toString();
    }

    private static void putBeaconsBack(List<Beacon> beaconsToPutBack) {
        if (beaconsToPutBack != null && beaconsToPutBack.size() > 0) {
            Iterator<Beacon> i = beaconsToPutBack.iterator();
            while (i.hasNext()) {
                beaconBuffer.add(i.next());
            }
        }
    }

    static void createEncodedPayload(List<Beacon> beaconsToSend, Writer writer) throws IOException {
        if (beaconsToSend != null && beaconsToSend.size() > 0) {
            JsonWriter jsonWriter = new JsonWriter(writer);
            toJson(beaconsToSend, jsonWriter);
        }
    }

    static synchronized List<Beacon> drainBeacons() {
        List<Beacon> beaconsToSend = Collections.synchronizedList(new ArrayList<Beacon>());
        Iterator<Beacon> iterator = beaconBuffer.iterator();
        if (beaconBuffer.size() > 0) {
            while (iterator.hasNext()) {
                Beacon b = iterator.next();
                beaconsToSend.add(b);
                beaconBuffer.remove(b);
            }
            createNewBeacon(currentBeacon);
        }
        return beaconsToSend;
    }

    private static void toJson(List<Beacon> beacons, JsonWriter writer) throws IOException {
        writer.beginArray();
        Iterator<Beacon> iterator = beacons.iterator();
        Beacon thisBeacon;
        while (iterator.hasNext()) {
            thisBeacon = iterator.next();
            thisBeacon.toJson(writer);
        }
        writer.endArray();
    }

    static class Beacon {

        private static final String AGENT_VERSION_KEY = "agentVersion";
        public static final String AGENT_VERSION = "4.4.3.0";

        DeviceInfo deviceInfo;
        VersionInfo versionInfo;

        private ArrayBlockingQueue<CustomEvent> customEvents = new ArrayBlockingQueue<CustomEvent>(Constants.CUSTOM_EVENTS_MAX);
        private ArrayBlockingQueue<NetworkRequestEvent> networkEvents = new ArrayBlockingQueue<NetworkRequestEvent>(Constants.NETWORK_REQUEST_EVENTS_MAX);
        private ArrayBlockingQueue<ErrorEvent> errorEvents = new ArrayBlockingQueue<ErrorEvent>(Constants.ERROR_EVENTS_MAX);

        public Beacon(DeviceInfo deviceInfo, VersionInfo versionInfo) {
            this.deviceInfo = deviceInfo;
            this.versionInfo = versionInfo;
        }

        public boolean offer(Event e) {
            Event.Type type = e.getType();
            if (type == Event.Type.CUSTOM_EVENT) {
                return customEvents.offer((CustomEvent) e);
            } else if (type == Event.Type.NETWORK_EVENT) {
                return networkEvents.offer((NetworkRequestEvent) e);
            } else if (type == Event.Type.ERROR_EVENT) {
                return errorEvents.offer((ErrorEvent) e);
            }
            return false;
        }

        void toJson(JsonWriter writer) throws IOException, IllegalStateException {
            writer.beginObject();
            deviceInfo.toJson(writer);
            versionInfo.toJson(writer);
            writer.name(AGENT_VERSION_KEY).value(AGENT_VERSION);
            //customEvents
            if (customEvents.size() > 0) {
                writer.name(CustomEvent.CUSTOM_EVENT_LABEL);
                writer.beginArray();
                for (CustomEvent thisEvent : customEvents) {
                    thisEvent.toJson(writer);
                }
                writer.endArray();
            }

            //NetworkRequestEvents
            if (networkEvents.size() > 0) {
                writer.name(NetworkRequestEvent.NETWORK_EVENT_TYPE_LABEL);
                writer.beginArray();
                for (NetworkRequestEvent thisEvent : networkEvents) {
                    thisEvent.toJson(writer);
                }
                writer.endArray();
            }

            //ErrorEvents
            if (errorEvents.size() > 0) {
                writer.name(ErrorEvent.ERROR_EVENT_LABEL);
                writer.beginArray();
                for (ErrorEvent thisEvent : errorEvents) {
                    thisEvent.toJson(writer);
                }
                writer.endArray();
            }
            writer.endObject();
        }
    }
}
