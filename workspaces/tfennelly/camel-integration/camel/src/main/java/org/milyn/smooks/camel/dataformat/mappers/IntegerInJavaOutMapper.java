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
package org.milyn.smooks.camel.dataformat.mappers;

import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.apache.camel.Exchange;
import org.milyn.payload.JavaResult;
import org.milyn.payload.JavaSource;
import org.milyn.smooks.camel.dataformat.SmooksMapper;

/**
 * A SmooksMapper that maps the Camel input body to an {@link InputStream}
 * and the Camel output to a {@link DOMResult}.
 * </p> 
 * 
 * @author Daniel Bevenius
 */
public class IntegerInJavaOutMapper implements SmooksMapper
{
	public JavaSource createSource(Exchange exchange)
	{
		Integer integer = exchange.getIn().getBody(Integer.class);
		return new JavaSource(integer);
	}
	
	public JavaResult createResult()
	{
		return new JavaResult();
	}
	
	public void mapResult(Result result, Exchange exchange)
	{
		exchange.getOut().setBody(((JavaResult) result));
	}
}
