/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/
package example;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.io.StreamUtils;
import org.xml.sax.SAXException;

/**
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class Main implements MessageListener
{

    private static final String RESPONSE_DESTINATION  = "/queue/smooksRouterQueue";
    private static byte[] messageIn = readInputMessage();
    private static int messageCounter;
    private Connection connection;

    protected String runSmooksTransform() throws IOException, SAXException
    {
        Smooks smooks = new Smooks("smooks-config.xml");
        ExecutionContext executionContext = smooks.createExecutionContext();

        StreamSource source = new StreamSource( new ByteArrayInputStream( messageIn ) ) ;
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult( stringWriter );
        smooks.filter(source, result, executionContext);

        return result.getWriter().toString();
    }

    public static void main(String[] args) throws IOException, SAXException, SmooksException, InterruptedException
    {
        Main smooksMain = new Main();
        try
        {
        	smooksMain.setupMessageListener();

            pause("Press 'enter' to display the input xml Order message...");
            System.out.println("\n");
            System.out.println( new String( messageIn ) );
            System.out.println("\n\n");

            System.out.println("This needs to be transformed to XML.");
            pause("Press 'enter' to display the transformed message...");
            smooksMain.runSmooksTransform();
            System.out.println("\n\n");

            Thread.sleep( 1000l );
            System.out.println("\n\n");
        }
        finally
        {
        	smooksMain.closeConnection();
        }
    }


    private void setupMessageListener()
    {
		InitialContext context = null;
    	try
		{
			context = new InitialContext();
			ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
	        connection = connectionFactory.createConnection();
	        Session session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
	        Destination destination = (Destination)context.lookup( RESPONSE_DESTINATION );
	        MessageConsumer consumer = session.createConsumer( destination );
	        consumer.setMessageListener( this );
	        connection.start();
	        System.out.println("JMS Connection started");
		}
    	catch (NamingException e)
		{
			e.printStackTrace();
		}
    	catch (JMSException e)
		{
			e.printStackTrace();
		}
    	finally
    	{
    		try { context.close(); } catch (NamingException e) { e.printStackTrace(); }
    	}
    }

    private void closeConnection()
    {
    	try
		{
    		if ( connection != null )
        		connection.close();
		}
    	catch (JMSException e)
		{
			e.printStackTrace();
		}
    }

    private static void pause(String message) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("> " + message);
            in.readLine();
        } catch (IOException e) {
        }
        System.out.println("\n");
    }

	public void onMessage( Message message )
	{
		messageCounter++;
        System.out.println("\n[ Received Message[" + messageCounter + "]");
        try
		{
			System.out.println("\t[JMSMessageID : " +  message.getJMSMessageID() + "]" );
	        System.out.println("\t[JMSCorrelelationID : " +  message.getJMSCorrelationID() + "]" );
	        if ( message instanceof ObjectMessage )
	        {
	            System.out.println("\t[MessageType : ObjectMessage]");
	            System.out.println( "\t[Object : " +  ((ObjectMessage)message).getObject() + "]" );
	        }
	        else if ( message instanceof TextMessage )
	        {
	            System.out.println("\t[MessageType : TextMessage]");
	            System.out.println( "\t[Text : " +  ((TextMessage)message).getText() + "]" );
	        }
		} catch (JMSException e)
		{
			e.printStackTrace();
		}
        System.out.println("]");
	}

    private static byte[] readInputMessage()
    {
        try {
            return StreamUtils.readStream(new FileInputStream("input-message.xml"));
        }
        catch (IOException e) {
            e.printStackTrace();
            return "<no-message/>".getBytes();
        }
    }

}