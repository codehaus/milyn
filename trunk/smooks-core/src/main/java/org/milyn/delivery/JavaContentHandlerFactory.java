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

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.classpath.ClasspathUtils;
import org.milyn.util.ClassUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Java ContentHandler instance creator.
 * <p/>
 * Java-based ContentHandler implementations should contain a public
 * constructor that takes a SmooksResourceConfiguration instance as a parameter.
 * @author tfennelly
 */
public class JavaContentHandlerFactory implements ContentHandlerFactory {

    /**
     * Public constructor.
     * @param config Configuration details for this ContentHandlerFactory.
     */
    public JavaContentHandlerFactory(SmooksResourceConfiguration config) {
    }

    /**
	 * Create a Java based ContentHandler instance.
     * @param resourceConfig The SmooksResourceConfiguration for the Java {@link ContentHandler}
     * to be created.
     * @return Java {@link ContentHandler} instance.
	 */
	public synchronized ContentHandler create(SmooksResourceConfiguration resourceConfig) throws InstantiationException {
		ContentHandler deliveryUnit = null;
        Exception exception = null;
        String className = null;
		
		try {
            className = ClasspathUtils.toClassName(resourceConfig.getResource());
			Class classRuntime = ClassUtil.forName(className, getClass());
			Constructor constructor;
			try {
				constructor = classRuntime.getConstructor(new Class[] {SmooksResourceConfiguration.class});
				deliveryUnit = (ContentHandler) constructor.newInstance(new Object[] {resourceConfig});
			} catch (NoSuchMethodException e) {
				deliveryUnit = (ContentHandler) classRuntime.newInstance();
			}
            Configurator.configure(deliveryUnit, resourceConfig);
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
                IllegalStateException state = new IllegalStateException("Failed to create an instance of Java ContentHandler [" + resourceConfig.getResource() + "].  See exception cause...");
                state.initCause(exception);
                throw state;
            }
        }
		
		return deliveryUnit;
	}
}
