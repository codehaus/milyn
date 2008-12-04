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
package org.milyn.persistence.container;

import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ParameterManager {

	private static final String PARAMETER_INDEX_CONTEXT_KEY = ParameterIndex.class.getName() + "#CONTEXT_KEY";

	private static final String PARAMETER_CONTAINER_CONTEXT_KEY = ParameterContainer.class.getName() + "#CONTEXT_KEY";

	public static String getParameterIndexName(int id) {
		return PARAMETER_INDEX_CONTEXT_KEY + "#" + id;
	}

	public static String getParameterContainerName(int id) {
		return PARAMETER_CONTAINER_CONTEXT_KEY + "#" + id;
	}


	public static ParameterIndex getParameterIndex(int id, ApplicationContext applicationContext) {
		String parameterIndexContextKey = getParameterIndexName(id);

		ParameterIndex parameterIndex = (ParameterIndex) applicationContext.getAttribute(parameterIndexContextKey);

		if(parameterIndex == null) {

			parameterIndex = new ParameterIndex();

			applicationContext.setAttribute(parameterIndexContextKey, parameterIndex);

		}

		return parameterIndex;

	}

	public static ParameterContainer getParameterContainer(int id, ExecutionContext executionContext) {
		String parameterContainerContextKey = getParameterContainerName(id);

		ParameterContainer container = (ParameterContainer) executionContext.getAttribute(parameterContainerContextKey);

		if(container == null) {

			container = new ParameterContainer(getParameterIndex(id, executionContext.getContext()));

			executionContext.setAttribute(parameterContainerContextKey, container);
		}

		return container;
	}


	private ParameterManager() {
	}



}
