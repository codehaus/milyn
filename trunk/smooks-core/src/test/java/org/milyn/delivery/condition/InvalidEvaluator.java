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
package org.milyn.delivery.condition;

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.expression.ExpressionEvaluator;
import org.milyn.expression.ExpressionEvaluationException;

import java.util.Map;

/**
 * Invalid evaluator.
 * <p/>
 * ExpressionEvaluator is an abstract interface, subtyped by different evaluator
 * types.  At time of writing we only supported evaluator type was
 * ExecutionContextExpressionEvaluator.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class InvalidEvaluator implements ExpressionEvaluator {
    public void setExpression(String conditionExpression) throws SmooksConfigurationException {
    }

    public boolean eval(Map beans) throws ExpressionEvaluationException {
        return false;
    }

    public Object getValue(Map beans) throws ExpressionEvaluationException {
        return null;
    }
}
