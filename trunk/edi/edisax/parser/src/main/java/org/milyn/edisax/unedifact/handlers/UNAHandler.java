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
package org.milyn.edisax.unedifact.handlers;

import java.io.IOException;
import java.util.Map;

import org.milyn.edisax.BufferedSegmentReader;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.edisax.model.internal.Description;
import org.milyn.edisax.unedifact.ControlBlockHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * UNA Segment Handler.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNAHandler implements ControlBlockHandler {

	/* (non-Javadoc)
	 * @see org.milyn.edisax.unedifact.ControlBlockHandler#process(org.milyn.edisax.BufferedSegmentReader, java.util.Map, org.xml.sax.ContentHandler)
	 */
	public void process(BufferedSegmentReader segmentReader, Map<Description, EdifactModel> mappingModels, ContentHandler contentHandler) throws IOException, SAXException {
		Delimiters delimiters = new Delimiters();

		// Read the delimiter chars one-by-one and set in the Delimiters instance...
		
		// 1st char is the component ("sub-element") delimiter...
		delimiters.setComponent( segmentReader.read(1));
		// 2nd char is the field ("data-element") delimiter...
		delimiters.setField(     segmentReader.read(1));
		// 3rd char is the decimal point indicator... ignoring for now...
		segmentReader.read(1);
		// 4th char is the escape char ("release")...
		delimiters.setEscape(    segmentReader.read(1));
		// 5th char is reserved for future use...
		segmentReader.read(1);
		// 6th char is the segment delimiter...
		delimiters.setSegment(   segmentReader.read(1));

		segmentReader.pushDelimiters(delimiters);
	}
}
