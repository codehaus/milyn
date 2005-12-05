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

package org.milyn.report;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.milyn.SmooksException;
import org.milyn.container.MockContainerResourceLocator;
import org.milyn.io.StreamUtils;
import org.milyn.ioc.BeanFactory;
import org.milyn.report.SmooksReportGenerator;
import org.milyn.report.serialize.MockReportPageWriterFactory;
import org.milyn.report.serialize.ReportPageWriterFactory;
import org.milyn.report.serialize.MockReportPageWriterFactory.TestPageWriter;
import org.milyn.util.CharUtils;

import junit.framework.TestCase;

public class SmooksReportGeneratorTest extends TestCase {

	MockReportPageWriterFactory writerFactory;
	
	protected void setUp() throws Exception {
		writerFactory = new MockReportPageWriterFactory();
	}

	/**
	 * This must be the 1st test in this class - before "SMOOKS_HOME" gets set.
	 */
	public void testSmooksTester_badenv() {
		try {
			new SmooksReportGenerator("http://x.com", "msie6", "ISO-8859-1", true, writerFactory);
			fail("Expected IllegalStateException on constructor.");
		} catch(IllegalStateException e) {
			// expected
		}
	}
	
	public void testSmooksTester_args() {
		System.setProperty("SMOOKS_HOME", MockContainerResourceLocator.TEST_STANDALONE_CTX_BASE.getAbsolutePath());

		// Good args
		new SmooksReportGenerator("http://x.com", "msie6", "ISO-8859-1", true, writerFactory);
		new SmooksReportGenerator("http://x.com", "msie6", "UTF-8", true, writerFactory);
		new SmooksReportGenerator("http://x.com", "msie6", null, true, writerFactory);
		
		// Bad args.  Test the baseURI arg.
		assertBad_SmooksTester_args(null, "msie6", "ISO-8859-1", true, writerFactory);
		assertBad_SmooksTester_args(" ", "msie6", "ISO-8859-1", true, writerFactory);
		assertBad_SmooksTester_args("asdasd", "msie6", "ISO-8859-1", true, writerFactory);
		assertBad_SmooksTester_args("/x/index.jsp", "msie6", "ISO-8859-1", true, writerFactory);
		assertBad_SmooksTester_args("mailto:tfennelly@xxx.com", "msie6", "ISO-8859-1", true, writerFactory);

		// Bad args.  Test the browserName arg.
		assertBad_SmooksTester_args("http://x.com", null, "ISO-8859-1", true, writerFactory);
		assertBad_SmooksTester_args("http://x.com", " ", "ISO-8859-1", true, writerFactory);
		assertBad_SmooksTester_args("http://x.com", "unknownbrowser", "ISO-8859-1", true, writerFactory);

		// Bad args.  Test the contentEncoding arg.
		assertBad_SmooksTester_args("http://x.com", "msie6", " ", true, writerFactory);
		assertBad_SmooksTester_args("http://x.com", "msie6", "xxx", true, writerFactory);


		// Bad args.  Test the writerFactory arg.
		assertBad_SmooksTester_args("http://x.com", "msie6", "ISO-8859-1", true, null);
	}
	
	private void assertBad_SmooksTester_args(String baseURI, String browserName, String contentEncoding, boolean deep, ReportPageWriterFactory writerFactory) {
		try {
			new SmooksReportGenerator(baseURI, browserName, contentEncoding, deep, writerFactory);
			fail("Expected IllegalArgumentException on constructor.");
		} catch(IllegalArgumentException e) {
			// expected
		}
	}
	
	public void test_resolveRequestURI() {
		System.setProperty("SMOOKS_HOME", MockContainerResourceLocator.TEST_STANDALONE_CTX_BASE.getAbsolutePath());

		// Tester - path on baseURI "/a/b/"
		SmooksReportGenerator tester = new SmooksReportGenerator("http://x.com/a/b/", "msie6", "ISO-8859-1", true, writerFactory);

		try {
			tester.resolveRequestURI("../pageoutsidecontext.html");
			fail("Expected IllegalArgumentException.");
		} catch(IllegalArgumentException e) {
			// expected
		}
		try {
			tester.resolveRequestURI("/pageoutsidecontext.html");
			fail("Expected IllegalArgumentException.");
		} catch(IllegalArgumentException e) {
			// expected
		}
		try {
			tester.resolveRequestURI("http://z.com");
			fail("Expected IllegalArgumentException.");
		} catch(IllegalArgumentException e) {
			// expected
		}
		URI uri1 = tester.resolveRequestURI("pageinsidecontext.html");
		assertEquals(URI.create("http://x.com/a/b/pageinsidecontext.html"), uri1);
		URI uri2 = tester.resolveRequestURI("c/d/pageinsidecontext.html");
		assertEquals(URI.create("http://x.com/a/b/c/d/pageinsidecontext.html"), uri2);

		// Another tester - no path on baseURI
		tester = new SmooksReportGenerator("http://x.com", "msie6", "ISO-8859-1", true, writerFactory);
		URI uri3 = tester.resolveRequestURI("pageinsidecontext.html");
		URI uri4 = tester.resolveRequestURI("/pageinsidecontext.html");
		assertEquals(uri3, uri4);
	}
	
	public void test_generateReport() {
		MockContainerResourceLocator resLocator = (MockContainerResourceLocator)BeanFactory.getBean("standaloneResourceLocator");
		
		System.setProperty("SMOOKS_HOME", MockContainerResourceLocator.TEST_STANDALONE_CTX_BASE.getAbsolutePath());

		// Setup the test features: 
		// 1. The actual page sources
		// 2. The report CDUs - these are setup in the test standalone context
		resLocator.setResource("http://x.com/a/b/request-page.html", getClass().getResourceAsStream("request-page.html"));
		resLocator.setResource("http://x.com/a/b/linked-page.jsp", getClass().getResourceAsStream("linked-page.jsp"));
		SmooksReportGenerator reportGenerator = new SmooksReportGenerator("http://x.com/a/b/", "msie6w,firefox", "ISO-8859-1", true, writerFactory);
		try {
			reportGenerator.generateReport("request-page.html");
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (SmooksException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		// Check the report output - written to the writers on created by the writerFactory
		List testPageWriters = writerFactory.getTestPageWriters();
		TestPageWriter testPageWriter;
		assertEquals("Unexpected number of report writers created.", 12, testPageWriters.size());

		// top level index page
		testPageWriter = (TestPageWriter)testPageWriters.get(0);
		assertIsExpected("expected/index.html", testPageWriter);

		// 'msie6w' index page
		testPageWriter = (TestPageWriter)testPageWriters.get(1);
		assertIsExpected("expected/msie6w-index.html", testPageWriter);
		// 'msie6w' request-page.html
		testPageWriter = (TestPageWriter)testPageWriters.get(2);
		assertIsExpected("expected/msie6w-request-page.html", testPageWriter);
		// 'msie6w' request-page.html - node reports
		testPageWriter = (TestPageWriter)testPageWriters.get(3);
		assertIsExpected("expected/msie6w-node1_request-page.html", testPageWriter);
		testPageWriter = (TestPageWriter)testPageWriters.get(4);
		assertIsExpected("expected/msie6w-node2_request-page.html", testPageWriter);
		// 'msie6w' linked-page.jsp.html
		testPageWriter = (TestPageWriter)testPageWriters.get(5);
		assertIsExpected("expected/msie6w-linked-page.jsp.html", testPageWriter);
		// 'msie6w' linked-page.jsp.html - node reports
		testPageWriter = (TestPageWriter)testPageWriters.get(6);
		assertIsExpected("expected/msie6w-node1_linked-page.jsp.html", testPageWriter);
		
		// 'firefox' index page
		testPageWriter = (TestPageWriter)testPageWriters.get(7);
		assertIsExpected("expected/firefox-index.html", testPageWriter);
		// 'firefox' request-page.html
		testPageWriter = (TestPageWriter)testPageWriters.get(8);
		assertIsExpected("expected/firefox-request-page.html", testPageWriter);
		// 'firefox' request-page.html - node reports
		testPageWriter = (TestPageWriter)testPageWriters.get(9);
		assertIsExpected("expected/firefox-node1_request-page.html", testPageWriter);
		// 'firefox' linked-page.jsp.html
		testPageWriter = (TestPageWriter)testPageWriters.get(10);
		assertIsExpected("expected/firefox-linked-page.jsp.html", testPageWriter);
		// 'firefox' linked-page.jsp.html - node reports
		testPageWriter = (TestPageWriter)testPageWriters.get(11);
		assertIsExpected("expected/firefox-node1_linked-page.jsp.html", testPageWriter);

		// To sysout:
		//System.out.println("[" + new String(testPageWriter.getWriter().toCharArray()) + "]");
	}

	private void assertIsExpected(String classpath, TestPageWriter testPageWriter) {
		boolean areEqual = CharUtils.compareCharStreams(getClass().getResourceAsStream(classpath), testPageWriter.getStream());
		if(!areEqual) {
			String expected = null;
			String actual = null;
			try {
				expected = new String(StreamUtils.readStream(getClass().getResourceAsStream(classpath)));
				actual = new String(StreamUtils.readStream(testPageWriter.getStream()));
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}

			System.out.println("---- Expected: [" + classpath +"] -------------------------------------------------------------");
			System.out.println("[" + expected + "]");
			System.out.println("---- Actual: -------------------------------------------------------------");
			System.out.println("[" + actual + "]");
			System.out.println("----------------------------------------------------------------------");
			fail("Report Generator output failure.  Expected [" + classpath + "]. See JUnit xml report file.");
		}
	}
}






