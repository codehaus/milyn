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

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.milyn.io.StreamUtils;
import org.milyn.logging.SmooksLogger;

/**
 * Page report writer base class.
 * @author tfennelly
 */
abstract class AbstractPageWriter {

	private Writer writer;

	/**
	 * Protected constructor.
	 * @param outputFile File to be written out to.
	 * @param encoding Character encoding for output file.
	 */
	protected AbstractPageWriter(Writer writer) {
		if(writer == null) {
			throw new IllegalArgumentException("null 'writer' arg in method call.");
		}
		this.writer = writer;
	}
	
	/**
	 * Write the content of the supplied String to the report.
	 * @param stream String whose content is to be writen to the report.
	 */
	public void write(String string) {
		try {
			writer.write(string);
			writer.flush();
		} catch (Exception e) {
			throwStateException(e);
		}
	}

	
	/**
	 * Write the content of the supplied String to the report, followed by a newline character.
	 * @param stream String whose content is to be writen to the report.
	 */
	public void writeln(String string) {
		try {
			writer.write(string);
			writer.write('\n');
			writer.flush();
		} catch (Exception e) {
			throwStateException(e);
		}
	}

	/**
	 * Write the content of the supplied stream to the report.
	 * @param stream Stream whose content is to be writen to the report.
	 */
	public void write(InputStream stream) {
		write(stream, null);
	}

	/**
	 * Write the content of the supplied stream to the report.
	 * @param stream Stream whose content is to be writen to the report.
	 */
	public void write(InputStream stream, String encoding) {
		if(stream == null) {
			throw new IllegalArgumentException("null 'stream' arg in method call.");
		}
		try {
			byte[] readBytes = StreamUtils.readStream(stream);
			writer.write(new String(readBytes, (encoding == null?"ISO-8859-1":encoding)));
		} catch (Exception e) {
			throwStateException(e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				SmooksLogger.getLog().error("ERROR closing stream.", e);
			}
		}
	}

	public void writeTitle(String title) {
		write("<div class='pagetitle'>");
		write(title);
		write("</div>\r\n");
	}
	
	/**
	 * Close the report writer IO resources.
	 */
	public void close() {
		try {
			write(getClass().getResourceAsStream("footer.html"));
			writer.flush();
			writer.close();
		} catch (Exception e) {
			throwStateException(e);
		} 
	}

	/**
	 * @param cause
	 * @throws IllegalStateException
	 */
	private void throwStateException(Exception cause) throws IllegalStateException {
		IllegalStateException state = new IllegalStateException("Invalid configuration. Unable to write page report.");
		state.initCause(cause);
		throw state;
	}

	/**
	 * Get the underlying writer instance.
	 * @return Page writer {@link Writer}.
	 */
	public Writer getWriter() {
		return writer;
	}
}
