/**
 * 
 */
package org.milyn.javabean.bcm;

import java.util.HashMap;
import java.util.Map;

import org.milyn.Smooks;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 * 
 */
public class BcmClassLoader extends ClassLoader {

	private final Map<String, Class<?>> loadedBCMClasses = new HashMap<String, Class<?>>();
	
	
	/**
	 * 
	 */
	public BcmClassLoader() {
		super(Smooks.class.getClassLoader());
	}

	/**
	 * @param parent
	 */
	public BcmClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	public Class<?> load(String name) {
		return loadedBCMClasses.get(name);
	}

	public Class<?> load(String name, byte[] data) {
	
		Class<?> cls = super.defineClass(name, data, 0, data.length);
		
		loadedBCMClasses.put(name, cls);
		
		return cls;
		
	}
}
