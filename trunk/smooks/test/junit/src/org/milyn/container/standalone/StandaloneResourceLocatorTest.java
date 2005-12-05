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

package org.milyn.container.standalone;

import java.io.IOException;
import java.io.InputStream;

import org.milyn.container.MockContainerResourceLocator;

import junit.framework.TestCase;

public class StandaloneResourceLocatorTest extends TestCase {
	
	public void testGetResourceLocator() {
		StandaloneResourceLocator standAloneResLocator = new StandaloneResourceLocator();
		
		standAloneResLocator.setBaseURI(MockContainerResourceLocator.TEST_STANDALONE_CTX_BASE.toURI());

		try {
			InputStream buildDotXmlStream = standAloneResLocator.getResource(null, "xxxxyz.txt");
			fail("Expected exception on non-existant file resource.");
		} catch (IOException e) {
			// OK
		}
		try {
			InputStream buildDotXmlStream = standAloneResLocator.getResource(null, "device-profile.xml");
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

}
