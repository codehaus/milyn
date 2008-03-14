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

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class ByteArrayOutputStrategy implements OutputStrategy
{
	OutputStream out;
	
	public ByteArrayOutputStrategy( final String fileName ) throws FileNotFoundException
	{
		out = new BufferedOutputStream( new FileOutputStream( fileName, true));
	}
	
	public void write( final Object object, final String encoding ) throws UnsupportedEncodingException, IOException
	{
		write( new String( (byte[])object, encoding ).getBytes() ) ;
	}
	
	protected void write( final byte[] bytes ) throws IOException
	{
		out.write( bytes );
	}
	
	public void close()
	{
		IOUtil.closeOutputStream( out );
	}

	public void flush() throws IOException
	{
		out.flush();
	}

}
