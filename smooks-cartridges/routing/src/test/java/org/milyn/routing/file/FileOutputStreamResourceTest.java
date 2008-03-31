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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.MockApplicationContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.io.AbstractOutputStreamResource;
import org.w3c.dom.Element;

/**
 * Unit test for {@link FileOutputStreamResource}
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 */
public class FileOutputStreamResourceTest
{
	private String resourceName = "testResourceName";
	private String fileNamePattern = "testFileName";
	private String destinationDirectory = System.getProperty( "java.io.tmpdir" );
	private String listFileName = "testListFileName";
	private FileOutputStreamResource resource = new FileOutputStreamResource();
	private SmooksResourceConfiguration config;
	
	@Test
	public void configure()
	{
        Configurator.configure( resource, config, new MockApplicationContext() );
        
        assertEquals( resourceName, resource.getResourceName() );
	}
	
	@Test
	public void visit() throws IOException
	{
        Configurator.configure( resource, config, new MockApplicationContext() );
        
		MockExecutionContext executionContext = new MockExecutionContext();
		resource.visitBefore( (Element)null, executionContext );
		
		OutputStream outputStream = AbstractOutputStreamResource.getOutputStream( resource.getResourceName(), executionContext );
		assertNotNull( outputStream );
		assertTrue( outputStream instanceof FileOutputStream );
		
		resource.visitAfter( (Element)null, executionContext );
		
		File file = new File ( destinationDirectory + File.separator + fileNamePattern );
		assertTrue( file.exists() );
		
		List<String> listFileNames = FileListAccessor.getListFileNames( executionContext );
		assertNotNull( listFileNames );
		assertTrue( listFileNames.size() == 1 );
		
		for (String listFile : listFileNames)
		{
			List<String> fileList = FileListAccessor.getFileList( executionContext, listFile );
    		assertTrue( fileList.size() == 1 );
			for (String fileName : fileList)
			{
				File file2 = new File( fileName );
        		assertEquals( fileNamePattern, file2.getName() );
				file2.delete();
			}
			new File( listFile ).delete();
		}
	}
	
	@Before
	public void setup()
	{
    	config = createConfig( resourceName, fileNamePattern, destinationDirectory, listFileName);
		
	}
	
	//	private
	
	private SmooksResourceConfiguration createConfig( 
			final String resourceName, 
			final String fileName ,
			final String destinationDirectory ,
			final String listFileName )
	{
    	SmooksResourceConfiguration config = new SmooksResourceConfiguration( "x", FileOutputStreamResource.class.getName() );
		config.setParameter( "resourceName", resourceName );
		config.setParameter( "fileNamePattern", fileName );
		config.setParameter( "destinationDirectory", destinationDirectory );
		config.setParameter( "listFileName", listFileName );
		return config;
	}
	
}
	