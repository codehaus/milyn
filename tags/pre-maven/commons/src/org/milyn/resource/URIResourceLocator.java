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
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.milyn.resource.ContainerResourceLocator;

/**
 * URI resource locator.
 * <p/>
 * Loads resources from a URI i.e. "file://", "http://" etc.
 * @author tfennelly
 */
public class URIResourceLocator implements ContainerResourceLocator {

	private URI baseURI = URI.create("./");

	public InputStream getResource(String configName, String defaultUri) throws IllegalArgumentException, IOException {
		return getResource(defaultUri);
	}

	public InputStream getResource(String uri) throws IllegalArgumentException, IOException {
        return getResource(resolveURI(uri, baseURI));
	}

	private InputStream getResource(URI uri) throws IllegalArgumentException, IOException {
		URL url = uri.toURL();
		URLConnection connection = url.openConnection();
		
		return connection.getInputStream();
	}
	
	/**
	 * Resolve the supplied uri against the suppled baseURI.
	 * <p/>
	 * Only resolved against the base URI if 'uri' is not absolute.
	 * @param uri URI to be resolved.
	 * @param baseURI The URI against which the 'uri' parameter is to be resolved.
	 * @return The resolved URI.
	 */
	public static URI resolveURI(String uri, URI baseURI) {
		URI uriObj;
		
		if(uri == null || uri.trim().equals("")) {
        	throw new IllegalArgumentException("null or empty 'uri' paramater in method call.");
        }
        
        if(uri.charAt(0) == '\\' || uri.charAt(0) == '/') {
        	uri = uri.substring(1);
        }
        uriObj = URI.create(uri);
        if(!uriObj.isAbsolute()) {
        	// Resolve the supplied URI against the baseURI (derived from the baseDir!).
			uriObj = baseURI.resolve(uriObj);
        }
        
		return uriObj;
	}

	/**
	 * Allows overriding of the baseURI (current dir).
	 * @param baseURI New baseURI.
	 */
	public void setBaseURI(URI baseURI) {
		if(baseURI == null) {
			throw new IllegalArgumentException("null 'baseURI' arg in method call.");
		}
		String baseURIString = baseURI.toString();
		char lastChar = baseURIString.charAt(baseURIString.length() - 1);
		
		// Make sure the base URI refers to a directory
		if(lastChar != '/' && lastChar != '\\') {
			this.baseURI = URI.create(baseURIString + '/');
		} else {
			this.baseURI = baseURI;
		}
	}
}