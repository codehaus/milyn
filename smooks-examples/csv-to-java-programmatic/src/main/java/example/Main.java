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
import org.milyn.csv.CSVReaderConfigurator;
import org.milyn.csv.CSVBinding;
import org.milyn.csv.CSVBindingType;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.io.StreamUtils;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.HashMap;

/**
 * Simple example main class.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Main {

    private static String messageIn = readInputMessage();

    protected static List runSmooksTransform() throws IOException, SAXException, SmooksException {

        Smooks smooks = new Smooks();

        try {
            // ****
            // And here's the configuration... configuring the CSV reader and the direct
            // binding config to create a List of Person objects (List<Person>)...
            // ****
            smooks.setReaderConfig(new CSVReaderConfigurator("firstName,lastName,gender,age,country")
                    .setBinding(new CSVBinding("customerList", Customer.class, CSVBindingType.LIST)));

            // Configure the execution context to generate a report...
            ExecutionContext executionContext = smooks.createExecutionContext();
            executionContext.setEventListener(new HtmlReportGenerator("target/report/report.html"));

            JavaResult javaResult = new JavaResult();
            smooks.filter(new StringSource(messageIn), javaResult, executionContext);

            return (List) javaResult.getBean("customerList");
        } finally {
            smooks.close();
        }
    }

    public static void main(String[] args) throws IOException, SAXException, SmooksException {
        System.out.println("\n\n==============Message In==============");
        System.out.println(new String(messageIn));
        System.out.println("======================================\n");

        List messageOut = Main.runSmooksTransform();

        System.out.println("==============Message Out=============");
        System.out.println(messageOut);
        System.out.println("======================================\n\n");
    }

    private static String readInputMessage() {
        try {
            return StreamUtils.readStreamAsString(new FileInputStream("input-message.csv"));
        } catch (IOException e) {
            e.printStackTrace();
            return "<no-message/>";
        }
    }
}
