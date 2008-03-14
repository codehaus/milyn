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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;

/**
 * 	FileListAccessor retreives the name of a file containing a 
 * 	list of files created during a transformation
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class FileListAccessor
{
	/**
	 * 	Key used in ExecutionContexts attribute map.
	 */
    public static final String FILE_NAME_CONTEXT_KEY = FileListAccessor.class.getName() + "#CONTEXT_KEY";
    
	private FileListAccessor() { }
	
	/**
	 * 	Retrieves the name of the file containing the list of files that
	 * 	were generated during a transformation.
	 * 
	 * @param execContext	- Smooks ExecutionContext
	 * @return String		- file name of the file containing the list of files or
	 * 						  null if it has not been set.
	 */
	public static String getFileName( final ExecutionContext execContext )
	{
		return (String) execContext.getAttribute( FILE_NAME_CONTEXT_KEY );
	}
	
	/**
	 * 	Sets the file name in the passed in ExecutionContext
	 * 
	 * @param fileName 		- file name to set. Must not be null or an emply String.
	 * @param execContext	- Smooks ExceutionContext
	 */
	public static void setFileName( final String fileName, final ExecutionContext execContext )
	{
		AssertArgument.isNotNullAndNotEmpty( fileName, "fileName" );
		execContext.setAttribute( FILE_NAME_CONTEXT_KEY, fileName );
	}
	
	/**
	 * 	Return the list of files contained in the passed in file "fromFile"
	 * 
	 * @param fromFile		- name of the file that contains the list of transformed files.
	 * @return List<String>	- where String is the absolute path to a file.
	 * @throws IOException	- If the "fromFile" cannot be found or something else IO related goes wrong.
	 */
	public static List<String> getFileList( final ExecutionContext execContext ) throws IOException
	{
		BufferedReader reader = null;
		try
		{
			String fileName = getFileName( execContext );
    		reader = new BufferedReader( new FileReader( fileName ) );
    		List<String> files = new ArrayList<String>();
    		String line = null;
    		while ( (line = reader.readLine() ) != null )
    		{
    			files.add( line );
    		}
    		return files;
    	}
		finally
		{
			if ( reader != null )
			{
				reader.close();
			}
		}
	}

}
