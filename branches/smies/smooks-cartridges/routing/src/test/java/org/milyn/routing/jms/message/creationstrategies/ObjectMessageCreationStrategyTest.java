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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.Diff;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mockrunner.mock.jms.JMSMockObjectFactory;
import com.mockrunner.mock.jms.MockConnectionFactory;

/**
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>				
 *
 */
public class ObjectMessageCreationStrategyTest
{
	private ObjectMessageCreationStrategy strategy = new ObjectMessageCreationStrategy();
	
	private static Session jmsSession;
	
	@Test
	public void visitAfter_objectMessage_DOM() throws ParserConfigurationException, JMSException, SAXException, IOException
	{
		final String expectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><testelement/>";
        
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = document.createElement( "testelement" );
        element.setNodeValue( "kalle" );
        Message message = strategy.createJMSMessage( element, null, jmsSession ) ;
        
        assertTrue ( message instanceof ObjectMessage );
        ObjectMessage objectMessage = (ObjectMessage) message;
        assertTrue( objectMessage.getObject() instanceof String );
        Diff diff = new Diff(expectedXML, objectMessage.getObject().toString() );
        assertXMLEqual( diff, true );
        
	}
	
	@Test
	@Ignore
	public void visitAfter_objectMessage_SAX() throws ParserConfigurationException, JMSException, IOException
	{
		//todo
	}
	
	@BeforeClass
	public static void setup() throws JMSException
	{
		JMSMockObjectFactory jmsObjectFactory = new JMSMockObjectFactory();
		MockConnectionFactory connectionFactory = jmsObjectFactory.getMockConnectionFactory();
		jmsSession = connectionFactory.createQueueConnection().createQueueSession( false, Session.AUTO_ACKNOWLEDGE );
	}

}
