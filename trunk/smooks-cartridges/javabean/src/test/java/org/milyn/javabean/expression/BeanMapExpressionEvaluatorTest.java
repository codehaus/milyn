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

import junit.framework.*;
import org.milyn.*;
import org.milyn.container.*;
import org.milyn.expression.*;
import org.xml.sax.*;

import javax.xml.transform.stream.*;
import java.io.*;
import java.util.*;

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
                    "\t\t1. Referencing a variable that is not bound into the context. In this case use VARS.isdef(\"someVar\") to check if the variable is bound in the context.\n" +
                    "\t\t2. Invalid expression reference to a List/Array based variable token.  Example List/Array referencing expression token: 'order.orderItems[0].productId'.",
                    e.getMessage());
        }
    }
}
