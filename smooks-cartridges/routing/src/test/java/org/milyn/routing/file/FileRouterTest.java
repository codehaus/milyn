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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.ExecutionContext;
import org.milyn.container.MockApplicationContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.delivery.java.JavaSource;
import org.milyn.javabean.BeanAccessor;
import org.milyn.routing.jms.TestBean;
import org.milyn.routing.util.RouterTestHelper;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Unit test for the FileRouter
 *
 * @author Daniel Bevenius
 *
 */
public class FileRouterTest
{
	private SmooksResourceConfiguration config;
	private MockExecutionContext execContext;
	private FileRouter router;
	private String beanId = "dummyId";
	private String filenamePattern = "${name}";
	private String listFileName = "testListFileName";
	private String destinationDir;
	private TestBean testbean;

	@Test ( expected = IllegalArgumentException.class )
	public void initializeMissingDirname()
	{
        configureFileRouter( beanId, null, filenamePattern, listFileName, router );
	}

	@Test ( expected = SmooksConfigurationException.class )
	public void initializeNonExistingDirname()
	{
        configureFileRouter( beanId, "/kddg/", filenamePattern, listFileName, router );
	}
	
	@Test
	public void visitAfter() throws ParserConfigurationException, FileNotFoundException, IOException, ClassNotFoundException
	{
		final String listFileName = "testListFile";
        configureFileRouter( beanId, destinationDir, filenamePattern, listFileName, router );

        router.visitAfter( (Element)null , execContext );
        
        final String listFilePath = destinationDir + File.separator + listFileName;

        String fileName = FileListAccessor.getFileName( execContext, listFilePath );
        assertNotNull( fileName );
        File listFile = new File ( fileName );
        assertEquals ( listFileName, listFile.getName() );
        
        List<String> fileNames = FileListAccessor.getFileList( execContext, listFilePath ); 
        
        File outputFile = new File( fileNames.get( 0 ) );
        assertTrue( outputFile.exists() );
        outputFile.deleteOnExit();

        assertEquals( testbean, new ObjectInputStream( new FileInputStream( outputFile )).readObject());
	}
	
	@Test
	public void filter() throws IOException, SAXException, ClassNotFoundException
	{
		Smooks smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ));
		ExecutionContext executionContext = smooks.createExecutionContext();
        BeanAccessor.addBean( executionContext, "testBean", testbean );
        
        final StringWriter writer = new StringWriter();
        smooks.filter(new JavaSource(testbean), new StreamResult(writer), executionContext);
        
        final String listFilePath = "target" + File.separator + "testFileList";
        
        List<String> fileNames = FileListAccessor.getFileList( executionContext, listFilePath ); 
        File outputFile = new File( fileNames.get( 0 ) );
        assertTrue( outputFile.exists() );
        outputFile.deleteOnExit();

        assertEquals( testbean, new ObjectInputStream( new FileInputStream( outputFile )).readObject());
        
		deleteFiles( executionContext, listFilePath );
		deleteFiles( executionContext, listFilePath + "2" );
	}
	
	private void deleteFiles( final ExecutionContext executionContext, final String listfileName ) throws IOException
	{
		List<String> fileList = FileListAccessor.getFileList( executionContext, listfileName );
		for (String file : fileList)
		{
			new File( file ).delete();
		}
		new File( listfileName ).delete();
	}
	
	@Before
	public void setup()
	{
    	router = new FileRouter();
    	config = new SmooksResourceConfiguration( "x", FileRouter.class.getName() );
		destinationDir = System.getProperty( "java.io.tmpdir" );
		assertTrue( new File( destinationDir ).exists() );
		testbean = RouterTestHelper.createBean();
        execContext = RouterTestHelper.createExecutionContext( beanId, testbean );
	}
	
	@After
	public void tearDown()
	{
        String fileName = FileListAccessor.getFileName( execContext, listFileName );
        if ( fileName != null )
        {
            new File( fileName ).delete();
        }
        new File ( listFileName ).delete();
	}

	private void configureFileRouter(
			final String beanId,
			final String destinationDir,
			final String filenamePattern,
			final String listFileName,
			final FileRouter router)
	{
        config.setParameter( "beanId", beanId );
        config.setParameter( "destinationDirectory", destinationDir );
		config.setParameter( "fileNamePattern", filenamePattern );
		config.setParameter( "listFileName", listFileName );
        Configurator.configure( router, config, new MockApplicationContext() );
	}
	
}
