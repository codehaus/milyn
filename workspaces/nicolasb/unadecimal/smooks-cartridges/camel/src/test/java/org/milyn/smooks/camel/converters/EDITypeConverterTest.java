/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License (version 2.1) as published by the Free Software
 * Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.smooks.camel.converters;

import junit.framework.TestCase;
import org.apache.camel.TypeConverter;
import org.apache.camel.impl.DefaultCamelContext;
import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.smooks.edi.EDIWritable;
import org.milyn.smooks.edi.unedifact.model.r41.UNB41;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactInterchange41;

import java.io.IOException;
import java.io.Writer;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class EDITypeConverterTest extends TestCase {

    public void testToUNEDIFACTString_interchange() throws Exception {
        DefaultCamelContext camelContext = new DefaultCamelContext();
        TypeConverter typeConverter = camelContext.getTypeConverter();

        UNEdifactInterchange41 unEdifactInterchange41 = new UNEdifactInterchange41();
        unEdifactInterchange41.setInterchangeHeader(new UNB41());

        String serialized = typeConverter.convertTo(String.class, unEdifactInterchange41);
        assertEquals("UNB+++++++++++'", serialized);

    }

    public void testToUNEDIFACTString_message() throws Exception {
        DefaultCamelContext camelContext = new DefaultCamelContext();
        TypeConverter typeConverter = camelContext.getTypeConverter();

        String serialized = typeConverter.convertTo(String.class, new EDIX());
        assertEquals("XXX", serialized);
    }

    public static class EDIX implements EDIWritable {

        public void write(Writer writer, Delimiters delimiters) throws IOException {
            writer.write("XXX");
        }
    }
}
