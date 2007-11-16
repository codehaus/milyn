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

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.io.StreamUtils;
import org.milyn.javabean.BeanAccessor;
import org.xml.sax.SAXException;
import se.sj.ipl.rollingstock.domain.RollingStockList;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Map;

/**
 * Simple example main class.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Main {

    private static byte[] messageIn = readInputMessage();

    protected static Map runSmooksTransform() throws IOException, SAXException, SmooksException {
        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks("smooks-config.xml");
         // Create an exec context - no profiles....
        StandaloneExecutionContext executionContext = smooks.createExecutionContext();

        // Filter the input message to the outputWriter, using the execution context...
        DOMResult domResult = new DOMResult();
        smooks.filter(new StreamSource(new ByteArrayInputStream(messageIn)), domResult, executionContext);

        return BeanAccessor.getBeanMap( executionContext );
    }

    public static void main(String[] args) throws IOException, SAXException, SmooksException {
        System.out.println("\n\n==============Message In==============");
        System.out.println(new String(messageIn));
        System.out.println("======================================\n");

        pause("The EDI input stream can be seen above.  Press 'enter' to see this stream transformed into the Java Object model...");

        Map beans = Main.runSmooksTransform();

        System.out.println("==============Message Out=============");
        
		RollingStockList rollingstocks = (RollingStockList) beans.get( "rollingstocks" );
		System.out.println( rollingstocks );
		System.out.println( "======================================\n\n");

        pause("And that's it!  Press 'enter' to finish...");
    }

    private static byte[] readInputMessage() {
        try {
            return StreamUtils.readStream(new FileInputStream("input-message.edi"));
        } catch (IOException e) {
            e.printStackTrace();
            return "<no-message/>".getBytes();
        }
    }

    private static void pause(String message) {
        System.out.println("> " + message);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            in.readLine();
        } catch (IOException e) {
        }
        System.out.println("\n");
    }
}
