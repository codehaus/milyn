package org.milyn.routing.jms.message.creationstrategies;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.repository.BeanRepositoryManager;

public class ObjectMessageCreationStrategy implements MessageCreationStrategy
{

	public Message createJMSMessage(
			final String beanId,
			final ExecutionContext context,
			final Session jmsSession )
		throws SmooksException
	{
        final Object bean = BeanRepositoryManager.getBeanRepository(context).getBean(beanId);
        return createObjectMessage( bean, jmsSession );
	}

	private ObjectMessage createObjectMessage(
			final Object object,
			final Session session )
		throws SmooksException
	{
		try
		{
			return session.createObjectMessage( object.toString() );
		}
		catch (JMSException e)
		{
			final String errorMsg = "JMSException while trying to set JMS Header Fields";
			throw new SmooksConfigurationException( errorMsg, e );
		}
	}

}
