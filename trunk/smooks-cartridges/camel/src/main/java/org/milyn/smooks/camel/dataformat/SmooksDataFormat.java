/*
 * Milyn - Copyright (C) 2006 - 2010
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.milyn.smooks.camel.dataformat;

import static org.milyn.smooks.camel.processor.SmooksProcessor.SMOOKS_EXECUTION_CONTEXT;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.spi.DataFormat;
import org.milyn.container.ExecutionContext;
import org.milyn.smooks.camel.component.SmooksComponent;
import org.milyn.smooks.camel.processor.SmooksProcessor;

/**
 * SmooksDataFormat is a Camel data format which is a pluggable transformer
 * capable of transforming from one dataformat to another.
 * <p/>
 * 
 * A smooks configuration for a SmooksDataFormat should not utilize Smooks
 * features such as routing that might allocated system resources. The reason
 * for this is that there is no functionality in the SmooksDataFormat which will
 * close those resources. If you need to use these Smooks features please take a
 * look at the {@link SmooksComponent} or {@link SmooksProcessor} as they hook
 * into Camels lifecycle manegment and will close resources correctly.
 * <p/>
 * 
 * @author Christian Mueller
 * @author Daniel Bevenius
 * 
 */
public class SmooksDataFormat implements DataFormat, CamelContextAware
{
    public static final String SMOOKS_DATA_FORMAT_RESULT_KEY = "SmooksDataFormatKeys";
    private String smooksConfig;
    private String resultBeanId;
    private SmooksProcessor processor;
    private CamelContext camelContext;
    private AtomicBoolean started = new AtomicBoolean();
    
    public SmooksDataFormat(String smooksConfig) throws Exception
    {
        this.smooksConfig = smooksConfig;
    }
    
    public SmooksDataFormat(String smooksConfig, String resultBeanId) throws Exception
    {
        this(smooksConfig);
        this.resultBeanId = resultBeanId;
    }

    public SmooksDataFormat(String smooksConfig, final CamelContext camelContext) throws Exception
    {
        this(smooksConfig);
        this.camelContext = camelContext;
    }
    
    public SmooksDataFormat(String smooksConfig, String resultBeanId, final CamelContext camelContext) throws Exception
    {
        this(smooksConfig, resultBeanId);
        this.camelContext = camelContext;
    }

    private void start() throws Exception
    {
        if (started.get() == false)
        {
	        processor = new SmooksProcessor(smooksConfig, camelContext);
	        processor.start();
	        started.set(true);
        }
    }

    public void marshal(Exchange exchange, Object graph, final OutputStream stream) throws Exception
    {
        start();
        
        processor.process(exchange);
        final Message out = exchange.getOut();
        final ExecutionContext smooksExecContext = out.getHeader(SMOOKS_EXECUTION_CONTEXT, ExecutionContext.class);
        
        stream.write(out.getBody(String.class).getBytes(smooksExecContext.getContentEncoding()));
    }

    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception
    {
        start();
        processor.process(exchange);
        exchange.setProperty(SMOOKS_DATA_FORMAT_RESULT_KEY, resultBeanId);
        return exchange.getOut().getBody();
    }

    public String getResultBeanId()
    {
        return resultBeanId;
    }

    public String getSmooksConfig()
    {
        return processor.getSmooksConfig();
    }

    public void setCamelContext(CamelContext camelContext)
    {
        this.camelContext = camelContext;
    }

    public CamelContext getCamelContext()
    {
        return camelContext;
    }

}