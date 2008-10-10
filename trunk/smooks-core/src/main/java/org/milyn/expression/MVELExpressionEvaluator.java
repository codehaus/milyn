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

import org.milyn.cdr.*;
import org.milyn.container.ExecutionContext;
import org.mvel.*;
import org.mvel.integration.impl.*;

import java.io.*;
import java.util.*;

/**
 * <a href="http://mvel.codehaus.org/">MVEL</a> expression evaluator.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MVELExpressionEvaluator implements ExpressionEvaluator {

    private static final String MVEL_VARIABLES_VARIABLE_NAME = "VARS";

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


    @SuppressWarnings("unchecked")
	public Object getValue(final Object contextObject) throws ExpressionEvaluationException {
        try {

        	if (contextObject instanceof ContextMapList){

           		MapVariableResolverFactory prevFactory = null;
        		for(Map<String, Object> context : (ContextMapList) contextObject) {

        			MapVariableResolverFactory factory = new MapVariableResolverFactory(context);

        			if(prevFactory != null) {
        				factory.setNextFactory(prevFactory);
        			}

        			prevFactory = factory;
        		}

        		return getValue(prevFactory);

        	} else if(contextObject instanceof Map) {

        		// We use two variableResolverFactories so that variables created in MVEL Scripts are put in the empty HashMap
        		// of the second VariableResolverFactory and not in the contextObject Map.
        		MapVariableResolverFactory vrFactory = new MapVariableResolverFactory((Map) contextObject);

	        	return getValue(vrFactory);

        	} else {

        		return MVEL.executeExpression(compiled, contextObject, new MapVariableResolverFactory(new HashMap<String, Object>()));

        	}

        } catch(Exception e) {
        	String msg = "Error evaluating MVEL expression '" + expression + "' against object type '" + contextObject.getClass().getName() + "'. " +
            				"Common issues include:" +
            				"\n\t\t1. Referencing a variable that is not bound into the context.";
        	if(contextObject instanceof Map || contextObject instanceof ContextMapList) {
        		msg += " In this case use "+ MVEL_VARIABLES_VARIABLE_NAME +".isdef(\"someVar\") to check if the variable is bound in the context.";
        	}
        	msg += "\n\t\t2. Invalid expression reference to a List/Array based variable token.  Example List/Array referencing expression token: 'order.orderItems[0].productId'.";

            throw new ExpressionEvaluationException(msg, e);
        }
    }

	/**
	 * @param vrFactory
	 * @return
	 */
	protected Object getValue(MapVariableResolverFactory vrFactory) {

		MapVariableResolverFactory variableResolverFactory = new MapVariableResolverFactory(new HashMap<String, Object>());
		variableResolverFactory.setNextFactory(vrFactory);

		// The VARS variable contains the MVELVariables object which gets access to the variableResolverFactory to be able to
		// do lookups in the variables of the resolver factory
		variableResolverFactory.createVariable(MVEL_VARIABLES_VARIABLE_NAME, new MVELVariables(variableResolverFactory));

		return  MVEL.executeExpression(compiled, variableResolverFactory);
	}


}
