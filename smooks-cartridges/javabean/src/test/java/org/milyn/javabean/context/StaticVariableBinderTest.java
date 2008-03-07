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
package org.milyn.javabean.context;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.BeanAccessor;
import org.milyn.javabean.expression.MVELExpressionEvaluator;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class StaticVariableBinderTest extends TestCase {

    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("staticvar-config.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();

        smooks.filter(new StreamSource(new StringReader("<x/>")), null, execContext);
        //assertEquals("{statvar={variable3=Hi Var3, variable1=Hi Var1, variable2=Hi Var2}}", BeanAccessor.getBeanMap(execContext).toString());
        assertEquals("Hi Var1", new MVELExpressionEvaluator("statvar.variable1").getValue(BeanAccessor.getBeanMap(execContext)));
        assertEquals("Hi Var2", new MVELExpressionEvaluator("statvar.variable2").getValue(BeanAccessor.getBeanMap(execContext)));
        assertEquals("Hi Var3", new MVELExpressionEvaluator("statvar.variable3").getValue(BeanAccessor.getBeanMap(execContext)));
    }
}
