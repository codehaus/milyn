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

import java.net.MalformedURLException;
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
	
	public void test_getJarsNegative()
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
		List<Class> jars = ClassUtil.getClasses( fileName );
		assertNotNull( "jars should not have been null", jars );
		assertEquals( String.class, jars.get(0) );
	}
	
}
