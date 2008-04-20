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

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ExecutionContext;

/**
 * Bean utility methods.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class BeanUtils {

    private static Log logger = LogFactory.getLog(BeanUtils.class);

    /**
     * Create the bean setter method instance for this visitor.
     *
     * @param setterName The setter method name.
     * @param setterParamType
     * @return The bean setter method.
     */
    public static Method createSetterMethod(String setterName, Object bean, Class<?> setterParamType) {
        Method beanSetterMethod = getMethod(setterName, bean, setterParamType);

        // Try it as a list...
        if (beanSetterMethod == null && List.class.isAssignableFrom(setterParamType)) {
            String setterNamePlural = setterName + "s";

            // Try it as a List using the plural name...
            beanSetterMethod = getMethod(setterNamePlural, bean, setterParamType);
            if(beanSetterMethod == null) {
                // Try it as an array using the non-plural name...
            }
        }

        // Try it as a primitive...
        if(beanSetterMethod == null && Integer.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, bean, Integer.TYPE);
        }
        if(beanSetterMethod == null && Long.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, bean, Long.TYPE);
        }
        if(beanSetterMethod == null && Float.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, bean, Float.TYPE);
        }
        if(beanSetterMethod == null && Double.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, bean, Double.TYPE);
        }
        if(beanSetterMethod == null && Character.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, bean, Character.TYPE);
        }
        if(beanSetterMethod == null && Byte.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, bean, Byte.TYPE);
        }
        if(beanSetterMethod == null && Boolean.class.isAssignableFrom(setterParamType)) {
            beanSetterMethod = getMethod(setterName, bean, Boolean.TYPE);
        }

        return beanSetterMethod;
    }

    private static Method getMethod(String setterName, Object bean, Class<?> setterParamType) {
        Method[] methods = bean.getClass().getMethods();
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
    
    public static SetterMethodInvocator createSetterMethodInvocator(String setterName, Object bean, Class<?> setterParamType, ClassPool classPool) {
    	
    	// smi = SetterMethodInvocator
    	CtClass smiInterface;
		try {
			
			smiInterface = classPool.get(SetterMethodInvocator.class.getName());
			
		} catch (NotFoundException e) {
			throw new RuntimeException("Could not get the SetterMethodInvocator interface", e);
		}
    	
		Class<?> smiClass = null;
		try {
			String smiClassName = "org.milyn.javabean.smi." + bean.getClass().getName() + "_" + setterName + "_" + setterParamType.getName();
			
			try {
				smiClass = classPool.getClassLoader().loadClass(smiClassName);
			} catch (ClassNotFoundException e) {
			}
			
			if(smiClass == null) {
				CtClass smiImpl = classPool.makeClass(smiClassName);
			  	
				String methodStr = "public void set(Object obj, Object arg) {" +
					"((" + bean.getClass().getName() + ")obj)." + setterName + "(" + parameterStr(setterName, bean, setterParamType) + ");" +
				"}";
				
				CtMethod setMethod = CtNewMethod.make(methodStr, smiImpl);
				
				smiImpl.addMethod(setMethod);
				
				smiImpl.addInterface(smiInterface);
				
				smiClass = smiImpl.toClass(bean.getClass().getClassLoader(), bean.getClass().getProtectionDomain());
			}
		} catch (CannotCompileException e) {
			throw new RuntimeException("Could not create the SetterMethodInvocator class", e);
		}
    	
    	try {
			return (SetterMethodInvocator) smiClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("Could not create the SetterMethodInvocator object", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Could not create the SetterMethodInvocator object", e);
		}
    	
    }
    
    private static String parameterStr(String setterName, Object bean, Class<?> setterParamType) {
    	
    	if(setterParamType.equals(Byte.class)) {
    		
    		return autobox(setterName, bean.getClass(), Byte.class, byte.class);
    		
    	} else if(setterParamType.equals(Short.class)) {
    		
    		return autobox(setterName, bean.getClass(), Short.class, short.class);
    		
		} else if(setterParamType.equals(Integer.class)) {

    		return autobox(setterName, bean.getClass(), Integer.class, int.class);
    		
		} else if(setterParamType.equals(Long.class)) {

    		return autobox(setterName, bean.getClass(), Long.class, long.class);
		
		} else if(setterParamType.equals(Float.class)) {

    		return autobox(setterName, bean.getClass(), Float.class, float.class);
    		
		} else if(setterParamType.equals(Double.class)) {

    		return autobox(setterName, bean.getClass(), Double.class, double.class);
    		
		} else if(setterParamType.equals(Boolean.class)) {

    		return autobox(setterName, bean.getClass(), Boolean.class, boolean.class);
    			
		} else if(setterParamType.equals(Character.class)) {

    		return autobox(setterName, bean.getClass(), Character.class, char.class);
    				
    	} else {
    		if(setterParamType.isArray()) {
    			
    			return "(" + setterParamType.getComponentType().getName() + "[]) arg";
    		} else {
    			return "(" + setterParamType.getName() + ") arg";
    		}
    	}
    	
    }
    
    private static String autobox(String setterName, Class<?> beanClass, Class<?> paramType, Class<?> paramPrimativeType) {
    	
    	if(isMethodPresent(setterName, beanClass, paramType)) {
			return "("+ paramType.getSimpleName() +") arg";
		} else if (isMethodPresent(setterName, beanClass, paramPrimativeType)){
			return "(("+ paramType.getSimpleName() +") arg)." + paramPrimativeType.getSimpleName() + "Value()";
		} else {
			
			throw new RuntimeException("Could not find the method '" + setterName + "' on the bean '" + beanClass 
					+ "' with a '" + paramType.getSimpleName() + "' or a '" + paramPrimativeType.getSimpleName() + "' parameter."); 
			
		}
    }
    
    private static boolean isMethodPresent(String methodName, Class<?> beanClass, Class<?> setterParamType) {
    	
    	Method[] methods = beanClass.getMethods();
    	
    	for(Method method : methods) {
    		
    		if(methodName.equals(method.getName())) {
	    		Class<?>[] parameterTypes = method.getParameterTypes(); 
	    		
	    		if(parameterTypes.length == 1 && parameterTypes[0].equals(setterParamType)) {
	    			return true;
	    		}
    		}
    		
    	}
    	return false;
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
