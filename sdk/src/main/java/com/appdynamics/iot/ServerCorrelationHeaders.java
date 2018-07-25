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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * If you are using HttpRequestTracker to report custom HTTP requests, you can use
 * this class to correlate those requests with business transactions.
 *
 * To do so, call the generate method on this class to retrieve a list of headers, and
 * set those header values on each outgoing HTTP request. Also, ensure that you are
 * passing all response headers to HttpRequestTracker.
 */

public class ServerCorrelationHeaders {

    private static Map<String, List<String>> headers;

    static {
        headers = new HashMap<String, List<String>>(2);
        headers.put("ADRUM", Collections.singletonList("isAjax:true"));
        headers.put("ADRUM_1", Collections.singletonList("isMobile:true"));
        headers = Collections.unmodifiableMap(headers);
    }

    /**
     * Generate HTTP headers that should be set on outgoing requests.
     *
     * @return a list of headers for Correlation
     * @see HttpRequestTracker
     * @see Instrumentation
     */
    public static Map<String, List<String>> generate() {
        return headers;
    }
}
