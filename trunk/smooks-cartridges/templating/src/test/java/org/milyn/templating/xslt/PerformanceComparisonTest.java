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

package org.milyn.templating.xslt;

import junit.framework.TestCase;
import org.milyn.io.StreamUtils;
import org.milyn.xml.XmlUtil;
import org.milyn.SmooksStandalone;
import org.milyn.templating.TemplatingUtils;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * Compare performance of raw XSLT Vs XSLT via Smooks. 
 * @author tfennelly
 */
public class PerformanceComparisonTest extends TestCase {

    private Transformer xsltTransformer;
    private SmooksStandalone smooksTransformer;
    private CharArrayWriter resultsWriter;

    protected void setUp() throws Exception {
        // Initialise the transformers...
        initialiseXsltTransformer();
        initialiseSmooksTransformer();
        resultsWriter = new CharArrayWriter();
    }


    protected void tearDown() throws Exception {
        resultsWriter.close();
    }

    public void test_comparePerformance() throws TransformerException, IOException, InterruptedException, SAXException {
        PerformancePack.runComparisons("perf-inputs-01", xsltTransformer, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-02", xsltTransformer, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-03", xsltTransformer, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-04", xsltTransformer, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-05", xsltTransformer, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-06", xsltTransformer, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-07", xsltTransformer, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-08", xsltTransformer, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-09", xsltTransformer, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-10", xsltTransformer, smooksTransformer, resultsWriter);

        System.out.println();
        System.out.println("===========================================================");
        System.out.println(new String(resultsWriter.toCharArray()));
        System.out.println("===========================================================");
        System.out.println();
    }

    private void initialiseXsltTransformer() throws IOException, TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        StreamSource xslStreamSource;
        byte[] xslt = StreamUtils.readStream(getClass().getResourceAsStream("transform-order.xsl"));

        xslStreamSource = new StreamSource(new InputStreamReader(new ByteArrayInputStream(xslt), "UTF-8"));
        Templates xslTemplate = transformerFactory.newTemplates(xslStreamSource);
        xsltTransformer = xslTemplate.newTransformer();
    }

    private void initialiseSmooksTransformer() {
        SmooksResourceConfiguration res = new SmooksResourceConfiguration("Order", "devicename", "org/milyn/templating/xslt/transform-order.xsl");
        String transResult = null;

        smooksTransformer = new SmooksStandalone();
        smooksTransformer.registerUseragent("devicename");
        res.setParameter("is-xslt-templatelet", "false");
        smooksTransformer.registerResource(res);
        TemplatingUtils.registerCDUCreators(smooksTransformer.getContext());
    }

    private static class PerformancePack {

        private Transformer xsltTransformer;
        private SmooksStandalone smooksTransformer;
        
        private byte[] messageBytesIn;
        private String messageOutExpected;
        private int expectedOrderItemCount = 0;
        private Writer resultsWriter;

        private static void runComparisons(String packageName, Transformer xsltTransformer, SmooksStandalone smooksTransformer, Writer resultsWriter) throws IOException, SAXException, TransformerException, InterruptedException {
            PerformancePack pack = new PerformancePack(packageName, xsltTransformer, smooksTransformer, resultsWriter);

            pack.runComparisons();
        }

        private PerformancePack(String packageName, Transformer xsltTransformer, SmooksStandalone smooksTransformer, Writer resultsWriter) throws IOException, SAXException {
            messageBytesIn = StreamUtils.readStream(getClass().getResourceAsStream(packageName + "/order-message.xml"));
            messageOutExpected = new String(StreamUtils.readStream(getClass().getResourceAsStream(packageName + "/order-expected.xml")));

            Document message = XmlUtil.parseStream(new ByteArrayInputStream(messageBytesIn), false, false);
            NodeList orderItems = XmlUtil.getNodeList(message, "/Order/order-item");

            expectedOrderItemCount = orderItems.getLength();

            this.xsltTransformer = xsltTransformer;
            this.smooksTransformer = smooksTransformer;
            this.resultsWriter = resultsWriter;
        }

        public void runComparisons() throws TransformerException, IOException, InterruptedException, SAXException {
            int numIterations = 10;
            int numTransPerIterations = 10;
            long totalXsltTime = 0;
            long totalSmooksTime = 0;

            System.out.println("A few warm up runs of each engine... ");
            performXSLTTransforms(10);
            Thread.sleep(300);
            performSmooksTransforms(10);
            Thread.sleep(300);

            System.out.println();
            System.out.println("Timed runs of each engine... ");
            for(int i = 0; i < numIterations; i++) {
                totalXsltTime += performXSLTTransforms(numTransPerIterations);
                Thread.sleep(300);
                totalSmooksTime += performSmooksTransforms(numTransPerIterations);
                Thread.sleep(300);
            }

            System.out.println();
            System.out.println("Total XSLT: " + totalXsltTime);
            System.out.println("Total Smooks: " + totalSmooksTime);
            System.out.println();

            // So what's the Smooks overhead ??...
            double smooksOverheadMillis = totalSmooksTime - totalXsltTime;
            double smooksOverheadPercentage = ((smooksOverheadMillis/totalXsltTime) * 100.0);
            if(smooksOverheadMillis > 0) {
                System.out.println("Smooks Overhead: " + smooksOverheadMillis + " (" + smooksOverheadPercentage + "%)");
            } else {
                System.out.println("Smooks Overhead: none!");
            }

            // Ring a bell if the overhead is greater than 10%
            //assertTrue(smooksOverheadPercentage < 10.0);

            resultsWriter.write("" + messageBytesIn.length + "," + smooksOverheadMillis + "," + smooksOverheadPercentage);
            resultsWriter.write("\n");
        }

        private long performXSLTTransforms(int numIterations) throws TransformerException, IOException, SAXException {
            // Test that what's produces is ok...
            Element result = applyXslt();
            assertMessageOk(result, "Order/OrderLines/order-item");

            // Now test how fast it is...
            long start = System.currentTimeMillis();
            for(int i = 0; i < numIterations; i++) {
                applyXslt();
            }

            long time = (System.currentTimeMillis() - start);
            System.out.println("XSLT: " + time);

            return time;
        }

        private long performSmooksTransforms(int numIterations) throws IOException, SAXException {
            // Test that what't produces is ok...
            Document message = applySmooks();
            assertMessageOk(message, "/Order/OrderLines/order-item");

            // Now test how fast it is...
            long start = System.currentTimeMillis();
            for(int i = 0; i < numIterations; i++) {
                applySmooks();
            }

            long time = (System.currentTimeMillis() - start);
            System.out.println("Smooks: " + time);

            return time;
        }

        private Element applyXslt() throws SAXException, IOException, TransformerException {
            Document message;
            Element result;

            message = XmlUtil.parseStream(new ByteArrayInputStream(messageBytesIn), false, false);
            result = message.createElement("result");
            xsltTransformer.transform(new DOMSource(message.getDocumentElement()), new DOMResult(result));

            return result;
        }

        private Document applySmooks() throws SAXException, IOException {
            Document message;

            message = XmlUtil.parseStream(new ByteArrayInputStream(messageBytesIn), false, false);
            smooksTransformer.filter("devicename", message);

            return message;
        }

        private void assertMessageOk(Node result, String orderItemsXPath) {
            NodeList orderItems = XmlUtil.getNodeList(result, orderItemsXPath);
            assertEquals(expectedOrderItemCount, orderItems.getLength());
            assertEquals(messageOutExpected, XmlUtil.serialize(result.getChildNodes()));
        }
    }
}
