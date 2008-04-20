/**
 * 
 */
package org.milyn.javabean.bcm;

import javassist.ClassClassPath;
import javassist.ClassPool;

import org.milyn.container.ApplicationContext;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ApplicationContextJavaPool {
	
	private static final String CLASSPOOL_CONTEXT_KEY = ClassPool.class.getName() + "#CONTEXT_KEY";
    
    public static ClassPool getClassPool(ApplicationContext appContext) {
    	
    	ClassPool classPool = (ClassPool) appContext.getAttribute(CLASSPOOL_CONTEXT_KEY);

        if(classPool == null) {
        	classPool = new ClassPool();
        	classPool.appendClassPath( new ClassClassPath( appContext.getClass()));
        	
            appContext.setAttribute(CLASSPOOL_CONTEXT_KEY, classPool);
        }

        return classPool;
    	
    }
	
}
