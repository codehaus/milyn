/**
 * 
 */
package org.milyn.javabean.invocator.reflect;

import java.lang.reflect.Method;

import org.milyn.container.ApplicationContext;
import org.milyn.javabean.BeanUtils;
import org.milyn.javabean.invocator.SetterMethodInvocator;
import org.milyn.javabean.invocator.SetterMethodInvocatorFactory;


/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ReflectiveSetterMethodInvocatorFactory implements
		SetterMethodInvocatorFactory {

	
	
	/* (non-Javadoc)
	 * @see org.milyn.javabean.invocator.SetterMethodInvocatorFactory#create(org.milyn.container.ApplicationContext, java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public SetterMethodInvocator create(ApplicationContext applicationContext,
			String setterName, Object bean, Class<?> setterParamType) {
		
		Method method = BeanUtils.createSetterMethod(setterName, bean, setterParamType);
		
		return new ReflectiveSetterMethodInvocator(method);
	}

}
