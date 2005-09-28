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

package org.milyn.cdr;

import junit.framework.TestCase;

public class CDRDefTest extends TestCase {

	public void testGetBoolParameter() {
		CDRDef cdrDef = new CDRDef("body", "device", "xxx");
		cdrDef.setParameter("x", "true");
		
		assertTrue("Expected x to be true", cdrDef.getBoolParameter("x", false));
		assertFalse("Expected y to be false", cdrDef.getBoolParameter("y", false));
	}

	public void testGetStringParameter() {
		CDRDef cdrDef = new CDRDef("body", "device", "xxx");
		cdrDef.setParameter("x", "xxxx");
		
		assertEquals("Expected x to be xxxx", "xxxx", cdrDef.getStringParameter("x", "yyyy"));
		assertEquals("Expected y to be yyyy", "yyyy", cdrDef.getStringParameter("y", "yyyy"));
	}
}
