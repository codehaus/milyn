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
import org.milyn.templating.TemplatingUtils;
import org.milyn.io.StreamUtils;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Simple example main class.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Main {

    public static byte[] inputMessage = readInputMessage("input-message.xml");
    private Smooks smooks;

    protected Main() throws IOException, SAXException {
        smooks = new Smooks("smooks-config.xml");
    }

    /**
     * Run the transform for the request or response.
     * @param message The request/response input message.
     * @return The transformed request/response.
     */
    protected String runSmooksTransform(byte[] message) {

        // Create an exec context for the target profile....
        StandaloneExecutionContext executionContext = smooks.createExecutionContext();
        CharArrayWriter outputWriter = new CharArrayWriter();

        // Filter the message to the outputWriter, using the execution context...
        smooks.filter(new StreamSource(new ByteArrayInputStream(message)), new StreamResult(outputWriter), executionContext);

        return outputWriter.toString();
    }

    public static void main(String[] args) throws IOException, SAXException, SmooksException {
        Main smooksMain = new Main();
        String transResult;

        pause("Press 'enter' to display the input message...");
        System.out.println("\n");
        System.out.println(new String(inputMessage));
        System.out.println("\n\n");

        System.out.println("This needs to be transformed.");
        pause("Press 'enter' to display the transformed message...");
        transResult = smooksMain.runSmooksTransform(inputMessage);
        System.out.println("\n");
        System.out.println(transResult);
        System.out.println("\n\n");

        pause("And that's it!");
        System.out.println("\n\n");
    }

    private static byte[] readInputMessage(String messageFile) {
        try {
            return StreamUtils.readStream(new FileInputStream(messageFile));
        } catch (IOException e) {
            e.printStackTrace();
            return "<no-message/>".getBytes();
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
}
