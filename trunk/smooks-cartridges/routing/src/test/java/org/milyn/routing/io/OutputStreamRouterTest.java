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

package org.milyn.routing.io;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.MockApplicationContext;
import org.milyn.io.file.FileOutputStreamResource;

/**
 * Unit test for {@link OutputStreamRouter}
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class OutputStreamRouterTest
{
	private String resourceName = "testResource";
	private String beanId = "testBeanId";
	private OutputStreamRouter router = new OutputStreamRouter();
	private SmooksResourceConfiguration config;
	
	@Test
	public void configure()
	{
        Configurator.configure( router, config, new MockApplicationContext() );
        
        assertEquals( resourceName, router.getResourceName() );
	}
	
	@Before
	public void setup()
	{
		config = createConfig( resourceName, beanId );
	}
	
	//	private
	
	private SmooksResourceConfiguration createConfig( 
			final String resourceName,
			final String beanId)
	{
    	SmooksResourceConfiguration config = new SmooksResourceConfiguration( "x", FileOutputStreamResource.class.getName() );
		config.setParameter( "resourceName", resourceName );
		config.setParameter( "beanId", beanId );
		return config;
	}

}
