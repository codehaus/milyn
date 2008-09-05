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

public class MVELVariables {

	private org.mvel.integration.VariableResolverFactory variableResolverFactory;

	public MVELVariables(
			org.mvel.integration.VariableResolverFactory variableResolverFactory) {

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