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
package org.milyn.persistence.dao.adapter.jpa;

import javax.persistence.EntityManager;

import org.milyn.assertion.AssertArgument;
import org.milyn.persistence.dao.DaoRegister;


/**
 * @author maurice_zeijen
 *
 */
public class EntityManagerRegister implements DaoRegister<EntityManagerDaoAdapter> {

	private final EntityManager entityManager;

	/**
	 *
	 */
	public EntityManagerRegister(final EntityManager entityManager) {
		AssertArgument.isNotNull(entityManager, "entityManager");

		this.entityManager = entityManager;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAORegistery#getDAO(java.lang.String)
	 */
	public EntityManagerDaoAdapter getDao(final String name) {

		return new EntityManagerDaoAdapter(entityManager);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAORegistery#returnDAO(java.lang.Object)
	 */
	public void returnDao(final EntityManagerDaoAdapter dao) {
	}

}
