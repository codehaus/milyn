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

package org.milyn.container.plugin;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;

/**
 * AbstractContainerPlugin is the class that application containers, for 
 * example ESBs, should extend to provide Smooks support.
 * </p>
 * Usage:
 * <pre>
 * public class ContainerPlugin extends AbstractContainerPlugin
 * {
 *	@Override
 *	protected Object packagePayload( Object resultObject, ExecutionContext executionContext )
 *	{
 *		return resultObject;
 *	}
 * } 
 * </pre>	
 * The method packagePayload gives the container a chance to enrich the resultObject in
 * any way suitable. This is why the ExceutionContext is passed into this method.
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public abstract class AbstractContainerPlugin
{
	private String mapBeanId;
	
	private Smooks smooks;

	/**
	 * 	The process method does the actual Smooks filtering.
	 * 
	 * @param payload			- the payload that is to be filtered
	 * @param resultType		- one of {@link ResultType}
	 * @return Object			- Object type specific to the current Container
	 * @throws SmooksException
	 */
	public final Object process( final Object payload, ResultType resultType, final ExecutionContext executionContext ) throws SmooksException
	{
        if(payload == null) 
        {
            return null;
        }
        
		Source source = null;
        Result result = null;
        
        //	use Source and Result from SourceResult if applicable.
		if ( payload instanceof SourceResult )
		{
			SourceResult sourceResult = (SourceResult) payload;
    		source = sourceResult.getSource();
			result = sourceResult.getResult();
			resultType = ResultType.NOMAP;
		}
		else
		{
            // Configure the source...
    		source = SourceFactory.getInstance().createSource( payload );
    		// Configure the result...
            result = ResultFactory.getInstance().createResult( resultType );
		}

        // Filter it through Smooks...
        smooks.filter( source, result, executionContext );
        
        // Map the result back into the message...
        Object retObject = ResultFactory.getInstance().mapResultToObject( result, resultType, mapBeanId );
        
		return packagePayload( retObject, executionContext );
		
	}
	
	/**
	 * 	Create an object that matches the Containers transport.
	 * 
	 * @param object
	 * @return Object	- Object that wraps the result from the transformation in 
	 * 					  Container specific manner.
	 */
	protected abstract Object packagePayload( final Object object, final ExecutionContext execContext );
	
	/**
	 * 	Set the Smooks instance. Different containers will have 
	 * 	different ways on managing Smooks instances.
	 * 
	 * @param smooks			- Smooks instance
	 * @param executionContext	- Smooks ExecutionContext
	 */
	public void setSmooksInstance( final Smooks smooks ) throws SmooksException
	{
		AssertArgument.isNotNull( smooks, "smooks" );
		this.smooks = smooks;
	}

	public String getMapBeanId()
	{
		return mapBeanId;
	}

	public void setMapBeanId( final String mapBeanId )
	{
		this.mapBeanId = mapBeanId;
	}
	
}
