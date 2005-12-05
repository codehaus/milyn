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

import java.util.List;

import org.milyn.cdr.CDRDef.Parameter;

import junit.framework.TestCase;

public class CDRDefTest extends TestCase {

	public void test_getParameter() {
		CDRDef cdrDef = new CDRDef("body", "device", "xxx");

		cdrDef.setParameter("x", "val x");
		assertEquals("Expected x to be 'val x'", "val x", cdrDef.getParameter("x").getValue());
		cdrDef.setParameter("y", "val y 1");
		cdrDef.setParameter("y", "val y 2");
		assertEquals("Expected y to be 'val y 1'", "val y 1", cdrDef.getParameter("y").getValue());
		
		List yParams = cdrDef.getParameters("y");
		assertEquals("val y 1", ((Parameter)yParams.get(0)).getValue());
		assertEquals("val y 2", ((Parameter)yParams.get(1)).getValue());
	}

	public void test_getBoolParameter() {
		CDRDef cdrDef = new CDRDef("body", "device", "xxx");
		cdrDef.setParameter("x", "true");
		
		assertTrue("Expected x to be true", cdrDef.getBoolParameter("x", false));
		assertFalse("Expected y to be false", cdrDef.getBoolParameter("y", false));
	}

	public void test_getStringParameter() {
		CDRDef cdrDef = new CDRDef("body", "device", "xxx");
		cdrDef.setParameter("x", "xxxx");
		
		assertEquals("Expected x to be xxxx", "xxxx", cdrDef.getStringParameter("x", "yyyy"));
		assertEquals("Expected y to be yyyy", "yyyy", cdrDef.getStringParameter("y", "yyyy"));
	}
}
