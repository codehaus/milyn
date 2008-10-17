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
package org.milyn.persistence.test.dao;

import java.util.Collection;
import java.util.Map;

import org.milyn.persistence.dao.annotation.Dao;
import org.milyn.persistence.dao.annotation.FindBy;
import org.milyn.persistence.dao.annotation.FindByQuery;
import org.milyn.persistence.dao.annotation.Flush;
import org.milyn.persistence.dao.annotation.Merge;
import org.milyn.persistence.dao.annotation.Param;
import org.milyn.persistence.dao.annotation.Persist;


/**
 * @author maurice_zeijen
 *
 */
@Dao
public interface AnnotatedDao {

	@Persist
	void persistIt(final Object entity);

	@Merge
	Object mergeIt(final Object merge);

	@Flush
	void flushIt();

	@FindByQuery
	Collection<?> findByQuery(String query, Object[] parameters);

	@FindByQuery
	Collection<?> findByQuery(String query, Map<String, Object> parameters);

	@FindBy("id")
	Collection<?> findById(@Param("id") Long id);

	@FindBy("name")
	Collection<?> findById(@Param("surname") String surname, @Param("firstname") String firstname);
}