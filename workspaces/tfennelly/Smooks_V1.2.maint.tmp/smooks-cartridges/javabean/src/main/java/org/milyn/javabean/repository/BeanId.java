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

package org.milyn.javabean.repository;



/**
 * Bean Id
 * <p />
 * The BeanId represents the id of a bean. It is used to set  and
 * retrieve instance of the bean from the BeanRepository.
 * <p />
 * The String representation of the BeanId is registered with a {@link BeanIdRegister}
 * and there by coupled to that {@link BeanIdRegister}. The BeanId holds the original
 * {@link String} beanId,also called beanIdName. It also holds the index of
 * the place it has within the {@link BeanIdRegister}. 
 * 
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanId {

	private final int index;

	private final String name;

	private final BeanIdRegister beanIdList;

	/**
	 * @param index
	 * @param name
	 */
	public BeanId(BeanIdRegister beanIdRegister, int index, String beanId) {
		this.beanIdList = beanIdRegister;
		this.index = index;
		this.name = beanId;
	}

	/**
	 * Returns the index of the place
	 * it holds in the {@link BeanIdRegister}.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the BeanId name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the BeanIdList with which
	 * it is registered.
	 * 
	 * @return the beanIdList
	 */
	public BeanIdRegister getBeanIdList() {
		return beanIdList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 54 + index;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}

		if(obj instanceof BeanId == false) {
			return false;
		}
		BeanId rhs = (BeanId) obj;
		if(this.beanIdList != rhs.beanIdList) {
			return false;
		}
		if(this.name != rhs.name) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

}
