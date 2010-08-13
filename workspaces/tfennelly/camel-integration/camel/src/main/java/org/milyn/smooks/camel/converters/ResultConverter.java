/*
 * Milyn - Copyright (C) 2006 - 2010
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
package org.milyn.smooks.camel.converters;

import java.util.Map;

import org.apache.camel.Converter;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringResult;

/**
 * 
 * @author Daniel Bevenius
 *
 */
@Converter
public class ResultConverter
{
	private ResultConverter()
	{
	}

	@Converter
	public static Integer toInteger(JavaResult result)
	{
		return (Integer) getSingleObjectFromJavaResult(result);
	}
	
	@Converter
	public static Double toDouble(JavaResult result)
	{
		return (Double) getSingleObjectFromJavaResult(result);
	}
	
	@Converter
	public static String toString(StringResult result)
	{
		return result.getResult();
	}
	
	private static Object getSingleObjectFromJavaResult(JavaResult result)
	{
		Map<String, Object> resultMap = result.getResultMap();
		if(resultMap.size() == 1) 
		{
			return resultMap.values().iterator().next();
		}
		return null;
	}
}
