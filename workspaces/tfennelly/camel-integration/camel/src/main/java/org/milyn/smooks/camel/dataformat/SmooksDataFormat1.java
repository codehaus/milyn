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

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.milyn.payload.JavaSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.SAXException;

/**
 * @author Christian Mueller
 */
public class SmooksDataFormat1 implements DataFormat {
    
    public static final String SMOOKS_EXECUTION_CONTEXT = "CamelSmooksExecutionContext";
    
    private Smooks smooks;
    private Resource smooksConfig;
    private String smooksResultKey = "result";
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    
    public SmooksDataFormat1() {
    }
    
    public SmooksDataFormat1(String smooksConfig) {
        setSmooksConfig(smooksConfig);
    }

    public void marshal(Exchange exchange, Object graph, final OutputStream stream) throws Exception {
        Smooks smooks = getSmooks();
        ExecutionContext executionContext = smooks.createExecutionContext();
        executionContext.setAttribute(Exchange.class, exchange);
        exchange.getOut().setHeader(SMOOKS_EXECUTION_CONTEXT, executionContext);

        JavaSource javaSource = new JavaSource(graph);
        javaSource.setEventStreamRequired(false);
        
        smooks.filterSource(executionContext, javaSource, new StreamResult(stream));
        
        executionContext.removeAttribute(Exchange.class);
    }

    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        Smooks smooks = getSmooks();
        ExecutionContext executionContext = smooks.createExecutionContext();
        executionContext.setAttribute(Exchange.class, exchange);

        JavaResult result = new JavaResult();
        
        smooks.filterSource(executionContext, new StreamSource(stream), result);
        
        executionContext.removeAttribute(Exchange.class);

        return result.getBean(smooksResultKey);
    }
    
    public synchronized Smooks getSmooks() throws IOException, SAXException {
        if (smooks == null) {
            smooks = createSmooks();
        }
        return smooks;
    }
    
    protected Smooks createSmooks() throws IOException, SAXException {
        if (smooksConfig == null) {
            throw new IllegalArgumentException("smooksConfig must be set");
        }
        
        return new Smooks(smooksConfig.getInputStream());
    }
    
    public String getSmooksResultKey() {
        return smooksResultKey;
    }

    public void setSmooksResultKey(String smooksResultKey) {
        this.smooksResultKey = smooksResultKey;
    }

    public Resource getSmooksConfig() {
        return smooksConfig;
    }

    public void setSmooksConfig(Resource smooksConfig) {
        this.smooksConfig = smooksConfig;
    }
    
    public void setSmooksConfig(String smooksConfig) {
        Resource resource = resourceLoader.getResource(smooksConfig);
        if (resource == null) {
            throw new IllegalArgumentException("Could not find resource for URI: " + smooksConfig + " using: " + resourceLoader);
        }
        setSmooksConfig(resource);
    }
}