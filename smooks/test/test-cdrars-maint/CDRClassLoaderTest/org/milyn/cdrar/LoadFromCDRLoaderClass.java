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

package org.milyn.cdrar;

import org.milyn.cdr.CDRClassLoader;
import org.milyn.cdr.cdrar.ClassLoaderTestInterface;
import org.milyn.cdr.cdrar.LoadFromClasspathClass;

import junit.framework.Assert;

/**
 * Load From CdrarLoader class.
 * <p/>
 * This class is a test class for the CDRClassLoader.  Its compiled binary
 * will be jar'd into CDRClassLoaderTest.cdrar located in this packages.
 * <p/>
 * Another file is used as part of the same test - {@link LoadFromClasspathClass}.
 * @author tfennelly
 */
public class LoadFromCDRLoaderClass implements ClassLoaderTestInterface {
	public void testMethod() {
		StaticInnerClassX instance = new StaticInnerClassX();
		
		if(!(instance.getClass().getClassLoader() instanceof CDRClassLoader)) {
			Assert.fail("Class should have been loaded by CDRClassLoader");
		}
	}
	
	static class StaticInnerClassX {
	}
}
