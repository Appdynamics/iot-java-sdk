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

import com.appdynamics.iot.AppKeyEnabledStateChangeListener;
import com.appdynamics.iot.Instrumentation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.appdynamics.iot.Instrumentation.State.DISABLED;
import static com.appdynamics.iotapps.MyIoTSampleApp.LOGGER;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MyAppKeyStateChangeListener implements AppKeyEnabledStateChangeListener {

    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            //User threads would prevent the JVM from shutting down
            //So we use a daemon thread for the ExecutorService
            t.setDaemon(true);
            return t;
        }
    });

    // Customize the time interval and number of retries
    // to the specific application being instrumented
    // The sample listener checks hourly for 3 days
    private static final int APPKEY_CHECK_RETRIES = 72; // 3 days
    private static final int APPKEY_CHECK_INITIAL_DELAY = 0;
    private static final int APPKEY_CHECK_INTERVAL = 1;
    private static final TimeUnit APPKEY_CHECK_TIME_UNIT = TimeUnit.HOURS;

    //after shutdown is called, it waits 2 minutes to terminate the program
    private static final int SCHEDULER_TERMINATION_TIMEOUT = 60;
    private static final TimeUnit SCHEDULER_TERMINATION_TIME_UNIT = TimeUnit.SECONDS;

    private int remainingRetries;

    public MyAppKeyStateChangeListener() {
        remainingRetries = APPKEY_CHECK_RETRIES;
    }

    @Override
    public void onStateChanged(Instrumentation.State state) {
        LOGGER.debug("onStateChanged callback called");
        if (state == DISABLED) {
            LOGGER.debug("Instrumentation is disabled");
            checkStatus();
        }
    }

    ScheduledFuture<?> statusHandle;

    Runnable statuschecker = new Runnable() {
        public void run() {
            if (remainingRetries <= 0 || Instrumentation.isAppKeyEnabledOnCloud()) {
                scheduler.schedule(new Runnable() {
                    public void run() {
                        statusHandle.cancel(true);
                    }
                }, 0, SECONDS);
            } else {
                //do nothing
                LOGGER.debug("Instrumentation is disabled");
            }
            remainingRetries--;
        }
    };

    public void checkStatus() {
        statusHandle =
                scheduler.scheduleAtFixedRate(statuschecker,
                        APPKEY_CHECK_INITIAL_DELAY,
                        APPKEY_CHECK_INTERVAL,
                        APPKEY_CHECK_TIME_UNIT);
    }

    //From: https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
    public void shutdownAndAwaitTermination() {
        scheduler.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!scheduler.awaitTermination(SCHEDULER_TERMINATION_TIMEOUT, SCHEDULER_TERMINATION_TIME_UNIT)) {
                scheduler.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!scheduler.awaitTermination(SCHEDULER_TERMINATION_TIMEOUT, SCHEDULER_TERMINATION_TIME_UNIT)) {
                    LOGGER.debug("AppKey Enable State Scheduler did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            scheduler.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
