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
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.MockExecutionContext;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.xml.XmlUtil;
import org.milyn.Smooks;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMResult;

/**
 *
 * @author tfennelly
 */
public class BeanPopulatorTest extends TestCase {

    public void testX() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

        sdf.clone();
        Date date = sdf.parse("Wed Nov 15 13:45:28 EST 2006");
        Calendar cal = (Calendar) sdf.getCalendar().clone();
        System.out.println(cal.getTime());
        System.out.println(cal.getTimeZone());
    }

    public void testConstructorConfigValidation() {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration("x", ProcessingPhaseBeanPopulator.class.getName());
        
        testConstructorConfigValidation(config, "Invalid Smooks bean configuration.  Both 'beanId' and 'beanClass' params are unspecified.");

        config.setParameter("beanId", " ");
        config.setParameter("beanClass", " ");

        config.setParameter("setterName", "setX");

        testConstructorConfigValidation(config, "Invalid Smooks bean configuration.  Both 'beanId' and 'beanClass' params are unspecified.");

        config.removeParameter("beanClass");
        config.setParameter("beanClass", MyBadBean.class.getName());

        testConstructorConfigValidation(config, "doesn't have a public default constructor");

        config.removeParameter("beanClass");
        config.setParameter("beanClass", MyGoodBean.class.getName());

        config.setParameter("attributeName", "attributeX");

        new ProcessingPhaseBeanPopulator().setConfiguration(config);
    }

    private void testConstructorConfigValidation(SmooksResourceConfiguration config, String expected) {
        try {
            new ProcessingPhaseBeanPopulator().setConfiguration(config);
            fail("Expected SmooksConfigurationException - " + expected);
        } catch(SmooksConfigurationException e) {
            if(e.getMessage().indexOf(expected) == -1) {
                fail("Expected message to contain [" + expected + "]. Actual [" + e.getMessage() + "]");
            }
        }
    }

    public void test_visit_validateSetterName() throws SAXException, IOException {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration("x", ProcessingPhaseBeanPopulator.class.getName());
        MockExecutionContext request = new MockExecutionContext();
        Document doc = XmlUtil.parseStream(getClass().getResourceAsStream("testxml.txt"), XmlUtil.VALIDATION_TYPE.NONE, true);
        
        config.setParameter("beanId", "userBean");
        config.setParameter("beanClass", MyGoodBean.class.getName());
        config.setParameter("attributeName", "phoneNumber");
        config.setParameter("setterName", "setX");
        ProcessingPhaseBeanPopulator pppu = new ProcessingPhaseBeanPopulator();
        pppu.setConfiguration(config);
        try {
            pppu.visitBefore(doc.getDocumentElement(), request);
            fail("Expected SmooksConfigurationException");
        } catch(SmooksConfigurationException e) {
            assertEquals("Bean [userBean] configuration invalid.  Bean setter method [setX(java.lang.String)] not found on type [org.milyn.javabean.MyGoodBean].  You may need to set a 'decoder' on the binding config.", e.getMessage());
        }
    }

    public void test_visit_1() throws SAXException, IOException {
        test_visit_userBean("expanded-config.xml");
    }
    public void test_visit_2() throws SAXException, IOException {
        test_visit_userBean("compressed-config.xml");        
    }

    public void test_visit_userBean(String configName) throws SAXException, IOException {

        Smooks smooks = new Smooks();

        smooks.addConfigurations(configName, getClass().getResourceAsStream(configName));
        StandaloneExecutionContext executionContext = smooks.createExecutionContext();

        smooks.filter(new StreamSource(getClass().getResourceAsStream("testxml.txt")), new DOMResult(), executionContext);

        MyGoodBean bean = (MyGoodBean)BeanAccessor.getBean("userBean", executionContext);
        assertNotNull("Null bean", bean);
        assertEquals("Myself", bean.getName());
        assertEquals("0861070070", bean.getPhoneNumber());
        assertEquals("Skeagh Bridge...", bean.getAddress());
    }

    public void test_populate_Order() throws SAXException, IOException {

        Smooks smooks = new Smooks();

        smooks.addConfigurations("order-01-smooks-config.xml", getClass().getResourceAsStream("order-01-smooks-config.xml"));
        StandaloneExecutionContext executionContext = smooks.createExecutionContext();

        smooks.filter(new StreamSource(getClass().getResourceAsStream("order-01.xml")), new DOMResult(), executionContext);

        Order order = (Order)BeanAccessor.getBean("order", executionContext);

        assertNotNull(order);
        assertNotNull(order.getHeader());
        assertNotNull(order.getOrderItems());
        assertEquals(2, order.getOrderItems().size());

        assertEquals(1163616328000L, order.getHeader().getDate().getTime());
        assertEquals("Joe", order.getHeader().getCustomerName());
        assertEquals(new Long(123123), order.getHeader().getCustomerNumber());
        
        assertTrue("PrivatePerson was not set to true", order.getHeader().getPrivatePerson());

        OrderItem orderItem = order.getOrderItems().get(0);
        assertEquals(8.90d, orderItem.getPrice());
        assertEquals(111, orderItem.getProductId());
        assertEquals(new Integer(2), orderItem.getQuantity());

        orderItem = order.getOrderItems().get(1);
        assertEquals(5.20d, orderItem.getPrice());
        assertEquals(222, orderItem.getProductId());
        assertEquals(new Integer(7), orderItem.getQuantity());
    }
}
