/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License (version 2.1) as published by the Free Software
 * Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */

package org.smooks.example.processors;

import com.thoughtworks.xstream.XStream;
import org.apache.camel.Consume;
import org.milyn.edi.unedifact.d93a.ORDERS.Orders;

/**
 * Orders Processing Service.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class OrdersService {

    @Consume(uri="jms:order")
    public void processOrder(Orders order) {

        System.out.println("================ Orders Message ================");
        System.out.println(new XStream().toXML(order.getBeginningOfMessage()));
        System.out.println(new XStream().toXML(order.getDateTimePeriod()));
        
    }
}
