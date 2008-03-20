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
public class AbstractContainerPlugin
{
	private Smooks smooks;
	
	public AbstractContainerPlugin( final Smooks smooks )
	{
		this.smooks = smooks;
	}

	/**
	 * 	The process method does the actual Smooks filtering.
	 * 
	 * @param payload			- the payload that is to be filtered
	 * @return Object			- Object type specific to the current Container
	 * @throws SmooksException
	 */
	public final Object process( final Object payload, Result result, final ExecutionContext executionContext ) throws SmooksException
	{
		AssertArgument.isNotNull( payload, "payload" );
        
		Source source = SourceFactory.getInstance().createSource( payload );
		SourceResult sourceResult = new SourceResult( source, result ) ;
		return process ( sourceResult, executionContext );
	}
	
	public final Object process( final SourceResult sourceResult, final ExecutionContext executionContext ) throws SmooksException
	{
		AssertArgument.isNotNull( sourceResult, "sourceResult" );
        // Filter it through Smooks...
		Result result = sourceResult.getResult();
		
        smooks.filter( sourceResult.getSource(), result, executionContext );
        
		return packagePayload( result, executionContext );
	}
	
	/**
	 * 	Create an object that matches the Containers transport.
	 * 
	 * @param object
	 * @return Object	- Object that wraps the result from the transformation in 
	 * 					  Container specific manner.
	 */
	protected Object packagePayload( final Object object, final ExecutionContext execContext )
	{
		return object;
	}
	
}
