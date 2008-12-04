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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.persistence.dao.register.SingleDaoRegister;
import org.milyn.persistence.test.dao.FullInterfaceDao;
import org.milyn.persistence.util.PersistenceUtil;
import org.mockito.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class EntityLocatorTest {
	private static final boolean ENABLE_REPORTING = false;

	@Mock
	private FullInterfaceDao<Object> dao;

	@Test
	public void test_entity_locate() throws Exception {
		Smooks smooks = new Smooks(getResourceAsStream("entity-locator-01.xml"));

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, new SingleDaoRegister<Object>(dao));

		enableReporting(executionContext, "test_entity_locate.html");

		Source source = new StreamSource(getClass().getResourceAsStream("input-message-01.xml" ) );
		smooks.filter(source, null, executionContext);

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
