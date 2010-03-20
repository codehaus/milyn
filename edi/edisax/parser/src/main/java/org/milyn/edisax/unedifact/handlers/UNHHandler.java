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

import org.milyn.edisax.BufferedSegmentListener;
import org.milyn.edisax.BufferedSegmentReader;
import org.milyn.edisax.EDIParser;
import org.milyn.edisax.interchange.ControlBlockHandler;
import org.milyn.edisax.interchange.InterchangeContext;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Description;
import org.milyn.edisax.unedifact.UNEdifactUtil;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * UNH Segment Handler.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNHHandler implements ControlBlockHandler {
	
	private UNTSegmentListener untSegmentListener = new UNTSegmentListener();

	public void process(InterchangeContext interchangeContext) throws IOException, SAXException {
		BufferedSegmentReader segmentReader = interchangeContext.getSegmentReader();
		Map<Description, EdifactModel> mappingModels = interchangeContext.getMappingModels();
		ContentHandler contentHandler = interchangeContext.getContentHandler();

		// Move to the end of the UNH segment i.e. start of the message..
		segmentReader.moveToNextSegment();
		
		// Process the message based on message name in the UNH fields...
		
		String[] fields = segmentReader.getCurrentSegmentFields();
		
		if(fields.length < 3) {
			throw new SAXException("Invalid UNH segment [" + segmentReader.getSegmentBuffer() + "]. Unable to access message name.");
		}
		
		String messageName = fields[2];
		EdifactModel mappingModel = UNEdifactUtil.getMappingModel(messageName, segmentReader.getDelimiters(), mappingModels);
		
		try {
			EDIParser parser = new EDIParser();
			
			parser.setContentHandler(contentHandler);
			parser.setMappingModel(mappingModel);
			parser.setBufferedSegmentReader(segmentReader);
			parser.setIndentDepth(interchangeContext.indentDepth);
			
			segmentReader.setSegmentListener(untSegmentListener);
			interchangeContext.indentDepth.value++;
			parser.parse();
			interchangeContext.indentDepth.value--;
		} finally {
			segmentReader.setSegmentListener(null);
		}		
		
		// We're at the end of the UNT segment now.  See the UNTSegmentListener below...
	}
	
	private class UNTSegmentListener implements BufferedSegmentListener {

		/* (non-Javadoc)
		 * @see org.milyn.edisax.BufferedSegmentListener#onSegment(java.lang.StringBuffer)
		 */
		public boolean onSegment(BufferedSegmentReader bufferedSegmentReader) {
			String[] fields = bufferedSegmentReader.getCurrentSegmentFields();
			
			// Stop the current segment consumer if we have reached the UNT segment i.e.
			// only return true if it's not UNT...
			return !fields[0].equals("UNT");
		}		
	}
}
