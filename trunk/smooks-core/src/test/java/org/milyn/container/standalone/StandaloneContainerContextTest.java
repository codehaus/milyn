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

package org.milyn.container.standalone;

import org.milyn.container.MockContainerResourceLocator;
import org.milyn.profile.DefaultProfileStore;

import junit.framework.TestCase;

public class StandaloneContainerContextTest extends TestCase {

	public void testConstructor() {
		try {
			new StandaloneContainerContext(null, new MockContainerResourceLocator());
			fail("Expected IllegalArgumentException on null baseDir.");
		} catch(IllegalArgumentException e) {
			//OK
		}
		try {
			new StandaloneContainerContext(new DefaultProfileStore(), null);
			fail("Expected IllegalArgumentException on null baseDir.");
		} catch(IllegalArgumentException e) {
			//OK
		}
	}
}
