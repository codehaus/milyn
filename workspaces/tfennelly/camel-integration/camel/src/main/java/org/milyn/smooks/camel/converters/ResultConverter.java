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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringResult;
import org.w3c.dom.Node;

/**
 * ResultConverter converts from different {@link Result} types. 
 *  
 * @author Daniel Bevenius
 */
@Converter
public class ResultConverter
{
	private ResultConverter()
	{
	}
	
	@Converter
	public static Node toDocument(DOMResult domResult)
	{
		return domResult.getNode();
	}
	
	@SuppressWarnings("rawtypes")
	@Converter
	public static List toList(JavaResult javaResult, Exchange exchange)
	{
        String resultKey = (String) exchange.getProperty("SmooksDataFormatKeys");
		List list = (List) getResultsFromJavaResult(javaResult, resultKey);
		return list;
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
	
	@SuppressWarnings("rawtypes")
	@Converter
	public static List toString(JavaResult result)
	{
		return (List) getSingleObjectFromJavaResult(result);
	}
	
	@Converter
	public static String toString(StringResult result)
	{
		return result.getResult();
	}
	
	@SuppressWarnings("rawtypes")
	@Converter
	public static Map toMap(JavaResult result, Exchange exchange)
	{
		Message outMessage = exchange.getOut();
		Map<String, Object> resultBeans = result.getResultMap();
		outMessage.setBody(resultBeans);
		
		Set<Entry<String, Object>> entrySet = resultBeans.entrySet();
		for (Entry<String, Object> entry : entrySet)
		{
			outMessage.setBody(entry.getValue(), entry.getValue().getClass());
		}
		return resultBeans;
	}
	
	private static Object getResultsFromJavaResult(JavaResult result, String resultKey)
	{
		Map<String, Object> resultMap = result.getResultMap();
		return resultMap.get(resultKey);
	}
	
	private static Object getSingleObjectFromJavaResult(JavaResult result)
	{
		Map<String, Object> resultMap = result.getResultMap();
		if(resultMap.size() == 1) 
		{
			return resultMap.values().iterator().next();
		}
		else
		{
			return resultMap.get("result");
		}
	}
}