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

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Initialize;
import org.milyn.cdr.annotation.Uninitialize;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.routing.jms.message.creationstrategies.MessageCreationStrategy;
import org.milyn.routing.jms.message.creationstrategies.StrategyFactory;
import org.milyn.routing.jms.message.creationstrategies.TextMessageCreationStrategy;
import org.w3c.dom.Element;

/**
 * <p/>
 * Router is a Visitor for DOM or SAX elements. It sends the content
 * as a JMS Message object to the configured destination.
 * <p/>
 * The type of the JMS Message is determined by the member {@link #messageType}
 * <p/>
 * Example configuration:
 * <pre>
 * &lt;resource-config selector="orderItems"&gt;
 *    &lt;resource&gt;org.milyn.routing.jms.Router&lt;/resource&gt;
 *    &lt;param name="destination"&gt;/queue/smooksRouterQueue&lt;/param&gt;
 * &lt;/resource-config&gt;
 *	....
 * Optional parameters:
 *    &lt;param name="beanId">beanId&lt;/param&gt;
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
 *    &lt;param name="messageType"&gt;ObjectMessage&lt;/param&gt;
 * </pre>
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 * @since 1.0
 *
 */
public class Router implements DOMElementVisitor, SAXElementVisitor
{
	private static final String NON_PERSISTENT = "non-persistent";
	private static final String PERSISTENT = "persistent";

	private Logger log = Logger.getLogger( Router.class );

    @ConfigParam ( use = Use.OPTIONAL, defaultVal = "org.jnp.interfaces.NamingContextFactory" )
    private String jndiContextFactory;

    @ConfigParam ( use = Use.OPTIONAL, defaultVal = "jnp://localhost:1099" )
    private String jndiProviderUrl;

    @ConfigParam ( use = Use.OPTIONAL, defaultVal = "org.jboss.naming:java.naming.factory.url.pkgs" )
    private String jndiNamingFactoryUrl;

    @ConfigParam( defaultVal = "ConnectionFactory" , use = Use.OPTIONAL )
    private String connectionFactoryName;

    @ConfigParam ( use = Use.REQUIRED )
    private String destinationName;

    @ConfigParam ( choice = { PERSISTENT, NON_PERSISTENT }, defaultVal = "persistent", use = Use.OPTIONAL )
    private String deliveryMode;

    @ConfigParam ( use = Use.OPTIONAL )
    private int priority = Message.DEFAULT_PRIORITY;

    @ConfigParam ( use = Use.OPTIONAL )
    private long timeToLive = Message.DEFAULT_TIME_TO_LIVE;

    @ConfigParam ( use = Use.OPTIONAL )
    private String securityPrincipal;

    @ConfigParam ( use = Use.OPTIONAL )
    private String securityCredential;

    @ConfigParam(defaultVal = ConfigParam.NULL)
    private String beanId;

    @ConfigParam ( use = Use.OPTIONAL, defaultVal = "false" )
    private boolean transacted;

    @ConfigParam (
    		defaultVal = "auto-acknowledge",
    		choice = {
    				"auto-acknowledge",
    				"client-acknowledge",
    				"dups-ok-acknowledge" } )
    private String jmsAcknowledgeMode;

    @ConfigParam(defaultVal = "false")
    private boolean visitBefore;

    @ConfigParam (  defaultVal = StrategyFactory.TEXT_MESSAGE,
    		choice = { StrategyFactory.TEXT_MESSAGE ,  StrategyFactory.OBJECT_MESSAGE }  )
    @SuppressWarnings("unused")
    private String messageType;

    // private non-configurable fields

    private MessageCreationStrategy messageCreationStrategy = new TextMessageCreationStrategy();

    private Destination destination;

    private Connection connection;

    private MessageProducer msgProducer;

    private Session session;

    public Router()
    {
    	log.info(" Created new JMS Router instance ");
    }

    @Initialize
    public void initialize() throws SmooksConfigurationException
    {
    	log.info( " initializing JMSRouter..." );
    	Context context = null;
    	try
		{
	    	context = new InitialContext();
			destination = (Destination) context.lookup( destinationName );
			msgProducer = createMessageProducer( destination, context );
			setMessageProducerProperties( );
		}
    	catch (NamingException e)
		{
    		final String errorMsg = "NamingException while trying to lookup [" + destinationName + "]";
    		log.error( errorMsg, e );
    		throw new SmooksConfigurationException( errorMsg, e );
		}
    	finally
    	{
    		if ( context != null )
				try { context.close(); } catch (NamingException e) { log.warn( "NamingException while trying to close initial Context"); }
    	}
    }

    public void visitAfter( Element element, ExecutionContext execContext ) throws SmooksException
	{
		if ( !visitBefore )
			visitDOM( element, execContext );
	}


	public void visitBefore( Element element, ExecutionContext execContext ) throws SmooksException
	{
		if ( visitBefore )
			visitDOM( element, execContext );
	}

	/**
	 * TODO:
	 */
	public void onChildElement( SAXElement arg0, SAXElement arg1, ExecutionContext arg2 ) throws SmooksException, IOException
	{
	}

	/**
	 * TODO:
	 */
	public void onChildText( SAXElement arg0, SAXText arg1, ExecutionContext arg2 ) throws SmooksException, IOException
	{
	}

	public void visitAfter( SAXElement element, ExecutionContext execContext ) throws SmooksException, IOException
	{
		if ( beanId != null )
			sendMessage( messageCreationStrategy.createJMSMessage( beanId, execContext, session ));
		else
			sendMessage( messageCreationStrategy.createJMSMessage( element, execContext, session ));
	}

	public void visitBefore( SAXElement element, ExecutionContext execContext ) throws SmooksException, IOException
	{
		if ( beanId != null )
			sendMessage( messageCreationStrategy.createJMSMessage( beanId, execContext, session ));
		else
			sendMessage( messageCreationStrategy.createJMSMessage( element, execContext, session ));
	}

	private void visitDOM( final Element element, final ExecutionContext execContext )
	{
		if ( beanId != null )
			sendMessage( messageCreationStrategy.createJMSMessage( beanId, execContext, session ));
		else
			sendMessage( messageCreationStrategy.createJMSMessage( element, execContext, session ));
	}

    @Uninitialize
    public void uninitialize()
    {
    	log.info( " unitialize ..." );
    	if ( connection != null )
			try
			{
				connection.stop();
			} catch ( JMSException e )
			{
				final String errorMsg = "JMSException while trying to stop connection";
				log.error( errorMsg, e );
			}

    	if ( msgProducer != null )
			try
			{
				msgProducer.close();
			} catch (JMSException e)
			{
				final String errorMsg = "JMSException while trying to close message producer";
				log.error( errorMsg, e );
			}
    }

    protected MessageProducer createMessageProducer( final Destination destination, final Context context )
	{
		try
		{
		    ConnectionFactory connFactory = (ConnectionFactory) context.lookup( connectionFactoryName );

			connection = ((securityPrincipal != null && securityCredential != null ) ?
					connFactory.createConnection( securityPrincipal, securityCredential ):
					connFactory.createConnection( ));

			session = connection.createSession( transacted,
					AcknowledgeModeEnum.getAckMode( jmsAcknowledgeMode.toUpperCase() ).getAcknowledgeModeInt() );

			msgProducer = session.createProducer( destination );
			connection.start();
			log.info ("connection started");
		}
		catch( JMSException e)
		{
			final String errorMsg = "JMSException while trying to create MessageProducer for Queue [" + destinationName + "]";
			log.error( errorMsg, e );
			throw new SmooksConfigurationException( errorMsg, e );
		}
		catch (NamingException e)
		{
			final String errorMsg = "NamingException while trying to lookup ConnectionFactory [" + connectionFactoryName + "]";
			log.error( errorMsg, e );
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
			msgProducer.setTimeToLive( timeToLive );
			msgProducer.setPriority( priority );

			final int deliveryModeInt = "non-persistent".equals( deliveryMode ) ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT;
			msgProducer.setDeliveryMode( deliveryModeInt );
		}
		catch (JMSException e)
		{
			final String errorMsg = "JMSException while trying to set JMS Header Fields";
			log.error( errorMsg, e );
			throw new SmooksConfigurationException( errorMsg, e );
		}
	}

	// getter and setters

	public String getDestinationName()
	{
		return destinationName;
	}

	public void setDestinationName( String destinationName )
	{
		this.destinationName = destinationName;
	}

	public String getConnectionFactoryName()
	{
		return connectionFactoryName;
	}

	public void setConnectionFactory( final String connectionFactoryName )
	{
		this.connectionFactoryName = connectionFactoryName;
	}

	public String getJmsSecurityPrincipal()
	{
		return securityPrincipal;
	}

	public void setJmsSecurityPrincipal( final String jmsSecurityPrincipal )
	{
		this.securityPrincipal = jmsSecurityPrincipal;
	}

	public String getJmsSecurityCredential()
	{
		return securityCredential;
	}

	public void setJmsSecurityCredential( final String jmsSecurityCredential )
	{
		this.securityCredential = jmsSecurityCredential;
	}

	public String getJmsDeliveryMode()
	{
		return deliveryMode;
	}

	public void setJmsDeliveryMode( final String jmsDeliveryMode )
	{
		this.deliveryMode = jmsDeliveryMode;
	}

	public int getJmsPriority()
	{
		return priority;
	}

	public void setJmsPriority( final int jmsPriority )
	{
		this.priority = jmsPriority;
	}

	public long getJmsTimeToLive()
	{
		return timeToLive;
	}

	public void setJmsTimeToLive( final long jmsTimeToLive )
	{
		this.timeToLive = jmsTimeToLive;
	}

	public boolean isJmsTransacted()
	{
		return transacted;
	}

	public void setJmsTransacted( boolean jmsTransacted )
	{
		this.transacted = jmsTransacted;
	}

	public String getJmsAcknowledgeMode()
	{
		return jmsAcknowledgeMode;
	}

	public void setJmsAcknowledgeMode( String jmsAcknowledgeMode )
	{
		this.jmsAcknowledgeMode = jmsAcknowledgeMode;
	}

	public Destination getDestination()
	{
		return destination;
	}

	public void setDestination( Destination destination )
	{
		this.destination = destination;
	}

	public void setConnectionFactoryName( String connectionFactoryName )
	{
		this.connectionFactoryName = connectionFactoryName;
	}

    public void setVisitBefore(boolean visitBefore) {
        this.visitBefore = visitBefore;
    }

	public String getJmsMessageType()
	{
		return messageType;
	}

	public void setJmsMessageType( String jmsMessageType )
	{
		messageCreationStrategy = StrategyFactory.getInstance().createStrategy( jmsMessageType );
		this.messageType = jmsMessageType;
	}

	// protected methods

	protected void sendMessage( final Message message ) throws SmooksException
	{
		try
		{
			msgProducer.send( message );
		}
		catch (JMSException e)
		{
			final String errorMsg = "JMSException while sending Text Mesage";
			log.error( errorMsg, e);
			throw new SmooksException( errorMsg, e );
		}
	}

	protected void close( final Connection connection )
	{
		if ( connection != null )
			try
			{
				connection.close();
			}
			catch (JMSException e)
			{
				final String errorMsg = "JMSException while trying to close connection";
				log.error( errorMsg, e );
			}
	}

	protected void close( final Session session )
	{
		if ( session != null )
			try
			{
				session.close();
			}
			catch (JMSException e)
			{
				final String errorMsg = "JMSException while trying to close session";
				log.error( errorMsg, e );
			}
	}

	public String getJndiContectFactory()
	{
		return jndiContextFactory;
	}

	public String getJndiProviderUrl()
	{
		return jndiProviderUrl;
	}

	public String getJndiNamingFactoryUrl()
	{
		return jndiNamingFactoryUrl;
	}

}
