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

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.milyn.smooks.camel.processor.SmooksProcessor;
import org.apache.camel.spi.DataFormat;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.milyn.payload.JavaSource;
import org.milyn.payload.StringResult;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.SAXException;

public class SmooksDataFormat2 implements DataFormat {
    
    private SmooksProcessor processor;
    private String smooksResultKey = "result";
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    
    public SmooksDataFormat2() {
        processor = new SmooksProcessor();
    }
    
    public SmooksDataFormat2(String smooksConfig) throws IOException, SAXException {
        processor = new SmooksProcessor(smooksConfig);
        //setSmooksConfig(smooksConfig);
    }

    public void marshal(Exchange exchange, Object graph, final OutputStream stream) throws Exception {
        synchronized (processor) {
            if (processor.getSmooksMapper() == null) {
                processor.setSmooksMapper(new SmooksMapper() {
                    public Source createSource(Exchange exchange) {
                        JavaSource javaSource = new JavaSource(exchange.getIn().getBody());
                        javaSource.setEventStreamRequired(false);
                        return javaSource;
                    }
                    
                    public Result createResult() {
                        return new StringResult();
                    }
                    
                    public void mapResult(Result result, Exchange exchange) {
                        exchange.getOut().setBody(((StringResult) result).getResult());
                    }
                });
            }
        }
        
        processor.process(exchange);
        
        ExecutionContext executionContext = exchange.getOut().getHeader(SmooksProcessor.SMOOKS_EXECUTION_CONTEXT, ExecutionContext.class);
        stream.write(exchange.getOut().getBody(String.class).getBytes(executionContext.getContentEncoding()));
    }

    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        synchronized (processor) {
            if (processor.getSmooksMapper() == null) {
                processor.setSmooksMapper(new SmooksMapper() {
                    public Source createSource(Exchange exchange) {
                        return new StreamSource(exchange.getIn().getBody(InputStream.class));
                    }
                    
                    public Result createResult() {
                        return new JavaResult();
                    }
                    
                    public void mapResult(Result result, Exchange exchange) {
                        exchange.getOut().setBody(((JavaResult) result).getBean(smooksResultKey));
                    }
                });
            }
        }
        
        processor.process(exchange);
        
        return exchange.getOut().getBody();
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
}