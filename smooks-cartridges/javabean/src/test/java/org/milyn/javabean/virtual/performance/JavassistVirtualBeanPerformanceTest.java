/**
 *
 */
package org.milyn.javabean.virtual.performance;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.container.ApplicationContext;
import org.milyn.container.MockApplicationContext;
import org.milyn.javabean.BeanUtils;
import org.milyn.javabean.invocator.PropertySetMethodInvocator;
import org.milyn.javabean.invocator.PropertySetMethodInvocatorFactory;
import org.milyn.javabean.invocator.javassist.JavassistSetterMethodInvocatorFactory;
import org.milyn.javabean.virtual.VirtualBeanGenerator;
import org.milyn.javabean.virtual.javassist.JavassistVirtualBeanGenerator;

/**
 * @author maurice_zeijen
 *
 */
@SuppressWarnings("unchecked")
public class JavassistVirtualBeanPerformanceTest extends VirtualPerformanceTest {

	private static final Log log = LogFactory.getLog(JavassistVirtualBeanPerformanceTest.class);

	/* (non-Javadoc)
	 * @see org.milyn.javabean.virtual.performance.VirtualPerformanceTest#directGet(int, java.lang.String[])
	 */
	@Override
	public void directGet(int num, String[] keys) {
		get(num, keys);
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.virtual.performance.VirtualPerformanceTest#directPut(int, java.lang.String[])
	 */
	@Override
	public void directPut(int num, String[] keys) {

		List<String> keyList = Arrays.asList(keys);
		PropertySetMethodInvocator[] invocators = new PropertySetMethodInvocator[keys.length];

		ApplicationContext appContext = creatApplicationContext();

		VirtualBeanGenerator generator = VirtualBeanGenerator.Factory.create(appContext);
		PropertySetMethodInvocatorFactory psiFactory = PropertySetMethodInvocatorFactory.Factory.create(appContext);

		Map map = generator.generate("test", keyList);
		for (int i = 0; i < keys.length; i++) {

			invocators[i] = psiFactory.create(BeanUtils.toSetterName(keys[i]), map.getClass(), String.class);
		}

		startTime();
		for (int i = 0; i < num; i++) {
			map = generator.generate("test", keyList);

			for (int j = 0; j < keys.length; j++) {
				invocators[j].set(map, keys[j]);
			}
		}
		publishTime();
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.virtual.performance.VirtualPerformanceTest#get(int, java.lang.String[])
	 */
	@Override
	public void get(int num, String[] keys) {

		List<String> keyList = Arrays.asList(keys);

		VirtualBeanGenerator generator = VirtualBeanGenerator.Factory.create(creatApplicationContext());

		startTime();
		for (int i = 0; i < num; i++) {
			Map map = generator.generate("test", keyList);

			for (int j = 0; j < keys.length; j++) {
				map.get(keys[j]);
			}
		}
		publishTime();
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.virtual.performance.VirtualPerformanceTest#put(int, java.lang.String[])
	 */
	@Override
	public void put(int num, String[] keys) {
		List<String> keyList = Arrays.asList(keys);

		VirtualBeanGenerator generator = VirtualBeanGenerator.Factory.create(creatApplicationContext());

		startTime();
		for (int i = 0; i < num; i++) {
			Map map = generator.generate("test", keyList);

			for (int j = 0; j < keys.length; j++) {
				map.put(keys[j], keys[j]);
			}
		}
		publishTime();
	}

	public ApplicationContext creatApplicationContext() {
		ApplicationContext applicationContext = new MockApplicationContext();
		applicationContext.setAttribute(VirtualBeanGenerator.IMPLEMENTATION_CONTEXT_KEY, JavassistVirtualBeanGenerator.class.getName());
		applicationContext.setAttribute(PropertySetMethodInvocatorFactory.IMPLEMENTATION_CONTEXT_KEY, JavassistSetterMethodInvocatorFactory.class.getName());

		return applicationContext;
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.virtual.performance.VirtualPerformanceTest#getLog()
	 */
	@Override
	public Log getLog() {
		return log;
	}
}
