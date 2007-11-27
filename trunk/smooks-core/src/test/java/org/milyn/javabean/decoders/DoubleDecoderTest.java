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

/**
 * Tests for the DoubleDecoder
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">daniel.bevenius@gmail.com</a>
 */
public class DoubleDecoderTest extends TestCase {
	private DoubleDecoder decoder = new DoubleDecoder();

    public void test_empty_data_string() {
        Object decode = decoder.decode( "" );
        assertTrue( decode instanceof Double);
        assertEquals( new Double(0), (Double)decode);
    }
    
    public void test_null_data_string() {
        Object decode = decoder.decode( null );
        assertEquals( new Double(0.0), (Double)decode);
    }


}
