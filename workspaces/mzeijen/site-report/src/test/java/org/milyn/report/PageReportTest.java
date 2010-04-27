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

package org.milyn.report;

import java.util.List;

import org.milyn.cdr.CDRDef;
import org.milyn.container.standalone.StandaloneContainerRequest;
import org.milyn.container.standalone.StandaloneContainerUtil;
import org.milyn.dom.MockElement;
import org.milyn.report.PageReport;
import org.milyn.report.PageReport.NodeReport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import junit.framework.TestCase;

public class PageReportTest extends TestCase {
	
	public void testGetInstance() {
		StandaloneContainerRequest request1 = null;
		StandaloneContainerRequest request2 = null;
		
		try {
			PageReport.getInstance(null);
			fail("Failed to throw IllegalArgumentException.");
		} catch(IllegalArgumentException e) {
			// OK
		}
		
		request1 = StandaloneContainerUtil.getRequest("http://x.y.z", "msie6");
		request2 = StandaloneContainerUtil.getRequest("http://x.y.z", "msie6");
		
		PageReport instance1 = PageReport.getInstance(request1);
		PageReport instance2 = PageReport.getInstance(request2);
		
		assertNotSame(instance1, instance2);
		assertSame(instance1, PageReport.getInstance(request1));
		assertSame(instance2, PageReport.getInstance(request2));
	}

	public void testReport_badarg() {
		report_assertbadarg(new MockElement("x"), null);
		report_assertbadarg(null, new CDRDef("x", "x", "x"));
	}
	public void report_assertbadarg(Node node, CDRDef config) {
		StandaloneContainerRequest request = StandaloneContainerUtil.getRequest("http://x.y.z", "msie6");
		PageReport pageReport = PageReport.getInstance(request);
		
		try {
			pageReport.report(node, config);
			fail("Failed to throw IllegalArgumentException.");
		} catch(IllegalArgumentException e) {
			//OK
		}
	}
	
	public void testReport() {
		StandaloneContainerRequest request = StandaloneContainerUtil.getRequest("http://x.y.z", "msie6");
		PageReport pageReport = PageReport.getInstance(request);
		Element element1 = new MockElement("x"); 
		Element element2 = new MockElement("y");
		CDRDef config1 = new CDRDef("x", "x", "x");
		CDRDef config2 = new CDRDef("x", "x", "x");
		CDRDef config3 = new CDRDef("x", "x", "x");
		CDRDef config4 = new CDRDef("x", "x", "x");
		
		pageReport.report(element1, config1);
		pageReport.report(element1, config2);
		pageReport.report(element2, config3);
		pageReport.report(element2, config4);
		
		assertEquals("Wrong report count.", 4, pageReport.getReportLength());

		List entries = pageReport.getNodeReportEntries();
		assertEquals("Wrong NodeReport list size.", 2, entries.size());
		
		NodeReport reportEntry1 = (NodeReport)entries.get(0);
		NodeReport reportEntry2 = (NodeReport)entries.get(1);
		assertTrue(reportEntry1.equals(element1));
		assertTrue(reportEntry2.equals(element2));
		
		assertEquals("Wrong NodeReport report count.", 2, reportEntry1.getReportCount());
		assertEquals("Wrong NodeReport report count.", 2, reportEntry2.getReportCount());

		assertEquals(config1, reportEntry1.getReportConfig(0));
		assertEquals(config2, reportEntry1.getReportConfig(1));
		assertEquals(config3, reportEntry2.getReportConfig(0));
		assertEquals(config4, reportEntry2.getReportConfig(1));
	}
}
