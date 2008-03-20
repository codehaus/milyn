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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.container.plugin.PayloadProcessor;
import org.milyn.delivery.java.JavaResult;
import org.milyn.javabean.BeanAccessor;
import org.milyn.routing.jms.TestBean;
import org.xml.sax.SAXException;

/**
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class FileRouterContainerPluginTest
{
	private static TestBean bean = new TestBean();
	
	
	@Test
	public void process() throws IOException, SAXException
	{

		Smooks smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ));
        PayloadProcessor plugin = new FileRouterContainerPlugin( smooks );
		ExecutionContext executionContext = smooks.createExecutionContext();
		bean.setName( "Daniel" );
        BeanAccessor.addBean( executionContext, "testBean", bean );
		
        Object object = plugin.process( bean, new JavaResult(), executionContext );
        
        
		assertNotNull( object );
		assertTrue ( object instanceof String );
		String fileName = (String) object;
		System.out.println(fileName);
	}
	
}
