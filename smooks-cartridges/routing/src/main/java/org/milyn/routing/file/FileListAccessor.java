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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	static Log log = LogFactory.getLog(  FileListAccessor.class );
	
	/*
	 * 	Key for filenames used in ExecutionContexts attribute map.
	 */
    private static final String LIST_FILE_NAME_CONTEXT_KEY = FileListAccessor.class.getName() + "#listFileName:";
    
    /*
	 * 	Keys for the entry containing the file lists (used in ExecutionContexts attribute map )
     */
    private static final String ALL_LIST_FILE_NAME_CONTEXT_KEY = FileListAccessor.class.getName() + "#allListFileName";
    
	private FileListAccessor() { }
	
	/**
	 * 	Retrieves the name of the file containing the list of files that
	 * 	were generated during a transformation.
	 * 
	 * @param execContext	- Smooks ExecutionContext
	 * @return String		- file name of the file containing the list of files or
	 * 						  null if it has not been set.
	 */
	public static String getFileName( final ExecutionContext execContext, final String listFileName )
	{
		return (String) execContext.getAttribute( LIST_FILE_NAME_CONTEXT_KEY + listFileName );
	}
	
	/**
	 * 	Sets the file name in the passed in ExecutionContext. Note that the filename should be
	 * 	specified with a path. This is so that the same filename can be used in multiple directories.
	 * 
	 * @param listFileName 	- file name to set including path. Must not be null or an emply String.
	 * @param execContext	- Smooks ExceutionContext
	 */
	public static void setFileName( final String listFileName, final ExecutionContext execContext )
	{
		AssertArgument.isNotNullAndNotEmpty( listFileName, "fileName" );
		execContext.setAttribute( LIST_FILE_NAME_CONTEXT_KEY + listFileName, listFileName );
		
		@SuppressWarnings ("unchecked")
		List<String> allListFiles = (List<String>) execContext.getAttribute( ALL_LIST_FILE_NAME_CONTEXT_KEY );
		if ( allListFiles == null  )
		{
			allListFiles = new ArrayList<String>();
		}
		
		//	no need to have duplicates
		if ( !allListFiles.contains( listFileName ))
		{
    		allListFiles.add( listFileName );
		}
		execContext.setAttribute( ALL_LIST_FILE_NAME_CONTEXT_KEY , allListFiles );
	}
	
	/**
	 * 	Return the list of files contained in the passed in file "fromFile"
	 * 
	 * @param executionContext	- Smooks execution context
	 * @param fromFile	- path to list file 
	 * @return List<String>	- where String is the absolute path to a file.
	 * @throws IOException	- If the "fromFile" cannot be found or something else IO related goes wrong.
	 */
	public static List<String> getFileList( final ExecutionContext executionContext, String fromFile ) throws IOException
	{
		BufferedReader reader = null;
		try
		{
			String fileName = getFileName( executionContext, fromFile );
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

	@SuppressWarnings ( "unchecked" )
	public static List<String> getAllListFileNames( final ExecutionContext executionContext )
	{
		return (List<String>) executionContext.getAttribute( ALL_LIST_FILE_NAME_CONTEXT_KEY );
	}

}
