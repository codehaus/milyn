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

package org.milyn.container.plugin;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.xml.sax.SAXException;

/**
 * Unit test for AbstractContainerPlugin 
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class AbstractContainerPluginTest
{
	private AbstractContainerPlugin plugin = new MockAbstractContainerPlugin();
	private Smooks smooks;
	
	@Test
	public void process() throws IOException, SAXException
	{
		plugin.setSmooksInstance( smooks );
		
		Object object = plugin.process( null, ResultType.JAVA, smooks.createExecutionContext() );
		assertNull( object );
	}
	
	@Test
	public void processSourceResult() throws IOException, SAXException
	{
		plugin.setSmooksInstance( smooks );
		SourceResult sourceResult = createSourceResult();
		
		Object object = plugin.process( sourceResult, ResultType.JAVA, smooks.createExecutionContext() );
		assertNotNull( object );
		assertTrue( object instanceof StreamResult );
	}
	
	private static class MockAbstractContainerPlugin extends AbstractContainerPlugin
	{
		@Override
		protected Object packagePayload( Object object, ExecutionContext execContext )
		{
			return object;
		}

		public Object process( Object payload, Result result ) throws SmooksException
		{
			return null;
		}
	}
	
	private SourceResult createSourceResult()
	{
		StreamSource source = new StreamSource( new StringReader("<text/>") );
		StreamResult result = new StreamResult( new StringWriter() );
		return new SourceResult( source, result );
	}
	
	@Before
	public void setup() throws IOException, SAXException
	{
		smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ));
		
	}

}
