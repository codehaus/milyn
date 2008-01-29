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

/**
 * Abstract delivery condition interface.
 *  
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface ConditionEvaluator {

    /**
     * Set the condition expression for the evaluator implementation.
     * 
     * @param conditionExpression The condition expression to be evaluated by the evaluator implementation.
     * @throws SmooksConfigurationException Invalid condition expression configuration.
     */
    public void setExpression(String conditionExpression) throws SmooksConfigurationException;

    /**
     * Factory method for creating ConditionEvaluator instances.
     */
    public static class Factory {

        public static ConditionEvaluator createInstance(String className, String conditionExpression) {
            try {
                ConditionEvaluator evaluator = (ConditionEvaluator) Class.forName(className).newInstance();

                if(!(evaluator instanceof ExecutionContextConditionEvaluator)) {
                    throw new SmooksConfigurationException("Unsupported ConditionEvaluator type '" + className + "'.  Currently only support '" + ExecutionContextConditionEvaluator.class.getName() + "' implementations.");
                }
                evaluator.setExpression(conditionExpression);

                return evaluator;
            } catch (ClassNotFoundException e) {
                throw new SmooksConfigurationException("Failed to load ConditionEvaluator Class '" + className + "'.", e);
            } catch (ClassCastException e) {
                throw new SmooksConfigurationException("Class '" + className + "' is not a valid ConditionEvaluator.  It doesn't implement " + ConditionEvaluator.class.getName());
            } catch (IllegalAccessException e) {
                throw new SmooksConfigurationException("Failed to load ConditionEvaluator Class '" + className + "'.", e);
            } catch (InstantiationException e) {
                throw new SmooksConfigurationException("Failed to load ConditionEvaluator Class '" + className + "'.", e);
            }
        }
    }    
}
