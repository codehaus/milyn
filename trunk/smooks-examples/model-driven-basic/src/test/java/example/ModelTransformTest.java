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

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.io.StreamUtils;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ModelTransformTest extends TestCase {

    public void test() throws IOException, SAXException {
        byte[] expected_res = StreamUtils.readStream(getClass().getResourceAsStream("expected.xml"));
        Main smooksMain = new Main();
        String transRes;

        transRes = smooksMain.runSmooksTransform(Main.inputMessage);
        assertTrue("Expected:\n" + new String(expected_res) + ". \nGot:\n" + transRes, StreamUtils.compareCharStreams(new ByteArrayInputStream(expected_res), new ByteArrayInputStream(transRes.getBytes())));
    }

    public void test_report() throws IOException, SAXException {
        Smooks smooks = new Smooks("smooks-config.xml");
        StringWriter outWriter = new StringWriter();

        HtmlReportGenerator.generateReport(smooks, new StreamSource(new ByteArrayInputStream(Main.inputMessage)), null, new File("/zap/report.html"), false);
        System.out.println(outWriter);
    }
}
