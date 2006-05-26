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

/**
 * Report page writer factory interface definition.
 * <p/>
 * The report is made up of a number of pages.
 * @author tfennelly
 */
public interface ReportPageWriterFactory {

	public static final String BROWSER_PAGELIST_INDEXPAGE = "srpagelist.html";

	/**
	 * Create the top level index.html page writer.
	 * @return Page writer.
	 */
	public abstract IndexPageWriter getIndexPageWriter();

	/**
	 * Create the writer for the supplied browsers list page.
	 * <p/>
	 * This is the page that lists all the pages being reported on (tested) for this
	 * browser.
	 * @param browserName The browser name.
	 * @return The writer.
	 */
	public abstract PageListWriter getPageListWriter(String browserName);

	/**
	 * Create a writer for a {@link org.milyn.report.PageReport} for the supplied 
	 * browser.
	 * <p/>
	 * This page will contain the "HTML'ified" source page which will include
	 * hyperlinks to whatever {@link NodeReportWriter element reports} were 
	 * generated for that page. 
	 * @param browserName The browser name.
	 * @param pagePath The page path relative to the baseURI.
	 * @return The writer.
	 */
	public abstract PageReportWriter getPageReportWriter(String browserName, String pagePath);

	/**
	 * Create a writer for a {@link org.milyn.report.PageReport.NodeReport} for the specified browser,
	 * page and node number.
	 * @param browserName Browser name.
	 * @param pagePath Test page path.
	 * @param nodeReportNumber Node report number.  Potentially multiple {@link org.milyn.report.PageReport.NodeReport}s
	 * per page.
	 * @return The writer.
	 */
	public abstract NodeReportWriter getNodeReportWriter(String browserName, String pagePath, int nodeReportNumber);
}