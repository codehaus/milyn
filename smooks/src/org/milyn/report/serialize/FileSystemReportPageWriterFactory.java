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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.milyn.io.StreamUtils;
import org.milyn.logging.SmooksLogger;
import org.milyn.report.SmooksReportGenerator;

import sun.io.CharToByteConverter;

/**
 * ReportPageWriterFactory implementation for outputing the report
 * to the file system.
 * <p/>
 * Generates a report in the following format:
 * <pre>
 *	&lt;outputFolder&gt;
 *	|
 *	|--index.html (list of browsers tested - see {@link org.milyn.report.serialize.IndexPageWriter})
 *		|
 *		|--&lt;browser_<i>A</i>&gt;
 *			|
 *			|--index.html (list of pages tested on browser_<i>A</i> - see {@link org.milyn.report.serialize.PageListWriter})
 *			|--&lt;page<i>X</i>.html&gt; (see {@link org.milyn.report.serialize.PageReportWriter})
 *			|--&lt;page<i>X</i>_node1.html&gt; (popped up from page_1.html - see {@link org.milyn.report.serialize.NodeReportWriter})
 *			|--&lt;page<i>X</i>_node2.html&gt;
 *			|--&lt;page<i>X</i>_node3.html&gt;
 *			|--&lt;page<i>Y</i>.html&gt;
 *			|--&lt;page<i>Y</i>_node1.html&gt;
 *			|--&lt;page<i>Y</i>_node2.html&gt;
 *			|--etc...
 *		|
 *		|--&lt;browser_<i>B</i>&gt;
 *			|
 *			|--index.html 
 * </pre>
 * @author tfennelly
 */
public class FileSystemReportPageWriterFactory implements ReportPageWriterFactory {

	private File outputFolder;
	private String contentEncoding;

	public FileSystemReportPageWriterFactory(String outputFolder, String contentEncoding) {
		setOutputFolder(outputFolder);
		if(contentEncoding != null) {
			try {
				CharToByteConverter.getConverter(contentEncoding);
			} catch (UnsupportedEncodingException e) {
				IllegalArgumentException argE = new IllegalArgumentException("Invalid 'contentEncoding' arg [" + contentEncoding + "].  This encoding is not supported.");
				argE.initCause(e);
				throw argE;
			}
			this.contentEncoding = contentEncoding;
		} else {
			this.contentEncoding = "ISO-8859-1";
		}
		copyClasspathFile("style.css");
		copyClasspathFile("warn.gif");
		copyClasspathFile("down.gif");
	}
	
	/**
	 * Copies a file from the classpath to the report output folder.
	 * @param classpath File classpath.
	 */
	private void copyClasspathFile(String classpath) {
		OutputStream os = null;
		try {
			byte[] bytes = StreamUtils.readStream(getClass().getResourceAsStream(classpath));
			File src = new File(classpath);
			
			os = new FileOutputStream(new File(outputFolder, src.getName()));			
			os.write(bytes);
		} catch (IOException e) {
			SmooksLogger.getLog().error(e);
			throwStateException("Failed to copy report output file during classpath file copy [" + classpath + "].", e);
		} finally {
			if(os != null) {
				try {
					os.close();
				} catch (IOException e) {
					SmooksLogger.getLog().error(e);
					throwStateException("Failed to close report output file during classpath file copy [" + classpath + "].", e);
				}
			}
		}
	}

	private void throwStateException(String message, Throwable cause) {
		IllegalStateException state = new IllegalStateException(message);
		state.initCause(cause);
		throw state;
	}

	/**
	 * Set output folder for this instance.
	 * <p/>
	 * Performs 'outputFolder' parameter validation.
	 * @param outputFolder The 'outputFolder' value.
	 * @throws IllegalArgumentException  'outputFolder' is null or an invalid folder spec.
	 */
	private void setOutputFolder(String outputFolder) throws IllegalArgumentException {
		if(outputFolder == null) {
			throw new IllegalArgumentException("null 'outputFolder' arg in constructor call.");
		}
		this.outputFolder = new File(outputFolder);
		SmooksReportGenerator.assertIsDirectory(this.outputFolder);
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.report.serialize.ReportPageWriterFactory#getIndexPageWriter()
	 */
	public IndexPageWriter getIndexPageWriter() {
		Writer writer = getWriter(new File(outputFolder, "index.html"));		
		return new IndexPageWriter(writer);
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.report.serialize.ReportPageWriterFactory#getPageListWriter(java.lang.String)
	 */
	public PageListWriter getPageListWriter(String browserName) {
		Writer writer = getWriter(new File(outputFolder, browserName + "/index.html"));		
		return new PageListWriter(writer);
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.report.serialize.ReportPageWriterFactory#getPageReportWriter(java.lang.String, java.lang.String)
	 */
	public PageReportWriter getPageReportWriter(String browserName, String pagePath) {
		Writer writer = getWriter(new File(outputFolder, browserName + "/" + pagePath));		
		return new PageReportWriter(writer);
	}	
	
	/* (non-Javadoc)
	 * @see org.milyn.report.serialize.ReportPageWriterFactory#getWarningReportWriter(java.lang.String, java.lang.String, int)
	 */
	public NodeReportWriter getNodeReportWriter(String browserName, String pagePath, int nodeReportNumber) {
		Writer writer = getWriter(new File(outputFolder, browserName + NodeReportWriter.getPath(pagePath, nodeReportNumber)));		
		return new NodeReportWriter(writer);
	}

	/**
	 * Construct a writer to the specified file.
	 * <p/>
	 * Creates the parent folders if required.
	 * @param file File to which the Writer is requred.
	 * @return Writer instance to the specified file.
	 */
	private Writer getWriter(File file) {
		Writer writer = null;
		
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			writer = new OutputStreamWriter(fos, contentEncoding);
		} catch (IOException e) {
			throwStateException(e);
		}
		
		return writer;
	}

	/**
	 * @param cause
	 * @throws IllegalStateException
	 */
	private void throwStateException(Exception cause) throws IllegalStateException {
		IllegalStateException state = new IllegalStateException("Invalid configuration. Unable to create page report writer.");
		state.initCause(cause);
		throw state;
	}
}
