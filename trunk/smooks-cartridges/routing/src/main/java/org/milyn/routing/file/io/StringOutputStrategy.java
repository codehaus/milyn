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

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class StringOutputStrategy extends ByteArrayOutputStrategy
{
	public StringOutputStrategy( final String fileName ) throws FileNotFoundException
	{
		super( fileName );
	}
	
	public void write( final Object object, final String encoding ) throws IOException
	{
		byte[] bytes = ((String)object).getBytes( encoding );
		write( bytes );
	}

}
