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

package com.appdynamics.iot.utils;

import java.util.ArrayList;

public class Utils {
    /**
     * @param list      Returns a copy of the list with no more than maxLength entries
     * @param maxLength Truncates the list to maxLength number of entries. If maxLength is 0 or negative, it is a no-op
     * @return
     */
    public static ArrayList truncateArrayList(ArrayList list, final int maxLength) {
        if (isArrayListEmpty(list) || list.size() <= maxLength || maxLength < 1) {
            return list;
        } else {

            return new ArrayList(list.subList(0, maxLength));
        }
    }

    public static boolean isArrayListEmpty(final ArrayList list) {
        return list == null || list.size() == 0;
    }
}
