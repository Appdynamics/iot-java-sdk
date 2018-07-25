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

import org.junit.Test;

import static org.junit.Assert.*;


public class StringUtilsTest {

    //from: https://github.com/apache/commons-lang/blob/master/src/test/java/org/apache/commons/lang3/StringUtilsTest.java
    @Test
    public void testAbbreviateStringInt() throws Exception {
        assertNull(StringUtils.abbreviate(null, 10));
        assertEquals("", StringUtils.abbreviate("", 10));
        assertEquals("short", StringUtils.abbreviate("short", 10));
        assertEquals("Now is ...", StringUtils.abbreviate("Now is the time for all good men to come to the aid of their party.", 10));

        final String raspberry = "raspberry peach";
        assertEquals("raspberry p...", StringUtils.abbreviate(raspberry, 14));
        assertEquals("raspberry peach", StringUtils.abbreviate("raspberry peach", 15));
        assertEquals("raspberry peach", StringUtils.abbreviate("raspberry peach", 16));
        assertEquals("abc...", StringUtils.abbreviate("abcdefg", 6));
        assertEquals("abcdefg", StringUtils.abbreviate("abcdefg", 7));
        assertEquals("abcdefg", StringUtils.abbreviate("abcdefg", 8));
        assertEquals("a...", StringUtils.abbreviate("abcdefg", 4));
        assertEquals("", StringUtils.abbreviate("", 4));

        try {
            StringUtils.abbreviate("abc", 3);
            fail("StringUtils.abbreviate expecting IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // empty
        }
    }

    @Test
    public void testAbbreviateStringStringInt() throws Exception {
        assertNull(StringUtils.abbreviate(null, null, 10));
        assertNull(StringUtils.abbreviate(null, "...", 10));
        assertEquals("paranaguacu", StringUtils.abbreviate("paranaguacu", null, 10));
        assertEquals("", StringUtils.abbreviate("", "...", 2));
        assertEquals("wai**", StringUtils.abbreviate("waiheke", "**", 5));
        assertEquals("And af,,,,", StringUtils.abbreviate("And after a long time, he finally met his son.", ",,,,", 10));

        final String raspberry = "raspberry peach";
        assertEquals("raspberry pe..", StringUtils.abbreviate(raspberry, "..", 14));
        assertEquals("raspberry peach", StringUtils.abbreviate("raspberry peach", "---*---", 15));
        assertEquals("raspberry peach", StringUtils.abbreviate("raspberry peach", ".", 16));
        assertEquals("abc()(", StringUtils.abbreviate("abcdefg", "()(", 6));
        assertEquals("abcdefg", StringUtils.abbreviate("abcdefg", ";", 7));
        assertEquals("abcdefg", StringUtils.abbreviate("abcdefg", "_-", 8));
        assertEquals("abc.", StringUtils.abbreviate("abcdefg", ".", 4));
        assertEquals("", StringUtils.abbreviate("", 4));

        try {
            @SuppressWarnings("unused") final String res = StringUtils.abbreviate("abcdefghij", "...", 3);
            fail("StringUtils.abbreviate expecting IllegalArgumentException");
        } catch (final IllegalArgumentException ex) {
            // empty
        }
    }

    @Test
    public void testAbbreviateStringStringIntInt() throws Exception {
        assertNull(StringUtils.abbreviate(null, null, 10, 12));
        assertNull(StringUtils.abbreviate(null, "...", 10, 12));
        assertEquals("", StringUtils.abbreviate("", null, 0, 10));
        assertEquals("", StringUtils.abbreviate("", "...", 2, 10));

        try {
            StringUtils.abbreviate("abcdefghij", "::", 0, 2);
            fail("StringUtils.abbreviate expecting IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // empty
        }
        try {
            StringUtils.abbreviate("abcdefghij", "!!!", 5, 6);
            fail("StringUtils.abbreviate expecting IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // empty
        }

        final String raspberry = "raspberry peach";
        assertEquals("raspberry peach", StringUtils.abbreviate(raspberry, "--", 12, 15));

        assertNull(StringUtils.abbreviate(null, ";", 7, 14));
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefgh;;", ";;", -1, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefghi.", ".", 0, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefgh++", "++", 1, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefghi*", "*", 2, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdef{{{{", "{{{{", 4, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdef____", "____", 5, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("==fghijk==", "==", 5, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("___ghij___", "___", 6, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 7, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 8, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 9, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("///ijklmno", "///", 10, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("//hijklmno", "//", 10, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("//hijklmno", "//", 11, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("...ijklmno", "...", 12, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 13, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 14, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("999ijklmno", "999", 15, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("_ghijklmno", "_", 16, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("+ghijklmno", "+", Integer.MAX_VALUE, 10);
    }

    private void assertAbbreviateWithAbbrevMarkerAndOffset(final String expected, final String abbrevMarker, final int offset, final int maxWidth) {
        final String abcdefghijklmno = "abcdefghijklmno";
        final String message = "abbreviate(String,String,int,int) failed";
        final String actual = StringUtils.abbreviate(abcdefghijklmno, abbrevMarker, offset, maxWidth);
        if (offset >= 0 && offset < abcdefghijklmno.length()) {
            assertTrue(message + " -- should contain offset character",
                    actual.indexOf((char) ('a' + offset)) != -1);
        }
        assertTrue(message + " -- should not be greater than maxWidth",
                actual.length() <= maxWidth);
        assertEquals(message, expected, actual);
    }

    //From: https://github.com/apache/commons-lang/blob/master/src/test/java/org/apache/commons/lang3/StringUtilsEmptyBlankTest.java
    @Test
    public void testIsEmpty() throws Exception {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty(" "));
        assertFalse(StringUtils.isEmpty("foo"));
        assertFalse(StringUtils.isEmpty("  foo  "));
    }

    @Test
    public void testRemoveChar() {
        // StringUtils.remove(null, *)       = null
        assertNull(StringUtils.remove(null, 'a'));
        assertNull(StringUtils.remove(null, 'a'));
        assertNull(StringUtils.remove(null, 'a'));

        // StringUtils.remove("", *)          = ""
        assertEquals("", StringUtils.remove("", 'a'));
        assertEquals("", StringUtils.remove("", 'a'));
        assertEquals("", StringUtils.remove("", 'a'));

        // StringUtils.remove("queued", 'u') = "qeed"
        assertEquals("qeed", StringUtils.remove("queued", 'u'));

        // StringUtils.remove("queued", 'z') = "queued"
        assertEquals("queued", StringUtils.remove("queued", 'z'));
    }

    public static String repeat(final char ch, final int repeat) {
        if (repeat <= 0) {
            return "";
        }
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }
}