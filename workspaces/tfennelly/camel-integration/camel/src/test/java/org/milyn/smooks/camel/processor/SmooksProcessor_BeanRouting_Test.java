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
package org.milyn.smooks.camel.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.javabean.Bean;
import org.milyn.payload.StringSource;
import org.milyn.smooks.camel.Coordinate;
import org.milyn.smooks.camel.routing.BeanRouter;

/**
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksProcessor_BeanRouting_Test extends CamelTestSupport {
	
	protected DirectProcessor directBProcessor;
	protected DirectProcessor directCProcessor;
	
	@Test
    public void test_dsl_configured() throws Exception {
		sendTo("direct:a1");        
        assertEquals(2, directBProcessor.coords.size());
        System.out.println(directBProcessor.coords);
        assertEquals(111, directBProcessor.coords.get(0).getX());
        assertEquals(222, directBProcessor.coords.get(0).getY());
        assertEquals(333, directBProcessor.coords.get(1).getX());
        assertEquals(444, directBProcessor.coords.get(1).getY());
    }

	@Test
    public void test_xml_configured() throws Exception {
		sendTo("direct:a2");
        assertEquals(1, directBProcessor.coords.size());
        assertEquals(111, directBProcessor.coords.get(0).getX());
        assertEquals(222, directBProcessor.coords.get(0).getY());
        assertEquals(1, directCProcessor.coords.size());
        assertEquals(333, directCProcessor.coords.get(0).getX());
        assertEquals(444, directCProcessor.coords.get(0).getY());
    }

	public void sendTo(String fromEndpoint) throws Exception {
		sendBody(fromEndpoint, new StringSource("<coords><coord x='111' y='222' /><coord x='333' y='444' /></coords>"));
    }

	/* (non-Javadoc)
	 * @see org.apache.camel.test.junit4.CamelTestSupport#createRouteBuilder()
	 */
	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                
            	// Create a Coordinate bean for each coord element and route it to "direct:b"...
            	Smooks smooks = new Smooks();
            	smooks.addVisitor(new Bean(Coordinate.class, "coordinate", "coords/coord").
        				bindTo("x", "coords/coord/@x").
        				bindTo("y", "coords/coord/@y"));
            	
        		smooks.addVisitor(new BeanRouter().setBeanId("coordinate").setToEndpoint("direct:b"), "coords/coord");
            	
                from("direct:a1").process(new SmooksProcessor(smooks));
                		/*
                		addVisitor(new Bean(Coordinate.class, "coordinate", "coords/coord").
                				bindTo("x", "coords/coord/@x").
                				bindTo("y", "coords/coord/@y")).
                		addVisitor(new BeanRouter().setBeanId("coordinate").setToEndpoint("direct:b"), "coords/coord"));
                		*/

                from("direct:a2").to("smooks://bean_routing_01.xml");
                
                directBProcessor = new DirectProcessor();
                from("direct:b").process(directBProcessor);            	
                directCProcessor = new DirectProcessor();
                from("direct:c").process(directCProcessor);            	
            }
        };
	}
	
	private class DirectProcessor implements Processor {

		private List<Coordinate> coords = new ArrayList<Coordinate>();
		
		public void process(Exchange exchange) throws Exception {
			coords.add((Coordinate) exchange.getIn().getBody());
		}		
	}
}
