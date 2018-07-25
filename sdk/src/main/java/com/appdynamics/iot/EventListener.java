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

import com.appdynamics.iot.events.Event;
import com.google.common.eventbus.Subscribe;

import static com.appdynamics.iot.Instrumentation.LOGGER;

class EventListener {

    @Subscribe
    public void handleEvent(Event c) {
        try {
            LOGGER.debug("EventListener : received \"handle Event\" ");
            BeaconManagerFactory.getBeaconManager().addEvent(c);
        } catch (Exception ex) {
            LOGGER.error("Unable to handle event.", ex);
        }
    }

    @Subscribe
    public void handleSendAllEvents(AgentConfiguration config) {
        try {
            LOGGER.debug("EventListener : received \"Send All Events\" Command");
            BeaconManagerFactory.getBeaconManager().sendAllBeacons(config);
        } catch (Exception ex) {
            LOGGER.error("Unable to send all events.", ex);
        }
    }
}
