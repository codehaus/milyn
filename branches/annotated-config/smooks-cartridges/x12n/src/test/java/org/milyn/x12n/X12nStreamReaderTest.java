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

package org.milyn.x12n;

import java.io.IOException;

import org.xml.sax.InputSource;

import junit.framework.TestCase;

/**
 *
 * @author tfennelly
 */
public class X12nStreamReaderTest extends TestCase {

    public void testStreamReader() throws IOException {
        InputSource is = new InputSource(getClass().getResourceAsStream("x12n-sample1.txt"));
        X12nStreamReader reader = new X12nStreamReader(is);
        
        assertTrue(reader.movetoNextSegment());
        assertEquals("ST", reader.readNextSegmentToken());
        assertEquals("270", reader.readNextSegmentToken());
        assertEquals("0001", reader.readNextSegmentToken().trim());
        assertEquals(null, reader.readNextSegmentToken());

        assertTrue(reader.movetoNextSegment());
        assertEquals("HL", reader.readNextSegmentToken());
        assertEquals("1", reader.readNextSegmentToken());
        assertEquals("", reader.readNextSegmentToken());
        assertEquals("20", reader.readNextSegmentToken());
        assertEquals("1", reader.readNextSegmentToken());
        assertEquals(null, reader.readNextSegmentToken());

        assertTrue(!reader.movetoNextSegment());
    }
}
