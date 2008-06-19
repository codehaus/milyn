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

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ListOfListsTest extends TestCase {

    public void test() throws IOException, SAXException {              
        Smooks smooks = new Smooks(getClass().getResourceAsStream("list-of-lists-config.xml"));
        JavaResult javaResult = new JavaResult();

        smooks.filter(new StreamSource(getClass().getResourceAsStream("list-of-lists-message.xml")), javaResult, smooks.createExecutionContext());
        assertEquals("[{blockNum=1, orderItems=[{price=8.9, productId=111, quantity=2}, {price=5.2, productId=222, quantity=7}]}, {blockNum=2, orderItems=[{price=8.9, productId=333, quantity=2}, {price=5.2, productId=444, quantity=7}]}]", javaResult.getBean("order").toString());
    }
}
