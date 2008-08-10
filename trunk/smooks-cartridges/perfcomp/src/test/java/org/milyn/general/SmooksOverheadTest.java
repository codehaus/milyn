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
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.InputSource;
import org.xml.sax.ext.DefaultHandler2;
import org.milyn.Smooks;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksOverheadTest extends TestCase {

    private static final int NUM_WARMUPS = 10;
    //private static final int NUM_ITERATIONS = 10000000;
    private static final int NUM_ITERATIONS = 200;

    public void test_saxonly_timings() throws SAXException, IOException {
        for(int i = 0; i < NUM_WARMUPS; i++) {
            readBySax();
        }

        long start = System.currentTimeMillis();
        for(int i = 0; i < NUM_ITERATIONS; i++) {
            readBySax();
        }
        System.out.println("Took: " + (System.currentTimeMillis() - start));
    }

    public void test_smookssax_timings_novis() throws IOException, SAXException {
        runSmooks("smooks-sax-empty.xml");
    }

    public void test_smookssax_timings_1vis() throws IOException, SAXException {
        runSmooks("smooks-sax-1vis.xml");
    }

    public void test_smookssax_timings_2vis() throws IOException, SAXException {
        runSmooks("smooks-sax-2vis.xml");
    }

    private void runSmooks(String config) throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream(config));

        for(int i = 0; i < NUM_WARMUPS; i++) {
            smooks.filter(new StreamSource(getMessageReader()));
        }

        long start = System.currentTimeMillis();
        for(int i = 0; i < NUM_ITERATIONS; i++) {
            smooks.filter(new StreamSource(getMessageReader()));
        }
        System.out.println("Took: " + (System.currentTimeMillis() - start));
    }

    private void readBySax() throws SAXException, IOException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        DefaultHandler2 handler = new DefaultHandler2();

        reader.setContentHandler(handler);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);

        reader.parse(new InputSource(getMessageReader()));
    }

    private InputStreamReader getMessageReader() {
        return new InputStreamReader(getClass().getResourceAsStream("order-message.xml"));
    }
}
