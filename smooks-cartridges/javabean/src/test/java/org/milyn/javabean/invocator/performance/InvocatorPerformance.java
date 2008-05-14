/**
 *
 */
package org.milyn.javabean.invocator.performance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.container.ApplicationContext;
import org.milyn.container.MockApplicationContext;
import org.milyn.javabean.invocator.PropertySetMethodInvocator;
import org.milyn.javabean.invocator.PropertySetMethodInvocatorFactory;
import org.milyn.javabean.invocator.javassist.JavassistSetterMethodInvocatorFactory;
import org.milyn.javabean.performance.model.Person;


/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class InvocatorPerformance {

	private final static Log logger = LogFactory
			.getLog(InvocatorPerformance.class);

	public static void main(String[] args) {

		int numLoops = 100000000;

		logger.info("Number of invocations: " + numLoops);

		PropertySetMethodInvocatorFactory[] setterMethodInvocatorFactories = {
				new DirectSetterMethodInvocatorFactory(),
				new org.milyn.javabean.invocator.reflect.ReflectiveSetterMethodInvocatorFactory(),
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
