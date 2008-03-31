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

package org.milyn.io;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.milyn.container.ExecutionContext;
import org.milyn.container.MockExecutionContext;
import org.w3c.dom.Element;

/**
 * Unit test for AbstractOutputStreamResouce 
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 */
public class AbstractOutputStreamResourceTest
{
	@Test
	public void getOutputStream () throws IOException
	{
		AbstractOutputStreamResource resource = new MockAbstractOutputStreamResource();
		MockExecutionContext executionContext = new MockExecutionContext();
		resource.visitBefore( (Element)null, executionContext );
		
		OutputStream outputStream = AbstractOutputStreamResource.getOutputStream( resource.getResourceName(), executionContext);
		assertNotNull( outputStream );
		assertTrue( outputStream instanceof ByteArrayOutputStream );
		
		resource.visitAfter( (Element)null, executionContext );
	}
	
	/**
	 * Mock class for testing
	 */
	private static class MockAbstractOutputStreamResource extends AbstractOutputStreamResource
	{
		@Override
		public OutputStream getOutputStream( final ExecutionContext executionContext )
		{
			return new ByteArrayOutputStream();
		}

		@Override
		public String getResourceName()
		{
			return "Mock";
		}

	}

}
