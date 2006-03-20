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

import org.milyn.device.MockUAContext;

import junit.framework.TestCase;

public class UATargetExpressionTest extends TestCase {

	public void testUATargetExpression() {
		MockUAContext mockContext1 = new MockUAContext("device1");
		MockUAContext mockContext2 = new MockUAContext("device2");
		UATargetExpression expression;

		// Add a few profiles
		mockContext1.addProfile("profile1");
		mockContext1.addProfile("profile2");
		mockContext2.addProfile("profile2");
		mockContext2.addProfile("accept:application/xhtml+xml");
		
		// Match against exact device name
		expression = new UATargetExpression("device1");
		assertTrue(expression.isMatchingDevice(mockContext1));
		assertTrue(!expression.isMatchingDevice(mockContext2));
		assertEquals(new Double(100.0), new Double(expression.getSpecificity(mockContext1)));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext2)));
		
		// Match against wildcard
		expression = new UATargetExpression("*");
		assertTrue(expression.isMatchingDevice(mockContext1));
		assertTrue(expression.isMatchingDevice(mockContext2));
		assertEquals(new Double(1), new Double(expression.getSpecificity(mockContext1)));
		assertEquals(new Double(1), new Double(expression.getSpecificity(mockContext2)));

		// Match against a profile
		expression = new UATargetExpression("profile1");
		assertTrue(expression.isMatchingDevice(mockContext1));
		assertTrue(!expression.isMatchingDevice(mockContext2));
		assertEquals(new Double(10), new Double(expression.getSpecificity(mockContext1)));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext2)));

		// Match against a profile and the device name
		expression = new UATargetExpression("profile1 AND device1");
		assertTrue(expression.isMatchingDevice(mockContext1));
		assertTrue(!expression.isMatchingDevice(mockContext2));
		assertEquals(new Double(110), new Double(expression.getSpecificity(mockContext1)));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext2)));

		// Match against 2 profiles
		expression = new UATargetExpression("profile1 AND profile2");
		assertTrue(expression.isMatchingDevice(mockContext1));
		assertTrue(!expression.isMatchingDevice(mockContext2));
		assertEquals(new Double(20), new Double(expression.getSpecificity(mockContext1)));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext2)));

		// Match against 1 profile and "not" a device.
		expression = new UATargetExpression("profile2 AND not:device1");
		assertTrue(!expression.isMatchingDevice(mockContext1));
		assertTrue(expression.isMatchingDevice(mockContext2));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext1)));
		assertEquals(new Double(11), new Double(expression.getSpecificity(mockContext2)));

		// Match against 1 profile and "not" a profile.
		expression = new UATargetExpression("accept:application/xhtml+xml AND not:profile1");
		assertTrue(!expression.isMatchingDevice(mockContext1));
		assertTrue(expression.isMatchingDevice(mockContext2));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext1)));
		assertEquals(new Double(11), new Double(expression.getSpecificity(mockContext2)));
	}
}
