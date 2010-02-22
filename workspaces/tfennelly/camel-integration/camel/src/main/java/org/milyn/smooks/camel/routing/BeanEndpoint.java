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
package org.milyn.smooks.camel.routing;

import java.util.UUID;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultExchange;

/**
 * Bean Endpoint used for routing beans from the Smooks {@link BeanContext}
 * to a Camel endpoint.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanEndpoint extends DefaultEndpoint implements Consumer {

	private String beanId;
	private String toEndpoint;
	private Processor processor;
	private boolean started;

	/**
	 * @param beanId
	 * @param toEndpoint
	 */
	public BeanEndpoint(String beanId, String toEndpoint) {
		this.beanId = beanId;
		this.toEndpoint = toEndpoint;
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.Endpoint#createConsumer(org.apache.camel.Processor)
	 */
	public Consumer createConsumer(Processor processor) throws Exception {
		this.processor = processor;
		return this;
	}
	
	public void sendBean(Object bean) throws Exception {
		Exchange exchange = new DefaultExchange(this);
		
		exchange.getIn().setBody(bean);

		if(!started) {
			throw new CamelExchangeException("Cannot send bean.  Endpoint not started.", exchange);
		}		
		
		processor.process(exchange);
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.Endpoint#createProducer()
	 */
	public Producer createProducer() throws Exception {
		throw new UnsupportedOperationException("Producer not supported on this endpoint.");
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.IsSingleton#isSingleton()
	 */
	public boolean isSingleton() {
		return false;
	}

	
	/* (non-Javadoc)
	 * @see org.apache.camel.impl.DefaultEndpoint#createEndpointUri()
	 */
	@Override
	protected String createEndpointUri() {
		// TODO What are we supposed to do here???  We never need to address this endpoint via a URI. Just generating a random/unique address for now :)
		return "smooks:/" + beanId + "/" + UUID.randomUUID().toString();
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.Consumer#getEndpoint()
	 */
	public Endpoint getEndpoint() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.Service#start()
	 */
	public void start() throws Exception {
		started = true;
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.Service#stop()
	 */
	public void stop() throws Exception {
		started = false;
	}
}
