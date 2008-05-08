/**
 * 
 */
package org.milyn.javabean.performance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.container.MockApplicationContext;
import org.milyn.javabean.invocator.SetterMethodInvocator;
import org.milyn.javabean.invocator.SetterMethodInvocatorFactory;
import org.milyn.javabean.invocator.javassist.JavassistSetterMethodInvocatorFactory;
import org.milyn.javabean.invocator.reflect.ReflectiveSetterMethodInvocatorFactory;
import org.milyn.javabean.performance.model.Person;


/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class PerformanceReflectionVsBcm {

	private final static Log logger = LogFactory
			.getLog(PerformanceReflectionVsBcm.class);
	
	public static void main(String[] args) {
		
		int numLoops = 1000000;
		
		SetterMethodInvocatorFactory[] setterMethodInvocatorFactories = {
				new ReflectiveSetterMethodInvocatorFactory(),
				new JavassistSetterMethodInvocatorFactory()
		};
		
		for(SetterMethodInvocatorFactory setterMethodInvocatorFactory: setterMethodInvocatorFactories) {
			logger.info("SetterMethodInvocatorFactory: " + setterMethodInvocatorFactory.getClass().getSimpleName());
			
			setterMethodInvocatorFactory.initialize(new MockApplicationContext());
			
			long beginTime = System.currentTimeMillis();
			
			SetterMethodInvocator setterMethodInvocator = setterMethodInvocatorFactory.create("setSurname", Person.class, String.class);
			
			logger.info("Construction: " + (System.currentTimeMillis() - beginTime));
			String str = "few";
			
			Person person = new Person();
			
			beginTime = System.currentTimeMillis();
			for(int i = 0; i < numLoops; i++) {
				
				setterMethodInvocator.set(person, str);
				
			}
			logger.info("Invocation: " + (System.currentTimeMillis() - beginTime));
			
		}
		
		
	}
	
}
