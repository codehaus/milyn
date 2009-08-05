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

import static org.testng.AssertJUnit.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.HashMap;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.MockApplicationContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.io.AbstractOutputStreamResource;
import org.milyn.io.StreamUtils;
import org.milyn.Smooks;
import org.milyn.FilterSettings;
import org.milyn.templating.freemarker.FreeMarkerTemplateProcessor;
import org.milyn.templating.TemplatingConfiguration;
import org.milyn.templating.OutputTo;
import org.milyn.javabean.Bean;
import org.milyn.payload.StringSource;
import org.testng.annotations.*;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Unit test for {@link FileOutputStreamResource}
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 */
@Test ( groups = "unit" )
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
		
		resource.executeVisitLifecycleCleanup(executionContext );
		
		File file = new File ( destinationDirectory, fileNamePattern );
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

    File file1 = new File("target/config-01-test/1/1.xml");
    File file2 = new File("target/config-01-test/2/2.xml");
    File file3 = new File("target/config-01-test/3/3.xml");
    
    @Test
    public void test_config_01() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config-01.xml"));

        try {
            smooks.filterSource(new StringSource("<root><a>1</a><a>2</a><a>3</a></root>"));

            assertEquals("1", getFileContents(file1));
            assertEquals("2", getFileContents(file2));
            assertEquals("3", getFileContents(file3));
        } finally {
            smooks.close();
        }
    }

    @Test
    public void test_config_01_programmatic() throws IOException, SAXException {
        Smooks smooks = new Smooks();

        try {
            smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);

            smooks.addVisitor(new Bean(HashMap.class, "object").bindTo("a", "a"));
            smooks.addVisitor(new FreeMarkerTemplateProcessor(new TemplatingConfiguration("${object.a}").setUsage(OutputTo.stream("fileOS"))), "a");
            smooks.addVisitor(new FileOutputStreamResource().setFileNamePattern("${object.a}.xml").setDestinationDirectoryPattern("target/config-01-test/${object.a}").setResourceName("fileOS"), "a");

            smooks.filterSource(new StringSource("<root><a>1</a><a>2</a><a>3</a></root>"));

            assertEquals("1", getFileContents(file1));
            assertEquals("2", getFileContents(file2));
            assertEquals("3", getFileContents(file3));
        } finally {
            smooks.close();
        }
    }

    private String getFileContents(File file) throws IOException {
        return new String(StreamUtils.readFile(file));
    }

    @BeforeClass
	public void setup()
	{
    	config = createConfig( resourceName, fileNamePattern, destinationDirectory, listFileName);
    }

    @BeforeMethod
    @AfterMethod
    public void deleteFiles() {
        file1.delete();
        file2.delete();
        file3.delete();
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
		config.setParameter( "destinationDirectoryPattern", destinationDirectory );
		config.setParameter( "listFileNamePattern", listFileName );
		return config;
	}
	
}
	