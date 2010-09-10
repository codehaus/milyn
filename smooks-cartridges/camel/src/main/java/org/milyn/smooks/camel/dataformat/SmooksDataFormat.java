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

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.camel.Exchange;
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
public class SmooksDataFormat implements DataFormat
{
    public static final String SMOOKS_DATA_FORMAT_RESULT_KEY = "SmooksDataFormatKeys";
    private String resultBeanId;
    private SmooksProcessor processor;

    public SmooksDataFormat(String smooksConfig) throws Exception
    {
        this(smooksConfig, null);
    }

    public SmooksDataFormat(String smooksConfig, String resultBeanId) throws Exception
    {
        this.resultBeanId = resultBeanId;
        createAndStartSmooksProcessor(smooksConfig);
    }

    private void createAndStartSmooksProcessor(String smooksConfig) throws Exception
    {
        processor = new SmooksProcessor(smooksConfig);
        processor.start();
    }

    public void marshal(Exchange exchange, Object graph, final OutputStream stream) throws Exception
    {
        processor.process(exchange);

        ExecutionContext executionContext = exchange.getOut().getHeader(SmooksProcessor.SMOOKS_EXECUTION_CONTEXT,
                ExecutionContext.class);
        stream.write(exchange.getOut().getBody(String.class).getBytes(executionContext.getContentEncoding()));
    }

    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception
    {
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

}