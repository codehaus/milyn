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

package org.milyn.routing.file.io;

import java.io.IOException;

/**
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class OutputStrategyFactory
{
	private static OutputStrategyFactory factory = new OutputStrategyFactory();
	
	private OutputStrategyFactory()
	{
	}
	
	public static OutputStrategyFactory getInstance()
	{
		return factory;
	}
	
	public OutputStrategy createStrategy( final String fileName, final Object bean ) throws IOException
	{
		if ( bean instanceof String )
		{
			return new StringOutputStrategy( fileName );
		}
		else if ( bean instanceof byte[] )
		{
			return new ByteArrayOutputStrategy( fileName );
		}
		else 
		{
    		return new ObjectOutputStrategy( fileName );
		}
	}
	
	

}
