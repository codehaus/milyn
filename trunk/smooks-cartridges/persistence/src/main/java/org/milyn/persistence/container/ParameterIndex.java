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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ParameterIndex {

	private int index = 0;

	private final Map<String, Parameter> indexMap = new HashMap<String, Parameter>();


	public synchronized Parameter register(String name) {

		Parameter key = indexMap.get(name);
		if(key == null) {
			int id = index++;

			key = new Parameter(this, id, name);

			indexMap.put(name, key);
		}
		return key;
	}

	public Parameter getParameter(String name) {
		return indexMap.get(name);
	}

	public boolean containsBeanId(String beanId) {
		return indexMap.containsKey(beanId);
	}

	public Map<String, Parameter> getKeyMap() {
		return Collections.unmodifiableMap(indexMap) ;
	}

	public int size() {
		return index;
	}
}
