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
package org.milyn.javabean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ExecutionContext;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Bean utility methods.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
abstract class BeanUtils {

    /**
     * Create the bean setter method instance for this visitor.
     *
     * @param setterName The setter method name.
     * @return The bean setter method.
     */
    public static Method createBeanSetterMethod(Object bean, String setterName, Class type) {
        Method beanSetterMethod = getMethod(type, bean, setterName);

        // Try it as a list...
        if (beanSetterMethod == null && List.class.isAssignableFrom(type)) {
            String setterNamePlural = setterName + "s";

            // Try it as a List using the plural name...
            beanSetterMethod = getMethod(type, bean, setterNamePlural);
            if(beanSetterMethod == null) {
                // Try it as an array using the non-plural name...
            }
        }

        // Try it as a primitive...
        if(beanSetterMethod == null && Integer.class.isAssignableFrom(type)) {
            beanSetterMethod = getMethod(Integer.TYPE, bean, setterName);
        }
        if(beanSetterMethod == null && Long.class.isAssignableFrom(type)) {
            beanSetterMethod = getMethod(Long.TYPE, bean, setterName);
        }
        if(beanSetterMethod == null && Float.class.isAssignableFrom(type)) {
            beanSetterMethod = getMethod(Float.TYPE, bean, setterName);
        }
        if(beanSetterMethod == null && Double.class.isAssignableFrom(type)) {
            beanSetterMethod = getMethod(Double.TYPE, bean, setterName);
        }
        if(beanSetterMethod == null && Character.class.isAssignableFrom(type)) {
            beanSetterMethod = getMethod(Character.TYPE, bean, setterName);
        }
        if(beanSetterMethod == null && Byte.class.isAssignableFrom(type)) {
            beanSetterMethod = getMethod(Byte.TYPE, bean, setterName);
        }
        if(beanSetterMethod == null && Boolean.class.isAssignableFrom(type)) {
            beanSetterMethod = getMethod(Boolean.TYPE, bean, setterName);
        }

        return beanSetterMethod;
    }

    private static Method getMethod(Class type, Object bean, String setterName) {
        Method[] methods = bean.getClass().getMethods();
        Method beanSetterMethod = null;

        for(Method method : methods) {
            if(method.getName().equals(setterName)) {
                Class[] params = method.getParameterTypes();
                if(params != null && params.length == 1 && params[0].isAssignableFrom(type)) {
                    beanSetterMethod = method;
                    break;
                }
            }
        }

        return beanSetterMethod;
    }

    public static String toSetterName(String property) {
        StringBuffer setterName = new StringBuffer();

        // Add the property string to the buffer...
        setterName.append(property);
        // Uppercase the first character...
        setterName.setCharAt(0, Character.toUpperCase(property.charAt(0)));
        // Prefix with "set"...
        setterName.insert(0, "set");

        return setterName.toString();
    }

    /**
     * Get the bean instance on which this populator instance is to set data.
     *
     * @param execContext The execution context.
     * @return The bean instance.
     */
    public static Object getBean(String beanId, ExecutionContext execContext) {
        Object bean;

        // Get the bean instance from the request.  If there is non, it's a bad config!!
        bean = BeanAccessor.getBean(beanId, execContext);
        if (logger.isDebugEnabled()) {
            logger.debug("Not creating a new bean instance for beanId [" + beanId + "].  Using [" + bean + "]");
        }
        if (bean == null) {
            throw new SmooksConfigurationException("Bean instance [" + beanId + "] not available and bean runtime class not set on configuration.");
        }

        return bean;
    }
}
