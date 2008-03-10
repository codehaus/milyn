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

import example.srcmodel.Order;
import example.trgmodel.LineOrder;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.java.JavaResult;
import org.milyn.delivery.java.JavaSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Simple example main class.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Main {

    /**
     * Run the transform for the request or response.
     * @param srcOrder The input Java Object.
     * @return The transformed Java Object XML.
     */
    protected LineOrder runSmooksTransform(Order srcOrder) throws IOException, SAXException {
        Smooks smooks = new Smooks("smooks-config.xml");
        ExecutionContext executionContext = smooks.createExecutionContext();

        // Transform the source Order to the target LineOrder via a
        // JavaSource and JavaResult instance...
        JavaSource source = new JavaSource(srcOrder);
        JavaResult result = new JavaResult();
        smooks.filter(source, result, executionContext);

        return (LineOrder) result.getBean("lineOrder");
    }

    public static void main(String[] args) throws IOException, SAXException, SmooksException {
        Main smooksMain = new Main();
        Order order = new Order();
        LineOrder lineOrder;

        pause("Press 'enter' to display the input Java Order message...");
        System.out.println("\n");
        System.out.println(order);
        System.out.println("\n\n");

        System.out.println("This needs to be transformed to XML.");
        pause("Press 'enter' to display the transformed message...");
        lineOrder = smooksMain.runSmooksTransform(order);
        System.out.println("\n");
        System.out.println(lineOrder);
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