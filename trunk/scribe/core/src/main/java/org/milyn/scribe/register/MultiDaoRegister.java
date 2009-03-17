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
import org.milyn.scribe.DaoRegister;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class MultiDaoRegister<T> extends AbstractDaoRegister<T> {

	public static <T> MultiDaoRegister<T> newInstance(Map<String, ? extends DaoRegister<T>> map) {
		AssertArgument.isNotNull(map, "map");

		return new MultiDaoRegister<T>(new HashMap<String, DaoRegister<T>>(map));
	}

	public static <T> Builder<T> builder() {
		return new Builder<T>();
	}

	public static <T> Builder<T> builder(Map<String, ? extends DaoRegister<T>> map) {
		return new Builder<T>(map);
	}

	private Map<String, DaoRegister<T>> daoRegisterMap = new HashMap<String, DaoRegister<T>>();

	/**
	 * @param hashMap
	 */
	private MultiDaoRegister(Map<String, DaoRegister<T>> daoRegisterMap) {
		this.daoRegisterMap = daoRegisterMap;
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.register.AbstractDaoRegister#getDao(java.lang.String)
	 */
	@Override
	public T getDao(String name) {

		int sepIndex = name.indexOf(".");
		String key = name;
		String subName = null;
		if(sepIndex >= 0) {
			key = name.substring(0, sepIndex);
			subName = name.substring(sepIndex+1);
		}

		DaoRegister<T> subDaoRegister = daoRegisterMap.get(key);

		if(subDaoRegister == null) {
			return null;
		}
		if(subName == null) {
			return subDaoRegister.getDefaultDao();
		} else {
			return subDaoRegister.getDao(subName);
		}
	}

	public int size() {
		return daoRegisterMap.size();
	}

	public Map<String, DaoRegister<T>> getDaoRegisterMap() {
		return new HashMap<String, DaoRegister<T>>(daoRegisterMap);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return daoRegisterMap.hashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("daoRegisterMap", daoRegisterMap, true)
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
		if(obj instanceof MultiDaoRegister == false) {
			return false;
		}

		@SuppressWarnings("unchecked")
		final MultiDaoRegister<T> other = (MultiDaoRegister<T>) obj;

		return daoRegisterMap.equals(other.daoRegisterMap);
	}

	/**
	 * Builds MultiDaoRegister
	 *
	 */
	static class Builder<T> {

		private final Map<String, DaoRegister<T>> map;

		/**
		 *
		 */
		private Builder() {
			map = new HashMap<String, DaoRegister<T>>();
		}

		/**
		 *
		 */
		public Builder(Map<String, ? extends DaoRegister<T>> map) {
			AssertArgument.isNotNull(map, "map");

			this.map = new HashMap<String, DaoRegister<T>>(map);
		}

		public Builder<T> put(String name, DaoRegister<T> daoRegister) {
			AssertArgument.isNotNull(name, "name");
			AssertArgument.isNotNull(daoRegister, "daoRegister");

			map.put(name, daoRegister);

			return this;
		}

		public Builder<T> putAll(Map<String, ? extends DaoRegister<T>> map) {
			AssertArgument.isNotNull(map, "map");

			this.map.putAll(map);

			return this;
		}

		public MultiDaoRegister<T> build() {
			return new MultiDaoRegister<T>(map);
		}

	}


}
