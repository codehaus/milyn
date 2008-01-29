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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.condition.ExecutionContextConditionEvaluator;
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
public class MVELExpressionEvaluator implements ExecutionContextConditionEvaluator {

    private static Log logger = LogFactory.getLog(MVELExpressionEvaluator.class);
    private String conditionExpression;
    private Serializable compiled;

    public void setExpression(String conditionExpression) throws SmooksConfigurationException {
        this.conditionExpression = conditionExpression.trim();
        compiled = MVEL.compileExpression(this.conditionExpression);
    }

    public boolean eval(ExecutionContext context) {
        Map beans = BeanAccessor.getBeanMap(context);
        try {
            return (Boolean) MVEL.executeExpression(compiled, beans);
        } catch(Exception e) {
            if(logger.isDebugEnabled()) {
                logger.debug("Error evaluating MVEL expression '" + conditionExpression + "' against bean Map contents: " + beans, e);
            }
            return false;
        }
    }
}
