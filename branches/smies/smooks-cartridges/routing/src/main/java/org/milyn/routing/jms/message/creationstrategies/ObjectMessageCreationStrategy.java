package org.milyn.routing.jms.message.creationstrategies;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.log4j.Logger;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.javabean.BeanAccessor;
import org.milyn.routing.jms.message.creationstrategies.util.TransformUtil;
import org.w3c.dom.Element;

public class ObjectMessageCreationStrategy implements MessageCreationStrategy
{
	private Logger log = Logger.getLogger( ObjectMessageCreationStrategy.class );

	public Message createJMSMessage( Element element, ExecutionContext context, Session jmsSession ) throws SmooksException
	{
		final String msgContent = TransformUtil.transformElement( element );
		return createObjectMessage( msgContent, context, jmsSession );
	}

	public Message createJMSMessage( SAXElement element, ExecutionContext context, Session jmsSession )
		throws SmooksException
	{
		return null;
	}

	public Message createJMSMessage( String beanId, ExecutionContext context, Session jmsSession ) 
		throws SmooksException
	{
        Object bean = BeanAccessor.getBean( beanId, context );
        return createObjectMessage( bean, context, jmsSession );
	}
	
	private ObjectMessage createObjectMessage( final Object object, final ExecutionContext context, final Session session ) 
		throws SmooksException
	{
		try
		{
			return session.createObjectMessage( object.toString() );
		}
		catch (JMSException e)
		{
			final String errorMsg = "JMSException while trying to set JMS Header Fields";
			log.error( errorMsg, e );
			throw new SmooksConfigurationException( errorMsg, e );
		}
	}

}
