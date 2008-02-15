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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import org.apache.log4j.Logger;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Initialize;
import org.milyn.cdr.annotation.Uninitialize;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.routing.jms.message.creationstrategies.util.TransformUtil;
import org.w3c.dom.Element;

/**
 * <p/>
 * Router is a Visitor for DOM or SAX elements. It appends the content
 * to the configured destination file.
 * </p>
 * Example configuration:
 * <pre>
 * &lt;resource-config selector="orderItems"&gt;
 *    &lt;resource&gt;org.milyn.routing.file.Router&lt;/resource&gt;
 *    &lt;param name="destinationFile">fileName&lt;/param&gt;
 * &lt;/resource-config&gt;
 *	....
 * Optional parameters:
 *    &lt;param name="executeBefore"&gt;true&lt;/param&gt;
 *    &lt;param name="omitXmlDeclaration"&gt;yes&lt;/param&gt;
 * </pre>
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 * @since 1.0
 *
 */
public class Router implements DOMElementVisitor, SAXElementVisitor
{
	private Logger log = Logger.getLogger( Router.class );

    @SuppressWarnings("unused")
	@ConfigParam ( use = Use.REQUIRED )
	private String destinationFile;

    @ConfigParam(defaultVal = "false")
    private boolean visitBefore;

    @ConfigParam(defaultVal = "yes", choice = { "yes", "no" } )
    private String omitXmlDeclaration;

    private BufferedWriter writer;

    private Properties outputProperties;

	@Initialize
	public void initialize()
	{
        try
		{
			writer = new BufferedWriter( new FileWriter( destinationFile, true));
			outputProperties = new Properties();
			outputProperties.put( OutputKeys.OMIT_XML_DECLARATION, omitXmlDeclaration );
		}
        catch (IOException e)
		{
    		final String errorMsg = "IOException while trying to create file [" + destinationFile + "]";
    		log.error( errorMsg, e );
    		throw new SmooksConfigurationException( errorMsg, e );
		}
	}

	@Uninitialize
	public void uninitialize()
	{
		if ( writer != null )
		{
			try
			{
				writer.flush();
				writer.close();
			}
			catch (IOException e)
			{
        		final String errorMsg = "IOException while trying to close writer to  file [" + destinationFile + "]";
        		log.error( errorMsg, e );
        		throw new SmooksConfigurationException( errorMsg, e );
			}
		}
	}

	public void setDestinationFile( final String destinationFile )
	{
		this.destinationFile = destinationFile;
	}

	public void visitAfter( Element element, ExecutionContext execContext ) throws SmooksException
	{
		if ( !visitBefore )
			visitDOM( element, execContext );
	}

	public void visitBefore( Element element, ExecutionContext execContext ) throws SmooksException
	{
		if ( visitBefore )
			visitDOM( element, execContext );
	}

	public void onChildElement( SAXElement arg0, SAXElement arg1, ExecutionContext arg2 ) throws SmooksException, IOException
	{
	}

	public void onChildText( SAXElement arg0, SAXText arg1, ExecutionContext arg2 ) throws SmooksException, IOException
	{
	}

	public void visitAfter( SAXElement arg0, ExecutionContext arg1 ) throws SmooksException, IOException
	{
	}

	public void visitBefore( SAXElement arg0, ExecutionContext arg1 ) throws SmooksException, IOException
	{
	}

	public String getDestinationFile()
	{
		return destinationFile;
	}

	private void visitDOM( Element element, ExecutionContext execContext ) throws SmooksException
	{
		try
		{
			execContext.setAttribute( "fileName", destinationFile );
			final String text = TransformUtil.transformElement( element, outputProperties );
			log.debug( "Will write : " + text + " to file : " + destinationFile  );
			writer.write( text );
			writer.flush();
		}
		catch (IOException e)
		{
    		final String errorMsg = "IOException while trying to append to file [" + destinationFile + "]";
    		log.error( errorMsg, e );
    		throw new SmooksException( errorMsg, e );
		}
	}

}
