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

package org.milyn.cdr.cdrar;

import java.util.jar.JarInputStream;

import org.milyn.cdr.CDRClassLoader;
import org.milyn.cdr.CDRStore;
import org.milyn.container.MockContainerContext;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class CDRClassLoaderTest extends TestCase {

	public CDRClassLoaderTest(String arg0) {
		super(arg0);
	}

	public void testLoadClass() {
		CDRStore cdrarStore = new CDRStore(new MockContainerContext());
		ClassLoader parentCL = cdrarStore.getClass().getClassLoader();
		
		try {
			cdrarStore.load("cdrar1", new JarInputStream(getClass().getResourceAsStream("CDRClassLoaderTest.cdrar")));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		CDRClassLoader classLoader = cdrarStore.getCdrarClassLoader();
		Class clazz = null;
		try {
			clazz = classLoader.loadClass("org.milyn.cdrar.LoadFromCDRLoaderClass");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(classLoader, clazz.getClassLoader());
		try {
			ClassLoaderTestInterface clazzInstance = (ClassLoaderTestInterface)clazz.newInstance();
			clazzInstance.testMethod();
		} catch (InstantiationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		try {
			clazz = classLoader.loadClass("org.milyn.cdr.cdrar.LoadFromClasspathClass");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(parentCL, clazz.getClassLoader());
		try {
			ClassLoaderTestInterface clazzInstance = (ClassLoaderTestInterface)clazz.newInstance();
			clazzInstance.testMethod();
		} catch (InstantiationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void test_toFileName_badargs() {
		try {
			CDRClassLoader.toFileName(null);
			fail("Expected IllegalArgumentException on null arg");
		} catch(IllegalArgumentException e) {
			// OK
		}
		try {
			CDRClassLoader.toFileName(" ");
			fail("Expected IllegalArgumentException on empty arg");
		} catch(IllegalArgumentException e) {
			// OK
		}
	}

	public void test_toFileName() {
		/* a.b.c.X converts to a/b/c/X.class
		 * a.b.c.X.class converts to a/b/c/X.class
		 * a/b/c/X.class converts to a/b/c/X.class
		 * a/b/c/X converts to a/b/c/X.class
		 */
		assertEquals("a/b/c/X.class", CDRClassLoader.toFileName("a.b.c.X"));
		assertEquals("a/b/c/X.class", CDRClassLoader.toFileName("a.b.c.X.class"));
		assertEquals("a/b/c/X.class", CDRClassLoader.toFileName("a/b/c/X.class"));
		assertEquals("a/b/c/X.class", CDRClassLoader.toFileName("a/b/c/X"));
	}

	public void test_toClassName_badargs() {
		try {
			CDRClassLoader.toClassName(null);
			fail("Expected IllegalArgumentException on null arg");
		} catch(IllegalArgumentException e) {
			// OK
		}
		try {
			CDRClassLoader.toClassName(" ");
			fail("Expected IllegalArgumentException on empty arg");
		} catch(IllegalArgumentException e) {
			// OK
		}
	}

	public void test_toClassName() {
		/* 
		 * a/b/c/X.class converts to a.b.c.X
		 * a/b/c/X converts to a.b.c.X
		 * a.b.c.X converts to a.b.c.X
		 * a.b.c.X.class converts to a.b.c.X
		 */
		assertEquals("a.b.c.X", CDRClassLoader.toClassName("a/b/c/X.class"));
		assertEquals("a.b.c.X", CDRClassLoader.toClassName("a/b/c/X"));
		assertEquals("a.b.c.X", CDRClassLoader.toClassName("a.b.c.X"));
		assertEquals("a.b.c.X", CDRClassLoader.toClassName("a.b.c.X.class"));
	}
}
