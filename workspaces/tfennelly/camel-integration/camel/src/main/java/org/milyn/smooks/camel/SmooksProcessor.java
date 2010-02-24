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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.milyn.Smooks;
import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.container.plugin.SourceFactory;
import org.milyn.delivery.Visitor;
import org.milyn.delivery.VisitorAppender;
import org.milyn.javabean.context.BeanContext;
import org.milyn.smooks.camel.result.JavaResultMapper;
import org.milyn.smooks.camel.result.ResultMapper;
import org.milyn.smooks.camel.result.StringResultMapper;
import org.xml.sax.SAXException;

/**
 * Smooks {@link Processor} for Camel.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksProcessor implements Processor {

	// TODO: What sort of Camel component should this be?  Looks like it could also be 
	// a Component + Endpoint as with the Velocity integration?? Or maybe there could 
	// be different components?
	
	private Smooks smooks = new Smooks();
	private ResultMapper resultMapper;
	
	/**
	 * Public constructor.
	 */
	public SmooksProcessor() {
	}
	
	/**
	 * Public constructor.
     * @param routeBuilder Associated {@link RouteBuilder} instance.
	 */
	public SmooksProcessor(RouteBuilder routeBuilder) {
		setRouteBuilder(routeBuilder);
	}
	
	/**
	 * Public constructor.
	 * @param smooks Smooks instance.
     * @param routeBuilder Associated {@link RouteBuilder} instance.
	 */
	public SmooksProcessor(Smooks smooks, RouteBuilder routeBuilder) {
		AssertArgument.isNotNull(smooks, "smooks");
		this.smooks = smooks;
		setRouteBuilder(routeBuilder);
    	// Force configuration of routes...
    	this.smooks.createExecutionContext();
	}
	
    /**
     * Public constructor.
     * @param resourceURI XML resource configuration stream URI.
     * @param routeBuilder Associated {@link RouteBuilder} instance.
     * @throws IOException  Error reading resource stream.
     * @throws SAXException Error parsing the resource stream.
     * @see SmooksResourceConfiguration
     */
    public SmooksProcessor(String resourceURI, RouteBuilder routeBuilder) throws IOException, SAXException {
		AssertArgument.isNotNullAndNotEmpty(resourceURI, "resourceURI");
		setRouteBuilder(routeBuilder);
    	this.smooks.addConfigurations(resourceURI);
    	// Force configuration of routes...
    	this.smooks.createExecutionContext();
    }

    /**
     * Public constructor.
     * @param resourceConfigStream XML resource configuration stream.
     * @param routeBuilder Associated {@link RouteBuilder} instance.
     * @throws IOException  Error reading resource stream.
     * @throws SAXException Error parsing the resource stream.
     * @see SmooksResourceConfiguration
     */
    public SmooksProcessor(InputStream resourceConfigStream, RouteBuilder routeBuilder) throws IOException, SAXException {
		AssertArgument.isNotNull(resourceConfigStream, "resourceConfigStream");
		setRouteBuilder(routeBuilder);
    	this.smooks.addConfigurations(resourceConfigStream);
    	// Force configuration of routes...
    	this.smooks.createExecutionContext();
    }

	private void setRouteBuilder(RouteBuilder routeBuilder) {
		AssertArgument.isNotNull(routeBuilder, "routeBuilder");
		smooks.getApplicationContext().setAttribute(RouteBuilder.class, routeBuilder);
	}

    /**
     * Add a visitor instance.
     * @param visitor The visitor implementation.
     * @param targetSelector The message fragment target selector.
     * @return This instance.
     */
    public SmooksProcessor addVisitor(Visitor visitor, String targetSelector) {    	
        smooks.addVisitor(visitor, targetSelector);
        return this;
    }

    /**
     * Add a visitor instance to <code>this</code> Smooks instance
     * via a {@link VisitorAppender}.
     *
     * @param appender The visitor appender.
     * @return This instance.
     */
	public SmooksProcessor addVisitor(VisitorAppender appender) {
        smooks.addVisitor(appender);
        return this;
	}
    
    /**
     * Map a character stream result to the {@link Exchange} out-message.
     * <p/>
     * Capture a StreamResult from the {@link Smooks#filterSource(javax.xml.transform.Source, javax.xml.transform.Result...)}
     * method and map the character data to the {@link Exchange} out-message.
     * 
     * @return This object instance.
     */
    public SmooksProcessor mapCharResult() {
    	resultMapper = new StringResultMapper();
    	return this;
    }
    
    /**
     * Map beans from the Smooks {@link BeanContext} to the Camel {@link Exchange} out-message.
     * <p/>
     * If no beans are specified, the full contents of the {@link BeanContext} is mapped in a {@link Map} instance.
     * If a list of beans is specified, all the specified beans are mapped (also in a {@link Map} instance).
     * If only a single bean is specified, it is mapped on its own (i.e. not in a {@link Map} instance).
     * 
     * @param beansToMap A list of bean names, or <code>null</code> to map all beans in the
     * {@link BeanContext}, as a {@link Map}.
     * @return This object instance.
     */
    public SmooksProcessor mapJavaResult(String... beansToMap) {
    	resultMapper = new JavaResultMapper(beansToMap);
    	return this;
    }
    
	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	public void process(Exchange exchange) throws Exception {
		ExecutionContext execContect = smooks.createExecutionContext();
		
		// Bind the exchange to the exec context so it can be available to any visitor logic
		// if needed...
		execContect.setAttribute(Exchange.class, exchange);
		
		// Filter...
		if(resultMapper != null) {
			Source source = getSource(exchange);
			Result result = resultMapper.createResult();
			smooks.filterSource(execContect, source, result);
			
			// And map the result back into the exchange...
			resultMapper.mapResult(result, exchange);
		} else {
			smooks.filterSource(execContect, getSource(exchange));			
		}
	}
	
	private Source getSource(Exchange exchange) {
		Message in = exchange.getIn();
		
		Source source = in.getBody(Source.class);
		if(source == null) {
			Reader reader = in.getBody(Reader.class);
			if(reader == null) {				
				source = SourceFactory.getInstance().createSource(in.getBody());
			} else {
				source = new StreamSource(reader);
			}
		}
		
		return source;
	}

	/**
	 * Close the Smooks instance.
	 */
	public void close() {
		// TODO: Is there are Camel lifecycle event that we can hook into so as to make sure we close the Smooks instance?
		smooks.close();
	}
}
