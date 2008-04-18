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

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.ByteResult;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringResult;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

/**
 * Processor class for an abstract payload type.
 * <p/>
 * This class can be used to ease Smooks integration with application
 * containers (for example ESBs).  It works out how to filter the supplied Object payload
 * through Smooks, to produce the desired {@link ResultType result type}.
 * <p/>
 * The "payload" object supplied to the {@link #process(Object, org.milyn.container.ExecutionContext)}
 * method can be one of type:
 * <ul>
 *  <li>{@link String},</li>
 *  <li>{@link Byte} array,</li>
 *  <li>{@link java.io.Reader},</li>
 *  <li>{@link java.io.InputStream},</li>
 *  <li>{@link Source},</li>
 *  <li>{@link SourceResult}, or</li>
 *  <li>any Java user type (gets wrapped in a {@link org.milyn.payload.JavaSource}).</li>
 * </ul>
 *
 * The {@link SourceResult} payload type allows full control over the filter
 * {@link Source} and {@link Result}.
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class PayloadProcessor
{
	private Smooks smooks;
    private ResultType resultType;
    private String javaResultBeanId;

    public PayloadProcessor( final Smooks smooks, final ResultType resultType )
	{
        AssertArgument.isNotNull(smooks, "smooks");
        AssertArgument.isNotNull(resultType, "resultType");
        this.smooks = smooks;
        this.resultType = resultType;
    }

    public void setJavaResultBeanId(final String javaResultBeanId) {
        AssertArgument.isNotNullAndNotEmpty(javaResultBeanId, "javaResultBeanId");
        this.javaResultBeanId = javaResultBeanId;
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
	public final Object process( final Object payload, final ExecutionContext executionContext ) throws SmooksException
	{
		AssertArgument.isNotNull( payload, "payload" );
		
		Source source;
		Result result;
		
		if ( payload instanceof SourceResult )
		{
			SourceResult sourceResult = (SourceResult) payload;
			source = sourceResult.getSource();
			result = sourceResult.getResult();
		}
		else
		{
    		source = SourceFactory.getInstance().createSource( payload );
            result = ResultFactory.getInstance().createResult(resultType);
        }
		
        // Filter it through Smooks...
        smooks.filter( source, result, executionContext );

        // Extract the result...
        if(result instanceof JavaResult) {
            if(javaResultBeanId != null) {
                return ((JavaResult)result).getResultMap().get(javaResultBeanId);
            } else {
                return ((JavaResult)result).getResultMap();
            }
        } else if(result instanceof StringResult) {
            return ((StringResult)result).getResult();
        } else if(result instanceof ByteResult) {
            return ((ByteResult)result).getResult();
        }

        return result;
	}
}
