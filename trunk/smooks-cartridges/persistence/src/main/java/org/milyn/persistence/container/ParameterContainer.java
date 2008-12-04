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


/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ParameterContainer {

	private Object[] entries;

	public ParameterContainer(ParameterIndex index) {
		entries = new Object[index.size()];
	}

	public void put(Parameter param, Object bean) {

		entries[param.getIndex()] = bean;

	}


	public boolean containsKey(Parameter param) {

		int index = param.getIndex();

		return entries.length > index && entries[index] != null;
	}


	public Object get(Parameter param) {

		int index = param.getIndex();

		if(entries.length <= index) {
			return null;
		}

		return entries[index];
	}

	public Object remove(Parameter param) {

		Object old = get(param);

		if(old != null) {
			entries[param.getIndex()] = null;
		}

		return old;
	}

	public void clear() {
		entries = new Object[entries.length];
	}

}
