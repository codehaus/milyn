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
package org.milyn.scribe.register;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.milyn.assertion.AssertArgument;



/**
 * @author maurice_zeijen
 *
 */
public class MapRegister<T> extends AbstractDaoRegister<T> {

	public static <T> MapRegister<T> newInstance(Map<String, ? extends T> map) {
		AssertArgument.isNotNull(map, "map");

		return new MapRegister<T>(new HashMap<String, T>(map));
	}

	public static <T> Builder<T> builder() {
		return new Builder<T>();
	}

	public static <T> Builder<T> builder(Map<String, ? extends T> map) {
		return new Builder<T>(map);
	}

	private final HashMap<String, ? extends T> map;

	/**
	 *
	 */
	private MapRegister(HashMap<String, ? extends T> map) {
		this.map = map;
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
	 * @see org.milyn.scribe.DAORegistery#getDAO(java.lang.String)
	 */
	@Override
	public T getDao(final String key) {
		return map.get(key);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("daoMap", map, true)
				.toString();
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

	public int size() {
		return map.size();
	}

	public static class Builder<T> {

		private final HashMap<String, T> map;

		/**
		 *
		 */
		private Builder() {
			map = new HashMap<String, T>();
		}

		/**
		 *
		 */
		public Builder(Map<String, ? extends T> map) {
			AssertArgument.isNotNull(map, "map");

			this.map = new HashMap<String, T>(map);
		}

		public Builder<T> put(String name, T dao) {
			AssertArgument.isNotNull(name, "name");
			AssertArgument.isNotNull(dao, "dao");

			map.put(name, dao);

			return this;
		}

		public Builder<T> putAll(Map<String, ? extends T> map) {
			AssertArgument.isNotNull(map, "map");

			this.map.putAll(map);

			return this;
		}

		public MapRegister<T> build() {
			return new MapRegister<T>(map);
		}

	}

}
