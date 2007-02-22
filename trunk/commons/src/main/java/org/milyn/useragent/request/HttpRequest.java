/*
 Milyn - Copyright (C) 2006

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

package org.milyn.useragent.request;

/**
 * Http interface definition. <p/> Definition of access to the HTTP request
 * attributes - namely the request headers and parameters. <p/> Method
 * signatures are based on the servlet spec HttpServletRequest class. <p/>
 * 
 * @author Tom Fennelly
 */

public interface HttpRequest extends Request {

	/**
	 * Get the named HTTP request header.
	 * 
	 * @param name
	 *            The request header name.
	 * @return The value of the header with the specified name, or null if the
	 *         header isn't present in the request.
	 */
	public String getHeader(String name);

	/**
	 * Get the named HTTP request parameter from the request query string.
	 * 
	 * @param name
	 *            The name of the required request parameter.
	 * @return The value of the request parameter, or null if the parameter
	 *         isn't present in the request.
	 */
	public String getParameter(String name);
}
