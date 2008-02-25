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

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ExecutionContext;
import org.milyn.expression.ExecutionContextExpressionEvaluator;
import org.milyn.expression.ExpressionEvaluationException;
import org.milyn.javabean.BeanAccessor;
import org.mvel.MVEL;

import java.io.Serializable;
import java.util.Map;

/**
 * <a href="http://mvel.codehaus.org/">MVEL</a> expression based resource targeting
 * condition evaluator.
 * <p/>
 * Evaluates <a href="http://mvel.codehaus.org/">MVEL</a> expressions on java objects
 * bound to the supplied {@link ExecutionContext} (via the {@link BeanAccessor}).
 * <p/>
 * This condition evaluator allows you to selectively target resources based on the
 * contents of the java objects bound to the supplied {@link ExecutionContext},
 * allowing you to perform e.g. content based templating and/or routing.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MVELExpressionEvaluator implements ExecutionContextExpressionEvaluator {

    private String expression;
    private Serializable compiled;

    public MVELExpressionEvaluator() {
    }

    public MVELExpressionEvaluator(String expression) throws SmooksConfigurationException {
        setExpression(expression);
    }

    public void setExpression(String expression) throws SmooksConfigurationException {
        this.expression = expression.trim();
        compiled = MVEL.compileExpression(this.expression);
    }

    public String getExpression() {
        return expression;
    }

    public boolean eval(ExecutionContext context) throws ExpressionEvaluationException {
        Map beans = BeanAccessor.getBeanMap(context);
        return eval(beans);
    }

    public Object getValue(ExecutionContext context) throws ExpressionEvaluationException {
        Map beans = BeanAccessor.getBeanMap(context);
        return getValue(beans);
    }

    public boolean eval(Map beans) throws ExpressionEvaluationException {
        return (Boolean) getValue(beans);
    }

    public Object getValue(Map beans) throws ExpressionEvaluationException {
        try {
            return MVEL.executeExpression(compiled, beans);
        } catch(Exception e) {
            throw new ExpressionEvaluationException("Error evaluating MVEL expression '" + expression + "' against bean Map contents: " + beans + ". " +
                    "Common issues include:" +
                    "\n\t\t1. Referencing variables not bound to the input Map (bean context etc). In this case add an assertion in the expression." +
                    "\n\t\t2. Invalid expression reference to a List/Array based variable token.  Example List/Array referencing expression token: 'order.orderItems[0].productId'.", e);
        }
    }
}
