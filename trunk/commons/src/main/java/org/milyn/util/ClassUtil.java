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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.classpath.InstanceOfFilter;
import org.milyn.classpath.IsAnnotationPresentFilter;
import org.milyn.classpath.Scanner;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Utility methods to aid in class/resource loading.
 * 
 * @author Kevin Conner
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
		final ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
        
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

    public static List<Class> findInstancesOf(final Class type, String[] igrnoreList, String[] includeList) {
        InstanceOfFilter filter = new InstanceOfFilter(type, igrnoreList, includeList);
        return findInstancesOf(type, filter);
    }

    public static List<Class> findInstancesOf(final Class type) {
        InstanceOfFilter filter = new InstanceOfFilter(type);
        return findInstancesOf(type, filter);
    }

    private static List<Class> findInstancesOf(Class type, InstanceOfFilter filter) {
        Scanner scanner = new Scanner(filter);

        try {
            long startTime = System.currentTimeMillis();
            scanner.scanClasspath(Thread.currentThread().getContextClassLoader());
            logger.info("Scanned classpath for instances of '" + type.getName() + "'.  Found " + filter.getClasses().size() + " matches. Scan took " + (System.currentTimeMillis() - startTime) + "ms.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to search classspath for instances of '" + type.getName() + "'.", e);
        }

        return filter.getClasses();
    }

    public static List<Class> findAnnotatedWith(Class<? extends Annotation> type, String[] igrnoreList, String[] includeList) {
        IsAnnotationPresentFilter filter = new IsAnnotationPresentFilter(type, igrnoreList, includeList);
        return findAnnotatedWith(type, filter);
    }

    public static List<Class> findAnnotatedWith(Class<? extends Annotation> type) {
        IsAnnotationPresentFilter filter = new IsAnnotationPresentFilter(type);
        return findAnnotatedWith(type, filter);
    }

    private static List<Class> findAnnotatedWith(Class<? extends Annotation> type, IsAnnotationPresentFilter filter) {
        Scanner scanner = new Scanner(filter);

        try {
            long startTime = System.currentTimeMillis();
            scanner.scanClasspath(Thread.currentThread().getContextClassLoader());
            logger.info("Scanned classpath for class annotated with annotation '" + type.getName() + "'.  Found " + filter.getClasses().size() + " matches. Scan took " + (System.currentTimeMillis() - startTime) + "ms.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to search classspath for class annotated with annotation '" + type.getName() + "'.", e);
        }

        return filter.getClasses();
    }

    public static Object newProxyInstance(Class[] classes, InvocationHandler handler) {
        final ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();

        if (threadClassLoader != null) {
            return Proxy.newProxyInstance(threadClassLoader, classes, handler);
        } else {
            return Proxy.newProxyInstance(ClassUtil.class.getClassLoader(), classes, handler);
        }
    }
}
