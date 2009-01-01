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
package org.milyn.javabean.programatic;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.javabean.Bean;
import org.milyn.javabean.Header;
import org.milyn.javabean.Order;
import org.milyn.payload.JavaResult;

import javax.xml.transform.stream.StreamSource;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class ProgramaticConfigTest extends TestCase {

    public void test() {
        Smooks smooks = new Smooks();

        Bean orderBean = new Bean(Order.class, "order", "/order", smooks);

        Bean headerBean = new Bean(Header.class, "header", "/order", smooks)
                                    .bindTo("order", orderBean)
                                    .bindTo("customerNumber", "header/customer/@number")
                                    .bindTo("customerName", "header/customer")
                                    .bindTo("privatePerson", "header/privatePerson");

        orderBean.bindTo("header", headerBean);

        JavaResult result = new JavaResult();
        smooks.filter(new StreamSource(getClass().getResourceAsStream("../order-01.xml")), result);

        Order order = (Order) result.getBean("order");
        int identity = System.identityHashCode(order);

        assertEquals("Order:" + identity + "[header[null, 123123, Joe, false, Order:" + identity + "]\n" +
                     "orderItems[null]\n" +
                     "norderItemsArray[null]]", order.toString());
    }
}
