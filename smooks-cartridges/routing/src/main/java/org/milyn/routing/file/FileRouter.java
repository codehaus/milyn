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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Initialize;
import org.milyn.cdr.annotation.VisitIf;
import org.milyn.cdr.annotation.VisitIfNot;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.javabean.BeanAccessor;
import org.milyn.routing.file.naming.DefaultNamingStrategy;
import org.milyn.routing.file.naming.NamingStrategy;
import org.w3c.dom.Element;

/**
 * <p/>
 * Router is a Visitor for DOM or SAX elements. It appends the content
 * to the configured destination file.
 * </p>
 * Example configuration:
 * <pre>
 * &lt;resource-config selector="orderItems"&gt;
 *    &lt;resource&gt;org.milyn.routing.file.FileRouter&lt;/resource&gt;
 *    &lt;param name="destinationDirectory">dir&lt;/param&gt;
 *    &lt;param name="destinationPrefix">prefix&lt;/param&gt;
 *    &lt;param name="destinationSuffix">suffix&lt;/param&gt;
 * &lt;/resource-config&gt;
 * </pre>
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 * @since 1.0
 *
 */
public class FileRouter implements DOMElementVisitor, SAXElementVisitor
{
	/**
	 * 	Key used in ExecutionContexts attribute map.
	 */
	public static final Object FILE_NAME_ATTR = "FileName";

	/*
	 * 	Logger
	 */
	private final Log log = LogFactory.getLog( FileRouter.class );

	/*
	 * 	Name of directory where files will be created.
	 */
	@ConfigParam ( name = "destinationDirectory", use = Use.REQUIRED )
	private String destDirName;

	/*
	 * 	File prefix for the created file
	 */
	@ConfigParam ( name = "destinationFilePrefix", use = Use.REQUIRED )
	private String destFilePrefix;

	/*
	 * 	File suffix for the created file
	 */
	@ConfigParam ( name = "destinationFileSuffix", use = Use.REQUIRED )
	private String destFileSuffix;

	/*
	 * 	BeanId is a key that is used to look up a bean
	 * 	in the execution context
	 */
    @ConfigParam( use = ConfigParam.Use.REQUIRED )
    private String beanId;

    /*
     * 	Writer that appends to file
     */
    //private BufferedWriter writer;

    /*
     * 	File object of the destination directory
     */
    private File destDir;

    /*
     * Naming strategy for generating the file pattern for output files.
     */
    private NamingStrategy namingStrategy = new DefaultNamingStrategy();

	@Initialize
	public void initialize()
	{
		destDir = new File ( destDirName );
    	if ( !destDir.exists() || !destDir.isDirectory() )
    	{
    		throw new SmooksException ( "Destination directory [" + destDirName + "] does not exist or is not a directory.");
    	}
	}

	//	Vistor methods

    @VisitIfNot(param = "visitBefore", value = "true", defaultVal = "true")
	public void visitAfter( final Element element, final ExecutionContext execContext ) throws SmooksException
	{
		visit( execContext );
	}

    @VisitIf(param = "visitBefore", value = "true", defaultVal = "true")
	public void visitBefore( final Element element, final ExecutionContext execContext ) throws SmooksException
	{
		visit( execContext );
	}

    @VisitIfNot(param = "visitBefore", value = "true", defaultVal = "true")
	public void visitAfter( final SAXElement saxElement, final ExecutionContext execContext ) throws SmooksException, IOException
	{
		visit( execContext );
	}

    @VisitIf(param = "visitBefore", value = "true", defaultVal = "true")
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

	/**
	 * Will delegate to the current NamingStrategy but it's also possible
	 * to directly sublass and override only this method.
	 *
	 * @param prefix
	 * @param suffix
	 * @return String	name of the file that will be used during this transform
	 */
	protected String generateFileName( final String prefix, final String suffix )
	{
    	return namingStrategy.generateFileName( destFilePrefix,  destFileSuffix );
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
		Writer writer = null;
    	final String filePattern = generateFilePattern();
		try
		{
        	final File file = new File( destDir, filePattern );
			writer = createFileWriter( file );
			execContext.setAttribute( FILE_NAME_ATTR, file.getAbsolutePath() );
            final Object bean = BeanAccessor.getBean( execContext, beanId );
            if ( bean == null )
            {
            	throw new SmooksException( "A bean with id [" + beanId + "] was not found in the executionContext");
            }

			writer.write( bean.toString() );
			writer.flush();
		}
		catch (IOException e)
		{
    		final String errorMsg = "IOException while trying to append to file [" + destDir + "/" + filePattern + "]";
    		throw new SmooksException( errorMsg, e );
		}
		finally
		{
			closeFileWriter( writer );
		}
	}

	private Writer createFileWriter( final File file )
	{
        try
		{
			return new BufferedWriter( new FileWriter( file, true));
		}
        catch (IOException e)
		{
    		final String errorMsg = "IOException while trying to create file [" + destFilePrefix + "]";
    		throw new SmooksConfigurationException( errorMsg, e );
		}
	}
	
	private String generateFilePattern()
	{
    	return namingStrategy.generateFileName( destFilePrefix,  destFileSuffix );
	}

	private void closeFileWriter( final Writer writer )
	{
    	log.debug( "Closing FileWriter");
		if ( writer != null )
		{
			try
			{
				writer.flush();
			}
			catch (IOException e)
			{
        		final String errorMsg = "IOException while trying to flush writer to file";
        		throw new SmooksConfigurationException( errorMsg, e );
			}

			try
			{
				writer.close();
			}
			catch (IOException e)
			{
        		final String errorMsg = "IOException while trying to close writer to  file";
        		throw new SmooksConfigurationException( errorMsg, e );
			}
		}
	}

}
