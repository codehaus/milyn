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

import java.util.List;

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
		FileListAccessor.addListFileName( null, execContext );
	}
	
	@Test
	public void addAndGetListFiles()
	{
		final String expectedFileName = "testing.txt";
		final String expectedFileName2 = "testing2.txt";
		FileListAccessor.addListFileName( expectedFileName , execContext );
		FileListAccessor.addListFileName( expectedFileName2 , execContext );
		List<String> list = FileListAccessor.getListFileNames( execContext );
		assertNotNull( list );
		assertTrue( list.size() == 2 );
	}
	
	@Before
	public void setup()
	{
		execContext = new MockExecutionContext();
	}

}
