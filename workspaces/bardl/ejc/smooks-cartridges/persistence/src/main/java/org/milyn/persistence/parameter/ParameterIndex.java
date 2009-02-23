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
package org.milyn.persistence.parameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public abstract class ParameterIndex<K extends Object, P extends Parameter<?>> {


	private final Map<K, P> indexMap = new HashMap<K, P>();

	public synchronized P register(K value) {

		P parameter = indexMap.get(value);
		if(parameter == null) {
			parameter = createParameter(value);

			indexMap.put(value, parameter);
		}
		return parameter;
	}

	protected abstract P createParameter(K value) ;

	public P getParameter(K keyValue) {
		return indexMap.get(keyValue);
	}

	public boolean containsBeanId(String beanId) {
		return indexMap.containsKey(beanId);
	}

	public Map<K, P> getKeyMap() {
		return Collections.unmodifiableMap(indexMap) ;
	}

	public int size() {
		return indexMap.size();
	}

}
