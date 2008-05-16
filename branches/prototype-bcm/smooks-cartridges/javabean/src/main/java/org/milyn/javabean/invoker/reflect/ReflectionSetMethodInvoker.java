/**
 * 
 */
package org.milyn.javabean.invoker.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.milyn.javabean.invoker.SetMethodInvoker;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ReflectionSetMethodInvoker implements SetMethodInvoker {

	private final Method method;
	
	/**
	 * @param method
	 */
	public ReflectionSetMethodInvoker(Method method) {
		this.method = method;
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.invoker.SetterMethodInvocator#set(java.lang.Object, java.lang.Object)
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
