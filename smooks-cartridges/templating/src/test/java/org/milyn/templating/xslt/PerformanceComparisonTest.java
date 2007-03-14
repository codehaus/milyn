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
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Compare performance of raw XSLT Vs XSLT via Smooks. 
 * @author tfennelly
 */
public class PerformanceComparisonTest extends TestCase {

    private Templates xslTemplate;
    private SmooksStandalone smooksTransformer;
    private static final int NUM_ITERATIONS = 1;

    protected void setUp() throws Exception {
        // Initialise the transformers...
        initialiseXsltTransformer();
        initialiseSmooksTransformer();
    }

    public void test_comparePerformance() throws TransformerException, IOException, InterruptedException, SAXException {
        /*
        PerformancePack.runComparisons("perf-inputs-01", xslTemplate, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-02", xslTemplate, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-03", xslTemplate, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-04", xslTemplate, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-05", xslTemplate, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-06", xslTemplate, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-07", xslTemplate, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-08", xslTemplate, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-09", xslTemplate, smooksTransformer, resultsWriter);
        PerformancePack.runComparisons("perf-inputs-10", xslTemplate, smooksTransformer, resultsWriter);

        System.out.println();
        System.out.println("===========================================================");
        System.out.println(new String(resultsWriter.toCharArray()));
        System.out.println("===========================================================");
        System.out.println();
        */
    }

    public void test_comparePerformance_multithreaded() throws TransformerException, IOException, InterruptedException, SAXException {
        List<PerformanceThread> threadList = new ArrayList<PerformanceThread>();

        threadList.add(new PerformanceThread("perf-inputs-01", xslTemplate, smooksTransformer));
        threadList.add(new PerformanceThread("perf-inputs-02", xslTemplate, smooksTransformer));
        threadList.add(new PerformanceThread("perf-inputs-03", xslTemplate, smooksTransformer));
        threadList.add(new PerformanceThread("perf-inputs-04", xslTemplate, smooksTransformer));
        threadList.add(new PerformanceThread("perf-inputs-05", xslTemplate, smooksTransformer));
        threadList.add(new PerformanceThread("perf-inputs-06", xslTemplate, smooksTransformer));
        threadList.add(new PerformanceThread("perf-inputs-07", xslTemplate, smooksTransformer));
        threadList.add(new PerformanceThread("perf-inputs-08", xslTemplate, smooksTransformer));
        threadList.add(new PerformanceThread("perf-inputs-09", xslTemplate, smooksTransformer));
        threadList.add(new PerformanceThread("perf-inputs-10", xslTemplate, smooksTransformer));
        /*
        */

        System.out.println();
        System.out.println("Starting Threads...");
        System.out.println();
        for(PerformanceThread thread : threadList) {
            thread.start();
            Thread.sleep(100);
        }

        boolean finished = false;
        while(!finished) {
            Thread.sleep(1000);
            finished = true;
            for(PerformanceThread thread : threadList) {
                if(!thread.isFinished()) {
                    finished = false;
                    break;
                }
            }
        }

        System.out.println();
        System.out.println("All Threads complete");

        System.out.println();
        System.out.println("=====================================RESULTS===========================================");
        for(PerformanceThread thread : threadList) {
            System.out.println("" + thread.performancePack.processCount + "," + thread.performancePack.messageBytesIn.length + "," + thread.performancePack.totalXsltTime + "," + thread.performancePack.totalSmooksTime + "," + thread.performancePack.smooksOverheadMillis + "," + thread.performancePack.smooksOverheadPercentage);
        }
        System.out.println("=======================================================================================");
        System.out.println();
    }

    private void initialiseXsltTransformer() throws IOException, TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        StreamSource xslStreamSource;
        byte[] xslt = StreamUtils.readStream(getClass().getResourceAsStream("transform-order.xsl"));

        xslStreamSource = new StreamSource(new InputStreamReader(new ByteArrayInputStream(xslt), "UTF-8"));
        xslTemplate = transformerFactory.newTemplates(xslStreamSource);
    }

    private void initialiseSmooksTransformer() {
        SmooksResourceConfiguration res = new SmooksResourceConfiguration("Order", "devicename", "org/milyn/templating/xslt/transform-order.xsl");

        smooksTransformer = new SmooksStandalone();
        smooksTransformer.registerUseragent("devicename");
        res.setParameter("is-xslt-templatelet", "false");
        //res.setParameter("streamResult", "false");
        smooksTransformer.registerResource(res);
        TemplatingUtils.registerCDUCreators(smooksTransformer.getContext());
    }

    private static class PerformanceThread extends Thread {

        private boolean finished = false;
        private String packageName;
        private Templates xslTemplate;
        private SmooksStandalone smooksTransformer;
        private PerformancePack performancePack;

        public PerformanceThread(String packageName, Templates xslTemplate, SmooksStandalone smooksTransformer) {
            this.packageName = packageName;
            this.xslTemplate = xslTemplate;
            this.smooksTransformer = smooksTransformer;
        }

        public void run() {
            try {
                performancePack = PerformancePack.runComparisons(packageName, xslTemplate, smooksTransformer);
            } catch (Throwable t) {
                t.printStackTrace();
                TestCase.fail(t.getMessage());
            } finally {
                finished = true;
            }
        }

        public boolean isFinished() {
            return finished;
        }
    }

    private static class PerformancePack {

        private Templates xslTemplate;
        private SmooksStandalone smooksTransformer;
        
        private byte[] messageBytesIn;
        private String messageOutExpected;
        private int expectedOrderItemCount = 0;
        private long totalXsltTime;
        private long totalSmooksTime;
        private long smooksOverheadMillis;
        private double smooksOverheadPercentage;
        private int processCount;

        private static PerformancePack runComparisons(String packageName, Templates xslTemplate, SmooksStandalone smooksTransformer) throws IOException, SAXException, TransformerException, InterruptedException {
            PerformancePack pack = new PerformancePack(packageName, xslTemplate, smooksTransformer);

            pack.runComparisons();

            return pack;
        }

        private PerformancePack(String packageName, Templates xslTemplate, SmooksStandalone smooksTransformer) throws IOException, SAXException, TransformerConfigurationException {
            messageBytesIn = StreamUtils.readStream(getClass().getResourceAsStream(packageName + "/order-message.xml"));
            messageOutExpected = new String(StreamUtils.readStream(getClass().getResourceAsStream(packageName + "/order-expected.xml")));

            Document message = XmlUtil.parseStream(new ByteArrayInputStream(messageBytesIn), false, false);
            NodeList orderItems = XmlUtil.getNodeList(message, "/Order/order-item");

            expectedOrderItemCount = orderItems.getLength();

            this.xslTemplate = xslTemplate;
            this.smooksTransformer = smooksTransformer;
        }

        public void runComparisons() throws TransformerException, IOException, InterruptedException, SAXException {
            int numIterations = NUM_ITERATIONS;
            int numTransPerIterations = 10;

            for(int i = 0; i < numIterations; i++) {
                totalXsltTime += performXSLTTransforms(numTransPerIterations);
                Thread.sleep(200);
                totalSmooksTime += performSmooksTransforms(numTransPerIterations);
                Thread.sleep(200);
            }

            // So what's the Smooks overhead ??...
            smooksOverheadMillis = totalSmooksTime - totalXsltTime;
            smooksOverheadPercentage = (((double)smooksOverheadMillis/(double)totalXsltTime) * 100.0);

            // Ring a bell if the overhead is greater than 10%
            //assertTrue(smooksOverheadPercentage < 10.0);
        }

        private long performXSLTTransforms(int numIterations) throws TransformerException, IOException, SAXException {
            // Test that what's produces is ok...
            Element result = (Element) applyXslt(false);
            assertMessageOk(result, "Order/OrderLines/order-item");

            // Now test how fast it is...
            long start = System.currentTimeMillis();
            for(int i = 0; i < numIterations; i++) {
                applyXslt(false);
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

        private Object applyXslt(boolean stream) throws SAXException, IOException, TransformerException {
            Document message;
            Object result = null;

            message = XmlUtil.parseStream(new ByteArrayInputStream(messageBytesIn), false, false);

            if(stream) {
                CharArrayWriter writer = new CharArrayWriter();
                synchronized(xslTemplate) {
                    xslTemplate.newTransformer().transform(new DOMSource(message.getDocumentElement()), new StreamResult(writer));
                    writer.toString();
                }
            } else {
                result = message.createElement("result");
                synchronized(xslTemplate) {
                    xslTemplate.newTransformer().transform(new DOMSource(message.getDocumentElement()), new DOMResult((Element)result));
                }
            }

            processCount++;

            return result;
        }

        private Document applySmooks() throws SAXException, IOException {
            Document message;

            message = XmlUtil.parseStream(new ByteArrayInputStream(messageBytesIn), false, false);
            smooksTransformer.filter("devicename", message);

            processCount++;
            
            return message;
        }

        private void assertMessageOk(Node result, String orderItemsXPath) {
            //NodeList orderItems = XmlUtil.getNodeList(result, orderItemsXPath);
            //assertEquals("Invalid Message count.", expectedOrderItemCount, orderItems.getLength());
            assertEquals("Message not as expected.", messageOutExpected, XmlUtil.serialize(result.getChildNodes()));
        }
    }
}
