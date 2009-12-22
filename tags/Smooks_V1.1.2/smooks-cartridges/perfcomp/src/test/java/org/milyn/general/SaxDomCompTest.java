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
package org.milyn.general;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.io.NullWriter;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SaxDomCompTest extends TestCase {

    public void test_dom() throws IOException, SAXException, InterruptedException {
        runTransform("smooks-dom.xml");
    }

    public void test_sax_01() throws IOException, SAXException, InterruptedException {
        runTransform("smooks-sax.xml");
    }

    public void test_sax_02() throws IOException, SAXException, InterruptedException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-sax.xml"));
        StringWriter writer = new StringWriter();

        runTransform(smooks, writer);

        //System.out.println(writer.toString());
    }

    private void runTransform(String config) throws IOException, SAXException, InterruptedException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream(config));
        NullWriter writer = new NullWriter();

        // warm ups...
        runTransform(smooks, writer);
        runTransform(smooks, writer);

        long start = System.currentTimeMillis();
        for(int i = 0; i < 1; i++) {
            runTransform(smooks, writer);
        }
        System.out.println(config + " took: " + (System.currentTimeMillis() - start) + "ms");
    }

    private void runTransform(Smooks smooks, Writer writer) throws InterruptedException {
        ExecutionContext executionContext = smooks.createExecutionContext();
        smooks.filter(new StreamSource(getClass().getResourceAsStream("order-message.xml")), new StreamResult(writer), executionContext);
        Thread.sleep(20);
    }
}