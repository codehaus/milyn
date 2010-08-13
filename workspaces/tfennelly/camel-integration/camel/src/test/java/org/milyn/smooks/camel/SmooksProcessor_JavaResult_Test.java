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

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.milyn.javabean.Bean;
import org.milyn.javabean.Value;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.milyn.smooks.camel.dataformat.mappers.StringInJavaOutMapper;
import org.milyn.smooks.camel.processor.SmooksProcessor;

/**
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksProcessor_JavaResult_Test extends CamelTestSupport {

	@Test
    public void test_single_value() throws Exception {
        Exchange response = template.request("direct:a", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(new StringSource("<coord x='1234' />"));
            }
        });
        assertOutMessageBodyEquals(response, 1234);
    }

	@Test
    public void test_multi_value() throws Exception {
        Exchange response = template.request("direct:b", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(new StringSource("<coord x='1234' y='98765.76' />"));
            }
        });
        Map<String, Object> body = (Map<String, Object>) response.getOut().getBody();
        assertEquals(1234, body.get("x"));
        assertEquals(98765.76D, (Double) body.get("y"), 0.01D);
        /* I'm unsure about the use of the JavaMessage class in this regard...
         * it will intercept the getBody method to find the value from the
         * map which would be the result from a multi-value JavaResult.
         * If users want to convert to a specific object instance they could create
         * a converter like this:
         * @Converter
         * public class CustomConverter
         * {
         * 	@Converter
         *  public static CustomClass toCustomClass(JavaResult result)
         *  {
         * 		// extract the values and create an instance of the CustomClass.
         * 		return customObjectInstance; 
         *  }
         * }
         * 
        assertEquals(1234, (int) response.getOut().getBody(Integer.class));
        assertEquals(98765.76D, (double) response.getOut().getBody(Double.class), 0.01D);
        */
    }
	
	@Test
    public void test_bean() throws Exception {
        Exchange response = template.request("direct:c", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(new StringSource("<coord x='111' y='222' />"));
            }
        });
        
        Coordinate coord = (Coordinate) response.getOut().getBody();
        
        assertEquals(111, coord.getX());
        assertEquals(222, coord.getY());
    }

	/* (non-Javadoc)
	 * @see org.apache.camel.test.junit4.CamelTestSupport#createRouteBuilder()
	 */
	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
            	
                from("direct:a").process(new SmooksProcessor().
                		addVisitor(new Value("x", "/coord/@x", Integer.class)).
		                setResultType("org.milyn.payload.JavaResult"));
                
                from("direct:b").process(new SmooksProcessor().
                		addVisitor(new Value("x", "/coord/@x", Integer.class)).
                		addVisitor(new Value("y", "/coord/@y", Double.class)).
		                setResultType("org.milyn.payload.JavaResult")).
                		convertBodyTo(Map.class);
                
                from("direct:c").process(new SmooksProcessor().
                		addVisitor(new Bean(Coordinate.class, "coordinate").
                				bindTo("x", "/coord/@x").
                				bindTo("y", "/coord/@y")).
		                setResultType("org.milyn.payload.JavaResult")).
                		convertBodyTo(Coordinate.class);

            	// TODO: Create a Processor (or something else) specifically for Java Binding.. make the DSL cleaner ??
            	
            }
        };
	}
}
