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
import org.milyn.xml.XmlUtil;
import org.milyn.io.StreamUtils;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMResult;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Vector;
import org.milyn.javabean.BeanAccessor;

/**
 * Simple example main class.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Main {

    private static byte[] messageIn = readInputMessage();
	private static HashMap beans;

    protected static String runSmooksTransform() throws IOException, SAXException, SmooksException {
        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks("smooks-config.xml");
         // Create an exec context - no profiles....
        StandaloneExecutionContext executionContext = smooks.createExecutionContext();

        // Filter the input message to the outputWriter, using the execution context...
        DOMResult domResult = new DOMResult();
        smooks.filter(new StreamSource(new ByteArrayInputStream(messageIn)), domResult, executionContext);
		beans = BeanAccessor.getBeans( executionContext );

        return XmlUtil.serialize(domResult.getNode().getChildNodes(), true);
    }

    public static void main(String[] args) throws IOException, SAXException, SmooksException {
        System.out.println("\n\n==============Message In==============");
        System.out.println(new String(messageIn));
        System.out.println("======================================\n");

        pause("The EDI input stream can be seen above.  Press 'enter' to see this stream transformed into the Java Object model...");

        String messageOut = Main.runSmooksTransform();

        System.out.println("==============Message Out=============");
        
		Vector rollingstocks = (Vector) beans.get( "rollingstockList" );
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
