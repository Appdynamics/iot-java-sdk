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

import com.appdynamics.iot.events.CustomEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.appdynamics.iotapps.MyIoTSampleApp.LOGGER;

public class CustomEventCreator {

    private static final String HOME_ROBOT = "MY_HOME_ROBOT";
    private static final String HOME_ROBOT_DESCRIPTION = "My First Home Robot";
    private static final String EVENT_NUMBER = "Event Number";
    private static final String KITCHEN = "Kitchen";
    private static final String MOPPED = "Mopped";
    private static final String BEDROOM1 = "Bedroom 1";
    private static final String VACUUMED = "Vacuumed";
    private static final String FRONT_DOOR = "Front Door";
    private static final boolean DOOR_STATUS = true;
    private static final String THERMOSTAT = "Thermostat (F)";
    private static final float TEMPERATURE = 31.7f;
    private static final String LAST_UPGRADE_ON = "Last upgrade on";
    private static final String ROBOT_CLOUD_REQUEST = "Cloud Response Code";
    private static final long RESPONSE_CODE = 200;

    public static CustomEvent create(int i) {
        CustomEvent.Builder builder = CustomEvent.builder(HOME_ROBOT, HOME_ROBOT_DESCRIPTION);
        long eventStartTime = System.currentTimeMillis();
        long duration = 6000;
        builder.withTimestamp(eventStartTime).withDuration(duration);
        builder.addStringProperty(KITCHEN, MOPPED);
        builder.addStringProperty(BEDROOM1, VACUUMED);
        builder.addBooleanProperty(FRONT_DOOR, DOOR_STATUS);
        builder.addDoubleProperty(THERMOSTAT, TEMPERATURE);
        builder.addLongProperty(ROBOT_CLOUD_REQUEST, RESPONSE_CODE);
        builder.addLongProperty(EVENT_NUMBER, i);

        String startDateString = "03/01/2017";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date lastUpgradeDate;
        try {
            lastUpgradeDate = df.parse(startDateString);
            builder.addDateProperty(LAST_UPGRADE_ON, lastUpgradeDate);
        } catch (ParseException e) {
            LOGGER.error("Parsing Exception {}", e.toString());
        }
        builder.withDuration(duration);
        return builder.build();
    }

}
