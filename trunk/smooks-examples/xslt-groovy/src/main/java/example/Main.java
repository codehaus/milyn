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
import org.milyn.smooks.scripting.ScriptingUtils;
import org.milyn.templating.TemplatingUtils;
import org.milyn.io.StreamUtils;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.TransformerFactory;
import java.io.*;

/**
 * Simple example main class.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Main {

    private static byte[] messageIn = readInputMessage();

    protected static Node runSmooksTransform() throws IOException, SAXException, SmooksException {

        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks("smooks-config.xml");
         // Create an exec context - no profiles....
        StandaloneExecutionContext executionContext = smooks.createExecutionContext();

        // Filter the input message to the outputWriter, using the execution context...
        DOMResult domResult = new DOMResult();
        smooks.filter(new StreamSource(new ByteArrayInputStream(messageIn)), domResult, executionContext);

        return domResult.getNode();
    }

    public static void main(String[] args) throws IOException, SAXException, SmooksException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        System.out.println("\n\n==============Message In==============");
        System.out.println(new String(messageIn));
        System.out.println("======================================\n");

        Node messageOut = Main.runSmooksTransform();

        System.out.println("==============Message Out=============");
        System.out.println(XmlUtil.serialize(messageOut.getChildNodes(), true));
        System.out.println("======================================");
        System.out.println("\n**** Used XSLT Processor: " + transformerFactory.getClass().getName());
        System.out.println("\n**** To switch XSLT Processor, update the <dependencies> in pom.xml.");
        System.out.println("\n\n");
    }

    private static byte[] readInputMessage() {
        try {
            return StreamUtils.readStream(new FileInputStream("input-message.xml"));
        } catch (IOException e) {
            e.printStackTrace();
            return "<no-message/>".getBytes();
        }
    }
}
