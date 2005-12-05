/*
	Milyn - Copyright (C) 2003

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

package org.milyn.report.serialize;

import java.io.File;

import junit.framework.TestCase;

public class NodeReportWriterTest extends TestCase {

	public void testGetPath() {
		assertEquals((new File("/a/node2_page.html")).toString(), NodeReportWriter.getPath((new File("/a/page.html")).toString(), 2));
		assertEquals((new File("/node2_page.html")).toString(), NodeReportWriter.getPath((new File("/page.html")).toString(), 2));
		assertEquals((new File("node2_page.html")).toString(), NodeReportWriter.getPath((new File("page.html")).toString(), 2));
	}
}
