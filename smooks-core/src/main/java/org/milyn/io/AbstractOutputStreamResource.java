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

package org.milyn.io;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ExecutionLifecycleCleanable;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.*;
import org.w3c.dom.Element;

/**
 * AbstractOuputStreamResource is the base class for handling output stream 
 * resources in Smooks.
 * <p/>
 * 
 * Example configuration:
 * <pre>
 * &lt;resource-config selector="$document"&gt;
 *    &lt;resource&gt;org.milyn.io.ConcreateImpl&lt;/resource&gt;
 *    &lt;param name="resourceName"&gt;resourceName&lt;/param&gt;
 * &lt;/resource-config&gt;
 * </pre>
 * 
 * Description of configuration properties:
 * <ul>
 * <li><code>resource </code> should be a concreate implementation of this class
 * <li><code>resourceName </code> the name of this resouce. Will be used to identify this resource
 * </ul>
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public abstract class AbstractOutputStreamResource implements SAXVisitBefore, SAXVisitAfter, DOMElementVisitor, ExecutionLifecycleCleanable 
{
	Log log = LogFactory.getLog( AbstractOutputStreamResource.class );
	
    private static final String RESOURCE_CONTEXT_KEY_PREFIX = AbstractOutputStreamResource.class.getName() + "#outputresource:";
    
    private static final String OUTPUTSTREAM_CONTEXT_KEY_PREFIX = AbstractOutputStreamResource.class.getName() + "#outputstream:";
    
    @ConfigParam
    private String resourceName;

	//	public
	
	/**
	 * Retrieve/create an output stream that is appropriate for the concreate implementation
	 * 
	 * @param executionContext Execution Context.
	 * @return OutputStream specific to the concreate implementation
	 */
	public abstract OutputStream getOutputStream( final ExecutionContext executionContext ) throws IOException;
	
	/**
	 * Return the name of this resource
	 * 
	 * @return <code>String</code>	- the name of the resource
	 */
	public String getResourceName() {
        return resourceName;
    }

	public void visitBefore( final SAXElement element, final ExecutionContext executionContext ) throws SmooksException, IOException
	{
		bind ( executionContext );
	}

	public void visitAfter( final SAXElement element, final ExecutionContext executionContext ) throws SmooksException, IOException
	{
		closeResource( executionContext );
	}

	public void visitBefore( final Element element, final ExecutionContext executionContext ) throws SmooksException
	{
		bind ( executionContext );
	}

	public void visitAfter( final Element element, final ExecutionContext executionContext ) throws SmooksException
	{
		closeResource( executionContext );
	}
	
	public void executeExecutionLifecycleCleanup( ExecutionContext executionContext )
	{
		closeResource( executionContext );
	}
	
    public static OutputStream getOutputStream(
    		final String resourceName,
            final ExecutionContext executionContext) throws SmooksException 
    {
        OutputStream outputStream = (OutputStream) executionContext.getAttribute( OUTPUTSTREAM_CONTEXT_KEY_PREFIX + resourceName );
        
        if( outputStream == null ) 
        {
            AbstractOutputStreamResource resource = (AbstractOutputStreamResource) executionContext.getAttribute( RESOURCE_CONTEXT_KEY_PREFIX + resourceName );
        
            if( resource == null ) 
            {
                throw new SmooksException( "OutputResource '" + resourceName + "' not bound to context.  Configure an '" + AbstractOutputStreamResource.class.getName() +  "' implementation and target it at '$document'." );
            }

            try 
            {
                outputStream = resource.getOutputStream( executionContext );
            } 
            catch ( IOException e ) 
            {
                throw new SmooksException( "Unable to set outputstream for '" + resourceName + "'.", e );
            }
            executionContext.setAttribute( OUTPUTSTREAM_CONTEXT_KEY_PREFIX + resourceName, outputStream );
        }

        return outputStream;
    }

	 
	/**
	 * Close the resource output stream.
     * <p/>
     * Classes overriding this method must call super on this method. This will
     * probably need to be done before performing any aditional cleanup.
	 * 
	 * @param executionContext Smooks ExecutionContext
	 */ 
    protected void closeResource( final ExecutionContext executionContext )
	{
		try 
		{
            OutputStream output = (OutputStream) executionContext.getAttribute( OUTPUTSTREAM_CONTEXT_KEY_PREFIX + getResourceName() );
            close( output );
		}
		finally
		{
            executionContext.removeAttribute( OUTPUTSTREAM_CONTEXT_KEY_PREFIX + getResourceName() );
            executionContext.removeAttribute( RESOURCE_CONTEXT_KEY_PREFIX + getResourceName() );
		}
	}

	private void bind( final ExecutionContext executionContext )
	{
        executionContext.setAttribute( RESOURCE_CONTEXT_KEY_PREFIX + getResourceName(), this );
	}
	
	private void close( final OutputStream outputStream )
	{
		if ( outputStream == null )
		{
			return;
		}
		try
		{
			outputStream.flush();
		} 
		catch (IOException e)
		{
			log.error( "IOException while trying to flush output stream : ", e );
		}
		try
		{
			outputStream.close();
		} 
		catch (IOException e)
		{
			log.error( "IOException while trying to close output stream : ", e );
		}
	}

}
