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
package example;

import junit.framework.*;

import java.io.*;
import java.util.List;

import com.acme.order.model.*;
import org.xml.sax.SAXException;

/**
 * Test that example produces expected result.
 *  
 * @author bardl
 */
public class    EJCtoJavaTest extends TestCase {

    public void test() throws IOException, SAXException {
        OrderFactory orderFactory = OrderFactory.getInstance();
        FileInputStream ediStream = new FileInputStream("input-message.edi");

        try {
            Order order = orderFactory.fromEDI(ediStream);

            Header header = order.getHeader();
            Name name = header.getCustomerDetails().getName();
            List<OrderItem> orderItems = order.getOrderItems().getOrderItem();
            OrderItem orderItem1 = orderItems.get(0);
            OrderItem orderItem2 = orderItems.get(1);

            assertEquals("Fletcher, Harry", name.getLastname() + ", " + name.getFirstname());
            assertEquals("harry.fletcher@gmail", header.getCustomerDetails().getEmail());
            assertNotNull(header.getDate());
            assertEquals("364, The 40-Year-Old Virgin, 29.98", orderItem1.getProductId() + ", " + orderItem1.getTitle() + ", " + orderItem1.getPrice());
            assertEquals("299, Pulp Fiction, 29.99", orderItem2.getProductId() + ", " + orderItem2.getTitle() + ", " + orderItem2.getPrice());
        } finally {
            ediStream.close();
        }
    }
}
