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
package org.milyn.cdr.xsd11.importtests;

import junit.framework.TestCase;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.milyn.Smooks;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.payload.StringSource;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ImportTest extends TestCase {

    public void test_11_imports_10() throws IOException, SAXException {
        testConfig("11_import_10.xml");
    }

    public void test_10_imports_11() throws IOException, SAXException {
        try {
            testConfig("10_import_11.xml");
            fail("Expected SmooksConfigurationException.");
        } catch(SmooksConfigurationException e) {
            assertEquals("Unsupported import of a v1.1 configuration from inside a v1.0 configuration.  Path to configuration: '/[root-config]/[11Config.xml]'.", e.getMessage());
        }
    }

    private void testConfig(String config) throws IOException, SAXException {
        Smooks smooks = new Smooks("/org/milyn/cdr/xsd11/importtests/" + config);
        smooks.filter(new StringSource("<a/>"), null);
    }
}
