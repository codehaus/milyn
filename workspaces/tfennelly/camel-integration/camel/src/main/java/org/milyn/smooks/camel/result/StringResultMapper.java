/*
	Milyn - Copyright (C) 2006 - 2010

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/
package org.milyn.smooks.camel.result;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.milyn.payload.StringResult;

/**
 * {@link String} result mapper.
 * <p/>
 * Used to map character based results from a {@link StreamResult} onto the
 * Camel {@link Exchange}. 
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class StringResultMapper implements ResultMapper {
	
	/* (non-Javadoc)
	 * @see org.milyn.smooks.camel.result.ResultMapper#getResult()
	 */
	public Result createResult() {
		return new StringResult();
	}

	/* (non-Javadoc)
	 * @see org.milyn.smooks.camel.result.ResultMapper#mapResult(org.apache.camel.Exchange)
	 */
	public Exchange mapResult(Result result, Exchange exchange) {
		Message outMessage = exchange.getOut();
		
		// Assume it should be null, but if it's not...
		if(outMessage == null) {
			outMessage = new DefaultMessage();
			exchange.setOut(outMessage);
		}
		outMessage.setBody(((StringResult)result).getResult());			
		
		return exchange;
	}
}
