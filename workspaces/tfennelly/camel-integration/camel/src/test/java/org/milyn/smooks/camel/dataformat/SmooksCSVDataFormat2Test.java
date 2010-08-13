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
package org.milyn.smooks.camel.dataformat;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;
import org.junit.Test;
import org.milyn.payload.JavaSourceWithoutEventStream;

/**
 * Smooks CSV DataFormat unit test.
 */
public class SmooksCSVDataFormat2Test extends CamelTestSupport {
	
    private static Customer charlesExpected;
    private static Customer chrisExpected;

    @EndpointInject(uri = "direct:unmarshal")
    private Endpoint unmarshal;

    @EndpointInject(uri = "direct:marshal")
    private Endpoint marshal;
    
    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    @SuppressWarnings("unchecked")
    @Test
    public void unmarshalCSV() throws Exception {
        result.expectedMessageCount(1);
        
        template.send(unmarshal, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("christian,mueller,Male,33,germany\n" +
                		"charles,moulliard,Male,43,belgium\n");
            }
        });

        assertMockEndpointsSatisfied();
        Exchange exchange = result.assertExchangeReceived(0);
        assertIsInstanceOf(List.class, exchange.getIn().getBody());
        List customerList = exchange.getIn().getBody(List.class);
        assertEquals(2, customerList.size());
        
        Customer chrisActual = (Customer) customerList.get(0);
        assertEquals(chrisActual, chrisActual);
        
        Customer charlesActual = (Customer) customerList.get(1);
        assertEquals(charlesExpected, charlesActual);
    }
    
    
    @BeforeClass
    public static void createExcpectedCustomers()
    {
    	charlesExpected = new Customer();
    	charlesExpected.setFirstName("charles");
    	charlesExpected.setLastName("moulliard");
    	charlesExpected.setAge(43);
    	charlesExpected.setGender(Gender.Male);
    	charlesExpected.setCountry("belgium");
    	
    	chrisExpected = new Customer();
    	chrisExpected.setFirstName("christian");
    	chrisExpected.setLastName("mueller");
    	chrisExpected.setAge(33);
    	chrisExpected.setGender(Gender.Male);
    	chrisExpected.setCountry("germany");
    }
    
    @Test
    public void marshalCSV() throws Exception {
        result.expectedMessageCount(1);
        
        final List<Customer> customerList = new ArrayList<Customer>();
        customerList.add(chrisExpected);
        customerList.add(charlesExpected);
        
        template.send(marshal, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(customerList);
            }
        });

        assertMockEndpointsSatisfied();
        Exchange exchange = result.assertExchangeReceived(0);
        assertEquals("christian,mueller,Male,33,germany\n" +
                "charles,moulliard,Male,43,belgium\n", exchange.getIn().getBody(String.class));
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                SmooksDataFormat2 csvUnmarshal = new SmooksDataFormat2("csv-smooks-unmarshal-config.xml");
                csvUnmarshal.setResultType("org.milyn.payload.JavaResult");

                from("direct:unmarshal").convertBodyTo(InputStream.class)
                .unmarshal(csvUnmarshal).convertBodyTo(List.class)
                .to("mock:result");
                
                SmooksDataFormat2 csvMarshal = new SmooksDataFormat2("csv-smooks-marshal-config.xml");
                csvMarshal.setResultType("org.milyn.payload.StringResult");
                
                from("direct:marshal").convertBodyTo(JavaSourceWithoutEventStream.class)
                .marshal(csvMarshal)
                .to("mock:result");
            }
        };
    }
}