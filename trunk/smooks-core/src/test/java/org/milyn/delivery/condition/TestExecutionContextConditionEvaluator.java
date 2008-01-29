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

import org.milyn.container.ExecutionContext;
import org.milyn.cdr.SmooksConfigurationException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TestExecutionContextConditionEvaluator implements ExecutionContextConditionEvaluator {

    public String condition;
    public boolean evalResult = true;
    public static ExecutionContext context;

    public void setExpression(String conditionExpression) throws SmooksConfigurationException {
        condition = conditionExpression;
        evalResult = conditionExpression.trim().equals("true");
    }

    public boolean eval(ExecutionContext context) {
        TestExecutionContextConditionEvaluator.context = context;
        return evalResult;
    }
}
