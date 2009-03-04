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
import org.milyn.cdr.ParameterAccessor;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.Filter;
import org.milyn.javabean.repository.BeanRepositoryManager;
import org.milyn.payload.JavaSource;
import org.milyn.payload.StringResult;
import org.milyn.payload.StringSource;
import org.milyn.templating.MockOutStreamResource;
import org.milyn.templating.MyBean;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class FreeMarkerContentHandlerFactoryExtendedConfigTest extends TestCase {

    public void testFreeMarkerTrans_01() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-ext-01.cdrl"));

        test_ftl(smooks, "<a><b><c x='xvalueonc1' /><c x='xvalueonc2' /></b></a>", "<a><b><mybean>xvalueonc1</mybean><mybean>xvalueonc2</mybean></b></a>");
        // Test transformation via the <context-object /> by transforming the root element using StringTemplate.
        test_ftl(smooks, "<c x='xvalueonc1' />", "<mybean>xvalueonc1</mybean>");
    }

    public void test_nodeModel_1() throws IOException, SAXException {
        test_nodeModel_1(Filter.StreamFilterType.DOM);
        test_nodeModel_1(Filter.StreamFilterType.SAX);
    }
    public void test_nodeModel_1(Filter.StreamFilterType filterType) throws IOException, SAXException {
        Smooks smooks = new Smooks("/org/milyn/templating/freemarker/test-configs-ext-05.cdrl");

        Filter.setFilterType(smooks, filterType);
        test_ftl(smooks, "<a><b><c>cvalue1</c><c>cvalue2</c><c>cvalue3</c></b></a>", "'cvalue1''cvalue2''cvalue3'");
    }

    public void test_nodeModel_2() throws IOException, SAXException {
        test_nodeModel_2(Filter.StreamFilterType.DOM);
        test_nodeModel_2(Filter.StreamFilterType.SAX);
    }
    public void test_nodeModel_2(Filter.StreamFilterType filterType) throws IOException, SAXException {
        Smooks smooks = new Smooks("/org/milyn/templating/freemarker/test-configs-ext-06.cdrl");

        Filter.setFilterType(smooks, filterType);
        test_ftl(smooks, "<a><b><c>cvalue1</c><c>cvalue2</c><c>cvalue3</c></b></a>", "<a><b><x>'cvalue1'</x><x>'cvalue2'</x><x>'cvalue3'</x></b></a>");
    }

    public void test_nodeModel_3() throws IOException, SAXException {
        test_nodeModel_3(Filter.StreamFilterType.DOM);
        test_nodeModel_3(Filter.StreamFilterType.SAX);
    }
    public void test_nodeModel_3(Filter.StreamFilterType filterType) throws IOException, SAXException {
        Smooks smooks = new Smooks("/org/milyn/templating/freemarker/test-configs-ext-07.cdrl");

        Filter.setFilterType(smooks, filterType);
        test_ftl(smooks, "<a><b javabind='javaval'><c>cvalue1</c><c>cvalue2</c><c>cvalue3</c></b></a>", "'cvalue1''cvalue2''cvalue3' javaVal=javaval");
    }

    public void testFreeMarkerTrans_02() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-ext-02.cdrl"));

        test_ftl(smooks, "<a><b><c x='xvalueonc1' /><c x='xvalueonc2' /></b></a>", "<a><b><mybean>xvalueonc1</mybean><mybean>xvalueonc2</mybean></b></a>");
        // Test transformation via the <context-object /> by transforming the root element using StringTemplate.
        test_ftl(smooks, "<c x='xvalueonc1' />", "<mybean>xvalueonc1</mybean>");
    }

    public void testFreeMarkerTrans_03() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-ext-03.cdrl"));

        // Initialise the input bean map...
        Map<String, Object> myBeans = new HashMap<String, Object>();
        MyBean myBean = new MyBean();
        myBean.setX("xxxxxxx");
        myBeans.put("myBeanData", myBean);

        JavaSource source = new JavaSource(myBeans);
        source.setEventStreamRequired(false);

        // Create the output writer for the transform and run it...
        StringWriter myTransformResult = new StringWriter();
        smooks.filter(source, new StreamResult(myTransformResult), smooks.createExecutionContext());

        // Check it...
        assertEquals("<mybean>xxxxxxx</mybean>", myTransformResult.toString());
    }

    public void testFreeMarkerTrans_bind() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-ext-04.cdrl"));
        StringReader input;
        ExecutionContext context;

        context = smooks.createExecutionContext();
        input = new StringReader("<a><b><c x='xvalueonc2' /></b></a>");
        smooks.filter(new StreamSource(input), null, context);

        assertEquals("<mybean>xvalueonc2</mybean>", BeanRepositoryManager.getBeanRepository(context).getBean("mybeanTemplate"));

        context = smooks.createExecutionContext();
        input = new StringReader("<c x='xvalueonc1' />");
        smooks.filter(new StreamSource(input), null, context);
        assertEquals("<mybean>xvalueonc1</mybean>", BeanRepositoryManager.getBeanRepository(context).getBean("mybeanTemplate"));
    }

    public void test_template_include() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-ext-include.cdrl"));

        test_ftl(smooks, "<a><c/></a>",
                         "<a><maintemplate><included>blah</included></maintemplate></a>");
    }

    public void testInsertBefore() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-ext-insert-before.cdrl"));

        test_ftl(smooks, "<a><b x='xvalueonc1' /><c/><d/></a>",
                         "<a><b x=\"xvalueonc1\"></b><mybean>xvalueonc1</mybean><c></c><d></d></a>");

        smooks = new Smooks(getClass().getResourceAsStream("test-configs-insert-before.cdrl"));
        Filter.setFilterType(smooks, Filter.StreamFilterType.SAX);
        test_ftl(smooks, "<a><b x='xvalueonc1' /><c/><d/></a>",
                         "<a><b x=\"xvalueonc1\" /><mybean>xvalueonc1</mybean><c /><d /></a>");

        smooks = new Smooks(getClass().getResourceAsStream("test-configs-insert-before.cdrl"));
        Filter.setFilterType(smooks, Filter.StreamFilterType.SAX);
        ParameterAccessor.setParameter(Filter.DEFAULT_SERIALIZATION_ON, "false", smooks);
        test_ftl(smooks, "<a><b x='xvalueonc1' /><c>11<f/>11</c><d/></a>",
                         "<mybean>xvalueonc1</mybean>");
    }

    public void testInsertAfter() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-ext-insert-after.cdrl"));

        test_ftl(smooks, "<a><b x='xvalueonc1' /><c/><d/></a>",
                         "<a><b x=\"xvalueonc1\"></b><c></c><mybean>xvalueonc1</mybean><d></d></a>");

        smooks = new Smooks(getClass().getResourceAsStream("test-configs-insert-after.cdrl"));
        Filter.setFilterType(smooks, Filter.StreamFilterType.SAX);
        test_ftl(smooks, "<a><b x='xvalueonc1' /><c/><d/></a>",
                         "<a><b x=\"xvalueonc1\" /><c /><mybean>xvalueonc1</mybean><d /></a>");

        smooks = new Smooks(getClass().getResourceAsStream("test-configs-insert-after.cdrl"));
        Filter.setFilterType(smooks, Filter.StreamFilterType.SAX);
        ParameterAccessor.setParameter(Filter.DEFAULT_SERIALIZATION_ON, "false", smooks);
        test_ftl(smooks, "<a><b x='xvalueonc1' /><c>11<f/>11</c><d/></a>",
                         "<mybean>xvalueonc1</mybean>");
    }

    public void testAddTo() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-ext-addto.cdrl"));

        test_ftl(smooks, "<a><b x='xvalueonc1' /><c/><d/></a>",
                         "<a><b x=\"xvalueonc1\"></b><c><mybean>xvalueonc1</mybean></c><d></d></a>");

        smooks = new Smooks(getClass().getResourceAsStream("test-configs-addto.cdrl"));
        Filter.setFilterType(smooks, Filter.StreamFilterType.SAX);
        test_ftl(smooks, "<a><b x='xvalueonc1' /><c/><d/></a>",
                         "<a><b x=\"xvalueonc1\" /><c><mybean>xvalueonc1</mybean></c><d /></a>");

        smooks = new Smooks(getClass().getResourceAsStream("test-configs-addto.cdrl"));
        Filter.setFilterType(smooks, Filter.StreamFilterType.SAX);
        test_ftl(smooks, "<a><b x='xvalueonc1' /><c>1111</c><d/></a>",
                         "<a><b x=\"xvalueonc1\" /><c>1111<mybean>xvalueonc1</mybean></c><d /></a>");

        smooks = new Smooks(getClass().getResourceAsStream("test-configs-addto.cdrl"));
        Filter.setFilterType(smooks, Filter.StreamFilterType.SAX);
        test_ftl(smooks, "<a><b x='xvalueonc1' /><c><f/></c><d/></a>",
                         "<a><b x=\"xvalueonc1\" /><c><f /><mybean>xvalueonc1</mybean></c><d /></a>");

        smooks = new Smooks(getClass().getResourceAsStream("test-configs-addto.cdrl"));
        Filter.setFilterType(smooks, Filter.StreamFilterType.SAX);
        test_ftl(smooks, "<a><b x='xvalueonc1' /><c>11<f/>11</c><d/></a>",
                         "<a><b x=\"xvalueonc1\" /><c>11<f />11<mybean>xvalueonc1</mybean></c><d /></a>");

        smooks = new Smooks(getClass().getResourceAsStream("test-configs-addto.cdrl"));
        Filter.setFilterType(smooks, Filter.StreamFilterType.SAX);
        ParameterAccessor.setParameter(Filter.DEFAULT_SERIALIZATION_ON, "false", smooks);
        test_ftl(smooks, "<a><b x='xvalueonc1' /><c>11<f/>11</c><d/></a>",
                         "<mybean>xvalueonc1</mybean>");
    }

    public void test_outputTo_Stream() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-ext-outputToOutStream.cdrl"));
        ExecutionContext context = smooks.createExecutionContext();

        MockOutStreamResource.outputStream = new ByteArrayOutputStream();
        smooks.filter(new StringSource("<a/>"), null, context);

        assertEquals("data to outstream", MockOutStreamResource.outputStream.toString());
    }

    private void test_ftl(Smooks smooks, String input, String expected) {
        ExecutionContext context = smooks.createExecutionContext();
        test_ftl(smooks, context, input, expected);
    }

    private void test_ftl(Smooks smooks, ExecutionContext context, String input, String expected) {
        StringResult result = new StringResult();

        smooks.filter(new StringSource(input), result, context);

        assertEquals(expected, result.getResult());
    }
}