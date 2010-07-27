/*
	Milyn - Copyright (C) 2006 - 2010

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.milyn.io.StreamUtils;

/**
 * Simple example main class.
 * 
 */
public class Main
{
	public static void main(String... args) throws Exception
	{
		String payload = readInputMessage();
		printStartMessage(payload);
		CamelContext camelContext = configureAndStartCamel();
		sendMessageToCamel(camelContext, payload);
		printEndMessage();
	}
	
	private static String readInputMessage()
	{
		try
		{
			byte[] bytes = StreamUtils.readStream(Main.class.getResourceAsStream("/input-message.xml"));
			return new String(bytes);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			return "<no-message/>";
		}
	}

	private static void printStartMessage(String payload)
	{
		System.out.println("\n\n==============Message In==============");
		System.out.println(payload);
		System.out.println("======================================\n");
		pause("The example xml can be seen above.  Press 'enter' have this");
	}
	
	private static void pause(String message)
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("> " + message);
			in.readLine();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("\n");
	}

	private static CamelContext configureAndStartCamel() throws Exception
	{
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new ExampleRouteBuilder());
		context.start();
		return context;
	}
	
	private static void sendMessageToCamel(CamelContext context, String payload)
	{
		DefaultProducerTemplate producerTemplate = new DefaultProducerTemplate(context);
		producerTemplate.sendBody("direct:input",  new StreamSource(new StringReader(payload)));
	}

	private static void printEndMessage()
	{
		System.out.println("\n\n");
		pause("And that's it!  Press 'enter' to finish...");
	}

}
