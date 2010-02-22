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

import org.apache.camel.Exchange;
import org.milyn.Smooks;

/**
 * Smooks Result to Camel {@link Exchange} mapper.
 * <p/>
 * Different types of result need to be mapped in different ways.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface ResultMapper {
	
	/**
	 * Get the {@link Result} instance to be used in the {@link Smooks#filterSource(javax.xml.transform.Source, Result...)}
	 * method call.
	 * 
	 * @return The Result instance.
	 */
	Result createResult();

	/**
	 * Map the result onto the Camel {@link Exchange}.
	 * @param result The result instance to be mapped. 
	 * @param exchange The exchange.
	 * @return The Exchange.
	 */
	Exchange mapResult(Result result, Exchange exchange);
}
