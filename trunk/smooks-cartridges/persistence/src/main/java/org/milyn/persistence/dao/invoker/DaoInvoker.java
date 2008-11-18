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

import java.util.Collection;
import java.util.Map;

/**
 * @author maurice_zeijen
 *
 */
public interface DaoInvoker {

	void persist(Object obj);

	Object merge(Object obj);

	void flush();

	Collection<?> lookupByQuery(String query, Object[] parameters);

	Collection<?> lookupByQuery(String query, Map<String, ?> parameters);

//	Collection<?> findBy(String name, Object[] parameters);

	Object lookup(String name, Map<String, ?> parameters);
}
