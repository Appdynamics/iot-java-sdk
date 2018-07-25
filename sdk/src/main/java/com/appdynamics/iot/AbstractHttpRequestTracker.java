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
 * AbstractHttpRequestTracker is a simple implementation of HttpRequestTracker, without the
 * {@link HttpRequestTracker#reportDone()} method.
 *
 * WARNING: Code in this class is being executed on the client's thread, so any exceptions here
 * will crash the clients app.  Currently, nothing here could throw a throwable, so
 * no try/catches are written.  But if anything more complex is written here we must
 * add try/catches.
 */
abstract class AbstractHttpRequestTracker implements HttpRequestTracker {
    protected Throwable throwable;
    protected String error;
    protected Integer responseCode;
    protected String statusLine;
    protected Map<String, List<String>> responseHeaderFields;
    protected Map<String, List<String>> requestHeaderFields;
    protected String instrumentationSource;

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public HttpRequestTracker withThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    @Override
    public String getError() {
        return error;
    }

    @Override
    public HttpRequestTracker withError(String error) {
        this.error = error;
        return this;
    }

    @Override
    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public HttpRequestTracker withResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    @Override
    public HttpRequestTracker withStatusLine(String statusLine) {
        this.statusLine = statusLine;
        return this;
    }

    @Override
    public Map<String, List<String>> getResponseHeaderFields() {
        return responseHeaderFields;
    }

    @Override
    public HttpRequestTracker withResponseHeaderFields(Map<String, List<String>> responseHeaderFields) {
        this.responseHeaderFields = responseHeaderFields;
        return this;
    }

    @Override
    public Map<String, List<String>> getRequestHeaderFields() {
        return requestHeaderFields;
    }

    @Override
    public HttpRequestTracker withRequestHeaderFields(Map<String, List<String>> requestHeaderFields) {
        this.requestHeaderFields = requestHeaderFields;
        return this;
    }
}
