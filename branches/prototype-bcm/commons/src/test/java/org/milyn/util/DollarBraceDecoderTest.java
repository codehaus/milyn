/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/
package org.milyn.util;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DollarBraceDecoderTest  extends TestCase {

    public void test_getTokens() {
        assertEquals("[]", DollarBraceDecoder.getTokens("aaaaaa").toString());
        assertEquals("[x]", DollarBraceDecoder.getTokens("aaa${x}aaa").toString());
        assertEquals("[x, x]", DollarBraceDecoder.getTokens("aaa${x}a${x}aa").toString());
        assertEquals("[x, x, y]", DollarBraceDecoder.getTokens("aaa${x}a${x}a${y}a").toString());
        assertEquals("[x, x, y]", DollarBraceDecoder.getTokens("a}aa${x}a${x}a${y}a").toString());
        assertEquals("[x, x, y]", DollarBraceDecoder.getTokens("a}a${a${x}a${x}a${y}a").toString());
        assertEquals("[x, x, y]", DollarBraceDecoder.getTokens("a}a${a${x}a${x}a${y}a${").toString());
    }

    public void test_replaceTokens() {
        assertEquals("aaaaaa", DollarBraceDecoder.replaceTokens("aaaaaa", "?"));
        assertEquals("aaa?aaa", DollarBraceDecoder.replaceTokens("aaa${x}aaa", "?"));
        assertEquals("aaa?a?aa", DollarBraceDecoder.replaceTokens("aaa${x}a${x}aa", "?"));
        assertEquals("aaa?a?a?a", DollarBraceDecoder.replaceTokens("aaa${x}a${x}a${y}a", "?"));
        assertEquals("test-?.txt", DollarBraceDecoder.replaceTokens( "test-${currentDate.date?string('yyyy')}.txt", "?"));
        assertEquals("test-?.txt", DollarBraceDecoder.replaceTokens( "test-${currentDate.date?string('yyyy-MM-dd-HH-mm-sss')}.txt", "?"));
        assertEquals("test-?.txt", DollarBraceDecoder.replaceTokens( "test-${currentDate.date?string(\"yyyy-MM-dd-HH-mm-sss\")}.txt", "?"));
    }
    
}
