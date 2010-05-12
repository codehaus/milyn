/*
	Milyn - Copyright (C) 2006 - 2010

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

import java.io.IOException;

import org.milyn.archive.Archive;
import org.milyn.edisax.EDIConfigurationException;
import org.xml.sax.SAXException;

/**
 * EJCTest tests compiling edi-mapping to classModel.
 *
 * @author bardl 
 */
public class EJCTest extends TestCase {

    public void testOrderModel() throws EDIConfigurationException, IOException, SAXException, IllegalNameException, ClassNotFoundException {
    	String configName = "order-mapping.xml";
        EJC ejc = new EJC();
                    
        ClassModel classModel = ejc.compile(getClass().getResourceAsStream(configName), configName, "test.pakageName");
        
        ECTTestUtil.assertEquals(classModel, getClass().getResourceAsStream("order-mapping-model.txt"));

        Archive archive = ECTTestUtil.buildModelArchive(classModel);
    }
}
