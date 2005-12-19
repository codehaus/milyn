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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.milyn.SmooksException;
import org.milyn.SmooksStandalone;
import org.milyn.cdr.ParameterAccessor;
import org.milyn.container.standalone.StandaloneContainerRequest;
import org.milyn.device.ident.UnknownDeviceException;
import org.milyn.logging.SmooksLogger;
import org.milyn.report.serialize.FileSystemReportPageWriterFactory;
import org.milyn.report.serialize.IndexPageWriter;
import org.milyn.report.serialize.PageListWriter;
import org.milyn.report.serialize.PageReportWriter;
import org.milyn.report.serialize.ReportPageWriterFactory;
import org.w3c.dom.Node;

import sun.io.CharToByteConverter;

/**
 * Smooks Report Generator class.
 * <p/>
 * Uses {@link org.milyn.SmooksStandalone} to run a set of tests on
 * a page/site. The "layout" of the resulting report depends on the 
 * {@link org.milyn.report.serialize.ReportPageWriterFactory} used.
 * @author tfennelly
 */
public class SmooksReportGenerator {
	private File smooksHome;
	private SmooksStandalone smooksSA;
	private URI baseURI;
	private boolean deep;
	private File outputFolder;
	private String contentEncoding;
	private StringTokenizer browsers;
	private IndexPageWriter indexWriter;
	private ReportPageWriterFactory writerFactory;
	private Log logger = SmooksLogger.getLog();

	/**
	 * Public default constructor.
	 * <p/>
	 * Checks the environment for the "SMOOKS_HOME" variable.  This variable needs to be set
	 * to the Smooks install location.
	 * <p/>
	 * The test can be run "deep", allowing report generation for pages
	 * linked off the requestPage and relative to requestBaseURI.  This allows
	 * testing of an "web application".  {@link AHrefRecorder} must be configured
	 * on &lt;a href="..." /&gt; elements in order to perform a deep test.
	 * @param baseURI Base URI.  On a deep test, pages outside this context are not tested. 
	 * @param browserName The browser name of the browser to be tested.  Accepts a comma
	 * seperated list of browser names to allow report generation for multiple browsers.
	 * @param contentEncoding Encoding of the content to be tested.  Null 
	 * defaults to "ISO-8859-1".
	 * @param deep True if the {@link #runTest(String, String, String) tests} are 
	 * to run "deep", otherwise false.
	 * @param writerFactory The {@link ReportPageWriterFactory} to be used on this 
	 * report generator instance.
	 */
	public SmooksReportGenerator(String baseURI, String browserName, String contentEncoding, boolean deep, ReportPageWriterFactory writerFactory) {
		String smooksHome = System.getProperty("SMOOKS_HOME");
		
		if(smooksHome == null) {
			throw new IllegalStateException("Environment variable 'SMOOKS_HOME' not set.  This must be set to the Smooks install directory.");
		}
		setSmooksHome(smooksHome);
		setSmooksStandalone(browserName, contentEncoding);
		setBaseURI(baseURI);
		this.deep = deep;
		if(writerFactory == null) {
			throw new IllegalArgumentException("null 'writerFactory' arg in constructor call.");
		}
		this.writerFactory = writerFactory;
	}

	/**
	 * Set the {@link SmooksStandalone} instance for this SmooksReportGenerator instance.
	 * @param browserName The browser name for the prowser being tested.  This browser 
	 * must be profiled in the device-profiles.xml file.
	 * @param contentEncoding Encoding of the content to be tested.  Null 
	 * defaults to "ISO-8859-1".
	 */
	private void setSmooksStandalone(String browserName, String contentEncoding) {
		if(browserName == null) {
			throw new IllegalArgumentException("null or empty 'browserName' arg in constructor call.");
		}
		contentEncoding = (contentEncoding == null)?"ISO-8859-1":contentEncoding;
		try {
			CharToByteConverter.getConverter(contentEncoding);
			this.contentEncoding = contentEncoding;
		} catch (UnsupportedEncodingException e) {
			IllegalArgumentException argE = new IllegalArgumentException("Invalid 'contentEncoding' arg [" + contentEncoding + "].  This encoding is not supported.");
			argE.initCause(e);
			throw argE;
		}
		try {
			browsers = new StringTokenizer(browserName, ",");
			smooksSA = new SmooksStandalone(smooksHome, browsers.nextToken().trim(), contentEncoding);
		} catch (UnknownDeviceException e) {
			IllegalArgumentException argE = new IllegalArgumentException("Invalid 'browserName' arg [" + browserName + "].  'browserName' must be profiled in device-profile.xml.");
			argE.initCause(e);
			throw argE;
		}
	}

	/**
	 * Set the baseURI.
	 * <p/>
	 * Performs param validation.
	 * @param baseURI Base URI value.
	 */
	private void setBaseURI(String baseURI) {
		if(baseURI == null) {
			throw new IllegalArgumentException("null 'baseURI' arg in constructor call.");
		}
		if(!baseURI.endsWith("/")) {
			baseURI += "/";
		}
		try {
			this.baseURI = new URI(baseURI);
		} catch (URISyntaxException e) {
			IllegalArgumentException argE = new IllegalArgumentException("Invalid 'baseURI' arg [" + baseURI + "].  Not a valid URI.");
			argE.initCause(e);
			throw argE;
		}
		String scheme = this.baseURI.getScheme();
		if(!(this.baseURI.isAbsolute() && !this.baseURI.isOpaque() && 
				(scheme.equals("http") || scheme.equals("https")) )) {
			throw new IllegalArgumentException("Invalid 'baseURI' arg [" + baseURI + "].  Must be absolute, non-opaque with 'http' or 'https' scheme.");
		}
	}
	
	/**
	 * Set "SMOOKS_HOME" for this instance.
	 * <p/>
	 * Performs "SMOOKS_HOME" parameter validation.
	 * @param smooksHome "SMOOKS_HOME" value.
	 * @throws IllegalArgumentException
	 */
	private void setSmooksHome(String smooksHome) throws IllegalArgumentException {
		if(smooksHome == null) {
			throw new IllegalArgumentException("null 'smooksHome' arg in constructor call.");
		}
		this.smooksHome = new File(smooksHome);
		assertIsDirectory(this.smooksHome);
	}

	/**
	 * Assert whether or not the specified file is a directory or not.
	 * @param file File to perform assertion on.
	 * @throws IllegalArgumentException file is not a valid directory on the file system.
	 */
	public static void assertIsDirectory(File file) throws IllegalArgumentException {
		if(!file.exists()) {
			throw new IllegalArgumentException("No such file or directory [" + file + "].");
		}
		if(!file.isDirectory()) {
			throw new IllegalArgumentException("[" + file + "] is not a directory.");
		}
	}

	/**
	 * Run Smooks on the specified page.
	 * <p/>
	 * This will generate a report in the specified  
	 * {@link #SmooksReportGenerator(String, String, String, boolean, ReportPageWriterFactory) output folder}
	 * @param requestPage Start page.  Resolved against the 
	 * {@link #SmooksReportGenerator(String, String, String, boolean, ReportPageWriterFactory) baseURI}.  
	 * The resulting URI must be within the context of the 
	 * {@link #SmooksReportGenerator(String, String, String, boolean, ReportPageWriterFactory) baseURI}.
	 * @throws IOException Unable to read requestPage content.
	 * @throws SmooksException Excepting processing content stream.
	 */
	public void generateReport(String requestPage) throws IOException, SmooksException {
		URI requestPageURI = URI.create(requestPage);
		boolean run = true;
		
		// The supplied request page is the root of the test run.  As smooks
		// filters the root request page, the AHrefRecorder TransUnit records
		// the anchor elements.  This list of anchors is used to perform a 
		// deep test.
		try {
			indexWriter = writerFactory.getIndexPageWriter();
			indexWriter.writeTitle("Browser List");
			while(run) {
				String curBrowserName = smooksSA.getSession().getBrowserName();
				String curBrowserNameFN = ParameterAccessor.getStringParameter("friendly-name", curBrowserName, smooksSA.getSession().getDeliveryConfig());
				PageListWriter pageList;
				
				// Add a link to the to the top level index page for this browsers
				// pageList page.
				indexWriter.addBrowser(curBrowserName, curBrowserNameFN);
				// Create the index page for the current browser - contains links to
				// all the test pages for this browser. See javadoc at top of this file.
				pageList = writerFactory.getPageListWriter(curBrowserName);
				pageList.writeTitle(curBrowserNameFN);
				pageList.write(getClass().getResourceAsStream("serialize/page-list-info.html"));
				pageList.writeln("<div class='pagelist'>");
				
				// Start the test run.
				generateReport(requestPageURI, pageList, curBrowserName);
				run = false;
		
				// If tests are to be run "deep"...
				if(deep) {
					List ahrefList = SessionAHrefList.getList(smooksSA.getSession());
					
					for(int i = 0; i < ahrefList.size(); i++) {
						try {
							generateReport((URI)ahrefList.get(i), pageList, curBrowserName);
						} catch(IllegalArgumentException e) {
							SmooksLogger.getLog().warn("Not processing source of internal anchor href. " + e.getMessage());
						} catch(IOException e) {
							SmooksLogger.getLog().warn("Failed to process source of internal anchor href. [" + e.getClass().getName() + "] " + e.getMessage());
						}
					}
				}
				pageList.writeln("</div>");
				pageList.close();				
				while(browsers.hasMoreTokens()) {
					try {
						smooksSA.setBrowser(browsers.nextToken().trim());
						run = true;
						break;
					} catch (UnknownDeviceException e) {
						SmooksLogger.getLog().error(e);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Test run failed with unexpected exception.", e);
		} finally {
			if(indexWriter != null) {
				indexWriter.close();
			}
		}
	}

	/**
	 * Run Smooks on the specified page.
	 * <p/>
	 * This will generate a report in the specified  
	 * {@link #SmooksReportGenerator(String, String, String, boolean, ReportPageWriterFactory) output folder}
	 * @param requestPage Start page.  Resolved against the 
	 * {@link #SmooksReportGenerator(String, String, String, boolean, ReportPageWriterFactory) baseURI}.  
	 * The resulting URI must be within the context of the 
	 * {@link #SmooksReportGenerator(String, String, String, boolean, ReportPageWriterFactory) baseURI}.
	 * @param pageList The page list page for the current browser.
	 * @param curBrowserName Current browser name.
	 * @throws IOException Unable to read requestPage content.
	 * @throws SmooksException Excepting processing content stream.
	 */
	private void generateReport(URI requestPage, PageListWriter pageList, String curBrowserName) throws IOException, SmooksException {
		URI pageURI = resolveRequestURI(requestPage);
		Node doc = smooksSA.process(pageURI);
		StandaloneContainerRequest request = smooksSA.getLastRequest();
		PageReport report = PageReport.getInstance(request);
		PageReportWriter pageReportWriter;
		int nodeReportCount = report.getNodeReportEntries().size();

		logger.info("Running report on [" + requestPage + "] for browser [" + curBrowserName + "]");
		
		// Add a link for the request page report to the browsers pageList.
		String pagePath = pageList.addPage(pageURI, baseURI, nodeReportCount);
		pageReportWriter = writerFactory.getPageReportWriter(curBrowserName, pagePath);
		
		// Apply the report to the source page and generate the node reports.
		report.applyReport(curBrowserName, pagePath, baseURI, writerFactory);

		// Write the request page report for the page.
		pageReportWriter.writeTitle(pageURI.toString());
		pageReportWriter.writeSummary(report, pagePath);
		pageReportWriter.writeln("<div class='srccontent indented'>");
		smooksSA.serialize(doc, pageReportWriter.getWriter());
		pageReportWriter.writeln("</div>");
		pageReportWriter.close();
	}

	/**
	 * Resolve the supplied requestPage URI string against the 
	 * {@link #SmooksReportGenerator(String, String, String, boolean, ReportPageWriterFactory) baseURI}.
	 * <p/>
	 * Makes sure the supplied requestPage URI results in an absolute URI within the
	 * context of the {@link #SmooksReportGenerator(String, String, String, boolean, ReportPageWriterFactory) baseURI}.
	 * @param requestPage Request Page URI String.
	 * @return Resolved request URI.
	 */
	protected URI resolveRequestURI(String requestPage) {
		return resolveRequestURI(URI.create(requestPage));
	}

	/**
	 * Resolve the supplied requestPage URI string against the 
	 * {@link #SmooksReportGenerator(String, String, String, boolean, ReportPageWriterFactory) baseURI}.
	 * <p/>
	 * Makes sure the supplied requestPage URI results in an absolute URI within the
	 * context of the {@link #SmooksReportGenerator(String, String, String, boolean, ReportPageWriterFactory) baseURI}.
	 * @param requestPage Request Page URI.
	 * @return Resolved request URI.
	 */
	private URI resolveRequestURI(URI requestPage) {
		URI pageURI;
		if(requestPage == null) {
			throw new IllegalArgumentException("null 'requestPage' arg in method call.");
		}
		pageURI = baseURI.resolve(requestPage);
		if(!pageURI.toString().startsWith(baseURI.toString())) {
			throw new IllegalArgumentException("'requestPage' [" + requestPage + "] must be within the context of the baseURI [" + baseURI + "].");
		}
		return pageURI;
	}
	
	/**
	 * Command line execution.
	 * @param argsMap Command line argsMap.
	 * @throws SmooksException Exception processing content while running report.
	 * @throws IOException Exception while generating report output.
	 */
	public static void main(String[] args) throws IOException, SmooksException {
		CommandLineArgsMap argsMap = new CommandLineArgsMap(args);
		try {
			String browserList = argsMap.getArgValue("-l", true);
			boolean deep = argsMap.isSet("-r", false);
			String outputFolder = argsMap.getArgValue("-d", true);
			String encoding = argsMap.getArgValue("-e", false);
			String baseURI = argsMap.getArgValue("-b", true);
			String start = argsMap.getArgValue("-s", true);
			SmooksReportGenerator reportGenerator;
			FileSystemReportPageWriterFactory writerFactory;

			writerFactory = new FileSystemReportPageWriterFactory(outputFolder, encoding);
			reportGenerator = new SmooksReportGenerator(baseURI, browserList, encoding, deep, writerFactory);
			reportGenerator.generateReport(start);
		} catch(IllegalArgumentException e) {
			System.out.println("\n\n");
			System.out.println("ERROR: Missing or invalid command line argument: " + e.getMessage());
			System.out.println("USAGE:");
			System.out.println("\t[SMOOKS_HOME]/bin/report -l [comma-sep-browser-list] -e [encoding] -r -d [output-folder] -b [base-URL] -s [start-page]");
			System.out.println("\t\t-e\tencoding: Content encoding - defaults to ISO-8859-1.");
			System.out.println("\t\t-r\trecursive: Perform a deep test run - defaults to off.");
			System.out.println("\n\n");
		}		
	}
	
	protected static class CommandLineArgsMap {
		private HashMap argsMap = new HashMap();
		
		CommandLineArgsMap(String[] args) {
			int i = 0;
			while(i < args.length) {
				if(args[i].charAt(0) == '-') {
					if(i + 1 < args.length && args[i + 1].charAt(0) != '-') {
						argsMap.put(args[i], args[i + 1]);
					} else {
						argsMap.put(args[i], new Boolean(true));
					}
				}
				i++;
			}
		}
		
		protected String getArgValue(String name, boolean isMandatory) {
			Object argVal = argsMap.get(name);
			
			if(argVal == null) {
				if(isMandatory) {
					throw new IllegalArgumentException("arg '" + name + "' not set.");
				}
				return null;
			}
			if(argVal instanceof String) {
				return (String)argVal;
			}
			throw new UnsupportedOperationException("Call to getArgValue for non value arg '" + name + "' i.e. this arg is just an on/off switch.");
		}
		
		protected boolean isSet(String name, boolean isMandatory) {
			boolean isSet = (argsMap.get(name) != null);
			
			if(isMandatory && !isSet) {
				throw new IllegalArgumentException("arg '" + name + "' not set.");
			}
			
			return isSet;
		}
	}
}
