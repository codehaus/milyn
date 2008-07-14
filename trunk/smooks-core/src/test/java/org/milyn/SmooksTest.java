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

package org.milyn;

import com.sun.org.apache.xerces.internal.parsers.XIncludeParserConfiguration;
import junit.framework.TestCase;
import org.milyn.container.ExecutionContext;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.delivery.JavaContentHandlerFactory;
import org.milyn.delivery.dom.SmooksDOMFilter;
import org.milyn.payload.StringResult;
import org.milyn.payload.StringSource;
import org.milyn.profile.DefaultProfileSet;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author tfennelly
 */
public class SmooksTest extends TestCase {

    private ExecutionContext execContext;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        Smooks smooks = new Smooks();
        SmooksUtil.registerProfileSet(DefaultProfileSet.create("device1", new String[] {"profile1"}), smooks);
        execContext = new StandaloneExecutionContext("device1", smooks.getApplicationContext());
    }
	
	public void test_applyTransform_bad_params() {
		SmooksDOMFilter smooks = new SmooksDOMFilter(execContext);
		
		try {
			smooks.filter((Reader)null);
			fail("Expected exception on null stream");
		} catch (IllegalArgumentException e) {
			//Expected
		} catch (SmooksException e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
	}
	
	public void test_applyTransform_DocumentCheck() {
		SmooksDOMFilter smooks;
		InputStream stream = null;
		Node deliveryNode = null;
		
		stream = getClass().getResourceAsStream("html_1.html");
		smooks = new SmooksDOMFilter(execContext);
		try {
			deliveryNode = smooks.filter(new InputStreamReader(stream));
		} catch (SmooksException e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
		assertNotNull("Null transform 'Document' return.", deliveryNode);
	}

    public void test_setClassPath() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test_setClassLoader_01.xml"));
        TestClassLoader classLoader = new TestClassLoader();
        StringResult result = new StringResult();
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        smooks.setClassLoader(classLoader);

        ExecutionContext execCtx = smooks.createExecutionContext();
        assertTrue(classLoader.requests.contains(JavaContentHandlerFactory.class.getName()));
        assertTrue(contextClassLoader == Thread.currentThread().getContextClassLoader());

        classLoader.requests.clear();
        smooks.filter(new StringSource("<a/>"), result, execCtx);
        assertEquals("<b></b>", result.getResult());
        assertTrue(classLoader.requests.contains(XIncludeParserConfiguration.class.getName()));
        assertTrue(contextClassLoader == Thread.currentThread().getContextClassLoader());
    }

    private class TestClassLoader extends ClassLoader {
        private Set requests = new HashSet();

        public Class<?> loadClass(String name) throws ClassNotFoundException {
            requests.add(name);
            return null;
        }
    }
}
