/*
 * Milyn - Copyright (C) 2006
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.milyn.routing.jms.message.creationstrategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.milyn.container.MockExecutionContext;
import org.milyn.routing.jms.TestBean;
import org.milyn.routing.util.RouterTestHelper;
import org.xml.sax.SAXException;

import com.mockrunner.mock.jms.JMSMockObjectFactory;
import com.mockrunner.mock.jms.MockConnectionFactory;

/**
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class TextMessageCreationStrategyTest
{
	private TextMessageCreationStrategy strategy = new TextMessageCreationStrategy();

	private static Session jmsSession;

	@Test
	public void createJMSMessage() throws ParserConfigurationException, JMSException, SAXException, IOException
	{
		final String beanId = "123";
		final TestBean bean = RouterTestHelper.createBean();
        MockExecutionContext executionContext = RouterTestHelper.createExecutionContext( beanId, bean );

        Message message = strategy.createJMSMessage( beanId, executionContext, jmsSession ) ;

        assertTrue ( message instanceof TextMessage );

        TextMessage textMessage = (TextMessage) message;
        assertEquals( bean.toString(), textMessage.getText() );

	}

	@BeforeClass
	public static void setup() throws JMSException
	{
		JMSMockObjectFactory jmsObjectFactory = new JMSMockObjectFactory();
		MockConnectionFactory connectionFactory = jmsObjectFactory.getMockConnectionFactory();
		jmsSession = connectionFactory.createQueueConnection().createQueueSession( false, Session.AUTO_ACKNOWLEDGE );
	}

}
