package org.milyn.expression;

import org.mvel.integration.VariableResolverFactory;

public class MVELVariables {

	private VariableResolverFactory variableResolverFactory;

	public MVELVariables(
			VariableResolverFactory variableResolverFactory) {

		this.variableResolverFactory = variableResolverFactory;
	}

	public boolean isResolveable(String var) {

//		VariableResolverFactory currentFactory = variableResolverFactory;
//		do {
//			if(currentFactory.isResolveable(var)) {
//				return true;
//			} else {
//				currentFactory = currentFactory.getNextFactory();
//			}
//		} while (currentFactory != null);

		return variableResolverFactory.isResolveable(var);
	}

	public boolean isUnresolveable(String var) {
		return !isResolveable(var);
	}

}
