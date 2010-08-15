/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.milyn.smooks.camel.processor;

import java.io.File;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class SmooksProcessor2Test extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;
    
    private static final String expectedResponse = "<Order>\n"
        + "\t<header>\n"
        + "\t\t<order-id>1</order-id>\n"
        + "\t\t<status-code>0</status-code>\n"
        + "\t\t<net-amount>59.97</net-amount>\n"
        + "\t\t<total-amount>64.92</total-amount>\n"
        + "\t\t<tax>4.95</tax>\n"
        + "\t\t<date>Wed Nov 15 13:45:28 EST 2006</date>\n"
        + "\t</header>\n"
        + "\t<customer-details>\n"
        + "\t\t<username>user1</username>\n"
        + "\t\t<name>\n"
        + "\t\t\t<firstname>Harry</firstname>\n"
        + "\t\t\t<lastname>Fletcher</lastname>\n"
        + "\t\t</name>\n"
        + "\t\t<state>SD</state>\n"
        + "\t</customer-details>\n"
        + "\t<order-item>\n"
        + "\t\t<position>1</position>\n"
        + "\t\t<quantity>1</quantity>\n"
        + "\t\t<product-id>364</product-id>\n"
        + "\t\t<title>The 40-Year-Old Virgin</title>\n"
        + "\t\t<price>29.98</price>\n"
        + "\t</order-item>\n"
        + "\t<order-item>\n"
        + "\t\t<position>2</position>\n"
        + "\t\t<quantity>1</quantity>\n"
        + "\t\t<product-id>299</product-id>\n"
        + "\t\t<title>Pulp Fiction</title>\n"
        + "\t\t<price>29.99</price>\n"
        + "\t</order-item>\n"
        + "</Order>";

    @Test
    public void unmarshalEDI() throws Exception {
        assertOneProcessedMessage();
    }

	private void assertOneProcessedMessage() throws InterruptedException
	{
		result.expectedMessageCount(1);

        assertMockEndpointsSatisfied();

        Exchange exchange = result.assertExchangeReceived(0);
        assertIsInstanceOf(Document.class, exchange.getIn().getBody());
        assertEquals(expectedResponse, exchange.getIn().getBody(String.class));
	}
    
    @Test
    public void assertSmooksReportWasCreated() throws Exception
    {
        result.expectedMessageCount(1);
        assertMockEndpointsSatisfied();
        
        File report = new File("target/smooks-report.html");
        report.deleteOnExit();
        assertTrue("Smooks report was not generated.", report.exists());
    }
    
    @Test
    @Ignore("need to figure out how I can stop and start the processor in a unit test")
    public void stopStartContext() throws Exception
    {
    	assertOneProcessedMessage();
    	stopCamelContext();
    	startCamelContext();
    	assertOneProcessedMessage();
    }
    
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                SmooksProcessor processor = new SmooksProcessor("edi-to-xml-smooks-config.xml");
                processor.setResultType("javax.xml.transform.dom.DOMResult");
                processor.setReportPath("target/smooks-report.html");
                
                from("file://src/test/data?noop=true")
                .process(processor).convertBodyTo(Node.class)
                .to("mock:result");
            }
        };
    }
}