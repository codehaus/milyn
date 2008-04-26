/**
 * 
 */
package org.milyn.javabean.invocator;

import org.milyn.container.ApplicationContext;
import org.milyn.javabean.invocator.javassist.JavassistSetterMethodInvocatorFactory;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public interface SetterMethodInvocatorFactory {

	
	SetterMethodInvocator create(ApplicationContext applicationContext, String setterName, Object bean, Class<?> setterParamType);
	
	
	public static final String IMPLEMENTATION_CONTEXT_KEY = SetterMethodInvocatorFactory.class.getName() + "#IMPLEMENTATION";

	public static class Factory {
		
		public static final String DEFAULT_IMPLEMENTATION = JavassistSetterMethodInvocatorFactory.class.getName();

		public static final String INSTANCE_CONTEXT_KEY = Factory.class.getName() + "#INSTANCE";

		public static SetterMethodInvocatorFactory create(ApplicationContext applicationContext) {
			
			SetterMethodInvocatorFactory setterMethodInvocatorFactory = (SetterMethodInvocatorFactory) applicationContext.getAttribute(INSTANCE_CONTEXT_KEY);
			
			if(setterMethodInvocatorFactory == null) {
				
				String setterMethodInvocatorFactoryImplementation = (String) applicationContext.getAttribute(IMPLEMENTATION_CONTEXT_KEY);
				
				if(setterMethodInvocatorFactoryImplementation == null) {
					setterMethodInvocatorFactoryImplementation = DEFAULT_IMPLEMENTATION;
				}
				
				try {
					Class<?> setterMethodInvocatorFactoryClass = applicationContext.getClass().getClassLoader().loadClass(setterMethodInvocatorFactoryImplementation);
					
					setterMethodInvocatorFactory = (SetterMethodInvocatorFactory) setterMethodInvocatorFactoryClass.newInstance();
					
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("Configured factory implementation class '" 
							+ setterMethodInvocatorFactoryImplementation + "' not found", e);
				} catch (InstantiationException e) {
					throw new RuntimeException("Configured factory implementation class '" 
							+ setterMethodInvocatorFactoryImplementation + "' could not be instantiated. " 
							+ "Make sure it has a parameterless public constructor.", e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Configured factory implementation class '"
							+ setterMethodInvocatorFactoryImplementation + "' could not be instantiated. " 
							+ "Make sure it has a parameterless public constructor.", e);
				}
				
				applicationContext.setAttribute(INSTANCE_CONTEXT_KEY, setterMethodInvocatorFactory);
				
			}
			
			return setterMethodInvocatorFactory;
			
			
		}	
		
		
	}
	
	
	
}
