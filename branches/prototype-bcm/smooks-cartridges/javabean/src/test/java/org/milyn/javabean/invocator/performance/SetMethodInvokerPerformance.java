/**
 *
 */
package org.milyn.javabean.invocator.performance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.container.ApplicationContext;
import org.milyn.container.MockApplicationContext;
import org.milyn.javabean.invocator.SetMethodInvoker;
import org.milyn.javabean.invocator.SetMethodInvokerFactory;
import org.milyn.javabean.invocator.asm.AsmSetMethodInvokerFactory;
import org.milyn.javabean.performance.model.Person;


/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class SetMethodInvokerPerformance {

	private final static Log logger = LogFactory
			.getLog(SetMethodInvokerPerformance.class);

	public static void main(String[] args) {

		int numLoops = 100000000;

		logger.info("Number of invocations: " + numLoops);

		SetMethodInvokerFactory[] setterMethodInvocatorFactories = {
				//new DirectSetterMethodInvocatorFactory(),
				//new org.milyn.javabean.invocator.reflect.ReflectionSetMethodInvokerFactory(),
				//new JavassistSetMethodInvokerFactory(),
				new AsmSetMethodInvokerFactory()
		};

		for(SetMethodInvokerFactory setterMethodInvocatorFactory: setterMethodInvocatorFactories) {
			logger.info("SetterMethodInvocatorFactory: " + setterMethodInvocatorFactory.getClass().getSimpleName());

			setterMethodInvocatorFactory.initialize(new MockApplicationContext());

			long beginTime = System.currentTimeMillis();

			SetMethodInvoker setMethodInvocator = setterMethodInvocatorFactory.create("setSurname", Person.class, String.class);

			logger.info("Construction: " + (System.currentTimeMillis() - beginTime)+ "ms");


			String str = "testSomething";

			Person person = new Person();

			beginTime = System.currentTimeMillis();

			setMethodInvocator.set(person, str);

			logger.info("Warmup: " + (System.currentTimeMillis() - beginTime)+ "ms");

			beginTime = System.currentTimeMillis();
			for(int i = 0; i < numLoops; i++) {

				setMethodInvocator.set(person, str);

			}
			logger.info("Invocation: " + (System.currentTimeMillis() - beginTime) + "ms");

		}


	}

	static class DirectSetterMethodInvocatorFactory implements SetMethodInvokerFactory {

		public SetMethodInvoker create(String setterName,
				Class<?> beanClass, Class<?> setterParamType) {

			return new DirectSetterMethodInvocator();
		}

		public void initialize(ApplicationContext applicationContext) {
		}

	}

	static class DirectSetterMethodInvocator implements SetMethodInvoker{

		public void set(Object obj, Object arg) {
			((Person)obj).setSurname((String)arg);
		}

	}

}
