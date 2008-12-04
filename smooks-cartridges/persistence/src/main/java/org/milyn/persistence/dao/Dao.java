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
package org.milyn.persistence.dao;

import javax.naming.OperationNotSupportedException;


/**
 * @author maurice_zeijen
 *
 */
public interface Dao<E> {

	/**
	 *
	 * @param name
	 * @param parameters
	 * @return
	 * @throws OperationNotSupportedException If the operation is not supported then the {@link OperationNotSupportedException} is thrown
	 */
	E persist(E entity);

	/**
	 *
	 * @param name
	 * @param parameters
	 * @return
	 * @throws OperationNotSupportedException If the operation is not supported then the {@link OperationNotSupportedException} is thrown
	 */
	E merge(E entity);

}
