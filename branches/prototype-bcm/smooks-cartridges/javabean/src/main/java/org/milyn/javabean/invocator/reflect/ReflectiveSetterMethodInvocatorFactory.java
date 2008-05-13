/**
 * 
 */
package org.milyn.javabean.invocator.reflect;

import java.lang.reflect.Method;

import org.milyn.container.ApplicationContext;
import org.milyn.javabean.BeanUtils;
import org.milyn.javabean.invocator.PropertySetMethodInvocator;
import org.milyn.javabean.invocator.PropertySetMethodInvocatorFactory;


/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ReflectiveSetterMethodInvocatorFactory implements
		PropertySetMethodInvocatorFactory {

	/* (non-Javadoc)
	 * @see org.milyn.javabean.invocator.SetterMethodInvocatorFactory#initialize(org.milyn.container.ApplicationContext)
	 */
	public void initialize(ApplicationContext applicationContext) {
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.javabean.invocator.SetterMethodInvocatorFactory#create(org.milyn.container.ApplicationContext, java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public PropertySetMethodInvocator create(String setterName, Class<?> beanClass, Class<?> setterParamType) {
		
		Method method = BeanUtils.createSetterMethod(setterName, beanClass, setterParamType);
		
		return new ReflectiveSetterMethodInvocator(method);
	}

	

}
