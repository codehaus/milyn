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

import org.apache.commons.lang.NotImplementedException;
import org.milyn.assertion.AssertArgument;
import org.milyn.persistence.ObjectStore;
import org.milyn.persistence.dao.MappedDao;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class MappedDaoInvokerFactory {

	private static final MappedDaoInvokerFactory instance = new MappedDaoInvokerFactory();

	//public static final String REPOSITORY_KEY = DaoInvokerFactory.class.getName() + "#REPOSITORY_KEY";

	public static final MappedDaoInvokerFactory getInstance() {
		return instance;
	}

	private MappedDaoInvokerFactory() {
	}


	@SuppressWarnings("unchecked")
	public MappedDaoInvoker create(final Object dao, final ObjectStore objectStore) {
		AssertArgument.isNotNull(dao, "dao");
		AssertArgument.isNotNull(objectStore, "objectStore");

		if(dao instanceof MappedDao) {
			return new InterfaceMappedDaoInvoker((MappedDao<? super Object>) dao);
		} else {

			throw new NotImplementedException("Only the NamedDAO Interface method is supported now");
		}
	}

}
