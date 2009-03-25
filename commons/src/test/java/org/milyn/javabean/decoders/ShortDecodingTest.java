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
package org.milyn.javabean.decoders;

import junit.framework.TestCase;

import org.milyn.javabean.DataDecodeException;

/**
 * Test for the ShortDecoder
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">daniel.bevenius@gmail.com</a>
 */
public class ShortDecodingTest extends TestCase {
	private final ShortDecoder decoder = new ShortDecoder();

    public void test_empty_ok_value() {
        assertEquals(new Short((short)1), decoder.decode("1"));
    }

    public void test_empty_data_string() {
        try {
            decoder.decode("");
            fail("Expected DataDecodeException");
        } catch (DataDecodeException e) {
            assertEquals("Failed to decode Short value ''.", e.getMessage());
        }
    }
}
