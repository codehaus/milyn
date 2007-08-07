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
import org.milyn.SmooksUtil;
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

    public static byte[] requestMessage = readInputMessage("request-message.xml");
    public static byte[] responseMessage = readInputMessage("response-message.xml");
    private Smooks smooks;

    protected Main() throws IOException, SAXException {
        // Instantiate Smooks with the configs...
        smooks = new Smooks();
        smooks.addConfigurations("smooks-request-config.xml");
        smooks.addConfigurations("smooks-response-config.xml");
        TemplatingUtils.registerCDUCreators(smooks);
    }

    /**
     * Run the transform for the request or response.
     * @param targetProfile "request" or "response".
     * @param message The request/response input message.
     * @return The transformed request/response.
     */
    protected String runSmooksTransform(String targetProfile, byte[] message) {

        // Create an exec context for the target profile....
        StandaloneExecutionContext executionContext = smooks.createExecutionContext(targetProfile);
        CharArrayWriter outputWriter = new CharArrayWriter();

        // Filter the message to the outputWriter, using the execution context...
        smooks.filter(new StreamSource(new ByteArrayInputStream(message)), new StreamResult(outputWriter), executionContext);

        return outputWriter.toString();
    }

    public static void main(String[] args) throws IOException, SAXException, SmooksException {
        Main smooksMain = new Main();
        String transResult;

        System.out.println("\n\n");
        pause("Press 'enter' to display the incoming resuest message...");
        System.out.println("\n");
        System.out.println(new String(requestMessage));
        System.out.println("\n\n");

        System.out.println("This needs to be transformed.");
        pause("Press 'enter' to display the transformed request message...");
        transResult = smooksMain.runSmooksTransform("request", requestMessage);
        System.out.println("\n");
        System.out.println(transResult);
        System.out.println("\n\n");

        System.out.println("Request message is processed and a response is returned...");
        pause("Press 'enter' to display the outgoing response message...");
        System.out.println("\n");
        System.out.println(new String(responseMessage));
        System.out.println("\n\n");

        System.out.println("This needs to be transformed.");
        pause("Press 'enter' to display the transformed response message...");
        transResult = smooksMain.runSmooksTransform("response", responseMessage);
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
