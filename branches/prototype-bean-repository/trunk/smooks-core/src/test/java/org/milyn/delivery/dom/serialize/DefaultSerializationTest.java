/*
	Milyn - Copyright (C) 2006

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
package org.milyn.delivery.dom.serialize;

import junit.framework.TestCase;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.milyn.Smooks;
import org.milyn.payload.StringResult;
import org.milyn.payload.StringSource;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DefaultSerializationTest extends TestCase {


    public void test_default_writing_off_no_serializers() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("DefaultWritingOff_No_Serializers_Test.xml"));

        StringSource stringSource = new StringSource("<a>aa<b>bbb<c />bbb</b>aaa</a>");
        StringResult stringResult = new StringResult();

        smooks.filter(stringSource, stringResult, smooks.createExecutionContext());

        // The "default.serialization.on" global param is set to "false" in the config, so
        // nothing should get writen to the result because there are no configured
        // serialization Visitors.
        assertEquals("", stringResult.getResult());

        assertTrue(SimpleDOMVisitor.visited);
    }

    public void test_default_writing_off_one_serializer() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("DefaultWritingOff_One_Serializer_Test.xml"));

        StringSource stringSource = new StringSource("<a>aa<b>bbb<c />bbb</b>aaa</a>");
        StringResult stringResult = new StringResult();

        smooks.filter(stringSource, stringResult, smooks.createExecutionContext());

        // The "default.serialization.on" global param is set to "false" in the config.
        // There's just a single result writing visitor configured on the "b" element...
        assertEquals("<b>bbbbbb</b>", stringResult.getResult());

        assertTrue(SimpleDOMVisitor.visited);
    }
}
