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

package org.milyn.device.profile;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class DefaultProfileSetTest extends TestCase {

	public DefaultProfileSetTest(String arg0) {
		super(arg0);
	}
	
	public void testAddProfile_exceptions() {
		DefaultProfileSet set = new DefaultProfileSet();

		try {
			set.addProfile((Profile)null);
			fail("no arg exception on null");
		} catch(IllegalArgumentException e) {}
		try {
			set.addProfile("");
			fail("no arg exception on empty");
		} catch(IllegalArgumentException e) {}
		try {
			set.addProfile(" ");
			fail("no arg exception on whitespace");
		} catch(IllegalArgumentException e) {}
	}
	
	public void testIsMember_exceptions() {
		DefaultProfileSet set = new DefaultProfileSet();

		try {
			set.isMember(null);
			fail("no arg exception on null");
		} catch(IllegalArgumentException e) {}
	}

	
	public void testIsMember() {
		DefaultProfileSet set = new DefaultProfileSet();

		assertTrue(!set.isMember("xxx"));
		set.addProfile("xxx");
		assertTrue(set.isMember("xxx"));
		assertTrue(set.isMember("XXX"));

		assertTrue(!set.isMember("yyy"));
		set.addProfile(" yyy");
		assertTrue(set.isMember("yyy"));
		assertTrue(set.isMember(" yyy"));
		assertTrue(set.isMember("yyy "));
		assertTrue(set.isMember("YYY"));
	}
}
