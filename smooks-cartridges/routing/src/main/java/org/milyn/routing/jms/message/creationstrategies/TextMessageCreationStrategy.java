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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.routing.jms.message.creationstrategies.util.TransformUtil;
import org.w3c.dom.Element;

/**
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>				
 *
 */
public class TextMessageCreationStrategy implements MessageCreationStrategy
{
	private Logger log = Logger.getLogger( TextMessageCreationStrategy.class );

	public Message createJMSMessage( final Element element, final ExecutionContext context, final Session session ) 
		throws SmooksException
	{
		final String msgContent = TransformUtil.transformElement( element );
		return createTextMessage( msgContent, context, session );
	}

	public Message createJMSMessage( SAXElement element, ExecutionContext context, Session session ) 
		throws SmooksException
	{
		final String msgContent = TransformUtil.transformElement( element );
		return createTextMessage( msgContent, context, session );
	}
	
	private TextMessage createTextMessage( final String msgContent, final ExecutionContext context, final Session session ) 
		throws SmooksException
	{
		try
		{
			return session.createTextMessage( msgContent );
		}
		catch (JMSException e)
		{
			final String errorMsg = "JMSException while trying to set JMS Header Fields";
			log.error( errorMsg, e );
			throw new SmooksConfigurationException( errorMsg, e );
		}
	}
	
	public Message createJMSMessage( String beanId, ExecutionContext context, Session jmsSession ) throws SmooksException
	{
		throw new UnsupportedOperationException( "Operation not implemented for beanId. Please use ObjectMessageCreationStrategy.");
	}
	
}
