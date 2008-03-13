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
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.MockApplicationContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.routing.jms.TestBean;
import org.milyn.routing.util.RouterTestHelper;
import org.w3c.dom.Element;

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
	private String tmpDir;
	private TestBean testbean;

	@Test ( expected = IllegalArgumentException.class )
	public void initializeMissingDirname()
	{
        configureFileRouter( beanId, null, filenamePattern, router );
	}

	@Test ( expected = SmooksConfigurationException.class )
	public void initializeNonExistingDirname()
	{
        configureFileRouter( beanId, "/kddg/", filenamePattern, router );
	}
	
	@Test
	public void onEventShouldCallCreateFileWriter()
	{
        configureFileRouter( beanId, tmpDir, filenamePattern, router );
	}

	@Test
	public void visitAfter() throws ParserConfigurationException, FileNotFoundException, IOException
	{
        configureFileRouter( beanId, tmpDir, filenamePattern, router );

        router.visitAfter( (Element)null , execContext );

        Object fileName = execContext.getAttribute( FileRouter.FILE_NAME_ATTR );
        assertTrue( fileName instanceof String );

        File outputFile = new File( (String)fileName );
        assertTrue( outputFile.exists() );
        outputFile.deleteOnExit();

        assertEquals( testbean.toString(), getFileContent( outputFile ) );
	}

	@Before
	public void setup()
	{
    	router = new FileRouter();
    	config = new SmooksResourceConfiguration( "x", FileRouter.class.getName() );
		tmpDir = System.getProperty( "java.io.tmpdir" );
		assertTrue( new File( tmpDir ).exists() );
		testbean = RouterTestHelper.createBean();
        execContext = RouterTestHelper.createExecutionContext( beanId, testbean );
	}

	private void configureFileRouter(
			final String beanId,
			final String destinationDir,
			final String filenamePattern,
			final FileRouter router)
	{
        config.setParameter( "beanId", beanId );
        config.setParameter( "destinationDirectory", destinationDir );
		config.setParameter( "fileNamePattern", filenamePattern );
        Configurator.configure( router, config, new MockApplicationContext() );
	}

	private String getFileContent(final File file ) throws IOException
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader( new FileReader( file ) );
            String str=null;
            StringBuilder sb = new StringBuilder();
            while ( (str = reader.readLine()) != null )
            {
            	sb.append( str );
            }
            return sb.toString();
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
