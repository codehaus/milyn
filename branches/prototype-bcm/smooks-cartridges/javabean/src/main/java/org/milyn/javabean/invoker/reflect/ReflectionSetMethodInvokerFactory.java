/**
 *
 */
package org.milyn.javabean.invoker.reflect;

import java.lang.reflect.Method;

import org.milyn.container.ApplicationContext;
import org.milyn.javabean.BeanUtils;
import org.milyn.javabean.invoker.SetMethodInvoker;
import org.milyn.javabean.invoker.SetMethodInvokerFactory;


/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ReflectionSetMethodInvokerFactory implements
		SetMethodInvokerFactory {

	/* (non-Javadoc)
	 * @see org.milyn.javabean.invoker.SetterMethodInvocatorFactory#initialize(org.milyn.container.ApplicationContext)
	 */
	public void initialize(ApplicationContext applicationContext) {
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.invoker.SetterMethodInvocatorFactory#create(org.milyn.container.ApplicationContext, java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public SetMethodInvoker create(Class<?> beanClass, String setterName, Class<?> setterParamType) {

		Method method = BeanUtils.createSetterMethod(setterName, beanClass, setterParamType);

		return new ReflectionSetMethodInvoker(method);
	}



}
