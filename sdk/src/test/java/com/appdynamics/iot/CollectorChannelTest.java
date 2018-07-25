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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CollectorChannelTest {

    private URL testUrl;

    CollectorChannel collectorChannel;

    @Before
    public void setup() {
        CollectorChannelFactory collectorChannelFactory = new CollectorChannelFactory() {
            @Override
            public CollectorChannel getCollectorChannel() {
                return new CollectorChannel() {
                    @Override
                    public OutputStream getOutputStream() throws IOException {
                        return null;
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return null;
                    }

                    @Override
                    public InputStream getErrorStream() throws IOException {
                        return null;
                    }

                    @Override
                    public int getResponseCode() throws IOException {
                        return 200;
                    }

                    @Override
                    public Map<String, List<String>> getHeaderFields() throws IOException {
                        return null;
                    }

                    @Override
                    public String getResponseMessage() throws IOException {
                        return "response message";
                    }
                };
            }
        };
        collectorChannel = collectorChannelFactory.getCollectorChannel();
    }

    @Test
    public void testURL() throws Exception {
        URL testUrl = new URL("https://test.mydomain.com");
        collectorChannel.setURL(testUrl);
        assertEquals(testUrl, collectorChannel.getURL());
    }

    @Test
    public void testConnectTimeout() throws Exception {
        collectorChannel.setConnectTimeout(100);
        assertEquals(100, collectorChannel.getConnectTimeout());
    }

    @Test
    public void testSetReadTimeout() throws Exception {
        collectorChannel.setReadTimeout(100);
        assertEquals(100, collectorChannel.getReadTimeout());
        collectorChannel.setReadTimeout(50);
        assertEquals(50, collectorChannel.getReadTimeout());
    }

    @Test
    public void testRequestProperty() throws Exception {
        String key1 = "key1";
        String value1 = "value1";
        String value11 = "value11";
        String key2 = "key2";
        String value2 = "value2";
        collectorChannel.addRequestProperty(key1, value1);
        assertEquals(value1, collectorChannel.getRequestProperties().get(key1).get(0));
        collectorChannel.addRequestProperty(key1, value11);
        assertEquals(value11, collectorChannel.getRequestProperties().get(key1).get(1));
        collectorChannel.addRequestProperty(key2, value2);
        assertEquals(value2, collectorChannel.getRequestProperties().get(key2).get(0));
    }

    @Test
    public void testRequestMethod() throws Exception {
        collectorChannel.setRequestMethod("request method");
        assertEquals("request method", collectorChannel.getRequestMethod());
    }
}