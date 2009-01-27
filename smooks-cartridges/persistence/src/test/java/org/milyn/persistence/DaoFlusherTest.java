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
package org.milyn.persistence;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.payload.StringSource;
import org.milyn.persistence.test.dao.FullInterfaceDao;
import org.milyn.persistence.test.dao.FullInterfaceMappedDao;
import org.milyn.persistence.test.util.BaseTestCase;
import org.milyn.persistence.util.PersistenceUtil;
import org.milyn.scribe.register.MapRegister;
import org.milyn.scribe.register.SingleDaoRegister;
import org.mockito.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class DaoFlusherTest  extends BaseTestCase   {

	private static final boolean ENABLE_REPORTING = false;

	private static final String SIMPLE_XML =  "<root />";

	@Mock
	private FullInterfaceDao<Object> dao;

	@Mock
	private FullInterfaceMappedDao<Object> mappedDao;

	@Test
	public void test_dao_flush() throws Exception {
		Smooks smooks = new Smooks(getResourceAsStream("doa-flusher-01.xml"));

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, new SingleDaoRegister<Object>(dao));

		enableReporting(executionContext, "report_test_dao_flush.html");

		smooks.filter(new StringSource(SIMPLE_XML), null, executionContext);

		verify(dao).flush();
	}

	@Test
	public void test_dao_flush_with_named_dao() throws Exception {

		Smooks smooks = new Smooks(getResourceAsStream("doa-flusher-02.xml"));
		Map<String, Object> daoMap = new HashMap<String, Object>();
		daoMap.put("dao1", dao);

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, new MapRegister<Object>(daoMap));

		enableReporting(executionContext, "report_test_dao_flush_with_named_dao.html");

		smooks.filter(new StringSource(SIMPLE_XML), null, executionContext);

		verify(dao).flush();
	}

	@Test
	public void test_mapped_dao_flush() throws Exception {
		Smooks smooks = new Smooks(getResourceAsStream("doa-flusher-03.xml"));

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, new SingleDaoRegister<Object>(mappedDao));

		enableReporting(executionContext, "report_test_mapped_dao_flush.html");

		smooks.filter(new StringSource(SIMPLE_XML), null, executionContext);

		verify(mappedDao).flush("flush");
	}

	@Test
	public void test_dao_flush_with_flushBefore() throws Exception {
		Smooks smooks = new Smooks(getResourceAsStream("doa-flusher-04.xml"));
		Map<String, Object> daoMap = new HashMap<String, Object>();
		daoMap.put("mappedDao", mappedDao);
		daoMap.put("dao", dao);

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, new MapRegister<Object>(daoMap));

		enableReporting(executionContext, "report_test_dao_flush_with_flushBefore.html");

		smooks.filter(new StringSource(SIMPLE_XML), null, executionContext);

		verify(mappedDao).flush("flushIt");
		verify(dao).flush();
	}


	/**
	 * @param resource
	 * @return
	 */
	private InputStream getResourceAsStream(String resource) {
		return DaoFlusherTest.class.getResourceAsStream(resource);
	}

	private void enableReporting(ExecutionContext executionContext, String reportFilePath) throws IOException {
		if(ENABLE_REPORTING) {
			executionContext.setEventListener(new HtmlReportGenerator("target/" + reportFilePath));
		}
	}
}
