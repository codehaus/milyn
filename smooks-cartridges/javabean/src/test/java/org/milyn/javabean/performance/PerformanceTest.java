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
import org.milyn.javabean.setter.PropertySetMethodInvocatorFactory;
import org.milyn.javabean.setter.javassist.JavassistSetterMethodInvocatorFactory;
import org.milyn.javabean.setter.reflect.ReflectiveSetterMethodInvocatorFactory;
import org.milyn.payload.JavaResult;
import org.milyn.util.ClassUtil;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class PerformanceTest {

	private static final Log logger = LogFactory.getLog(PerformanceTest.class);
	
	/**
	 * @param args
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, SAXException {
		try {
			logger.info("Start");
			
			boolean simple = true;
			
			String file = simple ? "/smooks-config-simple.xml" : "/smooks-config-orders.xml";
			
			Class<?>[] setterMethodInvocatorFactoryImpls = new Class[] {
					ReflectiveSetterMethodInvocatorFactory.class,
					JavassistSetterMethodInvocatorFactory.class
			};
			
			String name = simple ? "simple" : "orders";
			
			for(Class<?> setterMethodInvocatorFactoryImpl : setterMethodInvocatorFactoryImpls) {
				
				logger.info("SetterMethodInvocatorFactory implementation: " + setterMethodInvocatorFactoryImpl.getSimpleName());
				
				test(file, name + "-500.xml", setterMethodInvocatorFactoryImpl);
				test(file, name + "-500.xml", setterMethodInvocatorFactoryImpl);
				test(file, name + "-5000.xml", setterMethodInvocatorFactoryImpl);
				//test(file, name + "-50000.xml", setterMethodInvocatorFactoryImpl);
				//test(file, name + "-500000.xml" ,setterMethodInvocatorFactoryImpl);
			}
			
	
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	/**
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void test(String configFile, String orderFile, Class setterMethodInvocatorFactoryImpl) throws IOException, SAXException {
		Long beginTime = System.nanoTime();
		
		String packagePath = ClassUtil.toFilePath(PerformanceTest.class.getPackage());
		Smooks smooks = new Smooks(packagePath + configFile);
		
		smooks.getApplicationContext().setAttribute(PropertySetMethodInvocatorFactory.IMPLEMENTATION_CONTEXT_KEY, setterMethodInvocatorFactoryImpl.getName());
		
		ExecutionContext executionContext = smooks.createExecutionContext();
		
		JavaResult result = new JavaResult();
		
		File file = new File(orderFile);
		
		smooks.filter(new StreamSource(new InputStreamReader(new FileInputStream(file))), result, executionContext);
		
		Long endTime = System.nanoTime();
		
		logger.info(orderFile + ": " + (endTime - beginTime) + "ns");
		
		System.gc();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

}
