/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.edisax.interchange;

import java.io.IOException;
import java.util.Map;

import org.milyn.edisax.BufferedSegmentReader;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Description;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * UN/EDIFACT control block handler.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface ControlBlockHandler {

	/**
	 * Process a UN/EDIFACT control block.
	 * @param interchangeContext The interchange context.
     * @throws SAXException EDI processing exception.
	 */
	void process(InterchangeContext interchangeContext)  throws IOException, SAXException;
}
