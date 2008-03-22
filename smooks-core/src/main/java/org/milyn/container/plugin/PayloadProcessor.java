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
import org.milyn.delivery.StringResult;

/**
 * PayloadProcessor is the class that application containers, for 
 * example ESBs, can use to simplify Smooks support.
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class PayloadProcessor
{
	private Smooks smooks;
	
	public PayloadProcessor( final Smooks smooks )
	{
		this.smooks = smooks;
	}

	/**
	 * The process method does the actual Smooks filtering.
	 *  
	 * @param payload			- the payload that is to be filtered. Can either be an Object (String, byte[],
	 * 							  Reader, InputStream) or an instance of SourceResult.
	 * @return Result			- javax.xml.transform.Result object, will either be the specified Result instance 
	 * 							  specified in the passed-in SourceResult, or StringResult.
	 * @throws SmooksException
	 */
	public final Result process( final Object payload, final ExecutionContext executionContext ) throws SmooksException
	{
		AssertArgument.isNotNull( payload, "payload" );
		
		Source source = null;
		Result result = new StringResult();
		
		if ( payload instanceof SourceResult )
		{
			SourceResult sourceResult = (SourceResult) payload;
			source = sourceResult.getSource();
			result = sourceResult.getResult();
		}
		else
		{
    		source = SourceFactory.getInstance().createSource( payload );
		}
		
        // Filter it through Smooks...
        smooks.filter( source, result, executionContext );
        
		return prepareResult( result, executionContext );
	}
	
	protected Result prepareResult( final Result result, ExecutionContext executionContext )
	{
		return result;
	}
}
