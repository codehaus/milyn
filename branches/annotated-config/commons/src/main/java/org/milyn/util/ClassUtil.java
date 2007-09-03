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
package org.milyn.util;

import org.milyn.classpath.InstanceOfFilter;
import org.milyn.classpath.Filter;
import org.milyn.classpath.Scanner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

/**
 * Utility methods to aid in class/resource loading.
 * 
 * @author kevin
 */
public class ClassUtil {
    
    private static Log logger = LogFactory.getLog(ClassUtil.class);
    
    /**
	 * Load the specified class.
	 * 
	 * @param className
	 *            The name of the class to load.
	 * @param caller
	 *            The class of the caller.
	 * @return The specified class.
	 * @throws ClassNotFoundException
	 *             If the class cannot be found.
	 */
	public static Class forName(final String className, final Class caller) throws ClassNotFoundException {
		final ClassLoader threadClassLoader = Thread.currentThread()
				.getContextClassLoader();
		if (threadClassLoader != null) {
			try {
				return threadClassLoader.loadClass(className);
			} catch (final ClassNotFoundException cnfe) {
			} // ignore
		}

		final ClassLoader classLoader = caller.getClassLoader();
		if (classLoader != null) {
			try {
				return classLoader.loadClass(className);
			} catch (final ClassNotFoundException cnfe) {
			} // ignore
		}

		return Class.forName(className, true, ClassLoader
				.getSystemClassLoader());
	}

	/**
	 * Get the specified resource as a stream.
	 * 
	 * @param resourceName
	 *            The name of the class to load.
	 * @param caller
	 *            The class of the caller.
	 * @return The input stream for the resource or null if not found.
	 */
	public static InputStream getResourceAsStream(final String resourceName, final Class caller) {
		final String resource;
		if (resourceName.startsWith("/")) {
			resource = resourceName.substring(1);
		} else {
			final Package callerPackage = caller.getPackage();
			if (callerPackage != null) {
				resource = callerPackage.getName().replace('.', '/') + '/'
						+ resourceName;
			} else {
				resource = resourceName;
			}
		}
		final ClassLoader threadClassLoader = Thread.currentThread()
				.getContextClassLoader();
		if (threadClassLoader != null) {
			final InputStream is = threadClassLoader
					.getResourceAsStream(resource);
			if (is != null) {
				return is;
			}
		}

		final ClassLoader classLoader = caller.getClassLoader();
		if (classLoader != null) {
			final InputStream is = classLoader.getResourceAsStream(resource);
			if (is != null) {
				return is;
			}
		}

		return ClassLoader.getSystemResourceAsStream(resource);
	}

    public static List<Class> findInstancesOf(final Class type) {
        InstanceOfFilter filter = new InstanceOfFilter(type);
        Scanner scanner = new Scanner(filter);

        try {
            scanner.scanClasspath(Thread.currentThread().getContextClassLoader());
            logger.info("Scanned classpath for instances of '" + type.getName() + "'.  Found " + filter.getClasses().size() + " matches.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to search classspath for instances of '" + type.getName() + "'.", e);
        }

        return filter.getClasses();
    }
}
