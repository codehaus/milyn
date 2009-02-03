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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class SingleDaoRegister<T> extends AbstractDaoRegister<T> {

	private final T dao;

	/**
	 * @param dao
	 */
	public SingleDaoRegister(T dao) {
		this.dao = dao;
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.AbstractDaoRegister#getDao()
	 */
	@Override
	public T getDao() {
		return dao;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("dao", dao)
				.toString();
	}

}
