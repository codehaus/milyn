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

import org.milyn.annotation.AnnotatedClass;
import org.milyn.annotation.AnnotationManager;
import org.milyn.assertion.AssertArgument;
import org.milyn.container.ApplicationContext;
import org.milyn.container.BoundAttributeStore;
import org.milyn.persistence.dao.DAO;
import org.milyn.persistence.dao.reflection.AnnotatedDAORuntimeInfoRepository;

/**
 * @author maurice_zeijen
 *
 */
public class DAOInvokerFactory {

	private static final DAOInvokerFactory instance = new DAOInvokerFactory();

	private static final String REPOSITORY_KEY = DAOInvokerFactory.class.getName() + "#REPOSITORY_KEY";

	public static final DAOInvokerFactory getInstance() {
		return instance;
	}

	private DAOInvokerFactory() {
	}


	public DAOInvoker create(final Object dao, final ApplicationContext applicationContext) {
		AssertArgument.isNotNull(dao, "dao");
		AssertArgument.isNotNull(applicationContext, "applicationContext");

		if(dao instanceof DAO) {
			return new InterfaceDAOInvoker((DAO<?>) dao);
		} else {

			final AnnotatedClass annotatedClass =  AnnotationManager.getAnnotatedClass(dao.getClass());

			if(annotatedClass.getAnnotation(org.milyn.persistence.dao.annotation.DAO.class) != null) {

				final AnnotatedDAORuntimeInfoRepository repository = getAnnotatedDAORuntimeInfoRepository(applicationContext);

				return new AnnotatedDAOInvoker(dao, repository);

			} else {
				throw new IllegalArgumentException("The DAO argument doesn't implement the [" + DAO.class.getName() + "] interface " +
						"or is annotated with the [" + org.milyn.persistence.dao.annotation.DAO.class.getName() + "] annotation");
			}
		}
	}

	/**
	 * @param attributestore
	 * @return
	 */
	private AnnotatedDAORuntimeInfoRepository getAnnotatedDAORuntimeInfoRepository(final BoundAttributeStore attributestore) {
		AnnotatedDAORuntimeInfoRepository repository = (AnnotatedDAORuntimeInfoRepository) attributestore.getAttribute(REPOSITORY_KEY);

		if(repository == null) {
			repository = new AnnotatedDAORuntimeInfoRepository();

			attributestore.setAttribute(REPOSITORY_KEY, repository);
		}
		return repository;
	}

}
