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

package org.milyn.templating.stringtemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.milyn.Smooks;
import org.milyn.SmooksUtil;
import org.milyn.container.ExecutionContext;
import org.milyn.profile.DefaultProfileSet;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author tfennelly
 */
public class StringTemplateContentDeliveryUnitCreatorTest extends TestCase {

    public void testStringTemplateTrans_01() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs.cdrl"));

        test_st(smooks, "<a><b><c x='xvalueonc1' /><c x='xvalueonc2' /></b></a>", "<a><b><mybean>xvalueonc1</mybean><mybean>xvalueonc2</mybean></b></a>");
        // Test transformation via the <context-object /> by transforming the root element using StringTemplate.
        test_st(smooks, "<c x='xvalueonc1' />", "<mybean>xvalueonc1</mybean>");
    }

    private void test_st(Smooks smooks, String input, String expected) {
        InputStream stream = new ByteArrayInputStream(input.getBytes());
        ExecutionContext context = smooks.createExecutionContext();
        String result = SmooksUtil.filterAndSerialize(context, stream, smooks);

        assertEquals(expected, result);
    }

    public void test_st_bind() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-02.cdrl"));
        StringReader input;
        ExecutionContext context;

        context = smooks.createExecutionContext();
        input = new StringReader("<a><b><c x='xvalueonc2' /></b></a>");
        smooks.filter(new StreamSource(input), null, context);
        assertEquals("<mybean>xvalueonc2</mybean>", context.getAttribute("mybeanTemplate"));

        context = smooks.createExecutionContext();
        input = new StringReader("<c x='xvalueonc2' />");
        smooks.filter(new StreamSource(input), null, context);
        assertEquals("<mybean>xvalueonc2</mybean>", context.getAttribute("mybeanTemplate"));
    }
}
