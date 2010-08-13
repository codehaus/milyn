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
package org.milyn.smooks.camel.dataformat;

import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;

/**
 * A SmooksMapper that maps the Camel input body to an {@link InputStream}
 * and the Camel output to a {@link DOMResult}.
 * </p> 
 * 
 * @author Daniel Bevenius
 */
public class StreamInDomOutMapper implements SmooksMapper
{
	public StreamSource createSource(Exchange exchange)
	{
		return new StreamSource(exchange.getIn().getBody(InputStream.class));
	}
	
	public DOMResult createResult()
	{
		return new DOMResult();
	}
	
	public void mapResult(Result result, Exchange exchange)
	{
		exchange.getOut().setBody(((DOMResult) result).getNode());
	}
}
