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
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.Diff;
import org.junit.BeforeClass;
import org.junit.Test;
import org.milyn.delivery.sax.SAXElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Attributes2Impl;

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
	public void visitAfter_textMessage_DOM() throws ParserConfigurationException, JMSException, SAXException, IOException
	{
		final String expectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><testelement/>";
        
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = document.createElement( "testelement" );
        Message message = strategy.createJMSMessage( element, null, jmsSession ) ;
        
        assertTrue ( message instanceof TextMessage );
        
        TextMessage textMessage = (TextMessage) message;
        Diff diff = new Diff( expectedXML, textMessage.getText() );
        assertXMLEqual( diff, true );
        
	}
	
	@Test
	public void visitAfter_textMessage_SAX() throws ParserConfigurationException, JMSException, IOException, SAXException
	{
		final String expectedXML = "<a></a>";
        SAXElement saxElement = new SAXElement("http://x", "a", "x", new Attributes2Impl(), null);
        
        Message message = strategy.createJMSMessage( saxElement, null, jmsSession ) ;
        assertTrue ( message instanceof TextMessage );
        
        TextMessage textMessage = (TextMessage) message;
        Diff diff = new Diff( expectedXML, textMessage.getText() );
        assertXMLEqual( diff, true );
		
	}
	
	@BeforeClass
	public static void setup() throws JMSException
	{
		JMSMockObjectFactory jmsObjectFactory = new JMSMockObjectFactory();
		MockConnectionFactory connectionFactory = jmsObjectFactory.getMockConnectionFactory();
		jmsSession = connectionFactory.createQueueConnection().createQueueSession( false, Session.AUTO_ACKNOWLEDGE );
	}

}
