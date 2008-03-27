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
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ExecutionLifecycleCleanable;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
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
 *    &lt;param name="resourcename"&gt;resourceName&lt;&lt;/param&gt;
 * &lt;/resource-config&gt;
 * </pre>
 * 
 * Description of configuration properties:
 * <ul>
 * <li><code>resource </code> should be a concreate implementation of this class
 * <li><code>resourcename </code> the name of this resouce. Will be used to identify this resource
 * </ul>
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public abstract class AbstractOutputStreamResource implements SAXElementVisitor, DOMElementVisitor, ExecutionLifecycleCleanable 
{
	Log log = LogFactory.getLog( AbstractOutputStreamResource.class );
	
    private static final String RESOURCE_CONTEXT_KEY_PREFIX = AbstractOutputStreamResource.class.getName() + "#outputresource:";
    
    private static final String OUTPUTSTREAM_CONTEXT_KEY_PREFIX = AbstractOutputStreamResource.class.getName() + "#outputstream:";
    
	//	public
	
	/**
	 * Retrieve/create an output stream that is appropriate for the concreate implementation
	 * 
	 * @param <code>executionContext</code>
	 * @param <code>beanId</code>
	 * @return <code>OutputStream</code>	output stream specific to the concreate implementation
	 */
	public abstract OutputStream getOutputStream( final ExecutionContext executionContext, final String beanId ) throws IOException;
	
	/**
	 * Return the name of this resource
	 * 
	 * @return <code>String</code>	- the name of the resource
	 */
	public abstract String getResourceName();

	public void visitBefore( final SAXElement element, final ExecutionContext executionContext ) throws SmooksException, IOException
	{
		bind ( executionContext );
	}

	public void visitAfter( final SAXElement element, final ExecutionContext executionContext ) throws SmooksException, IOException
	{
		unbind( executionContext );
	}

	public void onChildElement( final SAXElement element, final SAXElement childElement, final ExecutionContext executionContext ) throws SmooksException, IOException
	{
		//	NoOp
	}

	public void onChildText( final SAXElement element, final SAXText childText, final ExecutionContext executionContext ) throws SmooksException, IOException
	{
		//	NoOp
	}

	public void visitBefore( final Element element, final ExecutionContext executionContext ) throws SmooksException
	{
		bind ( executionContext );
	}

	public void visitAfter( final Element element, final ExecutionContext executionContext ) throws SmooksException
	{
		unbind( executionContext );
	}
	
	public void executeExecutionLifecycleCleanup( ExecutionContext executionContext )
	{
		//unbind( executionContext );
	}
	
    public static OutputStream getOutputStream(  
    		final String resourceName, 
            final ExecutionContext executionContext,
            final String beanId) throws SmooksException 
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
                outputStream = resource.getOutputStream( executionContext, beanId );
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
	 * Hook for subclasses to perform operations before the attributes
	 * of this class are removed from the execution context.
	 * 
	 * @param <code>executionContext</code>	- Smooks ExecutionContext
	 */ 
    protected void preUnbindFromExecutionContext ( final ExecutionContext executionContext )
	{
		//	NoOp
	}
	
	//	private
	
	private void unbind( final ExecutionContext executionContext )
	{
		try 
		{
            OutputStream output = (OutputStream) executionContext.getAttribute( OUTPUTSTREAM_CONTEXT_KEY_PREFIX + getResourceName() );
            close( output );
		}
		finally
		{
    		preUnbindFromExecutionContext( executionContext );
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
