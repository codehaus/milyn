/*
	Milyn - Copyright (C) 2003

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

package org.milyn.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * URL stream org.milyn.resource locator.
 * @author tfennelly
 */
public class URLResourceLocator implements ExternalResourceLocator {

	/* (non-Javadoc)
	 * @see org.milyn.resource.ExternalResourceLocator#getResource(java.lang.String)
	 */
	public InputStream getResource(String url) throws IllegalArgumentException, IOException {
		if(url == null) {
			throw new IllegalArgumentException("null 'url' arg in method call.");
		} else if(url.trim().equals("")) {
			throw new IllegalArgumentException("empty 'url' arg in method call.");
		}
		System.out.println("URL: " + url);
		try {
	        URL resourceUrl = new URL(url);

	        return resourceUrl.openStream();
		} catch(MalformedURLException urlException) {
			IllegalArgumentException argException = new IllegalArgumentException("Malformed URL parameter [" + url + "].  See chained cause.");
			argException.initCause(urlException);
			throw argException;
		}
	}
}
