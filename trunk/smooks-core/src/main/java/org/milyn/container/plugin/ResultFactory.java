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

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import org.milyn.delivery.java.JavaResult;

/**
 * Factory for javax.xml.transform.Result objects.
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class ResultFactory
{
	private static ResultFactory factory = new ResultFactory();
	
	private ResultFactory() {} 
	
	public static ResultFactory getInstance()
	{
		return factory;
	}
	
	public Result createResult( final ResultType type )
	{
    	Result result = null;
		switch ( type )
		{
		case STRING:
            result = new StreamResult( new StringWriter() );
			break;
		case BYTES:
            result = new StreamResult( new ByteArrayOutputStream() );
			break;
		case JAVA:
            result = new JavaResult();
			break;
		case NOMAP:
			break;

		default:
			result = null;
			break;
		}
		
		return result;
	}

	public Object mapResultToObject( Result result, final ResultType resultType, final String beanId )
	{
        Object retObject = null;
        switch ( resultType )
        {
            case STRING:
                final StreamResult strResult = (StreamResult) result;
                retObject = strResult.getWriter().toString();
                break;
            case BYTES:
                final StreamResult byteArrayResult = (StreamResult) result;
                retObject = byteArrayResult.getOutputStream();
                break;
			case JAVA:
				final JavaResult javaResult = (JavaResult) result;
				retObject = beanId == null ?  javaResult.getResultMap() : javaResult.getBean( beanId );
				break;
			case NOMAP:
				retObject = result;
				break;
        }
        return retObject;
	}

}
