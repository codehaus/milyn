/**
 *
 */
package org.milyn.javabean.performance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.milyn.util.ClassUtil;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class DummyPerformanceTest {

	private static final Log logger = LogFactory.getLog(DummyPerformanceTest.class);

	/**
	 * @param args
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, SAXException {
		try {
			logger.info("Start");

			boolean simple = true;

			String file = simple ? "/smooks-config-simple-dummy.xml" : "/smooks-config-orders-dummy.xml";

			String name = simple ? "simple" : "orders";

			test(file, name + "-1.xml", name + "-1.xml");
			//test(file, name + "-1.xml", name + "-500.xml", setterMethodInvocatorFactoryImpl);
			//test(file, name + "-1.xml", name + "-5000.xml", setterMethodInvocatorFactoryImpl);
			//test(file, name + "-1.xml", name + "-50000.xml", setterMethodInvocatorFactoryImpl);
			test(file, name + "-1.xml", name + "-500000.xml");



		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	/**
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void test(String configFile, String warmupFilename, String inFilename) throws IOException, SAXException {
		Long beginTime = System.currentTimeMillis();

		String packagePath = ClassUtil.toFilePath(PerformanceTest.class.getPackage());
		Smooks smooks = new Smooks(packagePath + configFile);

		ExecutionContext executionContext = smooks.createExecutionContext();

		JavaResult result = new JavaResult();

		File warmupFile = new File(warmupFilename);
		File inFile  = new File(inFilename);
		Long endTime = System.currentTimeMillis();

		logger.info(inFile + " initialize: " + (endTime - beginTime) + "ms");

		beginTime = System.currentTimeMillis();

		smooks.filter(new StreamSource(new InputStreamReader(new FileInputStream(warmupFile))), result, executionContext);

		endTime = System.currentTimeMillis();

		logger.info(inFile + " filter warmup: " + (endTime - beginTime) + "ms");

		beginTime = System.currentTimeMillis();

		smooks.filter(new StreamSource(new InputStreamReader(new FileInputStream(inFile))), result, executionContext);

		endTime = System.currentTimeMillis();

		logger.info(inFile + " filter run: " + (endTime - beginTime) + "ms");

		System.gc();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
}
