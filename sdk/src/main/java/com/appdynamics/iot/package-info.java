/** Copyright (c) 2018 AppDynamics LLC and its affiliates
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

/**
 * Connected Device APIs to report performance and domain specific events to the AppDynamics Collector.
 *
 * Instrumentation can be remotely turned off from the UI, when that happens the instrumentation runtime stops collecting
 * and sending events. The application can monitor the state change by registering an AppKeyEnabledStateChangeListener
 *
 * <p>
 * The goal of these APIs are:
 * <ul>
 * <li> Collect performance data for HTTP network requests <code>{@link com.appdynamics.iot.events.NetworkRequestEvent NetworkRequestEvent}</code>
 * <li> Collect error and exception events <code>{@link com.appdynamics.iot.events.ErrorEvent ErrorEvent}</code>
 * <li> Collect any business specific custom event <code>{@link com.appdynamics.iot.events.CustomEvent CustomEvent}</code>
 * <li> Send a collection of events at a given time
 * </ul>
 *
 * The <code>{@link com.appdynamics.iot.Instrumentation Instrumentation}</code> class provides static
 * methods to collect and send different events.
 */
package com.appdynamics.iot;
