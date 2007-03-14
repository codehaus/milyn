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

package org.milyn.cdr;

import org.milyn.useragent.MockUAContext;

import junit.framework.TestCase;

public class UseragentExpressionTest extends TestCase {

	public void testUseragentExpression() {
		MockUAContext mockContext1 = new MockUAContext("device1");
		MockUAContext mockContext2 = new MockUAContext("device2");
		UseragentExpression expression;

		// Add a few profiles
		mockContext1.addProfile("profile1");
		mockContext1.addProfile("profile2");
		mockContext2.addProfile("profile2");
		mockContext2.addProfile("accept:application/xhtml+xml");
		
		// Match against exact device name
		expression = new UseragentExpression("device1");
		assertTrue(expression.isMatch(mockContext1.getProfileSet()));
		assertTrue(!expression.isMatch(mockContext2.getProfileSet()));
		assertEquals(new Double(100.0), new Double(expression.getSpecificity(mockContext1.getProfileSet())));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext2.getProfileSet())));
		
		// Match against wildcard
		expression = new UseragentExpression("*");
		assertTrue(expression.isMatch(mockContext1.getProfileSet()));
		assertTrue(expression.isMatch(mockContext2.getProfileSet()));
		assertEquals(new Double(5), new Double(expression.getSpecificity(mockContext1.getProfileSet())));
		assertEquals(new Double(5), new Double(expression.getSpecificity(mockContext2.getProfileSet())));

		// Match against a profile
		expression = new UseragentExpression("profile1");
		assertTrue(expression.isMatch(mockContext1.getProfileSet()));
		assertTrue(!expression.isMatch(mockContext2.getProfileSet()));
		assertEquals(new Double(10), new Double(expression.getSpecificity(mockContext1.getProfileSet())));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext2.getProfileSet())));

		// Match against a profile and the device name
		expression = new UseragentExpression("profile1 AND device1");
		assertTrue(expression.isMatch(mockContext1.getProfileSet()));
		assertTrue(!expression.isMatch(mockContext2.getProfileSet()));
		assertEquals(new Double(110), new Double(expression.getSpecificity(mockContext1.getProfileSet())));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext2.getProfileSet())));

		// Match against 2 profiles
		expression = new UseragentExpression("profile1 AND profile2");
		assertTrue(expression.isMatch(mockContext1.getProfileSet()));
		assertTrue(!expression.isMatch(mockContext2.getProfileSet()));
		assertEquals(new Double(20), new Double(expression.getSpecificity(mockContext1.getProfileSet())));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext2.getProfileSet())));

		// Match against 1 profile and "not" a device.
		expression = new UseragentExpression("profile2 AND not:device1");
		assertTrue(!expression.isMatch(mockContext1.getProfileSet()));
		assertTrue(expression.isMatch(mockContext2.getProfileSet()));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext1.getProfileSet())));
		assertEquals(new Double(11), new Double(expression.getSpecificity(mockContext2.getProfileSet())));

		// Match against 1 profile and "not" a profile.
		expression = new UseragentExpression("accept:application/xhtml+xml AND not:profile1");
		assertTrue(!expression.isMatch(mockContext1.getProfileSet()));
		assertTrue(expression.isMatch(mockContext2.getProfileSet()));
		assertEquals(new Double(0), new Double(expression.getSpecificity(mockContext1.getProfileSet())));
		assertEquals(new Double(11), new Double(expression.getSpecificity(mockContext2.getProfileSet())));
	}
}
