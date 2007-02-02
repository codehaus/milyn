package org.milyn.util;

import java.io.InputStream;

/**
 * Utility methods to aid in class/resource loading.
 * @author kevin
 */
public class ClassUtil {
    /**
     * Load the specified class.
     * @param className The name of the class to load.
     * @param caller The class of the caller.
     * @return The specified class.
     * @throws ClassNotFoundException If the class cannot be found.
     */
    public static Class forName(final String className, final Class caller)
        throws ClassNotFoundException
    {
        final ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader() ;
        if (threadClassLoader != null)
        {
            try
            {
                return threadClassLoader.loadClass(className) ;
            }
            catch (final ClassNotFoundException cnfe) {} // ignore
        }
        
        
        final ClassLoader classLoader = caller.getClassLoader() ;
        if (classLoader != null)
        {
            try
            {
                return classLoader.loadClass(className) ;
            }
            catch (final ClassNotFoundException cnfe) {} // ignore
        }
        
        return Class.forName(className, true, ClassLoader.getSystemClassLoader()) ;
    }
    
    /**
     * Get the specified resource as a stream.
     * @param resourceName The name of the class to load.
     * @param caller The class of the caller.
     * @return The input stream for the resource or null if not found.
     */
    public static InputStream getResourceAsStream(final String resourceName, final Class caller)
    {
    	final String resource ;
    	if (resourceName.startsWith("/"))
    	{
    		resource = resourceName.substring(1) ;
    	}
    	else
    	{
    		final Package callerPackage = caller.getPackage() ;
    		if (callerPackage != null)
    		{
    			resource = callerPackage.getName().replace('.', '/') + '/' + resourceName ;
    		}
    		else
    		{
    			resource = resourceName ;
    		}
    	}
        final ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader() ;
        if (threadClassLoader != null)
        {
        	final InputStream is = threadClassLoader.getResourceAsStream(resource) ;
        	if (is != null)
        	{
        		return is ;
        	}
        }
        
        final ClassLoader classLoader = caller.getClassLoader() ;
        if (classLoader != null)
        {
	    	final InputStream is = classLoader.getResourceAsStream(resource) ;
	    	if (is != null)
	    	{
	    		return is ;
	    	}
        }
        
        return ClassLoader.getSystemResourceAsStream(resource) ;
    }
}
