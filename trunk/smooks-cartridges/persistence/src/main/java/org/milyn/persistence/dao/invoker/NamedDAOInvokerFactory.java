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
import org.milyn.container.ApplicationContext;
import org.milyn.persistence.dao.NamedDAO;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class NamedDAOInvokerFactory {

	private static final NamedDAOInvokerFactory instance = new NamedDAOInvokerFactory();

	private static final String REPOSITORY_KEY = DAOInvokerFactory.class.getName() + "#REPOSITORY_KEY";

	public static final NamedDAOInvokerFactory getInstance() {
		return instance;
	}

	private NamedDAOInvokerFactory() {
	}


	public NamedDAOInvoker create(final Object dao, final ApplicationContext applicationContext) {
		AssertArgument.isNotNull(dao, "dao");
		AssertArgument.isNotNull(applicationContext, "applicationContext");

		if(dao instanceof NamedDAO) {
			return new InterfaceNamedDAOInvoker((NamedDAO<? super Object>) dao);
		} else {

			throw new NotImplementedException("Only the NamedDAO Interface method is supported now");
		}
	}

}
