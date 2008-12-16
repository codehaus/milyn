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

import org.milyn.scribe.annotation.Dao;
import org.milyn.scribe.annotation.Merge;
import org.milyn.scribe.annotation.Persist;
import org.milyn.scribe.annotation.ReturnsNoEntity;


/**
 * @author maurice_zeijen
 *
 */
@Dao
public interface AnnotatedDaoNoEntityReturned {

	@Persist
	@ReturnsNoEntity
	Object persistIt(final Object entity);

	@Merge
	@ReturnsNoEntity
	Object mergeIt(final Object merge);

}