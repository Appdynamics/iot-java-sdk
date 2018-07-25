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

package com.appdynamics.iot.events;

import com.appdynamics.iot.utils.Constants;
import com.appdynamics.iot.utils.StringUtils;
import com.appdynamics.iot.utils.StringUtilsTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NetworkRequestEventTest {

    private static URL testUrl;

    @BeforeClass
    public static void setup() {
        try {
            testUrl = new URL("https://myapp.mydomain.com/iot");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNetworkRequestEventDefault() throws Exception {
        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl).build();
        assertNull(n.statusCode);
        assertEquals(n.url, testUrl.toString());
        assertNull(n.networkError);
        assertNull(n.requestContentLength);
        assertNull(n.responseContentLength);
        assertNull(n.responseHeaders);
    }

    @Test
    public void testNetworkRequestEventNullUrl() throws Exception {
        NetworkRequestEvent n = NetworkRequestEvent.builder(null).build();
        assertNull(n.url);
    }

    @Test
    public void testNetworkRequestEventLongUrl() throws Exception {
        URL longUrl = new URL("https://" + StringUtilsTest.repeat('u', Constants.NETWORK_REQUEST_EVENT_URL_MAX - 7));
        NetworkRequestEvent n = NetworkRequestEvent.builder(longUrl).build();
        assertEquals(n.url.length(), Constants.NETWORK_REQUEST_EVENT_URL_MAX);
        assertEquals(n.getUrl(), StringUtils.abbreviate(longUrl.toString(), Constants.NETWORK_REQUEST_EVENT_URL_MAX));
    }

    @Test
    public void testGetType() throws Exception {
        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl).build();
        assertEquals(n.getType(), Event.Type.NETWORK_EVENT);
    }

    @Test
    public void testWithStatusCode() throws Exception {
        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl)
                .withStatusCode(300).build();
        assertEquals(n.statusCode.intValue(), 300);
    }

    @Test
    public void testWithNetworkError() throws Exception {
        final String nwError = StringUtilsTest.repeat('n', 10);
        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl)
                .withNetworkError(nwError).build();
        assertEquals(n.networkError, nwError);
    }

    @Test
    public void testWithNetworkErrorLong() throws Exception {
        final String longNwError = StringUtilsTest.repeat('m', Constants.NETWORK_REQUEST_EVENT_ERROR_MAX + 1);
        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl)
                .withNetworkError(longNwError).build();
        assertEquals(n.networkError.length(), Constants.NETWORK_REQUEST_EVENT_ERROR_MAX);
    }

    @Test
    public void testWithRequestContentLength() throws Exception {
        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl)
                .withRequestContentLength(60).build();
        assertEquals(n.requestContentLength.intValue(), 60);
    }

    @Test
    public void testWithResponseContentLength() throws Exception {
        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl)
                .withResponseContentLength(60).build();
        assertEquals(n.responseContentLength.intValue(), 60);
    }

    @Test
    public void testWithResponseContentLengthNegative() throws Exception {
        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl)
                .withRequestContentLength(-60).build();
        assertNull(n.responseContentLength);
    }

    @Test
    public void testWithServerResponseHeaders() throws Exception {
        Map<String, List<String>> testHeaders = new HashMap<String, List<String>>() {
            {
                put("ADRUM_0", Arrays.asList("btId:1"));
                put("ADRUM_1", Arrays.asList("btERT:95"));
                put("ADRUM_2", Arrays.asList("btDuration:65"));
            }
        };

        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl)
                .withServerResponseHeaders(testHeaders).build();
        assertEquals("btId:1", n.responseHeaders.get("ADRUM_0").get(0));
        assertEquals("btERT:95", n.responseHeaders.get("ADRUM_1").get(0));
        assertEquals("btDuration:65", n.responseHeaders.get("ADRUM_2").get(0));
        assertNull(n.responseHeaders.get("ADRUM_3"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidWithServerResponseHeaders() throws Exception {
        Map<String, List<String>> testHeaders = new HashMap<String, List<String>>() {
            {
                put("ADRUM_0", Arrays.asList("btId:1"));
                put("ADRUM_1", Arrays.asList("btERT:95"));
                put("ADRUM_2", Arrays.asList("btDuration:65"));
            }
        };

        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl)
                .withServerResponseHeaders(testHeaders).build();
        assertNull(n.responseHeaders.get("ADRUM_0").get(1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableWithServerResponseHeaders() throws Exception {
        Map<String, List<String>> testHeaders = new HashMap<String, List<String>>() {
            {
                put("ADRUM_0", Arrays.asList("btId:1"));
                put("ADRUM_1", Arrays.asList("btERT:95"));
                put("ADRUM_2", Arrays.asList("btDuration:65"));
            }
        };

        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl)
                .withServerResponseHeaders(testHeaders).build();
        n.responseHeaders.put("INVALID", Arrays.asList("Invalid"));
    }

    @Test
    public void testWithServerResponseHeadersNull() throws Exception {
        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl)
                .withServerResponseHeaders(null).build();
        assertNull(n.responseHeaders);
    }

    @Test
    public void testWithCaseInsensitiveServerResponseHeaders() throws Exception {
        Map<String, List<String>> testHeaders = new HashMap<String, List<String>>() {
            {
                put("Adrum_0", Arrays.asList("btId:1"));
                put("adrum_1", Arrays.asList("btERT:95"));
                put("Invalid_Adrum", Arrays.asList("btDuration:65"));
            }
        };

        NetworkRequestEvent n = NetworkRequestEvent.builder(testUrl)
                .withServerResponseHeaders(testHeaders).build();
        assertEquals("btId:1", n.responseHeaders.get("Adrum_0").get(0));
        assertEquals("btERT:95", n.responseHeaders.get("adrum_1").get(0));
        assertNull(n.responseHeaders.get("Invalid_Adrum"));
    }
}