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
package org.milyn.routing.jms;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.Diff;
import org.junit.BeforeClass;
import org.junit.Test;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.MockApplicationContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.javabean.BeanAccessor;
import org.mockejb.jndi.MockContextFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mockrunner.mock.ejb.EJBMockObjectFactory;
import com.mockrunner.mock.jms.JMSMockObjectFactory;
import com.mockrunner.mock.jms.MockQueue;
import com.mockrunner.mock.jms.MockQueueConnectionFactory;

/**
 * Unit test for the Router class
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>				
 *
 */
public class RouterTest 
{
	private String selector = "x";
	private SmooksResourceConfiguration config = new SmooksResourceConfiguration(selector, Router.class.getName());
	private String queueName = "queue/testQueue";
	
	private static MockQueue queue;
	private static MockQueueConnectionFactory connectionFactory;
	
	@Test( expected = SmooksConfigurationException.class )
	public void config_validation_missing_destinationType()
	{
        Configurator.configure( new Router(), config, new MockApplicationContext() );
	}
	
	@Test
	public void visitAfter_textMessage() throws ParserConfigurationException, JMSException, SAXException, IOException
	{
		final String expectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><testelement/>";
        config.setParameter( "destinationName", queueName );
        
        Router router = new Router();
        Configurator.configure( router, config, new MockApplicationContext() );
        
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = document.createElement( "testelement" );
        router.visitAfter( element , new MockExecutionContext() );
        
        Message message = queue.getMessage();
        assertTrue ( message instanceof TextMessage );
        
        TextMessage textMessage = (TextMessage) message;
        Diff diff = new Diff(expectedXML, textMessage.getText() );
        assertXMLEqual( diff, true );
	}
	
	@Test
	public void visitAfter_textMessage_beanId() throws ParserConfigurationException, JMSException, SAXException, IOException
	{
		final String beanId = "beanId";
		final String address = "Fleminggatan";
		final String name = "Daniel";
		final String phoneNumber = "555-555-5555";
    	final String expectedString = "TestBean [name:" + name + ", address:" + address + ", phoneNumber:" + phoneNumber + "]";
    	
		TestBean bean = new TestBean();
		bean.setAddress( address );
		bean.setName( name );
		bean.setPhoneNumber( phoneNumber );
		
		final boolean addToList = false;
        MockExecutionContext executionContext = new MockExecutionContext();
        BeanAccessor.addBean( beanId, bean, executionContext, addToList );
        
        config.setParameter( "destinationName", queueName );
        config.setParameter( "beanId", beanId );
        Router router = new Router();
        Configurator.configure( router, config, new MockApplicationContext() );
        
        router.visitAfter( (SAXElement)null, executionContext );
        
        Message message = queue.getMessage();
        assertTrue ( message instanceof TextMessage );
        
        TextMessage textMessage = (TextMessage) message;
        assertEquals( expectedString, textMessage.getText() );
	}
	
	@BeforeClass
	public static void setUpInitialContext() throws Exception
    {
        EJBMockObjectFactory mockObjectFactory = new EJBMockObjectFactory();
        Context context = mockObjectFactory.getContext();
        JMSMockObjectFactory jmsObjectFactory = new JMSMockObjectFactory();
		connectionFactory = jmsObjectFactory.getMockQueueConnectionFactory();
        context.bind("ConnectionFactory",  connectionFactory);
        queue = jmsObjectFactory.getDestinationManager().createQueue("testQueue");
        context.bind("queue/testQueue", queue);
		MockContextFactory.setAsInitial();
    }
	
}
