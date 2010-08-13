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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.milyn.container.ExecutionContext;
import org.milyn.smooks.camel.processor.SmooksProcessor;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.SAXException;

/**
 * 
 * @author Christian Mueller
 *
 */
public class SmooksDataFormat2 implements DataFormat {
    
    private SmooksProcessor processor;
    private String smooksResultKey = "result";
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
	private String resultType;
    
    public SmooksDataFormat2() {
        processor = new SmooksProcessor();
    }
    
    public SmooksDataFormat2(String smooksConfig) throws IOException, SAXException {
        processor = new SmooksProcessor(smooksConfig);
    }

    public void marshal(Exchange exchange, Object graph, final OutputStream stream) throws Exception {
        synchronized (processor) {
        	setResultTypeOnProcessor(resultType, "org.milyn.payload.StringResult");
        }
        
        processor.process(exchange);
        
        ExecutionContext executionContext = exchange.getOut().getHeader(SmooksProcessor.SMOOKS_EXECUTION_CONTEXT, ExecutionContext.class);
        stream.write(exchange.getOut().getBody(String.class).getBytes(executionContext.getContentEncoding()));
    }

    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        synchronized (processor) {
        	setResultTypeOnProcessor(resultType, "org.milyn.payload.JavaResult");
        }
        
        processor.process(exchange);
        exchange.setProperty("SmooksDataFormatKeys", smooksResultKey);
        return exchange.getOut().getBody();
    }
    
    private void setResultTypeOnProcessor(String resultType, String defaultResultType)
    {
    	String type = resultType != null ? resultType:defaultResultType;
        processor.setResultType(type);
    }
    
    public String getSmooksResultKey() {
        return smooksResultKey;
    }

    public void setSmooksResultKey(String smooksResultKey) {
        this.smooksResultKey = smooksResultKey;
    }

    public Resource getSmooksConfig() {
        return processor.getSmooksConfig();
    }

    public void setSmooksConfig(Resource smooksConfig) {
        processor.setSmooksConfig(smooksConfig);
    }
    
    public void setSmooksConfig(String smooksConfig) {
        Resource resource = resourceLoader.getResource(smooksConfig);
        if (resource == null) {
            throw new IllegalArgumentException("Could not find resource for URI: " + smooksConfig + " using: " + resourceLoader);
        }
        processor.setSmooksConfig(resource);
    }
    
    public void setResultType(String resultType)
    {
    	this.resultType = resultType;
    }
}