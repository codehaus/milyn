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
package org.milyn.persistence;

import org.milyn.cdr.ParameterAccessor;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.persistence.dao.DaoRegister;


/**
 * @author maurice_zeijen
 *
 */
public final class PersistenceUtil {

	public static final String PARAM_NAME_DAO_REGISTERY = "dao.register.name";

	public static final String PARAM_VALUE_DAO_REGISTERY = PersistenceUtil.class.getName() + "#DAORegister";

	/**
	 *
	 */
	private PersistenceUtil() {
	}

	public static String getDAORegisterAttributeName(final ContentDeliveryConfig config) {

		return ParameterAccessor.getStringParameter(PARAM_NAME_DAO_REGISTERY, PARAM_VALUE_DAO_REGISTERY, config);

	}

	public static DaoRegister<?> getDAORegister(final ExecutionContext executionContext) {

		return (DaoRegister<?>) executionContext.getAttribute(PersistenceUtil.getDAORegisterAttributeName(executionContext.getDeliveryConfig())) ;

	}

	public static void  setDAORegister(final ExecutionContext executionContext, final DaoRegister<?> registery) {

		executionContext.setAttribute(getDAORegisterAttributeName(executionContext.getDeliveryConfig()), registery) ;

	}

}