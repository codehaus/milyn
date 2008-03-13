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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.java.JavaSource;
import org.milyn.io.StreamUtils;
import org.milyn.javabean.BeanAccessor;
import org.milyn.routing.file.FileRouter;
import org.xml.sax.SAXException;

import example.model.Order;

/**
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class Main
{
    protected String runSmooksTransform(final Object order) throws IOException, SAXException
    {
        final Smooks smooks = new Smooks("smooks-config.xml");
        final ExecutionContext executionContext = smooks.createExecutionContext();
        BeanAccessor.addBean( executionContext, "order", order );

        try
        {
            final StringWriter writer = new StringWriter();
            smooks.filter(new JavaSource(order), new StreamResult(writer), executionContext);
        }
        finally
        {
        }
        return (String)executionContext.getAttribute( FileRouter.FILE_NAME_ATTR );
    }

    public static void main(String[] args) throws IOException, SAXException, SmooksException, InterruptedException
    {
        final Main smooksMain = new Main();
        final Order order = new Order();

        pause("Press 'enter' to display the input Order Object...");
        System.out.println("\n");
        System.out.println( order );
        System.out.println("\n\n");

        System.out.println("This needs to be transformed and appended to a file");
        pause("Press 'enter' to display the transformed message...");
        String fileName = smooksMain.runSmooksTransform(order);
        System.out.println("\n\n");
        System.out.println( "Transformed and appended to file : " + fileName );
        System.out.println( "File contents :");
        System.out.println( new String( StreamUtils.readStream(new FileInputStream( fileName ) ) ) );
        System.out.println("\n\n");
        pause("That's it ");
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

}