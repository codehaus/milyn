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

package org.milyn.routing.file.naming;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.junit.Test;

/**
 * Unit test for FreeMarkerNamingStrategy
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class FreeMarkerNamingStrategyTest
{
	@Test
	public void test() throws NamingStrategyException
	{
		HashMap<String,Object> root = createOrder( 40 );
		FreeMarkerNamingStrategy strategy = new FreeMarkerNamingStrategy();
		String generateFileName = strategy.generateFileName( "${order.nr}", root );
		assertNotNull( generateFileName );
	}
	
	private HashMap<String,Object> createOrder( final int nr )
	{
		HashMap<String,Object> root = new HashMap<String,Object>();
		HashMap<String, Object> order = new HashMap<String,Object>();
		order.put( "nr", "40" );
		root.put("order", order );
		return root;
	}
	
}
