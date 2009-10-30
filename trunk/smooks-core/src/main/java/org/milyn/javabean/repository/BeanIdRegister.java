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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.milyn.assertion.AssertArgument;
import org.milyn.javabean.context.BeanIdIndex;

/**
 * Bean Id List
 * <p/>
 * Represents a map of BeanId's. Every BeanId has it own unique index. The index
 * is incremental. The index starts with zero.
 * <p/>
 * Once a BeanId is registered it can never be unregistered.
 *
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 * @deprecated Use the BeanIdIndex
 */
@Deprecated
public class BeanIdRegister {

	private final BeanIdIndex beanIdIndex;

	public BeanIdRegister() {
		beanIdIndex = new BeanIdIndex();
	}

	BeanIdRegister(BeanIdIndex beanIdIndex) {
		this.beanIdIndex = beanIdIndex;
	}

	public boolean containsBeanId(String beanId) {
		return beanIdIndex.containsBeanId(beanId);
	}

	public boolean equals(Object obj) {
		return beanIdIndex.equals(obj);
	}

	public BeanId getBeanId(String beanId) {
		return beanIdIndex.getBeanId(beanId);
	}

	public Map<String, BeanId> getBeanIdMap() {
		return beanIdIndex.getBeanIdMap();
	}

	public int hashCode() {
		return beanIdIndex.hashCode();
	}

	public BeanId register(String beanIdName) {
		return beanIdIndex.register(beanIdName);
	}

	public int size() {
		return beanIdIndex.size();
	}

	BeanIdIndex getBeanIdIndex() {
		return beanIdIndex;
	}
}
