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

/**
 * {@link org.milyn.container.ExecutionContext} based condition evaluator.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface ExecutionContextConditionEvaluator extends ConditionEvaluator {

    /**
     * Evaluate a condition based on the supplied {@link org.milyn.container.ExecutionContext}.
     * @param context The context.
     * @return True if the condition evaluates successfully, otherwise false.
     */
    public boolean eval(ExecutionContext context);
}
