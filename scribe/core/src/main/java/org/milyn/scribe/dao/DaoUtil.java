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
package org.milyn.scribe.dao;

import org.milyn.annotation.AnnotationManager;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public final class DaoUtil {

	public static boolean isDao(Object dao) {
		return dao instanceof Dao || AnnotationManager.getAnnotatedClass(dao.getClass()).isAnnotationPresent(org.milyn.scribe.dao.annotation.Dao.class);
	}

	public static boolean isMappedDao(Object mappedDao) {
		return mappedDao instanceof MappedDao || AnnotationManager.getAnnotatedClass(mappedDao.getClass()).isAnnotationPresent(org.milyn.scribe.dao.annotation.MappedDao.class);
	}

}
