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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.annotation.VisitAfterIf;
import org.milyn.delivery.annotation.VisitBeforeIf;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.javabean.BeanAccessor;
import org.milyn.routing.file.io.OutputStrategy;
import org.milyn.routing.file.io.OutputStrategyFactory;
import org.milyn.routing.file.naming.NamingStrategy;
import org.milyn.routing.file.naming.NamingStrategyException;
import org.milyn.routing.file.naming.TemplatedNamingStrategy;
import org.w3c.dom.Element;

/**
 * <p/>
 * FileRouter is a Visitor for DOM or SAX elements. It appends the content
 * to the configured destination file.
 * </p>
 * The name of the output file(s) is determined by a NamingStrategy, the
 * default being {@link TemplatedNamingStrategy}
 * </p>
 * As the number of files produced by a single transformation could be 
 * quite large the filename are not stored in memory.<br> 
 * Instead they are appended to a file. This is simple a list of the file names created
 * during the transformation. <br>
 * The name of this file containing the list of file will have the same
 * as the naming strategy with the ".lst" (list) suffix.
 * </p> 
 * Example configuration:
 * <pre>
 * &lt;resource-config selector="orderItems"&gt;
 *    &lt;resource&gt;org.milyn.routing.file.FileRouter&lt;/resource&gt;
 *    &lt;param name="beanId">beanId&lt;/param&gt;
 *    &lt;param name="destinationDirectory">dir&lt;/param&gt;
 *    &lt;param name="fileNamePattern">${orderid}-${customName}.txt&lt;/param&gt;
 * &lt;/resource-config&gt;
 * Optional parameters:
 *    &lt;param name="encoding"&gt;UTF-8&lt;/param&gt;
 * </pre>
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 * @since 1.0
 *
 */
@VisitAfterIf(	condition = "!parameters.containsKey('visitBefore') || parameters.visitBefore.value != 'true'")
@VisitBeforeIf(	condition = "!parameters.containsKey('visitAfter') || parameters.visitAfter.value != 'true'")
public class FileRouter implements DOMElementVisitor, SAXElementVisitor
{
	/*
	 * 	Log
	 */
    @SuppressWarnings( "unused" )
	private final Log log = LogFactory.getLog( FileRouter.class );
    
    /*
     * 	System line separator
     */
	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

	/*
	 * 	Name of directory where files will be created.
	 */
	@ConfigParam ( name = "destinationDirectory", use = Use.REQUIRED )
	private String destDirName;

	/*
	 * 	File prefix for the created file
	 */
	@ConfigParam ( name = "fileNamePattern", use = Use.REQUIRED )
	private String fileNamePattern;

	/*
	 * 	BeanId is a key that is used to look up a bean
	 * 	in the execution context
	 */
    @ConfigParam( use = ConfigParam.Use.REQUIRED )
    private String beanId;

    /*
     *	Character encoding to be used when writing character
     * 	output
     */
    @ConfigParam( use = ConfigParam.Use.OPTIONAL, defaultVal = "UTF-8" )
	private String encoding;
    
    /*
     * 	File object of the destination directory
     */
    private File destinationDir;

    /*
     * Naming strategy for generating the file pattern for output files.
     */
    private NamingStrategy namingStrategy = new TemplatedNamingStrategy();


	@Initialize
	public void initialize()
	{
		destinationDir = new File ( destDirName );
    	if ( !destinationDir.exists() || !destinationDir.isDirectory() )
    	{
    		throw new SmooksException ( "Destination directory [" + destDirName + "] does not exist or is not a directory.");
    	}
	}

	//	Vistor methods

	public void visitAfter( final Element element, final ExecutionContext execContext ) throws SmooksException
	{
		visit( execContext );
	}

	public void visitBefore( final Element element, final ExecutionContext execContext ) throws SmooksException
	{
		visit( execContext );
	}

	public void visitAfter( final SAXElement saxElement, final ExecutionContext execContext ) throws SmooksException, IOException
	{
		visit( execContext );
	}

	public void visitBefore( final SAXElement saxElement, final ExecutionContext execContext ) throws SmooksException, IOException
	{
		visit( execContext );
	}

	public void onChildElement( final SAXElement saxElement, final SAXElement arg1, final ExecutionContext execContext ) throws SmooksException, IOException
	{
		//	NoOp
	}

	public void onChildText( final SAXElement saxElement, final SAXText saxText, final ExecutionContext execContext ) throws SmooksException, IOException
	{
		//	NoOp
	}

	//	protected
	protected String generateFilePattern( final Object object )
	{
    	try
		{
			return namingStrategy.generateFileName( fileNamePattern, object );
		} 
    	catch (NamingStrategyException e)
		{
    		throw new SmooksException( e.getMessage(), e );
		}
	}
	
	//	private

	/**
	 * 	Extracts the bean identified by beanId and append that object
	 * 	to the destination file.
	 *
	 * 	@param execContext
	 *  @throws SmooksException	if the bean cannot be found in the ExecutionContext
	 */
	private void visit( final ExecutionContext execContext ) throws SmooksException
	{
        final Object bean = BeanAccessor.getBean( execContext, beanId );
        if ( bean == null )
        {
        	throw new SmooksException( "A bean with id [" + beanId + "] was not found in the executionContext");
        }
        
		final String fileName = destinationDir.getAbsolutePath() + File.separator + generateFilePattern( bean );
		
		OutputStrategy outputStrategy = null;
		try
		{
    		outputStrategy = OutputStrategyFactory.getInstance().createStrategy( fileName, bean );
			outputStrategy.write( bean, encoding );
			outputStrategy.flush();
    		addFileToFileList( fileName, execContext );
		}
		catch (IOException e)
		{
    		final String errorMsg = "IOException while trying to append to file [" + destinationDir + "/" + fileNamePattern + "]";
    		throw new SmooksException( errorMsg, e );
		}
		finally
		{
			if ( outputStrategy != null )
				outputStrategy.close();
		}
	}
	
	private void addFileToFileList( final String transformedFileName, final ExecutionContext execContext ) throws IOException
	{
		String fileNamesList = FileListAccessor.getFileName( execContext );
		if ( fileNamesList == null )
		{
    		fileNamesList = transformedFileName + ".lst";
    		FileListAccessor.setFileName( fileNamesList, execContext );
		}
		
		FileWriter writer = null;
		try
		{
			writer = new FileWriter( fileNamesList, true );
			writer.write( transformedFileName + LINE_SEPARATOR );
		} 
		finally
		{
			if ( writer != null )
			{
				writer.close();
			}
		}
		
	}

}
