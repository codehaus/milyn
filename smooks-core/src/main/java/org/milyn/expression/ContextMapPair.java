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

import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ContextMapPair implements ContextMapList {

	private Map<String, Object> m1;

	private Map<String, Object> m2;

	/**
	 *
	 * @param m1
	 * @param m2
	 */
	public ContextMapPair(Map<String, Object> m1, Map<String, Object> m2) {
		this.m1 = m1;
		this.m2 = m2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Map<String, Object>> iterator() {

		return new Iterator<Map<String,Object>>() {

			int i = 0;

			public boolean hasNext() {
				return i < 2;
			}

			public Map<String, Object> next() {
				i = i + 1;

				if(i == 1) {
					return m1;
				} else {
					return m2;
				}
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}



}
