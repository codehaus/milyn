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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.Ignore;
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
	public void visitAfter() throws ParserConfigurationException, FileNotFoundException, IOException, ClassNotFoundException
	{
        configureFileRouter( beanId, tmpDir, filenamePattern, router );

        router.visitAfter( (Element)null , execContext );

        Object fileName = execContext.getAttribute( FileRouter.FILE_NAMES_CONTEXT_KEY );
        assertTrue( fileName instanceof List );
        List<String> fileNames = (List<String>) fileName;
        File outputFile = new File( fileNames.get( 0 ) );
        assertTrue( outputFile.exists() );
        outputFile.deleteOnExit();

        assertEquals( testbean, new ObjectInputStream( new FileInputStream( outputFile )).readObject());
	}
	
	@Test
	public void filter() throws IOException, SAXException
	{
		Smooks smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ));
		ExecutionContext executionContext = smooks.createExecutionContext();
		TestBean bean = new TestBean();
		bean.setName( "Daniel" );
        BeanAccessor.addBean( executionContext, "testBean", bean );
        
        final StringWriter writer = new StringWriter();
        smooks.filter(new JavaSource(bean), new StreamResult(writer), executionContext);
        Object fileName = executionContext.getAttribute( FileRouter.FILE_NAMES_CONTEXT_KEY );
        System.out.println( "FileNames " + fileName );
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

}
