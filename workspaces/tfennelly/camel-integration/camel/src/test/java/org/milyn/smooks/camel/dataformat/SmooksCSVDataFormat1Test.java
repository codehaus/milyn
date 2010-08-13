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


import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Smooks CSV DataFormat unit test.
 */
public class SmooksCSVDataFormat1Test extends CamelTestSupport {

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
                		"charles,moulliard,Male,43,belgium");
            }
        });

        assertMockEndpointsSatisfied();
        Exchange exchange = result.assertExchangeReceived(0);
        assertIsInstanceOf(List.class, exchange.getIn().getBody());
        List customerList = exchange.getIn().getBody(List.class);
        assertEquals(2, customerList.size());
        
        Customer chris = (Customer) customerList.get(0);
        assertEquals("christian", chris.getFirstName());
        assertEquals("mueller", chris.getLastName());
        assertEquals(Gender.Male, chris.getGender());
        assertEquals(33, chris.getAge());
        assertEquals("germany", chris.getCountry());
        
        Customer charles = (Customer) customerList.get(1);
        assertEquals("charles", charles.getFirstName());
        assertEquals("moulliard", charles.getLastName());
        assertEquals(Gender.Male, charles.getGender());
        assertEquals(43, charles.getAge());
        assertEquals("belgium", charles.getCountry());
    }
    
    @Test
    public void marshalCSV() throws Exception {
        result.expectedMessageCount(1);
        
        final List<Customer> customerList = new ArrayList<Customer>();
        final Customer chris = new Customer();
        chris.setFirstName("christian");
        chris.setLastName("mueller");
        chris.setGender(Gender.Male);
        chris.setAge(33);
        chris.setCountry("germany");
        customerList.add(chris);
        Customer charles = new Customer();
        charles.setFirstName("charles");
        charles.setLastName("moulliard");
        charles.setGender(Gender.Male);
        charles.setAge(43);
        charles.setCountry("belgium");
        customerList.add(charles);
        
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
                SmooksDataFormat1 csvUnmarshal = new SmooksDataFormat1("csv-smooks-unmarshal-config.xml");

                from("direct:unmarshal")
                .unmarshal(csvUnmarshal)
                .to("mock:result");
                
                SmooksDataFormat1 csvMarshal = new SmooksDataFormat1("csv-smooks-marshal-config.xml");
                
                from("direct:marshal")
                .marshal(csvMarshal)
                .to("mock:result");
            }
        };
    }
}