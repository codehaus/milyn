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
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ListOfListsTest extends TestCase {

    public void test() throws IOException, SAXException {              
        Smooks smooks = new Smooks(getClass().getResourceAsStream("list-of-lists-config.xml"));
        JavaResult javaResult = new JavaResult();

        smooks.filter(new StreamSource(getClass().getResourceAsStream("list-of-lists-message.xml")), javaResult, smooks.createExecutionContext());
//        assertEquals("[{blockNum=1, orderItems=[{price=8.9, productId=111, quantity=2}, {price=5.2, productId=222, quantity=7}]}, {blockNum=2, orderItems=[{price=8.9, productId=333, quantity=2}, {price=5.2, productId=444, quantity=7}]}]", javaResult.getBean("order").toString());
        assertEquals(getExpectedOrderArray(), javaResult.getBean("order"));
    }
    
    private ArrayList<HashMap<String, Object>> getExpectedOrderArray()
    {
    	ArrayList<HashMap<String,Object>> order = new ArrayList<HashMap<String, Object>>();
    	ArrayList<HashMap<String,Object>> orderItems = new ArrayList<HashMap<String,Object>>();
    	
    	HashMap<String, Object> orderItemBlock1 = new HashMap<String, Object>();
    	orderItemBlock1.put("blockNum", 1);
    	HashMap<String, Object> orderItem1_1 = getOrderItem(111, 2, 8.9);
    	HashMap<String, Object> orderItem1_2 = getOrderItem(222, 7, 5.2);
    	
    	orderItems.add(orderItem1_1);
    	orderItems.add(orderItem1_2);
    	
    	orderItemBlock1.put("orderItems", orderItems);
    	
    	HashMap<String, Object> orderItemBlock2 = new HashMap<String, Object>();
    	orderItemBlock2.put("blockNum", 2);
    	HashMap<String, Object> orderItem2_1 = getOrderItem(333, 2, 8.9);
    	HashMap<String, Object> orderItem2_2 = getOrderItem(444, 7, 5.2);
    	ArrayList<HashMap<String,Object>> orderItems2 = new ArrayList<HashMap<String,Object>>();
    	orderItems2.add(orderItem2_1);
    	orderItems2.add(orderItem2_2);
    	orderItemBlock2.put("orderItems", orderItems2);
    	
    	order.add(orderItemBlock1);
    	order.add(orderItemBlock2);
    	
    	return order;
    }

	private HashMap<String, Object> getOrderItem(final long productId, final int quantity, final double price)
	{
		HashMap<String, Object> orderItem = new HashMap<String, Object>();
    	orderItem.put("productId", productId);
    	orderItem.put("quantity", quantity);
    	orderItem.put("price", price);
		return orderItem;
	}
    
}
