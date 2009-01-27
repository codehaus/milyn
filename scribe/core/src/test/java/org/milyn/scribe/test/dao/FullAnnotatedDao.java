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
package org.milyn.scribe.test.dao;

import java.util.Collection;
import java.util.Map;

import org.milyn.scribe.dao.annotation.Dao;
import org.milyn.scribe.dao.annotation.Flush;
import org.milyn.scribe.dao.annotation.Insert;
import org.milyn.scribe.dao.annotation.Lookup;
import org.milyn.scribe.dao.annotation.LookupByQuery;
import org.milyn.scribe.dao.annotation.Param;
import org.milyn.scribe.dao.annotation.Update;
import org.milyn.scribe.dao.annotation.Delete;


/**
 * @author maurice_zeijen
 *
 */
@Dao
public interface FullAnnotatedDao {

	@Insert
	Object insertIt(final Object entity);

	@Update
	Object updateIt(final Object entity);

	@Flush
	void flushIt();

	@Delete
	void deleteIt(Object entity);

	@LookupByQuery
	Collection<?> findByQuery(String query, Object[] parameters);

	@LookupByQuery
	Collection<?> findByQuery(String query, Map<String, Object> parameters);

	@Lookup("id")
	Collection<?> findById(@Param("id") Long id);

	@Lookup("name")
	Collection<?> findById(@Param("surname") String surname, @Param("firstname") String firstname);

}