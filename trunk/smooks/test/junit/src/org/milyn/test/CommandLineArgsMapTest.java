/*
	Milyn - Copyright (C) 2003

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

package org.milyn.report;

import org.milyn.report.SmooksReportGenerator.CommandLineArgsMap;

import junit.framework.TestCase;

public class CommandLineArgsMapTest extends TestCase {

	public void test_CommandLineArgsMap() {
		CommandLineArgsMap argsMap = new CommandLineArgsMap(new String[] {"ignored", "-a", "aval", "-b", "bval", "ignored", "-c"});
		
		assertEquals("aval", argsMap.getArgValue("-a", false));
		assertEquals("bval", argsMap.getArgValue("-b", false));
		assertTrue(argsMap.isSet("-c", false));
		assertTrue(!argsMap.isSet("-x", false));
		try {
			argsMap.getArgValue("-c", false);
			fail("Expected UnsupportedOperationException");
		} catch(UnsupportedOperationException e) {
			// OK
		}
		try {
			argsMap.getArgValue("-x", true);
			fail("Expected IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			// OK
		}
		try {
			argsMap.isSet("-x", true);
			fail("Expected IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			// OK
		}
	}
}
