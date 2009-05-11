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
import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.io.StreamUtils;
import org.milyn.rules.RuleEvalResult;
import org.milyn.validation.ValidationResult;
import org.xml.sax.SAXException;

/**
 * Simple example main class.
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 */
public class Main
{
    public static void main(final String... args) throws IOException, SAXException, SmooksException
    {
        final byte[] messageIn = readInputMessage();
        System.out.println("\n\n==============Message In==============");
        System.out.println(new String(messageIn));
        System.out.println("======================================");

        final List<RuleEvalResult> results = Main.runSmooksTransform(messageIn);

        System.out.println("\n==============Validation Result=======");
        if (results.isEmpty())
        {
            System.out.println("Message was valid");
        }
        else
        {
            for (RuleEvalResult result : results)
            {
                System.out.println(result);
            }
        }
        System.out.println("======================================\n");
    }

    protected static List<RuleEvalResult> runSmooksTransform(final byte[] messageIn) throws IOException, SAXException, SmooksException
    {
        // Instantiate Smooks with the config...
        final Smooks smooks = new Smooks("smooks-config.xml");

        try {
             // Create an exec context - no profiles....
            final ExecutionContext executionContext = smooks.createExecutionContext();
            final CharArrayWriter outputWriter = new CharArrayWriter();
            final ValidationResult validationResult = new ValidationResult();

            // Configure the execution context to generate a report...
            executionContext.setEventListener(new HtmlReportGenerator("target/report/report.html"));

            // Filter the input message to the outputWriter, using the execution context...
            smooks.filterSource(executionContext, new StreamSource(new ByteArrayInputStream(messageIn)), new StreamResult(outputWriter), validationResult);

            return validationResult.getWarnings();
        }
        finally
        {
            smooks.close();
        }
    }

    private static byte[] readInputMessage()
    {
        try
        {
            return StreamUtils.readStream(new FileInputStream("input-message.xml"));
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            return "<no-message/>".getBytes();
        }
    }
}
