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
package org.milyn.smooks.camel.converters;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.milyn.payload.StringResult;

/**
 * Unit test for {@link ResultConverter}.
 * 
 * @author Daniel Bevenius
 *
 */
public class ResultConverterTest
{
    @Test
    public void stringResultToStreamSource() throws Exception
    {
        StringResult stringResult = new StringResult();
        StringWriter stringWriter = new StringWriter();
        stringWriter.write("Bajja");
        stringResult.setWriter(stringWriter);
        
        StreamSource streamSource = ResultConverter.toStreamSource(stringResult);
        BufferedReader reader = new BufferedReader(streamSource.getReader());
        String readLine = reader.readLine();
        assertEquals("Bajja", readLine);
    }

}
