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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.junit.Test;
import org.milyn.Smooks;
import org.xml.sax.SAXException;

/**
 * Unit test for PayloadProcessor 
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class PayloadProcessorTest
{
	private Smooks smooks;
	private PayloadProcessor processor;
	
	@Test ( expected = IllegalArgumentException.class )
	public void process() throws IOException, SAXException
	{
		processor.process( null, smooks.createExecutionContext() );
	}
	
	@Test
	public void processSourceResult() throws IOException, SAXException
	{
		SourceResult sourceResult = createSourceResult();
		Object object = processor.process( sourceResult, smooks.createExecutionContext() );
		
		assertNotNull( object );
		assertTrue( object instanceof StreamResult );
	}
	
	@Test
	public void processObject()
	{
    	Object payload = "<testing/>";
		Object object = processor.process( payload, smooks.createExecutionContext() );
		
		assertNotNull( object );
		assertTrue( object instanceof StreamResult );
		assertTrue( ((StreamResult)object).getWriter() instanceof StringWriter );
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
    	processor = new PayloadProcessor( smooks );
	}

}
