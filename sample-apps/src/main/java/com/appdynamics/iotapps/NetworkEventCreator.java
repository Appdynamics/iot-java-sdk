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

import com.appdynamics.iot.HttpRequestTracker;
import com.appdynamics.iot.Instrumentation;
import com.appdynamics.iot.ServerCorrelationHeaders;
import com.appdynamics.iot.events.NetworkRequestEvent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.appdynamics.iotapps.MyIoTSampleApp.LOGGER;

public class NetworkEventCreator {

    private static final String[] TEST_URLS = {
        "http://ip.jsontest.com/?callback=showMyIP"
    };

    private static final String[] TEST_URL_PARAMS = {
        ""
    };

    public static void sendPostAndAddEvent() {
        for (int i = 0; i < TEST_URLS.length; i++) {
            NetworkEventCreator.sendPostAndAddEvent(TEST_URLS[i], TEST_URL_PARAMS[i]);
        }
    }

    private static void sendPostAndAddEvent(String url, String urlParameters) {
        sendPostAndAddEventWithHeaders(url, urlParameters, false);
    }

    public static NetworkRequestEvent create() {
        try {
            NetworkRequestEvent.Builder b = NetworkRequestEvent.builder(new URL(TEST_URLS[0]));
            b.withDuration(900L)
                    .withStatusCode(200)
                    .withNetworkError("Network Error Manual")
                    .withResponseContentLength(39)
                    .addBooleanProperty("BooleanKey", false)
                    .addDateProperty("DateKey", new Date());
            return b.build();
        } catch (MalformedURLException e) {
            LOGGER.error("Malformed URL: {}", e.toString());
        }

        return null;
    }

    public static void sendPostAndAddEventWithHeaders() {
        for (int i = 0; i < TEST_URLS.length; i++) {
            NetworkEventCreator.sendPostAndAddEventWithHeaders(TEST_URLS[i], TEST_URL_PARAMS[i], true);
        }
    }

    private static void sendPostAndAddEventWithHeaders(String url, String urlParameters, boolean isCorrelationOn) {
        try {
            URL thisUrl = new URL(url);
            // [AppDynamics Instrumentation] Get a Tracker
            final HttpRequestTracker tracker = Instrumentation.beginHttpRequest(thisUrl);

            HttpURLConnection con = (HttpURLConnection) thisUrl.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            // [AppDynamics Instrumentation] Get the correlation Header and add it to the http connection
            if (isCorrelationOn) {
                addCorrelationHeadersToRequest(con);
            }

            con.setDoInput(true);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            if (urlParameters != null && urlParameters.length() > 0) {
                wr.writeBytes(urlParameters);
            }
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            // [AppDynamics Instrumentation] Retrieve the headers from the response
            Map<String, List<String>> headerFields = null;
            if (isCorrelationOn) {
                headerFields = con.getHeaderFields();
            }

            LOGGER.info("Sending 'POST' request to URL : {}", url);
            LOGGER.info("Post parameters : {}", urlParameters);
            LOGGER.info("Response Code : {}", responseCode);

            BufferedReader in;
            String inputLine;

            if (responseCode >= 200 && responseCode < 300) {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
            } else {
                in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream()));
            }
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // [AppDynamics Instrumentation] Initiate adding NetworkRequestEvent
            if (responseCode >= 200 && responseCode < 300) {
                if (isCorrelationOn && headerFields != null && headerFields.size() > 0) {
                    tracker.withResponseCode(responseCode)
                            .withResponseHeaderFields(headerFields)
                            .reportDone();
                } else {
                    tracker.withResponseCode(responseCode)
                            .reportDone();
                }
            } else {
                tracker.withResponseCode(responseCode)
                        .withError(response.toString()).reportDone();
            }
            // End: Add for AppDynamics Instrumentation - Initiate adding NetworkRequestEvent
        } catch (MalformedURLException e) {
            LOGGER.error("Malformed URL: {}", e.toString());
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }
    }

    private static void addCorrelationHeadersToRequest(HttpURLConnection connection) {
        Iterator<Map.Entry<String, List<String>>> i = ServerCorrelationHeaders.generate().entrySet().iterator();
        Map.Entry<String, List<String>> m;
        List<String> l;
        while (i.hasNext()) {
            m = i.next();
            l = m.getValue();
            connection.addRequestProperty(m.getKey(), l.get(0));
            LOGGER.debug("Adding ADRUM Header to request {} : {}", m.getKey(), l.get(0));
        }
    }
}