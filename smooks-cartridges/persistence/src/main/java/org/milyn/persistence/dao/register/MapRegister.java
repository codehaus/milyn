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
package org.milyn.persistence.dao.register;

import java.util.HashMap;
import java.util.Map;

import org.milyn.persistence.dao.DaoRegister;



/**
 * @author maurice_zeijen
 *
 */
public class MapRegister<T> implements DaoRegister<T> {

	private final HashMap<String, T> map;

	/**
	 *
	 */
	public MapRegister() {
		map = new HashMap<String, T>();
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public MapRegister(Map<String, T> map) {
		if(map instanceof HashMap) {
			this.map = (HashMap<String, T>) ((HashMap<String, T>) map).clone();
		} else {
			this.map = new HashMap<String, T>(map);
		}
	}

	public T put(final String key, final T dao) {

		return map.put(key, dao);

	}

	public boolean containsKey(final String key) {

		return map.containsKey(key);

	}

	public boolean containsDAO(final T dao) {

		return map.containsValue(dao);

	}

	/**
	 * Returns a clone of the underlying map.
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, T> getAll() {

		return (Map<String, T>) map.clone();
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAORegistery#getDAO(java.lang.String)
	 */
	public T getDAO(final String key) {
		return map.get(key);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAORegistery#returnDAO(java.lang.Object)
	 */
	public void returnDAO(final T dao) {
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return map.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {

		if(obj == null) {
			return false;
		}
		if(obj instanceof MapRegister == false) {
			return false;
		}

		@SuppressWarnings("unchecked")
		final MapRegister<T> other = (MapRegister<T>) obj;

		return map.equals(other.map);
	}

	public void clear() {
		map.clear();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}


	public void putAll(final Map<? extends String, ? extends T> t) {
		map.putAll(t);

	}

	public T remove(final String key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

}
