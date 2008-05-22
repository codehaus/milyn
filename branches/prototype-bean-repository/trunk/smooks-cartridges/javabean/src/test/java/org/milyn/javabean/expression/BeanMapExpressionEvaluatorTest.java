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
package org.milyn.javabean.expression;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.expression.ExpressionEvaluationException;
import org.milyn.javabean.expression.BeanMapExpressionEvaluator;
import org.milyn.container.ExecutionContext;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanMapExpressionEvaluatorTest extends TestCase {

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
        //assertFalse(DOMVisitor.visited);
    }

    public void testInvalidExpression() {
        // Just eval on an unbound variable...
        try {
            new BeanMapExpressionEvaluator("x.y").eval(new HashMap());
            fail("Expected ExpressionEvaluationException");
        } catch(ExpressionEvaluationException e) {
            assertEquals("Error evaluating MVEL expression 'x.y' against object type 'java.util.HashMap'. Common issues include:\n" +
                    "\t\t1. Referencing variables not bound to the context. In this case add an assertion in the expression.\n" +
                    "\t\t2. Invalid expression reference to a List/Array based variable token.  Example List/Array referencing expression token: 'order.orderItems[0].productId'.",
                    e.getMessage());
        }
    }
}
