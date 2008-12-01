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
package org.milyn.persistence.dao.adapter.ibatis;

import static junit.framework.Assert.*;

import org.milyn.persistence.test.TestGroup;
import org.milyn.persistence.test.util.BaseTestCase;
import org.mockito.MockitoAnnotations.Mock;
import org.testng.annotations.Test;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class SqlMapClientRegisterTest extends BaseTestCase {

	@Mock
	SqlMapClient sqlMapClient;

	@Test( groups = TestGroup.UNIT )
	public void test_getDao() {

		SqlMapClientRegister register = new SqlMapClientRegister(sqlMapClient);

		SqlMapClientDaoAdapter entityManagerDaoAdapter = register.getDao();

		assertNotNull(entityManagerDaoAdapter);

		assertSame(sqlMapClient, entityManagerDaoAdapter.getSqlMapClient());

	}


}
