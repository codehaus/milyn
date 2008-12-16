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
package org.milyn.persistence.dao.reflection;

import java.util.HashMap;

/**
 * @author maurice_zeijen
 *
 */
public class AnnotatedDaoRuntimeInfoFactory {

	private final HashMap<String, AnnotatedDaoRuntimeInfo> repository = new HashMap<String, AnnotatedDaoRuntimeInfo>();

	public AnnotatedDaoRuntimeInfo create(final Class<?> daoClass) {

		final String daoClassName = daoClass.getName();

		AnnotatedDaoRuntimeInfo runtimeInfo = repository.get(daoClassName);

		if(runtimeInfo == null) {

			runtimeInfo = new AnnotatedDaoRuntimeInfo(daoClass);

			repository.put(daoClassName, runtimeInfo);

		}

		return runtimeInfo;
	}


}
