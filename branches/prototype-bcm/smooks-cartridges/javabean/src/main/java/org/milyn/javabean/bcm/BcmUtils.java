/**
 * 
 */
package org.milyn.javabean.bcm;

import org.milyn.container.ApplicationContext;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public final class BcmUtils {
	
	private static final String CLASSLOADER_CONTEXT_KEY = BcmUtils.class.getName() + "#CLASSLOADER_CONTEXT_KEY";
    
    public static BcmClassLoader getClassloader(ApplicationContext appContext) {
    	
    	BcmClassLoader classloader = (BcmClassLoader) appContext.getAttribute(CLASSLOADER_CONTEXT_KEY);

        if(classloader == null) {
        	classloader = new BcmClassLoader(appContext.getClass().getClassLoader());
        	
            appContext.setAttribute(CLASSLOADER_CONTEXT_KEY, classloader);
        }

        return classloader;
    	
    }

	
	private BcmUtils(){
	}
	
}
