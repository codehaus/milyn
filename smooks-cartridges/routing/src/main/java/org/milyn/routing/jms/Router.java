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
import java.util.Properties;

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
 * The type of the JMS Message is determined by the member {@link #jmsMessageType}
 * <p/>
 * Example configuration:
 * <pre>
 *	.... 
 * </pre>
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>				
 *
 */
public class Router implements DOMElementVisitor, SAXElementVisitor
{
	private Logger log = Logger.getLogger( Router.class );
	
	/**
	 * JMS Queue or Topic name
	 */
    @ConfigParam ( use = Use.REQUIRED )
    private String destinationName;
    
    @ConfigParam( defaultVal = "ConnectionFactory" , use = Use.OPTIONAL )
    private String connectionFactoryName;
    
    @ConfigParam ( use = Use.OPTIONAL )
    private Properties jndiEnvironment;
    
    @ConfigParam ( choice = { "persistent", "non-persistent" }, use = Use.OPTIONAL )
    private String jmsDeliveryMode;
    
    @ConfigParam ( use = Use.OPTIONAL )
    private int jmsPriority = Message.DEFAULT_PRIORITY;
    
    @ConfigParam ( use = Use.OPTIONAL )
    private long jmsTimeToLive = Message.DEFAULT_TIME_TO_LIVE;
    
    @ConfigParam ( use = Use.OPTIONAL )
    private String jmsSecurityPrincipal;
    
    @ConfigParam ( use = Use.OPTIONAL )
    private String jmsSecurityCredential;
    
    @ConfigParam(defaultVal = ConfigParam.NULL)
    private String beanId;
    
    /**
     * Indicates wheather JMS sends should be transacted
     * <p>
     * Default is false (non-transacted session)
     */
    @ConfigParam ( use = Use.OPTIONAL )
    private boolean jmsTransacted;
    
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
    private String jmsMessageType;
    
    /**
     * Strategy for creating JMS Message object.
     */
    private MessageCreationStrategy messageCreationStrategy = new TextMessageCreationStrategy();
    
    private Destination destination;
    
    private Connection connection;
    
    private MessageProducer msgProducer;
    
    private Session jmsSession;
    
    @Initialize
    public void initialize() throws SmooksConfigurationException 
    {
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
    
    @Uninitialize
    public void uninitialize() 
    {
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
			    
			connection = ((jmsSecurityPrincipal != null && jmsSecurityCredential != null ) ? 
					connFactory.createConnection( jmsSecurityPrincipal, jmsSecurityCredential ):
					connFactory.createConnection( ));
					
			jmsSession = connection.createSession( jmsTransacted, 
					AcknowledgeModeEnum.getAckMode( jmsAcknowledgeMode.toUpperCase() ).getAcknowledgeModeInt() );
			
			msgProducer = jmsSession.createProducer( destination );
			connection.start();
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
			msgProducer.setTimeToLive( jmsTimeToLive );
			msgProducer.setPriority( jmsPriority );
			if ( jmsDeliveryMode != null )
			{
				final int deliveryMode = jmsDeliveryMode.equals( "non-peristent" ) ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT;
				msgProducer.setDeliveryMode( deliveryMode );
			}
		}
		catch (JMSException e)
		{
			final String errorMsg = "JMSException while trying to set JMS Header Fields";
			log.error( errorMsg, e );
			throw new SmooksConfigurationException( errorMsg, e );
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
			sendMessage( messageCreationStrategy.createJMSMessage( beanId, execContext, jmsSession ));
		else
			sendMessage( messageCreationStrategy.createJMSMessage( element, execContext, jmsSession ));
	}
	
	public void visitBefore( SAXElement element, ExecutionContext execContext ) throws SmooksException, IOException
	{
		if ( beanId != null )
			sendMessage( messageCreationStrategy.createJMSMessage( beanId, execContext, jmsSession ));
		else
			sendMessage( messageCreationStrategy.createJMSMessage( element, execContext, jmsSession ));
	}
	
	private void visitDOM( final Element element, final ExecutionContext execContext )
	{
		if ( beanId != null )
			sendMessage( messageCreationStrategy.createJMSMessage( beanId, execContext, jmsSession ));
		else
			sendMessage( messageCreationStrategy.createJMSMessage( element, execContext, jmsSession ));
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

	public Properties getJndiEnvironment()
	{
		return jndiEnvironment;
	}

	public void setJndiEnvironment( final Properties jndiEnvironment )
	{
		this.jndiEnvironment = jndiEnvironment;
	}

	public String getJmsSecurityPrincipal()
	{
		return jmsSecurityPrincipal;
	}

	public void setJmsSecurityPrincipal( final String jmsSecurityPrincipal )
	{
		this.jmsSecurityPrincipal = jmsSecurityPrincipal;
	}

	public String getJmsSecurityCredential()
	{
		return jmsSecurityCredential;
	}

	public void setJmsSecurityCredential( final String jmsSecurityCredential )
	{
		this.jmsSecurityCredential = jmsSecurityCredential;
	}
	
	public String getJmsDeliveryMode()
	{
		return jmsDeliveryMode;
	}

	public void setJmsDeliveryMode( final String jmsDeliveryMode )
	{
		this.jmsDeliveryMode = jmsDeliveryMode;
	}

	public int getJmsPriority()
	{
		return jmsPriority;
	}

	public void setJmsPriority( final int jmsPriority )
	{
		this.jmsPriority = jmsPriority;
	}

	public long getJmsTimeToLive()
	{
		return jmsTimeToLive;
	}

	public void setJmsTimeToLive( final long jmsTimeToLive )
	{
		this.jmsTimeToLive = jmsTimeToLive;
	}

	public boolean isJmsTransacted()
	{
		return jmsTransacted;
	}

	public void setJmsTransacted( boolean jmsTransacted )
	{
		this.jmsTransacted = jmsTransacted;
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
		return jmsMessageType;
	}

	public void setJmsMessageType( String jmsMessageType )
	{
		messageCreationStrategy = StrategyFactory.getInstance().createStrategy( jmsMessageType );
		this.jmsMessageType = jmsMessageType;
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
	
}
