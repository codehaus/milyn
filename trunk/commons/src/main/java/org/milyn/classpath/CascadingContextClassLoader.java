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
package org.milyn.classpath;

import org.milyn.assertion.AssertArgument;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Cascading Context ClassLoader.
 * <p/>
 * Installs this ClassLoader instance as the Context ClassLoader. When asked to load
 * a Class or resource, it first delegates to the "new" ClassLoader (supplied in the constructor)
 * and then delegates to the original Context ClassLoader.
 * <p/>
 * Call {@link #resetContextClassLoader()} to reinstall the original Context ClassLoader.
 *
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class CascadingContextClassLoader extends ClassLoader {

    private ClassLoader newContextClassLoader;
    private ClassLoader originalContextClassLoader;

    public CascadingContextClassLoader(ClassLoader newContextClassLoader) {
        AssertArgument.isNotNull(newContextClassLoader, "newContextClassLoader");
        this.newContextClassLoader = newContextClassLoader; 
        originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this);
    }

    /**
     * Reinstall the original Context ClassLoader.
     */
    public void resetContextClassLoader() {
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> clazz = newContextClassLoader.loadClass(name);
        if(clazz == null) {
            clazz = originalContextClassLoader.loadClass(name);
        }
        return clazz;
    }

    public URL getResource(String name) {
        URL resource = newContextClassLoader.getResource(name);
        if(resource == null) {
            resource = originalContextClassLoader.getResource(name);
        }
        return resource;
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        // Just using the new Context ClassLoader for this.  Should we merge in the
        // results from the original Context ClassLoader??
        return newContextClassLoader.getResources(name);
    }

    public InputStream getResourceAsStream(String name) {
        InputStream stream = newContextClassLoader.getResourceAsStream(name);
        if(stream == null) {
            stream = originalContextClassLoader.getResourceAsStream(name);
        }
        return stream;
    }

    public synchronized void setDefaultAssertionStatus(boolean enabled) {
        newContextClassLoader.setDefaultAssertionStatus(enabled);
        originalContextClassLoader.setDefaultAssertionStatus(enabled);
    }

    public synchronized void setPackageAssertionStatus(String packageName, boolean enabled) {
        newContextClassLoader.setPackageAssertionStatus(packageName, enabled);
        originalContextClassLoader.setPackageAssertionStatus(packageName, enabled);
    }

    public synchronized void setClassAssertionStatus(String className, boolean enabled) {
        newContextClassLoader.setClassAssertionStatus(className, enabled);
        originalContextClassLoader.setClassAssertionStatus(className, enabled);
    }

    public synchronized void clearAssertionStatus() {
        newContextClassLoader.clearAssertionStatus();
        originalContextClassLoader.clearAssertionStatus();
    }
}
