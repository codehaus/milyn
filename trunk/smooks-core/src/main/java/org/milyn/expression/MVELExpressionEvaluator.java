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
package org.milyn.expression;

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ExecutionContext;
import org.mvel.MVEL;

import java.io.Serializable;

/**
 * <a href="http://mvel.codehaus.org/">MVEL</a> expression evaluator.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MVELExpressionEvaluator implements ExpressionEvaluator {
    private String expression;
    private Serializable compiled;

    public void setExpression(String expression) throws SmooksConfigurationException {
        this.expression = expression.trim();
        compiled = MVEL.compileExpression(this.expression);
    }

    public String getExpression() {
        return expression;
    }

    public boolean eval(Object contextObject) throws ExpressionEvaluationException {
        return (Boolean) getValue(contextObject);
    }

    public Object getValue(Object contextObject) throws ExpressionEvaluationException {
        try {
            return MVEL.executeExpression(compiled, contextObject);
        } catch(Exception e) {
            throw new ExpressionEvaluationException("Error evaluating MVEL expression '" + expression + "' against object type '" + contextObject.getClass().getName() + "'. " +
                    "Common issues include:" +
                    "\n\t\t1. Referencing variables not bound to the context. In this case add an assertion in the expression." +
                    "\n\t\t2. Invalid expression reference to a List/Array based variable token.  Example List/Array referencing expression token: 'order.orderItems[0].productId'.", e);
        }
    }
}
