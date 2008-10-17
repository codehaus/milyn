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
import java.util.Map;

/**
 * @author maurice_zeijen
 *
 */
public class AnnotatedDaoRuntimeInfoRepository {

	private final HashMap<String, AnnotatedDaoRuntimeInfo> repository = new HashMap<String, AnnotatedDaoRuntimeInfo>();

	public AnnotatedDaoRuntimeInfo get(final Class<?> daoClass) {

		final String daoClassName = daoClass.getName();

		AnnotatedDaoRuntimeInfo runtimeInfo = repository.get(daoClassName);

		if(runtimeInfo == null) {

			runtimeInfo = new AnnotatedDaoRuntimeInfo(daoClass);

			repository.put(daoClassName, runtimeInfo);

		}

		return runtimeInfo;
	}

	public boolean contains(final Class<?> daoClass) {

		final String daoClassName = daoClass.getName();

		return repository.containsKey(daoClassName);
	}

	public boolean contains(final AnnotatedDaoRuntimeInfo runtimeInfo) {
		return repository.containsValue(runtimeInfo);
	}


	/**
	 * Returns a shallow clone of the repository
	 *
	 * @return a shallow clone of the repository
	 */
	@SuppressWarnings("unchecked")
	public Map<String, AnnotatedDaoRuntimeInfo> getRepository() {

		return (Map<String, AnnotatedDaoRuntimeInfo>) repository.clone();

	}

}
