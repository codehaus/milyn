/*
	Milyn - Copyright (C) 2003

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

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.MockContainerRequest;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 *
 * @author tfennelly
 */
public class BeanPopulatorTest extends TestCase {

    public void testConstructorConfigValidation() {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration("x", ProcessingPhasePopulatorUnit.class.getName());
        
        testConstructorConfigValidation(config, "'beanId' param not specified");

        config.setParameter("beanId", "beanID");

        testConstructorConfigValidation(config, "'setterName' param not specified");

        config.setParameter("setterName", "setX");
        config.setParameter("beanClass", " ");

        testConstructorConfigValidation(config, "beanClass' param empty");

        config.removeParameter("beanClass");
        config.setParameter("beanClass", MyBadBean.class.getName());

        testConstructorConfigValidation(config, "doesn't have a default constructor");

        config.removeParameter("beanClass");
        config.setParameter("beanClass", MyGoodBean.class.getName());

        ProcessingPhasePopulatorUnit pppu;

        config.setParameter("attributeName", "attributeX");

        pppu = new ProcessingPhasePopulatorUnit(config);
    }

    private void testConstructorConfigValidation(SmooksResourceConfiguration config, String expected) {
        try {
            new ProcessingPhasePopulatorUnit(config);
            fail("Expected SmooksConfigurationException - " + expected);
        } catch(SmooksConfigurationException e) {
            if(e.getMessage().indexOf(expected) == -1) {
                fail("Expected message to contain [" + expected + "]. Actual [" + e.getMessage() + "]");
            }
        }
    }

    public void test_visit_validateSetterName() throws SAXException, IOException {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration("x", ProcessingPhasePopulatorUnit.class.getName());
        MockContainerRequest request = new MockContainerRequest();
        Document doc = XmlUtil.parseStream(getClass().getResourceAsStream("testxml.txt"), false, true);
        
        config.setParameter("beanId", "userBean");
        config.setParameter("beanClass", MyGoodBean.class.getName());
        config.setParameter("attributeName", "phoneNumber");
        config.setParameter("setterName", "setX");
        ProcessingPhasePopulatorUnit pppu = new ProcessingPhasePopulatorUnit(config);
        try {
            pppu.visit(doc.getDocumentElement(), request);
            fail("Expected SmooksConfigurationException");
        } catch(SmooksConfigurationException e) {
            assertEquals("Bean [userBean] configuration invalid.  Bean setter method [setX] not found.  Bean: [userBean:org.milyn.javabean.MyGoodBean].", e.getMessage());
        }
    }
    
    public void test_visit() throws SAXException, IOException {
        SmooksResourceConfiguration config = null;
        ProcessingPhasePopulatorUnit pppu;
        MockContainerRequest request = new MockContainerRequest();
        Document doc = XmlUtil.parseStream(getClass().getResourceAsStream("testxml.txt"), false, true);

        // create the configuration for populating the "name" property
        config = new SmooksResourceConfiguration("x", ProcessingPhasePopulatorUnit.class.getName());
        config.setParameter("beanId", "userBean");
        config.setParameter("beanClass", MyGoodBean.class.getName());
        config.setParameter("attributeName", "name");
        pppu = new ProcessingPhasePopulatorUnit(config);
        pppu.visit(doc.getDocumentElement(), request);

        // create the configuration for populating the "phoneNumber" property
        config = new SmooksResourceConfiguration("x", ProcessingPhasePopulatorUnit.class.getName());
        config.setParameter("beanId", "userBean");
        config.setParameter("attributeName", "phoneNumber");
        pppu = new ProcessingPhasePopulatorUnit(config);
        pppu.visit(doc.getDocumentElement(), request);
        
        // create the configuration for populating the "phoneNumber" property
        config = new SmooksResourceConfiguration("x", ProcessingPhasePopulatorUnit.class.getName());
        config.setParameter("beanId", "userBean");
        config.setParameter("setterName", "setAddress");
        pppu = new ProcessingPhasePopulatorUnit(config);
        pppu.visit(doc.getDocumentElement(), request);
        
        // Check did we get the phoneNumber populated on the bean
        MyGoodBean bean = (MyGoodBean)BeanAccessor.getBean("userBean", request);
        assertEquals("Myself", bean.getName());
        assertEquals("0861070070", bean.getPhoneNumber());
        assertEquals("Skeagh Bridge...", bean.getAddress());
    }
}
