/*
 * Milyn - Copyright (C) 2006
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.routing.file;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.milyn.container.MockExecutionContext;

/**
 * 	Unit test for FileListAccessor
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class FileListAccessorTest
{
	private MockExecutionContext execContext;

	@Test ( expected = IllegalArgumentException.class )
	public void setFileNameNegative()
	{
		FileListAccessor.setFileName( null, execContext );
	}
	
	@Test
	public void getFileName()
	{
		final String expectedFileName = "testing.txt";
		FileListAccessor.setFileName( expectedFileName , execContext );
		String actualFileName = FileListAccessor.getFileName( execContext );
		assertEquals( expectedFileName, actualFileName );
	}
	
	@Before
	public void setup()
	{
		execContext = new MockExecutionContext();
	}

}
