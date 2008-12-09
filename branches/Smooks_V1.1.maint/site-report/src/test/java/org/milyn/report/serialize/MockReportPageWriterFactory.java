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

package org.milyn.report.serialize;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.util.Vector;

public class MockReportPageWriterFactory implements ReportPageWriterFactory {

	Vector testPageWriters = new Vector();
	
	public IndexPageWriter getIndexPageWriter() {
		TestPageWriter indexWriter = getWriter("top index");
		return new IndexPageWriter(indexWriter.getWriter());
	}

	public PageListWriter getPageListWriter(String browserName) {
		TestPageWriter indexWriter = getWriter(browserName + " index");
		return new PageListWriter(indexWriter.getWriter());
	}

	public PageReportWriter getPageReportWriter(String browserName, String pagePath) {
		TestPageWriter indexWriter = getWriter(browserName + "/" + pagePath);
		return new PageReportWriter(indexWriter.getWriter());
	}

	public NodeReportWriter getNodeReportWriter(String browserName, String pagePath, int nodeReportNumber) {
		TestPageWriter indexWriter = getWriter(browserName + NodeReportWriter.getPath(pagePath, nodeReportNumber));
		return new NodeReportWriter(indexWriter.getWriter());
	}

	public Vector getTestPageWriters() {
		return testPageWriters;
	}
	
	private TestPageWriter getWriter(String desc) {
		TestPageWriter testWriter = new TestPageWriter(new CharArrayWriter(), desc);
		testPageWriters.add(testWriter);
		return testWriter;
	}

	public static class TestPageWriter {
		private CharArrayWriter writer;
		private String desc;

		private TestPageWriter(CharArrayWriter writer, String desc) {
			this.writer = writer;
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}

		public CharArrayWriter getWriter() {
			return writer;
		}

		public InputStream getStream() {
			String content = new String(writer.toCharArray());
			return new ByteArrayInputStream(content.getBytes());
		}
	}
}
