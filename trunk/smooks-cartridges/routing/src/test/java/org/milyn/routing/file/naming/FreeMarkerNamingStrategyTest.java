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
		FreeMarkerNamingStrategy strategy = new FreeMarkerNamingStrategy();
		String generateFileName = strategy.generateFileName( "{order.nr}", new Order(40) );
		System.out.println(generateFileName);
		assertNotNull( generateFileName );
	}
	
	private static class Order
	{
		private int nr;
		
		public Order( int nr )
		{
			this.nr = nr;
		}

		public int getNr()
		{
			return nr;
		}

		public void setNr( int nr )
		{
			this.nr = nr;
		}
		
	}

}
