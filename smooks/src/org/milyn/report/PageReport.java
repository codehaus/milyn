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

import java.net.URI;
import java.util.List;
import java.util.Vector;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.report.serialize.NodeReportWriter;
import org.milyn.report.serialize.ReportPageWriterFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Page Test Report class.
 * <p/>
 * Only one instance of this class is constructed per {@link org.milyn.container.ContainerRequest}.
 * @author tfennelly
 */
public class PageReport {
	
	private static final String REQUEST_TESTREPORT_KEY = PageReport.class.getName() + "#REQUEST_TESTREPORT_KEY";
	private Vector reportEntries = new Vector();
	private int reportLength = 0;
	private ContainerRequest containerRequest;
	
	/**
	 * Private Constructor.
	 * @param containerRequest Request.
	 */
	private PageReport(ContainerRequest containerRequest) {
		this.containerRequest = containerRequest;
	}

	/**
	 * Get the PageReport instance for the supplied request.
	 * @param containerRequest Container request.
	 * @return The PageReport instance for the supplied request.
	 */
	public static PageReport getInstance(ContainerRequest containerRequest) {
		PageReport testReport = null;
		
		if(containerRequest == null) {
			throw new IllegalArgumentException("null 'containerRequest' arg in method call.");
		}
		
		testReport = (PageReport)containerRequest.getAttribute(REQUEST_TESTREPORT_KEY);
		if(testReport == null) {
			testReport = new PageReport(containerRequest);
			containerRequest.setAttribute(REQUEST_TESTREPORT_KEY, testReport);
		}
		
		return testReport;
	}

	/**
	 * Add the report configuration for the supplied DOM node to this page report. 
	 * @param node The node to be associated with the report message entry.
	 * @param cdrDef Report configuration.  The test unit configuration
	 * will contain {@link CDRDef.Parameter parameters} used in the report.
	 */
	public void report(Node node, CDRDef cdrDef) {
		NodeReport reportEntry = getNodeReport(node);
		
		reportEntry.addReportConfig(cdrDef);
		reportLength++;
	}

	private NodeReport getNodeReport(Node node) {
		NodeReport reportEntry = null;
		
		for(int i = 0; i < reportEntries.size(); i++) {
			Object entry = reportEntries.get(i); 
			if(entry.equals(node)) {
				// Element already has an entry - retrieve and use it.
				reportEntry = (NodeReport)entry;
				break;
			}
		}

		if(reportEntry == null) {
			// No entry for this node.  Create the entry for the supplied node.
			reportEntry = new NodeReport(node);
			reportEntries.add(reportEntry);
		}
		
		return reportEntry;
	}

	/**
	 * Get the list of reportEntries ({@link PageReport.NodeReport}s) that have been {@link #report(Node, String) reported} 
	 * on this page report.
	 * @return The list of reportEntries ({@link PageReport.NodeReport}s) reported on this page.
	 */
	public List getNodeReportEntries() {
		return reportEntries;
	}

	/**
	 * Get the number of reports added on this page report.
	 * @return The report length.
	 */
	public int getReportLength() {
		return reportLength;
	}

	/**
	 * Apply the details of the report.
	 * <p/>
	 * This is basically adding of popup hyperlinks for each of the node reports.
	 * Writes each of the {@link PageReport.NodeReport}s using a 
	 * {@link org.milyn.report.serialize.NodeReportWriter}. 
	 * @param browserName The browser name for which the report is being written.
	 * @param pagePath The pagePath for the source test page i.e. the page from 
	 * which the report was generated. 
	 * @param baseURI The base URI under which this report was generated.
	 * @param writerFactory The {@link ReportPageWriterFactory} instance used to
	 * create the {@link org.milyn.report.serialize.NodeReportWriter} instances.
	 */
	public void applyReport(String browserName, String pagePath, URI baseURI, ReportPageWriterFactory writerFactory) {
		int reportSize = reportEntries.size();
		
		for(int i = 0; i < reportSize; i++) {
			NodeReport nodeReport = (NodeReport)reportEntries.get(i);
			Node node = nodeReport.getNode();
			Document doc = node.getOwnerDocument();
			Element nodeReportElement = doc.createElement("nodereport");
			Node parent = node.getParentNode();
			String nodeReportPath = NodeReportWriter.getPath(pagePath, i + 1);
			NodeReportWriter nodeReportWriter;
			int nodeReportCount = nodeReport.getReportCount();
			
			// Add the "nodereport" element.  This will get serialized as an
			// <a href>.  We don't add this as an <a href> now because we want 
			// to serialize the source doc such that it's "HTML'ified" with the
			// exception of this nodereport which we'll serialise as an <a href>.
			parent.insertBefore(nodeReportElement, node);
			nodeReportElement.setAttribute("id", nodeReportPath);
			nodeReportElement.setAttribute("href", nodeReportPath);
			nodeReportElement.setAttribute("onClick", "return popup(this)");
			nodeReportElement.setAttribute("img-title", "The following node has " + nodeReportCount + " report item" + (nodeReportCount>1?"s":"") + ".  Click for detail...");
			if(i + 1 < reportSize) {
				String nextNodeReportPath = NodeReportWriter.getPath(pagePath, i + 2);
				nodeReportElement.setAttribute("next-id", nextNodeReportPath);
			}

			// Generate the node report itself.  This will be "popped up" from
			// the HTML'ified source page.
			nodeReportWriter = writerFactory.getNodeReportWriter(browserName, nodeReportPath, i + 1);
			String relPath = containerRequest.getRequestURI().toString().substring(baseURI.toString().length());
			nodeReportWriter.writeSource(relPath, nodeReport.getNode());
			nodeReportWriter.write(nodeReport);
			nodeReportWriter.close();
		}
	}
	
	/**
	 * An individual node report entry.
	 * <p/>
	 * This class stores all the reports made against a particular DOM node.
	 * @author tfennelly
	 */
	public static class NodeReport {
		
		private Node node;
		private List cdrDefs = new Vector();
		
		/**
		 * Private constructor.
		 * @param node The node to be associated with the report entry.
		 */
		private NodeReport(Node node) {
			if(node == null) {
				throw new IllegalArgumentException("null 'node' arg in constructor call.");
			}
			this.node = node;
		}

		/**
		 * Add a report entry.
		 * @param cdrDef Test unit configuration.  The test unit configuration
		 * will contain {@link CDRDef.Parameter parameters} used in the report.
		 */
		public void addReportConfig(CDRDef cdrDef) {
			if(cdrDef == null) {
				throw new IllegalArgumentException("null 'cdrDef' arg in method call.");
			}
			cdrDefs.add(cdrDef);
		}

		/**
		 * Get the DOM node associated with this node report entry .
		 * @return Report entry DOM node.
		 */
		public Node getNode() {
			return node;
		}

		/**
		 * Get the number of report entries on associated node.
		 * @return Report count.
		 */
		public int getReportCount() {
			return cdrDefs.size();
		}		

		/**
		 * Get the report configuration with the specified index (base 0).
		 * <p/>
		 * Based on the order in which the cdrDefs were added via {@link PageReport#report(Node, CDRDef)}.
		 * @return The config.
		 */
		public CDRDef getReportConfig(int index) {
			return (CDRDef)cdrDefs.get(index);
		}		

		/**
		 * Compare Object to this NodeReport.
		 * <p/>
		 * Equal if:<br/>
		 * 1. The object is an instance of {@link Node} and the object reference equals
		 * this entries {@link #getNode() node} member reference.<br/>
		 * 2. The object is an instance of this class and its {@link #getNode() node}
		 * reference is equal to the {@link #getNode() node} reference of this instance.
		 */
		public boolean equals(Object obj) {
			if(obj == node) {
				return true;
			} else if(obj instanceof NodeReport) {
				if(((NodeReport)obj).node == node) {
					return true;
				}
			}
			
			return false;
		}
	}
}
