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
package org.milyn.persistence.dao.invoker;

import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.milyn.assertion.AssertArgument;
import org.milyn.container.BoundAttributeStore;
import org.milyn.persistence.dao.NamedDAO;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class NamedDAOInvokerContext {

	private NamedDAOInvoker invoker;

	/**
	 *
	 */
	public NamedDAOInvokerContext(final Object dao, final BoundAttributeStore attributestore) {
		AssertArgument.isNotNull(dao, "dao");

		initializeInvoker(dao, attributestore);
	}

	@SuppressWarnings("unchecked")
	private void initializeInvoker(final Object dao, final BoundAttributeStore attributestore) {

		if(dao instanceof NamedDAO) {
			invoker = new InterfaceNamedDAOInvoker((NamedDAO<? super Object>) dao);
		} else {

			throw new NotImplementedException("Only the NamedDAO Interface method is supported now");
		}
	}


	public void persist(String name, final Object obj) {
		invoker.persist(name, obj);
	}

	public Object merge(String name, final Object obj) {
		return invoker.merge(name, obj);
	}

	public void flush(String name) {
		invoker.flush(name);
	}

	public Object findBy(final String name, final Map<String, ?> parameters) {
		return invoker.findBy(name, parameters);
	}

}
