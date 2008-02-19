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
package org.milyn.templating.freemarker;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.SmooksUtil;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.java.JavaSource;
import org.milyn.event.report.FlatReportGenerator;
import org.milyn.templating.MyBean;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tfennelly
 */
public class FreeMarkerContentHandlerFactoryTest extends TestCase {

    public void testFreeMarkerTrans_01() throws SAXException, IOException {
        testFreeMarkerTrans_01("test-configs-01.cdrl");
        testFreeMarkerTrans_01("test-configs-01-SAX.cdrl");
    }

    public void testFreeMarkerTrans_01(String config) throws SAXException, IOException {
        Smooks smooks = new Smooks("/org/milyn/templating/freemarker/" + config);

        test_ftl(smooks, "<a><b><c x='xvalueonc1' /><c x='xvalueonc2' /></b></a>", "<a><b><mybean>xvalueonc1</mybean><mybean>xvalueonc2</mybean></b></a>");
        // Test transformation via the <context-object /> by transforming the root element using StringTemplate.
        test_ftl(smooks, "<c x='xvalueonc1' />", "<mybean>xvalueonc1</mybean>");
    }

    public void testFreeMarkerTrans_02() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-02.cdrl"));

        test_ftl(smooks, "<a><b><c x='xvalueonc1' /><c x='xvalueonc2' /></b></a>", "<a><b><mybean>xvalueonc1</mybean><mybean>xvalueonc2</mybean></b></a>");
        // Test transformation via the <context-object /> by transforming the root element using StringTemplate.
        test_ftl(smooks, "<c x='xvalueonc1' />", "<mybean>xvalueonc1</mybean>");
    }

    public void testFreeMarkerTrans_03() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-03.cdrl"));

        // Initialise the input bean map...
        Map<String, Object> myBeans = new HashMap<String, Object>();
        MyBean myBean = new MyBean();
        myBean.setX("xxxxxxx");
        myBeans.put("myBeanData", myBean);

        // Create the "null" JavaSource and set the bean Map on it...
        JavaSource source = new JavaSource();
        source.setBeans(myBeans);

        // Create the output writer for the transform and run it...
        StringWriter myTransformResult = new StringWriter();
        smooks.filter(source, new StreamResult(myTransformResult), smooks.createExecutionContext());

        // Check it...
        assertEquals("<mybean>xxxxxxx</mybean>", myTransformResult.toString());
    }

    public void testFreeMarkerTrans_bind() throws SAXException, IOException {
        testFreeMarkerTrans_bind("test-configs-04.cdrl");
        testFreeMarkerTrans_bind("test-configs-04-SAX.cdrl");
    }
    public void testFreeMarkerTrans_bind(String config) throws SAXException, IOException {
        Smooks smooks = new Smooks("/org/milyn/templating/freemarker/" + config);
        StringReader input;
        ExecutionContext context;

        context = smooks.createExecutionContext();
        input = new StringReader("<a><b><c x='xvalueonc2' /></b></a>");
        smooks.filter(new StreamSource(input), null, context);
        assertEquals("<mybean>xvalueonc2</mybean>", context.getAttribute("mybeanTemplate"));

        context = smooks.createExecutionContext();
        input = new StringReader("<c x='xvalueonc1' />");
        smooks.filter(new StreamSource(input), null, context);
        assertEquals("<mybean>xvalueonc1</mybean>", context.getAttribute("mybeanTemplate"));
    }

    public void testX() throws IOException, SAXException {
        StringWriter output = new StringWriter();
        FlatReportGenerator.generateReport("/org/milyn/templating/freemarker/test-configs-04.cdrl", new StreamSource(new StringReader("<a><b><c x='xvalueonc2' /></b></a>")), output, false, false);
        System.out.println(output);
    }

    private void test_ftl(Smooks smooks, String input, String expected) {
        InputStream stream = new ByteArrayInputStream(input.getBytes());
        ExecutionContext context = smooks.createExecutionContext();
        String result = SmooksUtil.filterAndSerialize(context, stream, smooks);

        assertEquals(expected, result);
    }
}
