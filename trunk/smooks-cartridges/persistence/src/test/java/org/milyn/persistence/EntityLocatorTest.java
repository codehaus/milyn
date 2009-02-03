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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.persistence.test.dao.FullInterfaceDao;
import org.milyn.persistence.test.util.BaseTestCase;
import org.milyn.persistence.util.PersistenceUtil;
import org.milyn.scribe.register.SingleDaoRegister;
import org.mockito.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
@Test(groups="unit")
public class EntityLocatorTest extends BaseTestCase {
	private static final boolean ENABLE_REPORTING = true;

	@Mock
	private FullInterfaceDao<Object> dao;

	public void test_entity_locate() throws Exception {
		Object result = new Object();

		HashMap<String, Object> expectedArg3 = new HashMap<String, Object>();
		expectedArg3.put("d", new Integer(2));
		expectedArg3.put("e", new Integer(3));

		HashMap<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("arg1", new Integer(1));
		expectedMap.put("arg2", new Integer(5));
		expectedMap.put("arg3", expectedArg3);

		when(dao.lookup(eq("something"), eq(expectedMap))).thenReturn(result);

		Smooks smooks = new Smooks(getResourceAsStream("entity-locator-01.xml"));

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, new SingleDaoRegister<Object>(dao));

		enableReporting(executionContext, "test_entity_locate.html");

		Source source = new StreamSource(getClass().getResourceAsStream("input-message-01.xml" ) );
		smooks.filter(source, null, executionContext);

		assertSame(result, BeanRepository.getInstance(executionContext).getBean("entity"));

	}

	/**
	 * @param resource
	 * @return
	 */
	private InputStream getResourceAsStream(String resource) {
		return EntityLocatorTest.class.getResourceAsStream(resource);
	}

	private void enableReporting(ExecutionContext executionContext, String reportFilePath) throws IOException {
		if(ENABLE_REPORTING) {
			executionContext.setEventListener(new HtmlReportGenerator("target/" + reportFilePath));
		}
	}
}
