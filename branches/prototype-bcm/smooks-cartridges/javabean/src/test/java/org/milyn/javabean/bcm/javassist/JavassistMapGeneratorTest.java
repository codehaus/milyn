/**
 * 
 */
package org.milyn.javabean.bcm.javassist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.milyn.container.MockApplicationContext;
import org.milyn.javabean.virtual.VirtualBean;
import org.milyn.javabean.virtual.javassist.JavassistVirtualBeanGenerator;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class JavassistMapGeneratorTest extends TestCase {
	
	JavassistVirtualBeanGenerator generator;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		generator = new JavassistVirtualBeanGenerator();
		generator.initialize(new MockApplicationContext());
	}
	
	public void testGenerateNoKeys() {
		
		Map map = generator.generate("Test", new ArrayList<String>());
		
		assertNotNull(map);
		assertTrue(map instanceof HashMap);
				
	}
	
	public void testGenerateOneKey() {
		
		String testKey = "test";
		String testValue = "testValue";
		
		List<String> keys = new ArrayList<String>();
		keys.add(testKey);
		
		Map map = generator.generate("Test", keys);
		
		assertNotNull(map);
		assertTrue(map instanceof VirtualBean);
						
		map.put(testKey, testValue);
		
		assertFalse(((VirtualBean)map).isBackingMapUsed());
		
		assertTrue(map.containsKey(testKey));
		assertEquals(testValue, map.get(testKey));
		assertEquals(1, map.size());
		
		String testKey2 = "test2";
		String testValue2 = "testValue2";
		
		map.put(testKey2, testValue2);
		
		assertTrue(((VirtualBean)map).isBackingMapUsed());
		
		assertTrue(map.containsKey(testKey2));
		assertEquals(testValue2, map.get(testKey2));
		assertEquals(2, map.size());
	}

}
