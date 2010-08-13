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

import java.io.IOException;
import java.io.Reader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.container.plugin.SourceFactory;
import org.milyn.delivery.Visitor;
import org.milyn.delivery.VisitorAppender;
import org.milyn.smooks.camel.dataformat.SmooksMapper;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.SAXException;

/**
 * Smooks {@link Processor} for Camel.
 * 
 * @version $Revision$
 */
public class SmooksProcessor implements Processor
{
	public static final String SMOOKS_EXECUTION_CONTEXT = "CamelSmooksExecutionContext";

	private ResourceLoader resourceLoader = new DefaultResourceLoader();
	private Smooks smooks;
	private Resource smooksConfig;
	private SmooksMapper smooksMapper;

	/**
	 * Creates an instance of SmooksProcessor with a default configuration
	 * of the underlying Smooks instance.
	 */
	public SmooksProcessor()
	{
		smooks = new Smooks();
	}

	/**
	 * Creates an instance of SmooksProcessor with the specified configuration
	 * for the underlying Smooks instance.
	 * 
	 * @param configUri The path to the Smooks configuration file.
	 * @throws IOException
	 * @throws SAXException
	 */
	public SmooksProcessor(String configUri) throws IOException, SAXException
	{
		Resource config = getSmooksConfig(configUri);
		smooks = createSmooksFromResource(config);
	}

	/**
	 * Creates an instance of SmooksProcessor with the specified configuration
	 * for the underlying Smooks instance.
	 * 
	 * @param configUri The path to the Smooks configuration file.
	 * @throws IOException
	 * @throws SAXException
	 */
	public SmooksProcessor(Resource config) throws IOException, SAXException
	{
		smooks = createSmooksFromResource(config);
	}

	private Smooks createSmooksFromResource(Resource resource) throws IOException, SAXException
	{
		return new Smooks(resource.getInputStream());
	}

	public void process(Exchange exchange) throws Exception
	{
		ExecutionContext executionContext = smooks.createExecutionContext();
		executionContext.setAttribute(Exchange.class, exchange);
		exchange.getOut().setHeader(SMOOKS_EXECUTION_CONTEXT, executionContext);

		if (smooksMapper != null)
		{
			Source source = smooksMapper.createSource(exchange);
			Result result = smooksMapper.createResult();
			smooks.filterSource(executionContext, source, result);
			smooksMapper.mapResult(result, exchange);
		} 
		else
		{
			Source source = getSource(exchange);
			smooks.filterSource(executionContext, source);
		}
		executionContext.removeAttribute(Exchange.class);
	}

	private Source getSource(Exchange exchange)
	{
		Message in = exchange.getIn();

		Source source = in.getBody(Source.class);
		if (source == null)
		{
			Reader reader = in.getBody(Reader.class);
			if (reader == null)
			{
				source = SourceFactory.getInstance().createSource(in.getBody());
			} else
			{
				source = new StreamSource(reader);
			}
		}
		return source;
	}

	public Resource getSmooksConfig()
	{
		return smooksConfig;
	}

	public Resource getSmooksConfig(String configUri)
	{
		Resource resource = resourceLoader.getResource(configUri);
		if (resource == null)
		{
			throw new IllegalArgumentException("Could not find resource for URI: " + smooksConfig + " using: " + resourceLoader);
		}
		return resource;
	}

	public void setSmooksConfig(Resource smooksConfig)
	{
		this.smooksConfig = smooksConfig;
	}

	public void setSmooksConfig(String smooksConfig)
	{
		setSmooksConfig(getSmooksConfig(smooksConfig));
	}

	public SmooksMapper getSmooksMapper()
	{
		return smooksMapper;
	}

	public SmooksProcessor setSmooksMapper(SmooksMapper smooksMapper)
	{
		this.smooksMapper = smooksMapper;
		return this;
	}

	/**
	 * Add a visitor instance.
	 * 
	 * @param visitor
	 *            The visitor implementation.
	 * @param targetSelector
	 *            The message fragment target selector.
	 * @return This instance.
	 */
	public SmooksProcessor addVisitor(Visitor visitor, String targetSelector)
	{
		smooks.addVisitor(visitor, targetSelector);
		return this;
	}

	/**
	 * Add a visitor instance to <code>this</code> Smooks instance via a
	 * {@link VisitorAppender}.
	 * 
	 * @param appender
	 *            The visitor appender.
	 * @return This instance.
	 */
	public SmooksProcessor addVisitor(VisitorAppender appender)
	{
		smooks.addVisitor(appender);
		return this;
	}

}