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
package org.milyn.scribe.adapter.jpa;

import javax.persistence.EntityManager;

import org.milyn.assertion.AssertArgument;
import org.milyn.scribe.AbstractDaoRegister;


/**
 * @author maurice_zeijen
 *
 */
public class EntityManagerRegister extends AbstractDaoRegister<EntityManagerDaoAdapter> {

	private final EntityManagerDaoAdapter entityManager;

	/**
	 *
	 */
	public EntityManagerRegister(final EntityManager entityManager) {
		AssertArgument.isNotNull(entityManager, "entityManager");

		this.entityManager = new EntityManagerDaoAdapter(entityManager);
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.DAORegistery#getDAO()
	 */
	@Override
	public EntityManagerDaoAdapter getDao() {

		return entityManager;
	}

}
