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

import junit.framework.TestCase;
import com.envoisolutions.sxc.xpath.XPathEventHandler;
import com.envoisolutions.sxc.xpath.XPathBuilder;
import com.envoisolutions.sxc.xpath.XPathEvaluator;
import com.envoisolutions.sxc.xpath.XPathEvent;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.xml.sax.SAXException;
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SXCComparisonTest extends TestCase {

    private static final int NUM_WARMUPS = 10;
    private static final int NUM_ITERATIONS = 100;

    public void test_sxc() throws Exception {

        final boolean[] match = new boolean[]{false};

        XPathEventHandler eventHandler = new XPathEventHandler() {

            public void onMatch(XPathEvent event) throws XMLStreamException {
                match[0] = true;
            }

        };

        XPathBuilder builder = new XPathBuilder();
        builder.listen("*/global", eventHandler);

        XPathEvaluator evaluator = builder.compile();

        for(int i = 0; i < NUM_WARMUPS; i++) {
            evaluator.evaluate(getMessageReader());
        }

        long start = System.currentTimeMillis();
        for(int i = 0; i < NUM_ITERATIONS; i++) {
            evaluator.evaluate(getMessageReader());
        }
        assertTrue(match[0]);
        System.out.println("Took: " + (System.currentTimeMillis() - start));
    }

    public void test_smooks() throws IOException, SAXException {
        runSmooks("smooks-sax-sxc-test.xml");
    }

    private void runSmooks(String config) throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream(config));

        for(int i = 0; i < NUM_WARMUPS; i++) {
            smooks.filter(getMessageReader(), null);
        }

        long start = System.currentTimeMillis();
        for(int i = 0; i < NUM_ITERATIONS; i++) {
            smooks.filter(getMessageReader(), null);
        }
        System.out.println("Took: " + (System.currentTimeMillis() - start));
    }
    
    private Source getMessageReader() {
        //return new InputStreamReader(getClass().getResourceAsStream("order-message.xml"));
        return new StreamSource(new InputStreamReader(getClass().getResourceAsStream("big_global.xml")));
    }
}
