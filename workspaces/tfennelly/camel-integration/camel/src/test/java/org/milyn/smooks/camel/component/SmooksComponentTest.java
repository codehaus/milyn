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
package org.milyn.smooks.camel.component;

import static org.custommonkey.xmlunit.XMLAssert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.milyn.smooks.camel.dataformat.SmooksMapper;
import org.milyn.smooks.camel.dataformat.mappers.StreamInDomOutMapper;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Unit test for {@link SmooksComponent}.
 * 
 * @author 
 *
 */
public class SmooksComponentTest extends CamelTestSupport
{
	@EndpointInject(uri = "mock:result")
	private MockEndpoint result;

	@BeforeClass
	public static void setup()
	{
		XMLUnit.setIgnoreWhitespace(true);
	}

	@Override
	protected JndiRegistry createRegistry() throws Exception
	{
		JndiRegistry jndiRegistry = super.createRegistry();
		jndiRegistry.bind("smooksMapper", new StreamInDomOutMapper());
		return jndiRegistry;
	}
	
	@Test
	public void unmarshalEDI() throws Exception
	{
		result.expectedMessageCount(1);
		assertMockEndpointsSatisfied();

		Exchange exchange = result.assertExchangeReceived(0);
		
		assertIsInstanceOf(Document.class, exchange.getIn().getBody());
		assertXMLEqual(getExpectedOrderXml(), getBodyAsString(exchange));
	}

	private InputStreamReader getExpectedOrderXml()
	{
		return new InputStreamReader(getClass().getResourceAsStream("/xml/expected-order.xml"));
	}
	
	private StringReader getBodyAsString(Exchange exchange)
	{
		return new StringReader(exchange.getIn().getBody(String.class));
	}

	protected RouteBuilder createRouteBuilder() throws Exception
	{
		return new RouteBuilder()
		{
			public void configure() throws Exception
			{
				from("file://src/test/data?noop=true")
				.to("smooks://edi-to-xml-smooks-config.xml?smooksMapper=#smooksMapper")
				.to("mock:result");
			}
		};
	}
}