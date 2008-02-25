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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.MockApplicationContext;
import org.milyn.container.MockExecutionContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Unit test for the file Router
 *
 * @author Daniel Bevenius
 *
 */
public class RouterTest
{
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger( RouterTest.class );

	private SmooksResourceConfiguration config = new SmooksResourceConfiguration( "x", Router.class.getName() );

	@Test ( expected = SmooksConfigurationException.class )
	public void setDestinationFile_negative()
	{
        Configurator.configure( new Router(), config, new MockApplicationContext() );
	}

	@Test
	public void setDestinationFile()
	{
		Router router = new Router();
		String destinationFile = "test";
		config.setParameter( "destinationFile", destinationFile );
        Configurator.configure( router, config, new MockApplicationContext() );
        assertEquals( destinationFile, router.getDestinationFile() );
        router.uninitialize();
	}

	@Test
	public void visitAfter() throws ParserConfigurationException, FileNotFoundException, IOException
	{
		final String expectedXML = "<testelement/>";
		Router router = new Router();
        File file = File.createTempFile( "junittest", ".xml" );
		String destinationFile = file.getAbsolutePath();

		config.setParameter( "destinationFile", destinationFile );
		config.setParameter( "visitBefore", "false" );
        Configurator.configure( router, config, new MockApplicationContext() );

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = document.createElement( "testelement" );

        router.visitAfter( element , new MockExecutionContext() );
        router.uninitialize();
        assertTrue( file.exists() );
        assertEquals( expectedXML, getFileContent( file ) );
	}

	private String getFileContent(final File file ) throws IOException
	{
		BufferedReader reader = new BufferedReader( new FileReader( file ) );
        String str=null;
        StringBuilder sb = new StringBuilder();
        while ( (str = reader.readLine()) != null )
        	sb.append( str );
        return sb.toString();
	}


}
