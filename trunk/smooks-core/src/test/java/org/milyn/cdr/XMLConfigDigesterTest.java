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

package org.milyn.cdr;

import java.io.IOException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * Unit tests forthe ArciveDef class.
 * @author tfennelly
 */
public class XMLConfigDigesterTest extends TestCase {

	public void test_digestConfig() throws SAXException, IOException {
		// Valid doc
        SmooksResourceConfigurationList resList = XMLConfigDigester.digestConfig("test", getClass().getResourceAsStream("testconfig1.cdrl"));

        assertEquals(2, resList.size());
        
        // Test the overridden attribute values from the 1st config entry.
        assertEquals("a", resList.get(0).getSelector());
        assertEquals("xxx", resList.get(0).getUseragentExpressions()[0].getExpression());
        assertEquals("x.txt", resList.get(0).getPath());
        assertEquals("http://milyn.codehaus.org/smooks", resList.get(0).getNamespaceURI());
        
        // Test the default inherited attribute values from the 2nd config entry.
        assertEquals("b", resList.get(1).getSelector());
        assertEquals("yyy", resList.get(1).getUseragentExpressions()[0].getExpression());
        assertEquals("y.txt", resList.get(1).getPath());
        assertEquals("http://milyn.codehaus.org/smooks-default", resList.get(1).getNamespaceURI());
        
        // Test the parameters on the 2nd config entry.
        assertEquals("param1Val", resList.get(1).getStringParameter("param1"));
        assertEquals(true, resList.get(1).getBoolParameter("param2", false));
        assertEquals(false, resList.get(1).getBoolParameter("param3", true));
        assertEquals(false, resList.get(1).getBoolParameter("param4", false));
	}
}
