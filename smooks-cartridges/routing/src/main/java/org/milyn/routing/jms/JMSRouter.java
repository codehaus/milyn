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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Initialize;
import org.milyn.cdr.annotation.Uninitialize;
import org.milyn.cdr.annotation.VisitIf;
import org.milyn.cdr.annotation.VisitIfNot;
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
 *    &lt;param name="messageType"&gt;ObjectMessage&lt;/param&gt;
 * </pre>
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 * @since 1.0
 *
 */
public class JMSRouter implements DOMElementVisitor, SAXElementVisitor
{
	/*
	 *	Log instance
	 */
	private final Log log = LogFactory.getLog( JMSRouter.class );

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

    @VisitIfNot(param = "visitBefore", value = "true", defaultVal = "true")
    public void visitAfter( final Element element, final ExecutionContext execContext ) throws SmooksException
	{
		visit( execContext );
	}

    @VisitIf(param = "visitBefore", value = "true", defaultVal = "true")
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

	private void visit( final ExecutionContext execContext )
	{
		sendMessage( msgCreationStrategy.createJMSMessage( beanId, execContext, session ));
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
    public void initialize() throws SmooksConfigurationException
    {
    	log.info( " initializing JMSRouter..." );
    	Context context = null;
    	try
		{
	    	context = new InitialContext();
			destination = (Destination) context.lookup( jmsProperties.getDestinationName() );
			msgProducer = createMessageProducer( destination, context );
			setMessageProducerProperties( );
		}
    	catch (NamingException e)
		{
    		final String errorMsg = "NamingException while trying to lookup [" + jmsProperties.getDestinationName() + "]";
    		log.error( errorMsg, e );
    		throw new SmooksConfigurationException( errorMsg, e );
		}
    	finally
    	{
    		if ( context != null )
    		{
				try { context.close(); } catch (NamingException e) { log.warn( "NamingException while trying to close initial Context"); }
    		}
    	}
    }

    @Uninitialize
    public void uninitialize()
    {
    	log.info( "uninitializing JMSRouter" );
    	if ( connection != null )
    	{
			try
			{
				connection.stop();
			}
			catch ( JMSException e )
			{
				final String errorMsg = "JMSException while trying to stop connection";
				log.error( errorMsg, e );
			}
    	}

    	if ( msgProducer != null )
    	{
			try
			{
				msgProducer.close();
			}
			catch (JMSException e)
			{
				final String errorMsg = "JMSException while trying to close message producer";
				log.error( errorMsg, e );
			}
    	}
    }

	//	protected

    protected MessageProducer createMessageProducer( final Destination destination, final Context context )
	{
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
			log.info ("JMS Connection started");
		}
		catch( JMSException e)
		{
			final String errorMsg = "JMSException while trying to create MessageProducer for Queue [" + jmsProperties.getDestinationName() + "]";
			throw new SmooksConfigurationException( errorMsg, e );
		}
		catch (NamingException e)
		{
			final String errorMsg = "NamingException while trying to lookup ConnectionFactory [" + jmsProperties.getConnectionFactoryName() + "]";
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

	protected void sendMessage( final Message message ) throws SmooksException
	{
		try
		{
			msgProducer.send( message );
		}
		catch (JMSException e)
		{
			final String errorMsg = "JMSException while sending Text Mesage";
			throw new SmooksException( errorMsg, e );
		}
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
				log.error( errorMsg, e );
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
				log.error( errorMsg, e );
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

    @ConfigParam (
    		defaultVal = "auto-acknowledge",
    		choice = {
    				"auto-acknowledge",
    				"client-acknowledge",
    				"dups-ok-acknowledge" } )
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

}
