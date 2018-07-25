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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class UtilsTest {

    ArrayList<String> list;
    ArrayList<String> resultList;

    @Before
    public void setUp() throws Exception {
        list = new ArrayList<String>();
        resultList = new ArrayList<String>();
    }

    @Test
    public void testTruncateArrayList() throws Exception {
        assertEquals(Utils.truncateArrayList(null, 10), null);

        String[] strs = {"1", "2", "3", "4"};
        list.addAll(Arrays.asList(strs));
        assertEquals(list, Utils.truncateArrayList(list, 5));
        assertEquals(list, Utils.truncateArrayList(list, -1));


        resultList.add("1");
        resultList.add("2");
        assertEquals(resultList, Utils.truncateArrayList(list, 2));

    }


    @Test
    public void testIsArrayListEmpty() throws Exception {
        assertTrue(Utils.isArrayListEmpty(null));
        assertTrue(Utils.isArrayListEmpty(list));
        list.add("Hello");
        assertFalse(Utils.isArrayListEmpty(list));
        list.add("World");
        assertFalse(Utils.isArrayListEmpty(list));
        list.remove("Hello");
        list.remove("World");
        assertTrue(Utils.isArrayListEmpty(list));
    }
}