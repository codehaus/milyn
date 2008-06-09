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
package org.milyn.javabean.expressionbinding;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ExpressionBindingTest extends TestCase {

    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("01_binding.xml"));
        JavaResult result = new JavaResult();

        smooks.filter(new StreamSource(getClass().getResourceAsStream("01_message.xml")), result);

        Message message1 = (Message) result.getBean("message1");
        assertEquals(946143900000L, message1.getDate().getTime());
        assertEquals(977766300000L, message1.getDatePlus1Year().getTime());

        assertDateValue(result, "message2");
        assertDateValue(result, "message3");

        Map message2 = (Map) result.getBean("message2");
        Date messageDate = (Date) message2.get("datePlus1Year");
        System.out.println("Date plus 1 year: " + messageDate);
        assertEquals(977766300000L, messageDate.getTime());

        assertTrue(message2 == message1.getMessage2());        
    }

    private void assertDateValue(JavaResult result, String beanId) {
        Map message = (Map) result.getBean(beanId);
        Date messageDate = (Date) message.get("date");
        System.out.println("Date: " + messageDate);
        assertEquals(946143900000L, messageDate.getTime());
    }
}
