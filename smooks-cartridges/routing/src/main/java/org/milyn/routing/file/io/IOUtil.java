/*
 * JBoss, Home of Professional Open Source Copyright 2006, JBoss Inc., and
 * individual contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of individual
 * contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.milyn.routing.file.io;

import java.io.IOException;
import java.io.OutputStream;

import org.milyn.cdr.SmooksConfigurationException;

/**
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class IOUtil
{
	private IOUtil() {}
	
	public static void closeOutputStream( final OutputStream outputStream )
	{
		if ( outputStream != null )
		{
			try
			{
				outputStream.flush();
			}
			catch (IOException e)
			{
        		final String errorMsg = "IOException while trying to flush output stream to file";
        		throw new SmooksConfigurationException( errorMsg, e );
			}

			try
			{
				outputStream.close();
			}
			catch (IOException e)
			{
        		final String errorMsg = "IOException while trying to close output stream to file";
        		throw new SmooksConfigurationException( errorMsg, e );
			}
		}
	}

}
