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
package org.milyn.javabean.condition;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MVELExpressionEvaluatorTest extends TestCase {

    public void test() throws IOException, SAXException {
        Smooks smooks;
        ExecutionContext execContext;

        DOMVisitor.visited = false;
        smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));
        execContext = smooks.createExecutionContext();
        smooks.filter(new StreamSource(new StringReader("<a>hello</a>")), null, execContext);
        assertTrue(DOMVisitor.visited);

        DOMVisitor.visited = false;
        smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));
        execContext = smooks.createExecutionContext();
        smooks.filter(new StreamSource(new StringReader("<a>goodbye</a>")), null, execContext);
        assertFalse(DOMVisitor.visited);
    }
}
