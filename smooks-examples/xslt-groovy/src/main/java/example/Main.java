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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.container.ExecutionContext;
import org.milyn.io.StreamUtils;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Simple example main class.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Main {

    private static byte[] messageIn = readInputMessage();

    protected static Node runSmooksTransform() throws IOException, SAXException, SmooksException {
    	
    	Locale defaultLocale = Locale.getDefault();
    	Locale.setDefault(new Locale("en", "IE"));
    	
        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks("smooks-config.xml");

        try {
             // Create an exec context - no profiles....
            ExecutionContext executionContext = smooks.createExecutionContext();

            DOMResult domResult = new DOMResult();

            // Configure the execution context to generate a report...
            executionContext.setEventListener(new HtmlReportGenerator("target/report/report.html"));

            // Filter the input message to the outputWriter, using the execution context...
            smooks.filter(new StreamSource(new ByteArrayInputStream(messageIn)), domResult, executionContext);

            Locale.setDefault(defaultLocale);

            return domResult.getNode();
        } finally {
            smooks.close();
        }
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
