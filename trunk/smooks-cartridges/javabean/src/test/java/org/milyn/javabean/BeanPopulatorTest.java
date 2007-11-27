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

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.milyn.Smooks;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.io.StreamUtils;
import org.milyn.util.ClassUtil;
import org.xml.sax.SAXException;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

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
        SmooksResourceConfiguration config = new SmooksResourceConfiguration("x", BeanPopulator.class.getName());
        
        testConstructorConfigValidation(config, "Invalid Smooks bean configuration.  'beanClass' <param> not specified.");

        config.setParameter("beanId", "x");
        config.setParameter("beanClass", " ");

        testConstructorConfigValidation(config, "Invalid Smooks bean configuration.  'beanClass' <param> not specified.");
    }

    private void testConstructorConfigValidation(SmooksResourceConfiguration config, String expected) {
        try {
            Configurator.configure(new BeanPopulator(), config);
            fail("Expected SmooksConfigurationException - " + expected);
        } catch(SmooksConfigurationException e) {
            Throwable t = e.getCause();
            if(t.getMessage().indexOf(expected) == -1) {
                e.printStackTrace();
                fail("Expected message to contain [" + expected + "]. Actual [" + t.getMessage() + "]");
            }
        }
    }

    /*
    public void test_visit_validateSetterName() throws SAXException, IOException {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration("x", BeanPopulator.class.getName());
        MockExecutionContext request = new MockExecutionContext();
        Document doc = XmlUtil.parseStream(getClass().getResourceAsStream("testxml.txt"), XmlUtil.VALIDATION_TYPE.NONE, true);
        
        config.setParameter("beanId", "userBean");
        config.setParameter("beanClass", MyGoodBean.class.getName());
        config.setParameter("attributeName", "phoneNumber");
        config.setParameter("setterName", "setX");
        BeanPopulator pppu = new BeanPopulator();
        Configurator.configure(pppu, config);
        try {
            pppu.visitBefore(doc.getDocumentElement(), request);
            fail("Expected SmooksConfigurationException");
        } catch(SmooksConfigurationException e) {
            assertEquals("Bean [userBean] configuration invalid.  Bean setter method [setX(java.lang.String)] not found on type [org.milyn.javabean.MyGoodBean].  You may need to set a 'decoder' on the binding config.", e.getMessage());
        }
    }
    */

    public void test_visit_2() throws SAXException, IOException {
        test_visit_userBean("compressed-config.xml");        
    }

    public void test_visit_userBean(String configName) throws SAXException, IOException {

        Smooks smooks = new Smooks(getClass().getResourceAsStream(configName));
        StandaloneExecutionContext executionContext = smooks.createExecutionContext();

        smooks.filter(new StreamSource(getClass().getResourceAsStream("testxml.txt")), new DOMResult(), executionContext);

        MyGoodBean bean = (MyGoodBean)BeanAccessor.getBean("userBean", executionContext);
        assertNotNull("Null bean", bean);
        assertEquals("Myself", bean.getName());
        assertEquals("0861070070", bean.getPhoneNumber());
        assertEquals("Skeagh Bridge...", bean.getAddress());
    }

    public void test_populate_Order() throws SAXException, IOException, InterruptedException {
        test_populate_Order("order-01-smooks-config.xml");
        test_populate_Order("order-01-smooks-config-sax.xml");
    }

    public void test_populate_Order(String configName) throws SAXException, IOException, InterruptedException {

        String packagePath = ClassUtil.toFilePath(getClass().getPackage());
        Smooks smooks = new Smooks(packagePath + "/" + configName);
        StandaloneExecutionContext executionContext = smooks.createExecutionContext();
        String resource = StreamUtils.readStream(new InputStreamReader(getClass().getResourceAsStream("order-01.xml")));
        JavaResult result = new JavaResult();

        smooks.filter(new StreamSource(new StringReader(resource)), result, executionContext);

        Order order = (Order) result.getResultMap().get("order");

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
