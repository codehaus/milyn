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
package org.milyn.scribe.dao;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public abstract class AbstractDaoRegister<T> implements DaoRegister<T> {

	/* (non-Javadoc)
	 * @see org.milyn.scribe.dao.DaoRegister#getDao()
	 */
	public T getDao() {
		throw new UnsupportedOperationException("The getDao() method is not supported by this ('" + this.getClass().getName() + "') DaoRegister.");
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.dao.DaoRegister#getDao(java.lang.String)
	 */
	public T getDao(String name) {
		throw new UnsupportedOperationException("The getDao(String) method is not supported by this ('" + this.getClass().getName() + "') DaoRegister.");
	}

	public void returnDao(T dao) {};

}
