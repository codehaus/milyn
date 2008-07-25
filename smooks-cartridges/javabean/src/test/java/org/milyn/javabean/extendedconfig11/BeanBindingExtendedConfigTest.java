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
package org.milyn.javabean.extendedconfig11;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanBindingExtendedConfigTest extends TestCase {

    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test_value_01.xml"));
        JavaResult result = new JavaResult();
        ExecutionContext execContext = smooks.createExecutionContext();

        execContext.setEventListener(new HtmlReportGenerator("/zap/report.html"));
        smooks.filter(new StreamSource(getClass().getResourceAsStream("order-01.xml")), result, execContext);
        System.out.println(result.getResultMap());
    }
}
