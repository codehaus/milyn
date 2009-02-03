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
package org.milyn.scribe.invoker;

import java.util.Map;

/**
 * @author maurice_zeijen
 *
 */
public interface DaoInvoker {

	Object insert(Object entity);

	Object insert(String name, Object entity);

	Object update(Object entity);

	Object update(String name, Object entity);

	Object delete(Object entity);

	Object delete(String name, Object entity);

	void flush();

	Object lookupByQuery(String query, Object ... parameters);

	Object lookupByQuery(String query, Map<String, ?> parameters);

	Object lookup(String name, Map<String, ?> parameters);

	Object lookup(String name, Object ... parameters);

}
