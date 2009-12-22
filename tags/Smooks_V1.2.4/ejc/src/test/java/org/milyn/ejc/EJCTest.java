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
package org.milyn.ejc;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.IOException;

import org.milyn.edisax.EDIConfigurationException;
import org.xml.sax.SAXException;

/**
 * EJCTest tests compiling edi-mapping to classModel.
 *
 * @author bardl 
 */
public class EJCTest extends TestCase {

    public void testCompilation() throws EDIConfigurationException, IOException, SAXException, IllegalNameException, ClassNotFoundException {
//        InputStream in = null;
//        try {
//            String configName = "edi-to-xml-order-mapping.xml";
//            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(configName);
//
//            EJC ejc = new EJC();
//            ejc.compile(in, configName, "test.pakageName", Thread.currentThread().getContextClassLoader().getResource("").getFile());
//        } finally {
//            if (in != null) {
//                in.close();
//            }
//        }
    }
}
