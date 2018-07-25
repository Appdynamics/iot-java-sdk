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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

/**
 * Class for customizing the connection between the agent SDK and the AppDynamics  collector.
 */

public abstract class CollectorChannel {
    private URL url;
    private int connectTimeout;
    private int readTimeout;
    private Map<String, List<String>> requestProperties = new HashMap<String, List<String>>();
    private String requestMethod;

    /**
     * Sets the URL to which the request should be sent.
     *
     * @param url The URL of the Collector Channel
     */
    public void setURL(URL url) {
        this.url = url;
    }

    /**
     * Gets the URL that the request is being sent to
     *
     * @return the URL where the events are being sent
     */
    public URL getURL() {
        return url;
    }

    /**
     * Sets the timeout, in milliseconds, for establishing a connection to the collector.
     *
     * @param connectTimeout The https connection timeout in milliseconds
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Gets the connection timeout, in milliseconds
     *
     * @return The connection timeout, in milliseconds
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Sets the timeout, in milliseconds, for reading data from the InputStream established
     * with the collector. A timeout of zero is interpreted as an infinite timeout.
     *
     * @param readTimeout Timeout, in milliseconds, if data is not read on the input stream from the Collector
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * Gets the read timeout, in milliseconds
     *
     * @return the timeout set on reading from an InputStream
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Adds a header to the request.
     *
     * @param property The name of the header.
     * @param value    The value of the header.
     */
    public void addRequestProperty(String property, String value) {
        if (!requestProperties.containsKey(property)) {
            requestProperties.put(property, new ArrayList<String>());
        }
        requestProperties.get(property).add(value);
    }

    /**
     * Gets all the request properties as a list
     *
     * @return a map of http request properties
     */
    public Map<String, List<String>> getRequestProperties() {
        return Collections.unmodifiableMap(requestProperties);
    }

    /**
     * Sets the HTTP method used to make the request.
     *
     * @param requestMethod The request method, such as "GET" or "POST".
     */
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * Opens a stream for writing a request body.
     *
     * @return the output stream of the collector channel
     * @throws IOException if an I/O error occurs
     */
    public abstract OutputStream getOutputStream() throws IOException;

    /**
     * Sends the request, if it hasn't been sent already, and opens a stream for reading the response body.
     *
     * @return the input stream of the collector channel
     * @throws IOException if an I/O error occurs
     */
    public abstract InputStream getInputStream() throws IOException;

    /**
     * Sends the request, if it hasn't been sent already, and opens a stream for reading the response body.
     *
     * @return the error stream of the collector channel
     * @throws IOException if an I/O error occurs
     */
    public abstract InputStream getErrorStream() throws IOException;

    /**
     * Sends the request, if it hasn't been sent already, and returns the response status code.
     *
     * @return http response code
     * @throws IOException if an I/O error occurs
     */
    public abstract int getResponseCode() throws IOException;

    /**
     * Sends the request, if it hasn't been sent already, and returns the response headers
     *
     * @return a map of the http response header fields
     * @throws IOException if an I/O error occurs
     */
    public abstract Map<String, List<String>> getHeaderFields() throws IOException;

    /**
     * Sends the request, if it hasn't been sent already, and returns the http response status message.
     *
     * @return the http status message
     * @throws IOException if an I/O error occurs
     */
    public abstract String getResponseMessage() throws IOException;
}
