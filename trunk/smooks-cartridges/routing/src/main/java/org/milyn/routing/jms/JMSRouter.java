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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.templating.freemarker.FreeMarkerUtils;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.annotation.Uninitialize;
import org.milyn.delivery.annotation.VisitAfterIf;
import org.milyn.delivery.annotation.VisitBeforeIf;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.routing.SmooksRoutingException;
import org.milyn.routing.jms.message.creationstrategies.MessageCreationStrategy;
import org.milyn.routing.jms.message.creationstrategies.StrategyFactory;
import org.milyn.routing.jms.message.creationstrategies.TextMessageCreationStrategy;
import org.milyn.util.FreeMarkerTemplate;
import org.w3c.dom.Element;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * <p/>
 * Router is a Visitor for DOM or SAX elements. It sends the content
 * as a JMS Message object to the configured destination.
 * <p/>
 * The type of the JMS Message is determined by the "messageType" config param.
 * <p/>
 * Example configuration:
 * <pre>
 * &lt;resource-config selector="orderItems"&gt;
 *    &lt;resource&gt;org.milyn.routing.jms.JMSRouter&lt;/resource&gt;
 *    &lt;param name="beanId">beanId&lt;/param&gt;
 *    &lt;param name="destination"&gt;/queue/smooksRouterQueue&lt;/param&gt;
 * &lt;/resource-config&gt;
 *	....
 * Optional parameters:
 *    &lt;param name="executeBefore"&gt;true&lt;/param&gt;  
 *    &lt;param name="jndiContextFactory"&gt;ConnectionFactory&lt;/param&gt;
 *    &lt;param name="jndiProviderUrl"&gt;jnp://localhost:1099&lt;/param&gt;
 *    &lt;param name="jndiNamingFactory"&gt;org.jboss.naming:java.naming.factory.url.pkgs=org.jnp.interfaces&lt;/param&gt;
 *    &lt;param name="connectionFactory"&gt;ConnectionFactory&lt;/param&gt;
 *    &lt;param name="deliveryMode"&gt;persistent&lt;/param&gt;
 *    &lt;param name="priority"&gt;10&lt;/param&gt;
 *    &lt;param name="timeToLive"&gt;100000&lt;/param&gt;
 *    &lt;param name="securityPrincipal"&gt;username&lt;/param&gt;
 *    &lt;param name="securityCredential"&gt;password&lt;/param&gt;
 *    &lt;param name="acknowledgeMode"&gt;AUTO_ACKNOWLEDGE&lt;/param&gt;
 *    &lt;param name="transacted"&gt;false&lt;/param&gt;
 *    &lt;param name="correlationIdPattern"&gt;orderitem-${order.orderId}-${order.orderItem.itemId}&lt;/param&gt;
 *    &lt;param name="messageType"&gt;ObjectMessage&lt;/param&gt;
 *    &lt;param name="highWaterMark"&gt;50&lt;/param&gt; 
 *    &lt;param name="highWaterMarkTimeout"&gt;5000&lt;/param&gt; 
 *    &lt;param name="highWaterMarkPollFrequency"&gt;500&lt;/param&gt; 
 * </pre>
 * Description of configuration properties:
 * <ul>
 * <li><i>jndiContextFactory</i>: the JNDI ContextFactory to use.
 * <li><i>jndiProviderUrl</i>:  the JNDI Provider URL to use.
 * <li><i>jndiNamingFactory</i>: the JNDI NamingFactory to use.
 * <li><i>connectionFactory</i>: the ConnectionFactory to look up.
 * <li><i>deliveryMode</i>: the JMS DeliveryMode. 'persistent'(default) or 'non-persistent'.
 * <li><i>priority</i>: the JMS Priority to be used.
 * <li><i>timeToLive</i>: the JMS Time-To-Live to be used.
 * <li><i>securityPrincipal</i>: security principal use when creating the JMS connection.
 * <li><i>securityCredential</i>: the security credentials to use when creating the JMS connection. 
 * <li><i>acknowledgeMode</i>: the acknowledge mode to use. One of 'AUTO_ACKNOWLEDGE'(default), 'CLIENT_ACKNOWLEDGE', 'DUPS_OK_ACKNOWLEDGE'.
 * <li><i>transacted</i>: determines if the session should be transacted. Defaults to 'false'.
 * <li><i>correlationIdPattern</i>: JMS Correlation pattern that will be used for the outgoing message. Supports templating.
 * <li><i>messageType</i>: type of JMS Message that should be sent. 'TextMessage'(default) or 'ObjectMessage'.
 * <li><i>highWaterMark</i>: max number of messages that can be sitting in the JMS Destination at any any time. Default is 200.
 * <li><i>highWaterMarkTimeout</i>: number of ms to wait for the system to process JMS Messages from the JMS destination
 * 		so that the number of JMS Messages drops below the highWaterMark. Default is 60000 ms.
 * <li><i>highWaterMarkPollFrequency</i>: number of ms to wait between checks on the High Water Mark, while
 *      waiting for it to drop. Default is 1000 ms.
 * </ul>
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 * @since 1.0
 *
 */
@VisitBeforeIf(	condition = "parameters.containsKey('executeBefore') && parameters.executeBefore.value == 'true'")
@VisitAfterIf(	condition = "!parameters.containsKey('executeBefore') || parameters.executeBefore.value != 'true'")
public class JMSRouter implements DOMElementVisitor, SAXElementVisitor
{
	/*
	 *	Log instance
	 */
	private final Log logger = LogFactory.getLog( JMSRouter.class );

	/*
	 *	JNDI Properties holder
	 */
    private final JNDIProperties jndiProperties = new JNDIProperties();

    /*
     *	JMS Properties holder
     */
    private final JMSProperties jmsProperties = new JMSProperties();

    /*
	 * 	BeanId is a key that is used to look up a bean
	 * 	in the execution context
     */
    @ConfigParam( use = ConfigParam.Use.REQUIRED )
    private String beanId;

    @ConfigParam( use = ConfigParam.Use.OPTIONAL )
    private String correlationIdPattern;
    private FreeMarkerTemplate correlationIdTemplate;

    @ConfigParam(defaultVal = "200")
    private int highWaterMark;
    @ConfigParam(defaultVal = "60000")
    private long highWaterMarkTimeout;

    @ConfigParam(defaultVal = "1000")
    private long highWaterMarkPollFrequency;

    /*
     * 	Strategy for JMS Message object creation
     */
    private MessageCreationStrategy msgCreationStrategy = new TextMessageCreationStrategy();

    /*
     * 	JMS Destination
     */
    private Destination destination;

    /*
     * 	JMS Connection
     */
    private Connection connection;

    /*
     * 	JMS Message producer
     */
    private MessageProducer msgProducer;
    /*
     * 	JMS Session
     */
    private Session session;

    //	Vistor methods

    public void visitAfter( final Element element, final ExecutionContext execContext ) throws SmooksException
	{
		visit( execContext );
	}

	public void visitBefore( final Element element, final ExecutionContext execContext ) throws SmooksException
	{
		visit( execContext );
	}

	public void visitAfter( final SAXElement element, final ExecutionContext execContext ) throws SmooksException, IOException
	{
		visit( execContext );
	}

	public void visitBefore( final SAXElement element, final ExecutionContext execContext ) throws SmooksException, IOException
	{
		visit( execContext );
	}

	private void visit( final ExecutionContext execContext ) throws SmooksException	{
        Message message = msgCreationStrategy.createJMSMessage(beanId, execContext, session);

        if(correlationIdTemplate != null) {
            setCorrelationID(execContext, message);
        }

        sendMessage(message);
	}

    public void onChildElement( final SAXElement saxElement, final SAXElement saxElement2, final ExecutionContext execContect ) throws SmooksException, IOException
	{
		//	NoOp
	}

    public void onChildText( final SAXElement saxElement, final SAXText saxText, final ExecutionContext execContext ) throws SmooksException, IOException
	{
		//	NoOp
	}

    //	Lifecycle

    @Initialize
    public void initialize() throws SmooksConfigurationException, JMSException {
    	Context context = null;
        boolean initialized = false;

        try
		{
            if(correlationIdPattern != null) {
                correlationIdTemplate = new FreeMarkerTemplate(correlationIdPattern);
            }

	    	context = new InitialContext();
			destination = (Destination) context.lookup( jmsProperties.getDestinationName() );
			msgProducer = createMessageProducer( destination, context );
			setMessageProducerProperties( );

            initialized = true;
        }
    	catch (NamingException e)
		{
    		final String errorMsg = "NamingException while trying to lookup [" + jmsProperties.getDestinationName() + "]";
    		logger.error( errorMsg, e );
    		throw new SmooksConfigurationException( errorMsg, e );
        } finally {
    		if ( context != null )
    		{
				try { context.close(); } catch (NamingException e) { logger.warn( "NamingException while trying to close initial Context"); }
    		}

            if(!initialized) {
                releaseJMSResources();
            }
        }
    }

    @Uninitialize
    public void uninitialize() throws JMSException {
        releaseJMSResources();
    }

    protected MessageProducer createMessageProducer( final Destination destination, final Context context ) throws JMSException {
		try
		{
		    final ConnectionFactory connFactory = (ConnectionFactory) context.lookup( jmsProperties.getConnectionFactoryName() );

			connection = (jmsProperties.getSecurityPrincipal() == null && jmsProperties.getSecurityCredential() == null ) ?
					connFactory.createConnection():
					connFactory.createConnection( jmsProperties.getSecurityPrincipal(), jmsProperties.getSecurityCredential() );

			session = connection.createSession( jmsProperties.isTransacted(),
					AcknowledgeModeEnum.getAckMode( jmsProperties.getAcknowledgeMode().toUpperCase() ).getAcknowledgeModeInt() );

			msgProducer = session.createProducer( destination );
			connection.start();
			logger.info ("JMS Connection started");
		}
		catch( JMSException e)
		{
			final String errorMsg = "JMSException while trying to create MessageProducer for Queue [" + jmsProperties.getDestinationName() + "]";
            releaseJMSResources();
            throw new SmooksConfigurationException( errorMsg, e );
		}
		catch (NamingException e)
		{
			final String errorMsg = "NamingException while trying to lookup ConnectionFactory [" + jmsProperties.getConnectionFactoryName() + "]";
            releaseJMSResources();
			throw new SmooksConfigurationException( errorMsg, e );
		}

		return msgProducer;
	}

    /**
     * Sets the following MessageProducer properties:
     * <lu>
     * 	<li>TimeToLive
     * 	<li>Priority
     * 	<li>DeliveryMode
     * </lu>
     * <p>
     * Subclasses may override this behaviour.
     */
	protected void setMessageProducerProperties() throws SmooksConfigurationException
	{
		try
		{
			msgProducer.setTimeToLive( jmsProperties.getTimeToLive() );
			msgProducer.setPriority( jmsProperties.getPriority() );

			final int deliveryModeInt = "non-persistent".equals( jmsProperties.getDeliveryMode() ) ?
					DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT;
			msgProducer.setDeliveryMode( deliveryModeInt );
		}
		catch (JMSException e)
		{
			final String errorMsg = "JMSException while trying to set JMS Header Fields";
			throw new SmooksConfigurationException( errorMsg, e );
		}
	}

    protected void sendMessage( final Message message ) throws SmooksRoutingException
	{
        try {
            waitWhileAboveHighWaterMark();
        } catch (JMSException e) {
            throw new SmooksRoutingException("Exception while attempting to check JMS Queue High Water Mark.", e );
        }

        try
		{
            msgProducer.send( message );
		}
		catch (JMSException e)
		{
			final String errorMsg = "JMSException while sending Message.";
			throw new SmooksRoutingException( errorMsg, e );
		}
	}

    private void waitWhileAboveHighWaterMark() throws JMSException, SmooksRoutingException {
        if(highWaterMark == -1) {
            return;
        }

        if(session instanceof QueueSession) {
            QueueSession queueSession = (QueueSession) session;
            QueueBrowser queueBrowser = queueSession.createBrowser((Queue) destination);

            try {
                int length = getQueueLength(queueBrowser);
                long start = System.currentTimeMillis();

                if(logger.isDebugEnabled() && length >= highWaterMark) {
                    logger.debug("Length of JMS destination Queue '" + jmsProperties.getDestinationName() + "' has reached " + length +  ".  High Water Mark is " + highWaterMark +  ".  Waiting for Queue length to drop.");
                }

                while(length >= highWaterMark && (System.currentTimeMillis() < start + highWaterMarkTimeout)) {
                    try {
                        Thread.sleep(highWaterMarkPollFrequency);
                    } catch (InterruptedException e) {
                        logger.error("Interrupted", e);
                        return;
                    }
                    length = getQueueLength(queueBrowser);
                }

                // Check did the queue length drop below the HWM...
                if(length >= highWaterMark) {
                    throw new SmooksRoutingException("Failed to route JMS message to Queue destination '" + ((Queue) destination).getQueueName() + "'. Timed out (" + highWaterMarkTimeout + " ms) waiting for queue length to drop below High Water Mark (" + highWaterMark + ").  Consider increasing 'highWaterMark' and/or 'highWaterMarkTimeout' param values.");
                }
            } finally {
                queueBrowser.close();
            }
        }
    }

    private int getQueueLength(QueueBrowser queueBrowser) throws JMSException {
        int length = 0;
        Enumeration queueEnum = queueBrowser.getEnumeration();
        while(queueEnum.hasMoreElements()) {
            length++;
            queueEnum.nextElement();
        }
        return length;
    }

    protected void close( final Connection connection )
	{
		if ( connection != null )
		{
			try
			{
				connection.close();
			}
			catch (JMSException e)
			{
				final String errorMsg = "JMSException while trying to close connection";
				logger.error( errorMsg, e );
			}
		}
	}

    protected void close( final Session session )
	{
		if ( session != null )
		{
			try
			{
				session.close();
			}
			catch (JMSException e)
			{
				final String errorMsg = "JMSException while trying to close session";
				logger.error( errorMsg, e );
			}
		}
	}

    // getter/setter

    public Destination getDestination()
	{
		return destination;
	}

    @ConfigParam ( use = Use.OPTIONAL, defaultVal = "org.jnp.interfaces.NamingContextFactory" )
    public void setJndiContextFactory( final String contextFactory )
    {
    	jndiProperties.setContextFactory( contextFactory );
    }

    public String getJndiContectFactory()
	{
		return jndiProperties.getContextFactory();
	}


    @ConfigParam ( use = Use.OPTIONAL, defaultVal = "jnp://localhost:1099" )
    public void setJndiProviderUrl(final String providerUrl )
    {
    	jndiProperties.setProviderUrl( providerUrl );
    }

    public String getJndiProviderUrl()
	{
		return jndiProperties.getProviderUrl();
	}

    public String getJndiNamingFactoryUrl()
	{
		return jndiProperties.getNamingFactoryUrlPkgs();
	}

    @ConfigParam ( use = Use.OPTIONAL, defaultVal = "org.jboss.naming:java.naming.factory.url.pkgs" )
    public void setJndiNamingFactoryUrl(final String pkgUrl )
    {
    	jndiProperties.setNamingFactoryUrlPkgs( pkgUrl );
    }

    @ConfigParam ( use = Use.REQUIRED )
	public void setDestinationName( final String destinationName )
	{
		jmsProperties.setDestinationName( destinationName );
	}

    public String getDestinationName()
	{
		return jmsProperties.getDestinationName();
	}

    @ConfigParam ( choice = { "persistent", "non-persistent" }, defaultVal = "persistent", use = Use.OPTIONAL )
	public void setDeliveryMode( final String deliveryMode )
	{
    	jmsProperties.setDeliveryMode( deliveryMode );
	}

    private void setCorrelationID(ExecutionContext execContext, Message message) {
        Map<String, Object> beanMap = FreeMarkerUtils.getMergedModel(execContext);
        String correlationId = correlationIdTemplate.apply(beanMap);

        try {
            message.setJMSCorrelationID(correlationId);
        } catch (JMSException e) {
            throw new SmooksException("Failed to set CorrelationID '" + correlationId + "' on message.", e);
        }
    }

    public String getDeliveryMode()
	{
		return jmsProperties.getDeliveryMode();
	}

    @ConfigParam ( use = Use.OPTIONAL )
	public void setTimeToLive( final long timeToLive )
	{
    	jmsProperties.setTimeToLive( timeToLive );
	}

    public long getTimeToLive()
	{
		return jmsProperties.getTimeToLive();
	}

    @ConfigParam ( use = Use.OPTIONAL )
	public void setSecurityPrincipal( final String securityPrincipal )
	{
		jmsProperties.setSecurityPrincipal( securityPrincipal );
	}

    public String getSecurityPrincipal()
	{
		return jmsProperties.getSecurityPrincipal();
	}

    @ConfigParam ( use = Use.OPTIONAL )
	public void setSecurityCredential( final String securityCredential )
	{
    	jmsProperties.setSecurityCredential( securityCredential );
	}

    public String getSecurityCredential()
	{
		return jmsProperties.getSecurityCredential();
	}

    @ConfigParam ( use = Use.OPTIONAL, defaultVal = "false" )
	public void setTransacted( final boolean transacted )
	{
		jmsProperties.setTransacted( transacted );
	}

    public boolean isTransacted()
	{
		return jmsProperties.isTransacted();
	}

    @ConfigParam( defaultVal = "ConnectionFactory" , use = Use.OPTIONAL )
	public void setConnectionFactoryName( final String connectionFactoryName )
	{
		jmsProperties.setConnectionFactoryName( connectionFactoryName );
	}

    public String getConnectionFactoryName()
	{
		return jmsProperties.getConnectionFactoryName();
	}

    @ConfigParam ( use = Use.OPTIONAL )
	public void setPriority( final int priority )
	{
    	jmsProperties.setPriority( priority );
	}

    public int getPriority()
	{
		return jmsProperties.getPriority();
	}

    @ConfigParam (defaultVal = "AUTO_ACKNOWLEDGE",
    		choice = {"AUTO_ACKNOWLEDGE", "CLIENT_ACKNOWLEDGE", "DUPS_OK_ACKNOWLEDGE" } )
	public void setAcknowledgeMode( final String jmsAcknowledgeMode )
	{
		jmsProperties.setAcknowledgeMode( jmsAcknowledgeMode );
	}

    public String getAcknowledgeMode()
	{
		return jmsProperties.getAcknowledgeMode();
	}

    @ConfigParam (
    		defaultVal = StrategyFactory.TEXT_MESSAGE,
    		choice = { StrategyFactory.TEXT_MESSAGE ,  StrategyFactory.OBJECT_MESSAGE }  )
	public void setMessageType( final String messageType )
	{
		msgCreationStrategy = StrategyFactory.getInstance().createStrategy( messageType );
		jmsProperties.setMessageType( messageType );
	}

    public void setMsgCreationStrategy( final MessageCreationStrategy msgCreationStrategy )
	{
		this.msgCreationStrategy = msgCreationStrategy;
	}

    private void releaseJMSResources() throws JMSException {
        if (connection != null) {
            try {
                try {
                    connection.stop();
                } finally {
                    try {
                        closeProducer();
                    } finally {
                        closeSession();
                    }
                }
            } catch (JMSException e) {
                logger.error("JMSException while trying to stop JMS Connection.", e);
            } finally {
                connection.close();
                connection = null;
            }
        }
    }

    private void closeProducer() {
        if (msgProducer != null) {
            try {
                msgProducer.close();
            } catch (JMSException e) {
                logger.error("JMSException while trying to close JMS Message Producer.", e);
            } finally {
                msgProducer = null;
            }
        }
    }

    private void closeSession() {
        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                logger.error("JMSException while trying to close JMS Session.", e);
            } finally {
                session = null;
            }
        }
    }
}
