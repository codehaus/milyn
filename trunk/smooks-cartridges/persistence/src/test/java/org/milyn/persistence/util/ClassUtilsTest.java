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
package org.milyn.persistence.util;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.milyn.persistence.test.TestGroup;
import org.testng.annotations.Test;


/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ClassUtilsTest {


	@Test(groups = TestGroup.UNIT)
	public void test_indexOffAssignableClass() {

		assertEquals(0, ClassUtils.indexOffFirstAssignableClass(ArrayList.class, List.class)) ;
		assertEquals(1, ClassUtils.indexOffFirstAssignableClass(ArrayList.class, String.class, List.class)) ;
		assertEquals(1, ClassUtils.indexOffFirstAssignableClass(ArrayList.class, String.class, List.class, List.class)) ;
		assertEquals(-1, ClassUtils.indexOffFirstAssignableClass(ArrayList.class, String.class, String.class, String.class)) ;

	}

	@Test(groups = TestGroup.UNIT)
	public void test_containsAssignableClass() {

		assertEquals(true, ClassUtils.containsAssignableClass(ArrayList.class, List.class)) ;
		assertEquals(true, ClassUtils.containsAssignableClass(ArrayList.class, String.class, List.class)) ;
		assertEquals(true, ClassUtils.containsAssignableClass(ArrayList.class, String.class, List.class, List.class)) ;
		assertEquals(false, ClassUtils.containsAssignableClass(ArrayList.class, String.class, String.class, String.class)) ;

	}

}
