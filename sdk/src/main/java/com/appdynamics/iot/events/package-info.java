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
 * <p> Different types of events that can be instrumented </p>
 * <ul>
 * <li> Events to report performance data for HTTP network requests <code>{@link com.appdynamics.iot.events.NetworkRequestEvent NetworkRequestEvent}</code>
 * <li> Events to report error and exception events <code>{@link com.appdynamics.iot.events.ErrorEvent ErrorEvent}</code>
 * <li> Events to report any business specific custom event <code>{@link com.appdynamics.iot.events.CustomEvent CustomEvent}</code>
 * </ul>
 *
 * The <code>{@link com.appdynamics.iot.Instrumentation Instrumentation}</code> class provides static
 * methods to collect and send these events.
 */
package com.appdynamics.iot.events;
