/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.smooks.camel;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.milyn.payload.StringSource;
import org.milyn.smooks.camel.dataformat.mappers.StringInStringOutMapper;
import org.milyn.smooks.camel.processor.SmooksProcessor;

/**
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksProcessor_StringResult_Test extends CamelTestSupport {

	@Test
	@Ignore
    public void test() throws Exception {
        Exchange response = template.request("direct:a", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(new StringSource("<x/>"));
            }
        });
        
        assertEquals("<x></x>", DirectBProcessor.inMessage);
    }

	/* (non-Javadoc)
	 * @see org.apache.camel.test.junit4.CamelTestSupport#createRouteBuilder()
	 */
	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:a").
                process(new SmooksProcessor().setSmooksMapper(new StringInStringOutMapper())).
                convertBodyTo(String.class).
                to("direct:b");
                from("direct:b").process(new DirectBProcessor());
            }
        };
	}
	
	private static class DirectBProcessor implements Processor {

		private static String inMessage;
		
		public void process(Exchange exchange) throws Exception {
			inMessage = (String) exchange.getIn().getBody();
		}		
	}
}
