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

import org.milyn.cdr.CDRClassLoader;

import junit.framework.Assert;

/**
 * Load From Classpath class.
 * <p/>
 * This class is a test class for the CDRClassLoader.  Its compiled binary
 * will not be jar'd into CDRClassLoaderTest.jar (located in this package).
 * <p/>
 * Another file is used as part of the same test - {@link LoadFromCDRLoaderClass}.
 * @author tfennelly
 */
public class LoadFromClasspathClass implements ClassLoaderTestInterface {
	public void testMethod() {
		StaticInnerClassX instance = new StaticInnerClassX();
		
		if((instance.getClass().getClassLoader() instanceof CDRClassLoader)) {
			Assert.fail("Class should not have been loaded by CDRClassLoader");
		}
		
		try {
			Class.forName("org.milyn.cdrar.LoadFromCDRLoaderClass");
		} catch (ClassNotFoundException e) {
			// OK expected behaviour.
		}
	}
	
	static class StaticInnerClassX {
	}
}
