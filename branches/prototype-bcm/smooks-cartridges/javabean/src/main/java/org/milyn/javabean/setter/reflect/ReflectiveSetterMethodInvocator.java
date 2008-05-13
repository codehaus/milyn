/**
 * 
 */
package org.milyn.javabean.setter.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.milyn.javabean.setter.PropertySetMethodInvocator;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ReflectiveSetterMethodInvocator implements PropertySetMethodInvocator {

	private final Method method;
	
	/**
	 * @param method
	 */
	public ReflectiveSetterMethodInvocator(Method method) {
		this.method = method;
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.invocator.SetterMethodInvocator#set(java.lang.Object, java.lang.Object)
	 */
	public void set(Object obj, Object arg) {
		try {
			method.invoke(obj, arg);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Could not invoke setter method '" + method.getName() + "'", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Could not invoke setter method '" + method.getName() + "'", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Could not invoke setter method '" + method.getName() + "'", e);
		}
	}
	
}
