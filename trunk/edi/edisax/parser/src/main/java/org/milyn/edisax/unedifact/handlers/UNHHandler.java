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
import org.milyn.edisax.model.internal.Component;
import org.milyn.edisax.model.internal.Description;
import org.milyn.edisax.model.internal.Field;
import org.milyn.edisax.model.internal.Segment;
import org.milyn.edisax.unedifact.UNEdifactUtil;
import org.xml.sax.SAXException;

/**
 * UNH Segment Handler.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNHHandler implements ControlBlockHandler {

	private Segment unhSegment;
	private Segment untSegment;
	
	public UNHHandler() {
		createSegmentsDefs();
	}
	
	private UNTSegmentListener untSegmentListener = new UNTSegmentListener();

	public void process(InterchangeContext interchangeContext) throws IOException, SAXException {
		BufferedSegmentReader segmentReader = interchangeContext.getSegmentReader();
		Map<Description, EdifactModel> mappingModels = interchangeContext.getMappingModels();
		
		interchangeContext.getControlSegmentParser().startElement("unEdifactMessage", true);

		// Move to the end of the UNH segment and map it's fields..
		segmentReader.moveToNextSegment(false);
		interchangeContext.mapControlSegment(unhSegment, false);
		
		// Select the mapping model to use for this message...
		String[] fields = segmentReader.getCurrentSegmentFields();
		String messageName = fields[2];
		EdifactModel mappingModel = UNEdifactUtil.getMappingModel(messageName, segmentReader.getDelimiters(), mappingModels);
		
		// Map the message... stopping at the UNT segment...
		try {
			EDIParser parser = interchangeContext.newParser(mappingModel);
			
			segmentReader.setSegmentListener(untSegmentListener);
			parser.parse();
		} finally {
			segmentReader.setSegmentListener(null);
		}		
		
		// We're at the end of the UNT segment now.  See the UNTSegmentListener below.
		
		// Map the UNT segment...
		interchangeContext.mapControlSegment(untSegment, true);
		segmentReader.getSegmentBuffer().setLength(0);

		interchangeContext.getControlSegmentParser().endElement("unEdifactMessage", true);
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

	private void createSegmentsDefs() {
		// UNH Segment Definition...
		// http://www.gefeg.com/jswg/v41/se/se16.htm
		unhSegment = new Segment();
		unhSegment.setSegcode("UNH");
		unhSegment.setXmltag("UNH");
		unhSegment.setDescription("UNH - Message Header");
		unhSegment.setTruncatable(true);
		unhSegment.addField(new Field("messageRefNum", true));
		unhSegment.addField(new Field("messageIdentifier", true).
                addComponent(new Component("id", true)).
                addComponent(new Component("versionNum", true)).
                addComponent(new Component("releaseNum", true)).
                addComponent(new Component("controllingAgencyCode", true)).
                addComponent(new Component("associationAssignedCode", false)).
                addComponent(new Component("codeListDirVersionNum", false)).
                addComponent(new Component("typeSubFunctionId", false)));
		unhSegment.addField(new Field("commonAccessRef", false));
		unhSegment.addField(new Field("transferStatus", false).
                addComponent(new Component("sequence", true)).
                addComponent(new Component("firstAndLast", false)));
		unhSegment.addField(new Field("subset", false).
                addComponent(new Component("id", true)).
                addComponent(new Component("versionNum", false)).
                addComponent(new Component("releaseNum", false)).
                addComponent(new Component("controllingAgencyCode", false)));
		unhSegment.addField(new Field("implementationGuideline", false).
                addComponent(new Component("id", true)).
                addComponent(new Component("versionNum", false)).
                addComponent(new Component("releaseNum", false)).
                addComponent(new Component("controllingAgencyCode", false)));
		unhSegment.addField(new Field("scenario", false).
                addComponent(new Component("id", true)).
                addComponent(new Component("versionNum", false)).
                addComponent(new Component("releaseNum", false)).
                addComponent(new Component("controllingAgencyCode", false)));

		// UNT Segment Definition...
		// http://www.gefeg.com/jswg/v41/se/se20.htm
		untSegment = new Segment();
		untSegment.setSegcode("UNT");
		untSegment.setXmltag("UNT");
		untSegment.setDescription("UNT - Message Trailer");
		untSegment.setTruncatable(true);
		untSegment.addField(new Field("segmentCount", true));
		untSegment.addField(new Field("messageRefNum", true));
	}
}
