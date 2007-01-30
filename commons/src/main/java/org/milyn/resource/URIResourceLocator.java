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

package org.milyn.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.resource.ContainerResourceLocator;

/**
 * {@link java.net.URI} resource locator.
 * <p/>
 * Loads resources from a {@link java.net.URI} i.e. "file://", "http://", "classpath:/" etc.  
 * <p/>
 * Note, it adds support for referencing classpath based resources through a {@link java.net.URI} 
 * e.g. "classpath:/org/milyn/x/my-resource.xml" references a "/org/milyn/x/my-resource.xml" resource on the classpath.
 * "classpath" is in fact the default scheme for this class i.e. if no scheme is specified in the supplied URI,
 * this class defaults to treating the URI as a reference to a classpath based resource.
 * <p/>
 * All resource URIs are {@link URI#resolve(String) resolved} against a "base URI" which can be set through the 
 * System by the "org.milyn.resource.baseuri" System property.
 * 
 * @author tfennelly
 */
public class URIResourceLocator implements ContainerResourceLocator {

	/**
	 * Logger.
	 */
	private static final Log logger = LogFactory.getLog(URIResourceLocator.class);
	/**
	 * Scheme name for classpath based resources.
	 */
	public static String SCHEME_CLASSPATH = "classpath";
	/**
	 * System property key for the base URI.  Defaults to "./".
	 */
	public static final String BASE_URI_SYSKEY = "org.milyn.resource.baseuri";
	
	private URI baseURI = URI.create(System.getProperty(BASE_URI_SYSKEY, "./"));

	public InputStream getResource(String configName, String defaultUri) throws IllegalArgumentException, IOException {
		return getResource(defaultUri);
	}

	public InputStream getResource(String uri) throws IllegalArgumentException, IOException {
        return getResource(resolveURI(uri, baseURI));
	}

	private InputStream getResource(URI uri) throws IllegalArgumentException, IOException {
		URL url;
		String scheme = uri.getScheme();
		InputStream stream = null;
		
		if(scheme == null || scheme.equals(SCHEME_CLASSPATH)) {
			String path = uri.getPath();
			
			if(path == null) {
				throw new IllegalArgumentException("Unable to locate classpath resource [" + uri + "].  Resource path not specified in URI.");
			}
			
			if(!uri.isAbsolute()) {
				path = "/" + path;
			}
			stream = getClass().getResourceAsStream(path);
			if(stream == null) {
				logger.warn("Failed to access data stream for classpath resource [" + path + "].");
			}
		} else {
			url = uri.toURL();
			URLConnection connection = url.openConnection();
		
			stream = connection.getInputStream();
			if(stream == null) {
				logger.warn("Failed to access data stream for " + uri.getScheme() + " resource [" + uri + "].");
			}
		}
		
		return stream;
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
        	// Resolve the supplied URI against the baseURI...
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