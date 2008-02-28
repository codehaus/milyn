/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/
package org.milyn.javabean;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.java.JavaResult;
import org.milyn.io.StreamUtils;
import org.milyn.util.ClassUtil;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanBindingPopulatorTest extends TestCase {
	
	
	public void test_01_hierarchically() throws IOException, SAXException {
        test_01("bb-01-smooks-config.xml", "bb-01-hierarchically.xml");
        test_01("bb-01-smooks-config-sax.xml", "bb-01-hierarchically.xml");
	}
	
	public void test_01_flat() throws IOException, SAXException {
        test_01("bb-01-smooks-config.xml", "bb-01-flat.xml");
        test_01("bb-01-smooks-config-sax.xml", "bb-01-flat.xml");
	}

	/**
	 * @param configFile
	 * @param dataFile
	 * @throws IOException
	 * @throws SAXException
	 */
	private void test_01(String configFile, String dataFile)
			throws IOException, SAXException {
		String packagePath = ClassUtil.toFilePath(getClass().getPackage());
        Smooks smooks = new Smooks(packagePath + "/" + configFile);
        ExecutionContext executionContext = smooks.createExecutionContext();

        String resource = StreamUtils.readStream(new InputStreamReader(getClass().getResourceAsStream(dataFile)));
        JavaResult result = new JavaResult();
        
        smooks.filter(new StreamSource(new StringReader(resource)), result, executionContext);
        
        @SuppressWarnings("unchecked")
        ArrayList<A> as = (ArrayList<A>) result.getBean("root");
        
        assertNotNull(as);
        assertEquals(2, as.size());
           
		A a1 = as.get(0);
		assertNotNull(a1.getBList());
		assertEquals(3, a1.getBList().size());
		assertEquals("b1", a1.getBList().get(0).getValue());
		assertEquals(a1, a1.getBList().get(0).getA());
		
		A a2 = as.get(1);
		assertNotNull(a2.getBList());
		assertEquals(3, a2.getBList().size());
		assertEquals("b4", a2.getBList().get(0).getValue());
		assertEquals(a2, a2.getBList().get(0).getA());
	}

}
