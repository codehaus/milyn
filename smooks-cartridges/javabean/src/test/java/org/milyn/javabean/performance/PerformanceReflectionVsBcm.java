/**
 *
 */
package org.milyn.javabean.performance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.container.ApplicationContext;
import org.milyn.container.MockApplicationContext;
import org.milyn.javabean.performance.model.Person;
import org.milyn.javabean.setter.PropertySetMethodInvocator;
import org.milyn.javabean.setter.PropertySetMethodInvocatorFactory;
import org.milyn.javabean.setter.javassist.JavassistSetterMethodInvocatorFactory;


/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class PerformanceReflectionVsBcm {

	private final static Log logger = LogFactory
			.getLog(PerformanceReflectionVsBcm.class);

	public static void main(String[] args) {

		int numLoops = 1000000;

		logger.info("Number of invocations: " + numLoops);

		PropertySetMethodInvocatorFactory[] setterMethodInvocatorFactories = {
				new DirectSetterMethodInvocatorFactory(),
				new org.milyn.javabean.setter.reflect.ReflectiveSetterMethodInvocatorFactory(),
				new JavassistSetterMethodInvocatorFactory()
		};

		for(PropertySetMethodInvocatorFactory setterMethodInvocatorFactory: setterMethodInvocatorFactories) {
			logger.info("SetterMethodInvocatorFactory: " + setterMethodInvocatorFactory.getClass().getSimpleName());

			setterMethodInvocatorFactory.initialize(new MockApplicationContext());

			long beginTime = System.currentTimeMillis();

			PropertySetMethodInvocator setMethodInvocator = setterMethodInvocatorFactory.create("setSurname", Person.class, String.class);

			logger.info("Construction: " + (System.currentTimeMillis() - beginTime)+ "ms");
			String str = "testSomething";

			Person person = new Person();

			beginTime = System.currentTimeMillis();
			for(int i = 0; i < numLoops; i++) {

				setMethodInvocator.set(person, str);

			}
			logger.info("Invocation: " + (System.currentTimeMillis() - beginTime) + "ms");

		}


	}

	static class DirectSetterMethodInvocatorFactory implements PropertySetMethodInvocatorFactory {

		public PropertySetMethodInvocator create(String setterName,
				Class<?> beanClass, Class<?> setterParamType) {

			return new DirectSetterMethodInvocator();
		}

		public void initialize(ApplicationContext applicationContext) {
		}

	}

	static class DirectSetterMethodInvocator implements PropertySetMethodInvocator{

		public void set(Object obj, Object arg) {
			((Person)obj).setSurname((String)arg);
		}

	}

}
