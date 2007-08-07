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
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.resource.ContainerResourceLocator;
import org.milyn.util.ClassUtil;

/**
 * {@link java.net.URI} resource locator.
 * <p/>
 * Loads resources from a {@link java.net.URI} i.e. "file://", "http://", "classpath:/" etc. <p/> Note,
 * it adds support for referencing classpath based resources through a
 * {@link java.net.URI} e.g. "classpath:/org/milyn/x/my-resource.xml" references
 * a "/org/milyn/x/my-resource.xml" resource on the classpath.
 * <p/>
 * This class resolves resources based on whether or not the requested resource {@link URI} has
 * a URI scheme specified.  If it has a scheme, it simply resolves the resource by creating a
 * {@link URL} instance from the URI and opening a stream on that URL.  If the URI doesn't have a scheme,
 * this class will attempt to resolve the resource against the local filesystem and classpath
 * (in that order).  In all cases (scheme or no scheme), the resource URI is first resolved
 * against base URI, with the resulting URI being the one that's used.
 * <p/>
 * As already stated, all resource URIs are
 * {@link URI#resolve(String) resolved} against a "base URI".  This base URI can be set through the
 * {@link #setBaseURI(java.net.URI)} method, or via the System property "org.milyn.resource.baseuri".
 * The default base URI is simply "./", which has no effect on the input URI when resolved against it.
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
     * System property key for the base URI. Defaults to "./".
     */
    public static final String BASE_URI_SYSKEY = "org.milyn.resource.baseuri";

    private URI baseURI = URI.create(System.getProperty(BASE_URI_SYSKEY, "./"));

    public InputStream getResource(String configName, String defaultUri)
            throws IllegalArgumentException, IOException {
        return getResource(defaultUri);
    }

    public InputStream getResource(String uri) throws IllegalArgumentException, IOException {
        return getResource(resolveURI(uri));
    }

    private InputStream getResource(URI uri) throws IllegalArgumentException, IOException {
        URL url;
        String scheme = uri.getScheme();
        InputStream stream = null;

        if (scheme == null || scheme.equals(SCHEME_CLASSPATH)) {
            String path = uri.getPath();

            if (path == null) {
                throw new IllegalArgumentException("Unable to locate resource [" + uri +
                        "].  Resource path not specified in URI.");
            }

            // Try the filesystem first (only if there's no scheme), then try the classpath...
            File file = new File(path);
            if (scheme == null && file.exists()) {
                stream = new FileInputStream(file);
            } else {
                if (!uri.isAbsolute()) {
                    path = "/" + path;
                }
                stream = ClassUtil.getResourceAsStream(path, getClass());
            }

            if (stream == null) {
                throw new IOException("Failed to access data stream for resource [" + path + "]. No scheme specified. Tried filesystem and classpath.");
            }
        } else {
            url = uri.toURL();
            URLConnection connection = url.openConnection();

            stream = connection.getInputStream();
            if (stream == null) {
                throw new IOException("Failed to access data stream for " + uri.getScheme() + " resource [" + uri + "].");
            }
        }

        return stream;
    }

    /**
     * Resolve the supplied uri against the baseURI.
     * <p/>
     * Only resolved against the base URI if 'uri' is not absolute.
     *
     * @param uri URI to be resolved.
     * @return The resolved URI.
     */
    public URI resolveURI(String uri) {
        URI uriObj;

        if (uri == null || uri.trim().equals("")) {
            throw new IllegalArgumentException(
                    "null or empty 'uri' paramater in method call.");
        }

        if (uri.charAt(0) == '\\' || uri.charAt(0) == '/') {
            uri = uri.substring(1);
        }
        uriObj = URI.create(uri);
        if (!uriObj.isAbsolute()) {
            // Resolve the supplied URI against the baseURI...
            uriObj = baseURI.resolve(uriObj);
        }

        return uriObj;
    }

    /**
     * Allows overriding of the baseURI (current dir).
     *
     * @param baseURI New baseURI.
     */
    public void setBaseURI(URI baseURI) {
        if (baseURI == null) {
            throw new IllegalArgumentException(
                    "null 'baseURI' arg in method call.");
        }
        String baseURIString = baseURI.toString();
        char lastChar = baseURIString.charAt(baseURIString.length() - 1);

        // Make sure the base URI refers to a directory
        if (lastChar != '/' && lastChar != '\\') {
            this.baseURI = URI.create(baseURIString + '/');
        } else {
            this.baseURI = baseURI;
		}
	}
}
