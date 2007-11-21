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
import org.milyn.assertion.AssertArgument;
import org.milyn.classpath.InstanceOfFilter;
import org.milyn.classpath.IsAnnotationPresentFilter;
import org.milyn.classpath.Scanner;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
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
    
    /**
     * Will try to create a List of classes that are listed 
     * in the passed in file.
     * The fileName is expected to be found on the classpath.
     * 
     * @param fileName The name of the file containing the list of classes,
     * one class name per line.
     * @param instanceOf The instanceof filter.
     * @return List<Class<T>>	list of the classes contained in the file.
     */
    public static <T> List<Class<T>> getClasses(final String fileName, Class<T> instanceOf) {
    	AssertArgument.isNotNull( fileName, "fileName" );
        AssertArgument.isNotNull( instanceOf, "instanceOf" );

        long start = System.currentTimeMillis();
        List<Class<T>> classes = new ArrayList<Class<T>>();
        Enumeration<URL> cpURLs;
        int resCount = 0;

        try {
            cpURLs = Thread.currentThread().getContextClassLoader().getResources(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Error getting resource URLs for resource : " + fileName, e);
        }

        while (cpURLs.hasMoreElements()) {
            URL url = cpURLs.nextElement();
            addClasses(url, instanceOf, classes);
            resCount++;
        }

        logger.info("Loaded " + classes.size() + " classes from " + resCount + " URLs through class list file "
                + fileName + ".  Process took " + (System.currentTimeMillis() - start) + "ms.  Turn on debug logging for more info.");

        return classes;
    }

    private static <T>  void addClasses(URL url, Class<T> instanceOf, List<Class<T>> classes) {
        InputStream ins = null;
        BufferedReader br = null;

        try
    	{
            String className;
            int count = 0;

            ins = url.openStream();
            br = new BufferedReader( new InputStreamReader( ins ));
	    	while( (className = br.readLine()) != null )
	    	{
                Class clazz;

                className = className.trim();
                
                // Ignore blank lines and lines that start with a hash...
                if(className.equals("") || className.startsWith("#")) {
                    continue;
                }

                try {
                    clazz = forName(className, ClassUtil.class);
                } catch (ClassNotFoundException e) {
                    logger.warn("Failed to load class '" + className + "'. Class not found.");
                    continue;
                }

                if(instanceOf.isAssignableFrom(clazz)) {
                    classes.add(clazz);
                    logger.debug( "Adding " + className + " to list of classes");
                    count++;
                } else {
                    logger.info("Not adding class '" + clazz.getName() + "' to list.  Class does not implement/extend '" + instanceOf.getName() + "'.");
                }
            }
            logger.debug("Loaded '" + count + "' classes listed in '" + url + "'.");
    	}
    	catch (IOException e)
		{
            throw new RuntimeException("Failed to read from file : " + url, e);
		}
    	finally
    	{
    		close(ins);
    		close(br);
    	}
    }

    private static void close( final Closeable closable ) {
    	if(  closable != null )
    	{
			try
			{
				closable.close();
			} 
    		catch (IOException e)
			{
    			logger.warn( "Exception while trying to close : " + closable, e);
			}
    	}
    }

    public static String toFilePath(Package aPackage) {
        return "/" + aPackage.getName().replace('.', '/');
    }
}
