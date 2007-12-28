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

import example.model.Order;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.java.JavaSource;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Simple example main class.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Main {

    /**
     * Run the transform for the request or response.
     * @param inputJavaObject The input Java Object.
     * @return The transformed Java Object XML.
     */
    protected String runSmooksTransform(Object inputJavaObject) throws IOException, SAXException {
        Smooks smooks = new Smooks("smooks-config.xml");
        ExecutionContext executionContext = smooks.createExecutionContext();
        StringWriter writer = new StringWriter();

        // Filter the message to the result writer, using the execution context...
        smooks.filter(new JavaSource(inputJavaObject), new StreamResult(writer), executionContext);

        return writer.toString();
    }

    public static void main(String[] args) throws IOException, SAXException, SmooksException {
        Main smooksMain = new Main();
        Order javaInput = new Order();
        String transResult;

        pause("Press 'enter' to display the input Java Order message...");
        System.out.println("\n");
        System.out.println(javaInput);
        System.out.println("\n\n");

        System.out.println("This needs to be transformed to XML.");
        pause("Press 'enter' to display the transformed message...");
        transResult = smooksMain.runSmooksTransform(javaInput);
        System.out.println("\n");
        System.out.println(transResult);
        System.out.println("\n\n");

        pause("And that's it!");
        System.out.println("\n\n");
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