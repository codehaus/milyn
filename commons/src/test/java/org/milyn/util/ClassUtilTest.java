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
package org.milyn.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import junit.framework.TestCase;
/**
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>				
 *
 */
public class ClassUtilTest extends TestCase
{
	private String fileName = "META-INF/classes.inf";
	private String testClassesDirName = "target" + File.separator + "test-classes";
	private File jarFile = new File ( testClassesDirName + File.separator + "test.jar");
	
	public void test_getClassesNegative()
	{
		try
		{
			ClassUtil.getClasses( null );
		} 
		catch (Exception e)
		{
			assertTrue ( e instanceof IllegalArgumentException );
		}
	}
	
	public void test_getClasses() throws MalformedURLException 
	{
		List<Class> classes = ClassUtil.getClasses( fileName );
		assertNotNull( classes );
		assertTrue( classes.contains( String.class ) );
		assertTrue( classes.contains( Integer.class ) );
	}
	
	public void setUp() throws MalformedURLException
	{
		File testClassesDir = new File( testClassesDirName );
		URLClassLoader urlc = new URLClassLoader( new URL[] { jarFile.toURL(), testClassesDir.toURL() } ); 
		Thread.currentThread().setContextClassLoader( urlc );
	}
}
