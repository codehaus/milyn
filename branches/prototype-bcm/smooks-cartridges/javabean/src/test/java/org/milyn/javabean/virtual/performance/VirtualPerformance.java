/**
 *
 */
package org.milyn.javabean.virtual.performance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author maurice_zeijen
 *
 */
public class VirtualPerformance {

	public static final Log log = LogFactory.getLog(VirtualPerformance.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int num = 10000;

		String[] keys = generateKeys(100);

		VirtualPerformanceTest[] toTest= {
			new HashMapPerformanceTest(),
			new JavassistVirtualBeanPerformanceTest()
		};

		putPerformance(toTest, num, keys);
		gc();
		getPerformance(toTest, num, keys);
		gc();
		directPutPerformance(toTest, num, keys);

	}

	public static void putPerformance(VirtualPerformanceTest[] toTest, int num, String[] keys) {

		log.info("Testing 'put' performance");

		for (int i = 0; i < toTest.length; i++) {
			toTest[i].put(num, keys);
		}
	}


	public static void getPerformance(VirtualPerformanceTest[] toTest, int num, String[] keys) {

		log.info("Testing 'get' performance");


		for (int i = 0; i < toTest.length; i++) {
			toTest[i].get(num, keys);
		}
	}

	public static void directPutPerformance(VirtualPerformanceTest[] toTest, int num, String[] keys) {

		log.info("Testing 'directPut' performance");


		for (int i = 0; i < toTest.length; i++) {
			toTest[i].directPut(num, keys);
		}
	}

	public static String[] generateKeys(int num) {

		String[] keys = new String[num];

		for (int i = 0; i < num; i++) {
			keys[i] = "test" + i;
		}

		return keys;
	}

	public static void gc() {

		System.gc();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

	}
}
