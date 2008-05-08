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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.invocator.SetterMethodInvocator;
import org.milyn.javabean.invocator.SetterMethodInvocatorFactory;

/**
 * Bean utility methods.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 */
public abstract class BeanUtils {

    private static Log logger = LogFactory.getLog(BeanUtils.class);
    
    /**
     * Dynamically generates and instantiates a class that can call the desired method. 
     * 
     * @param applicationContext
     * @param setterName
     * @param bean
     * @param setterParamType
     * @return
     */
    public static SetterMethodInvocator createSetterMethodInvocator(ApplicationContext applicationContext, String setterName, Class<?> bean, Class<?> setterParamType) {
    	
    	SetterMethodInvocatorFactory factory = SetterMethodInvocatorFactory.Factory.create(applicationContext);
    	
    	SetterMethodInvocator setterMethodInvocator = factory.create(setterName, bean, setterParamType);

    	return setterMethodInvocator;
    }
    
    /**
     * Create the bean setter method instance for this visitor.
     *
     * @param setterName The setter method name.
     * @param setterParamType
     * @return The bean setter method.
     * @Deprecated Use the {@link #createSetterMethod(String, Class, Class)} method instead
     */
    @Deprecated
    public static Method createSetterMethod(String setterName, Object bean, Class<?> setterParamType) {
    	return createSetterMethod(setterName, bean.getClass(), setterParamType);
    }
    
    
    /**
     * Create the bean setter method instance for this visitor.
     *
     * @param setterName The setter method name.
     * @param setterParamType
     * @return The bean setter method.
     */
    public static Method createSetterMethod(String setterName, Class<?> beanClass, Class<?> setterParamType) {
        Method beanSetterMethod = getMethod(setterName, beanClass, setterParamType);

        // Try it as a list...
        if (beanSetterMethod == null && List.class.isAssignableFrom(setterParamType)) {
            String setterNamePlural = setterName + "s";

            // Try it as a List using the plural name...
            beanSetterMethod = getMethod(setterNamePlural, beanClass, setterParamType);
            if(beanSetterMethod == null) {
                // Try it as an array using the non-plural name...
            }
        }

        // Try it as a primitive...
        if(beanSetterMethod == null && Integer.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, beanClass, Integer.TYPE);
        }
        if(beanSetterMethod == null && Long.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, beanClass, Long.TYPE);
        }
        if(beanSetterMethod == null && Float.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, beanClass, Float.TYPE);
        }
        if(beanSetterMethod == null && Double.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, beanClass, Double.TYPE);
        }
        if(beanSetterMethod == null && Character.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, beanClass, Character.TYPE);
        }
        if(beanSetterMethod == null && Byte.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, beanClass, Byte.TYPE);
        }
        if(beanSetterMethod == null && Boolean.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, beanClass, Boolean.TYPE);
        }

        return beanSetterMethod;
    }

    private static Method getMethod(String setterName,  Class<?> beanClass, Class<?> setterParamType) {
        Method[] methods = beanClass.getMethods();
        Method beanSetterMethod = null;

        for(Method method : methods) {
            if(method.getName().equals(setterName)) {
                Class<?>[] params = method.getParameterTypes();
                if(params != null && params.length == 1 && params[0].isAssignableFrom(setterParamType)) {
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
    
    public static String toGetterName(String property) {
        StringBuffer getterName = new StringBuffer();

        // Add the property string to the buffer...
        getterName.append(property);
        // Uppercase the first character...
        getterName.setCharAt(0, Character.toUpperCase(property.charAt(0)));
        // Prefix with "get"...
        getterName.insert(0, "get");

        return getterName.toString();
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
        bean = BeanAccessor.getBean(execContext, beanId);

        if (bean == null) {
            throw new SmooksConfigurationException("Bean instance [" + beanId + "] not available and bean runtime class not set on configuration.");
        }

        return bean;
    }

    /**
     * Convert the supplied List into an array of the specified array type.
     * @param list The List instance to be converted.
     * @param arrayClass The array type.
     * @return The array.
     */
    public static Object convertListToArray(List<?> list, Class<?> arrayClass) {
        AssertArgument.isNotNull(list, "list");
        AssertArgument.isNotNull(arrayClass, "arrayClass");

        int length = list.size();
        Object arrayObj = Array.newInstance(arrayClass, list.size());
        for(int i = 0; i < length; i++) {
            try {
                Array.set(arrayObj, i, list.get(i));
            } catch(ClassCastException e) {
                logger.error("Failed to cast type '" + list.get(i).getClass().getName() + "' to '" + arrayClass.getName() + "'.", e);
            }
        }

        return arrayObj;
    }
}
