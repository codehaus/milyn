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
import org.milyn.javabean.bcm.OptimizedMap;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class JavassistMapGeneratorTest extends TestCase {
	
	JavassistMapGenerator generator;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		generator = new JavassistMapGenerator();
		generator.initialize(new MockApplicationContext());
	}
	
	public void testGenerateNoKeys() {
		
		Map map = generator.generateMap("Test", new ArrayList<String>());
		
		assertNotNull(map);
		assertTrue(map instanceof HashMap);
				
	}
	
	public void testGenerateOneKey() {
		
		String testKey = "test";
		String testValue = "testValue";
		
		List<String> keys = new ArrayList<String>();
		keys.add(testKey);
		
		Map map = generator.generateMap("Test", keys);
		
		assertNotNull(map);
		assertTrue(map instanceof OptimizedMap);
						
		map.put(testKey, testValue);
		
		assertFalse(((OptimizedMap)map).isBackingMapUsed());
		
		assertTrue(map.containsKey(testKey));
		assertEquals(testValue, map.get(testKey));
		assertEquals(1, map.size());
		
		String testKey2 = "test2";
		String testValue2 = "testValue2";
		
		map.put(testKey2, testValue2);
		
		assertTrue(((OptimizedMap)map).isBackingMapUsed());
		
		assertTrue(map.containsKey(testKey2));
		assertEquals(testValue2, map.get(testKey2));
		assertEquals(2, map.size());
	}

}
