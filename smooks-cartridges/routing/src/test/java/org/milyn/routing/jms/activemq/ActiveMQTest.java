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
package org.milyn.routing.jms.activemq;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Assert;
import org.milyn.Smooks;
import org.milyn.payload.StringSource;
import org.milyn.routing.jms.TestJMSMessageListener;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class ActiveMQTest {

    private static ActiveMQProvider mqProvider;

    @BeforeClass
    public static void startActiveMQ() throws Exception {
        mqProvider = new ActiveMQProvider();
        mqProvider.addQueue("objectAQueue");
        mqProvider.start();
    }

    @AfterClass
    public static void stopActiveMQ() throws Exception {
        mqProvider.stop();
    }

    @Test
    public void test_xml_config() throws IOException, SAXException, JMSException, InterruptedException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config-01.xml"));
        try {
            TestJMSMessageListener listener = new TestJMSMessageListener();

            mqProvider.addQueueListener("objectAQueue", listener);
            smooks.filter(new StringSource("<root><a>1</a><a>2</a><a>3</a></root>"));

            Thread.sleep(500);

            Assert.assertEquals(3, listener.getMessages().size());
        } finally {
            smooks.close();
        }
    }
}
