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

import java.util.List;
import java.util.Map;

/**
 * Convenient interface to collect a {@link com.appdynamics.iot.events.NetworkRequestEvent NetworkRequestEvent}
 * with AppDynamics Business Transaction Correlation
 *
 * <p>Usage</p>
 * <p>Suppose that you have a method like this for making HTTP requests:</p>
 * <pre>
 *     public byte[] sendRequest(URL url) throws HttpException {
 *          try {
 *              // implementation omitted
 *              return responseBody;
 *          } catch (UnderlyingException e) {
 *              throw new HttpException(e);
 *          }
 *     }
 * </pre>
 *
 * Here's how you would augment this method to report requests to the SDK:
 * <pre>
 *     public byte[] sendRequest(URL url) throws HttpException {
 *         HttpRequestTracker tracker = Instrumentation.beginHttpRequest(url);
 *         try {
 *              // implementation omitted
 *              tracker.withResponseCode(theResponseCode)
 *                     .withResponseHeaderFields(theResponseHeaderFields)
 *                     .reportDone();
 *              return responseBody;
 *         } catch (UnderlyingException e) {
 *              tracker.withThrowable(e)
 *                     .reportDone();
 *              throw new HttpException(e);
 *         }
 *     }
 * </pre>
 */
public interface HttpRequestTracker {
    /**
     * Gets the throwable associated with this request.
     *
     * @return an throwable describing the error.
     */
    public Throwable getThrowable();

    /**
     * Indicates that this request encountered an error.
     *
     * This method is preferred over {@link #withError(String)}. If both an error and an throwable
     * are specified, the error is ignored.
     *
     * @param throwable A throwable describing the error.
     * @return current HttpRequestTracker Object
     */
    public HttpRequestTracker withThrowable(Throwable throwable);

    /**
     * Gets the error message associated with this request.
     *
     * @return a String describing the error.
     */
    public String getError();

    /**
     * Indicates that this request encountered an error.
     *
     * @param error A string describing the error.
     * @return current HttpRequestTracker Object
     */
    public HttpRequestTracker withError(String error);

    /**
     * Gets the HTTP status code associated with this request.
     *
     * @return the status code of the response.
     * @throws java.lang.NullPointerException if {@link #withResponseCode(int)} has
     *                                        never been called to set the value.
     */
    public int getResponseCode();

    /**
     * Sets the HTTP response code associated with this request.
     *
     * If a response was received, this method must be called, or the request will not be reported.
     *
     * @param responseCode The status code of the response.
     * @return current HttpRequestTracker Object
     */
    public HttpRequestTracker withResponseCode(int responseCode);

    /**
     * Sets the HTTP status line associated with this request.
     *
     * This is optional
     *
     * @param statusLine Http Status
     * @return current HttpRequestTracker Object
     */
    public HttpRequestTracker withStatusLine(String statusLine);

    /**
     * Returns the response header fields associated with this request.
     *
     * @return the headers of the request.
     */
    public Map<String, List<String>> getResponseHeaderFields();

    /**
     * Sets the response headers associated with this request.
     *
     * @param responseHeaderFields The headers of the response.
     * @return current HttpRequestTracker Object
     */
    public HttpRequestTracker withResponseHeaderFields(Map<String, List<String>> responseHeaderFields);

    /**
     * Returns the request header fields associated with this request.
     *
     * @return the headers of the request.
     */
    public Map<String, List<String>> getRequestHeaderFields();

    /**
     * Sets the request headers associated with this request.
     *
     * @param requestHeaderFields The headers of the request.
     * @return current HttpRequestTracker Object
     */
    public HttpRequestTracker withRequestHeaderFields(Map<String, List<String>> requestHeaderFields);

    /**
     * Stops tracking an HTTP request.
     * Adds the NetworkBeacon to the Beacon Buffer
     *
     * Immediately after receiving a response or an error, set the appropriate fields and call this method to
     * report the outcome of the HTTP request. You should not continue to use this object after calling this
     * method -- if you need to track another request, obtain a new instance.
     */
    public void reportDone();
}
