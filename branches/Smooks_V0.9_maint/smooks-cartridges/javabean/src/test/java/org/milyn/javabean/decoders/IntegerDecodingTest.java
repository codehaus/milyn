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
import org.milyn.cdr.SmooksResourceConfiguration;

/**
 * Tests for the Calendar and Date decoders.
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">daniel.bevenius@gmail.com</a>
 */
public class IntegerDecodingTest extends TestCase {
	private IntegerDecoder decoder = new IntegerDecoder();
    private SmooksResourceConfiguration config = new SmooksResourceConfiguration();

    public void test_CalendarDecoder() {
        decoder.setConfiguration(config);
        Object decode = decoder.decode( "" );
        
        assertTrue( decode instanceof Integer);
        
        assertEquals( new Integer(0), (Integer)decode);
    }

}
