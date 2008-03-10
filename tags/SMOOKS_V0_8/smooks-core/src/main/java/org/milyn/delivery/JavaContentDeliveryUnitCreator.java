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

package org.milyn.delivery;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.milyn.cdr.ClasspathUtils;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.util.ClassUtil;

/**
 * Java ContentDeliveryUnit instance creator.
 * <p/>
 * Java-based ContentDeliveryUnit implementations should contain a public 
 * constructor that takes a SmooksResourceConfiguration instance as a parameter.
 * @author tfennelly
 */
public class JavaContentDeliveryUnitCreator implements ContentDeliveryUnitCreator {

    /**
     * Public constructor.
     * @param config Configuration details for this ContentDeliveryUnitCreator.
     */
    public JavaContentDeliveryUnitCreator(SmooksResourceConfiguration config) {        
    }

    /**
	 * Create a Java based ContentDeliveryUnit instance ie from a Java Class byte stream.
     * @param resourceConfig The SmooksResourceConfiguration for the Java {@link ContentDeliveryUnit}
     * to be created.
     * @return Java {@link ContentDeliveryUnit} instance.
	 */
	public synchronized ContentDeliveryUnit create(SmooksResourceConfiguration resourceConfig) throws InstantiationException {
		ContentDeliveryUnit deliveryUnit = null;
        Exception exception = null;
        String className = null;
		
		try {
            className = ClasspathUtils.toClassName(resourceConfig.getPath());
			Class classRuntime = ClassUtil.forName(className, getClass());
			Constructor constructor;
			try {
				constructor = classRuntime.getConstructor(new Class[] {SmooksResourceConfiguration.class});
				deliveryUnit = (ContentDeliveryUnit) constructor.newInstance(new Object[] {resourceConfig});
			} catch (NoSuchMethodException e) {
				deliveryUnit = (ContentDeliveryUnit) classRuntime.newInstance();
			}
			deliveryUnit.setConfiguration(resourceConfig);
        } catch (InstantiationException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        } catch (ClassNotFoundException e) {
            exception = e;
        } finally {
            // One of the above exception.
            if(exception != null) {
                IllegalStateException state = new IllegalStateException("Failed to create an instance of Java ContentDeliveryUnit [" + resourceConfig.getPath() + "].  See exception cause...");
                state.initCause(exception);
                throw state;
            }
        }
		
		return deliveryUnit;
	}
}