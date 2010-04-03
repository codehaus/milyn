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

import org.milyn.edisax.BufferedSegmentReader;
import org.milyn.edisax.interchange.ControlBlockHandler;
import org.milyn.edisax.interchange.InterchangeContext;
import org.milyn.edisax.model.internal.Component;
import org.milyn.edisax.model.internal.Field;
import org.milyn.edisax.model.internal.Segment;
import org.milyn.edisax.unedifact.UNEdifactUtil;
import org.xml.sax.SAXException;

/**
 * UNB Segment Handler.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNBHandler implements ControlBlockHandler {
	
	private Segment unbSegment;
	private Segment unzSegment;
	
	public UNBHandler() {
		createSegmentsDefs();
	}

	public void process(InterchangeContext interchangeContext) throws IOException, SAXException {
		BufferedSegmentReader segmentReader = interchangeContext.getSegmentReader();
		
		segmentReader.moveToNextSegment(false);
		interchangeContext.mapControlSegment(unbSegment, true);
		
        while(true) {
	        String segCode = segmentReader.peek(3);
	        
	        if(segCode.equals("UNZ")) {
	    		segmentReader.moveToNextSegment(false);
	    		interchangeContext.mapControlSegment(unzSegment, true);
	    		break;
	        } else {	        	
	        	ControlBlockHandler handler = UNEdifactUtil.getControlBlockHandler(segCode);
	        	handler.process(interchangeContext);
	        }
        }		
	}

	private void createSegmentsDefs() {
		// UNB Segment Definition...
		// http://www.gefeg.com/jswg/v41/se/se13.htm
		unbSegment = new Segment();
		unbSegment.setSegcode("UNB");
		unbSegment.setXmltag("UNB");
		unbSegment.setDescription("UNB - Interchange Header");
		unbSegment.setTruncatable(true);
		unbSegment.addField(new Field("syntaxIdentifier",               true).
                addComponent(new Component("id",                        true)).
                addComponent(new Component("version",                   true)).
                addComponent(new Component("serviceCodeListDirVersion", false)).
                addComponent(new Component("codedCharacterEncoding",    false)).
                addComponent(new Component("releaseNum",                false)));
		unbSegment.addField(new Field("sender",             true).
                addComponent(new Component("id",            true)).
                addComponent(new Component("codeQualifier", false)).
                addComponent(new Component("internalId",    false)).
                addComponent(new Component("internalSubId", false)));
		unbSegment.addField(new Field("recipient",          true).
                addComponent(new Component("id",            true)).
                addComponent(new Component("codeQualifier", false)).
                addComponent(new Component("internalId",    false)).
                addComponent(new Component("internalSubId", false)));
		unbSegment.addField(new Field("dateTime",  true).
                addComponent(new Component("date", true)).
                addComponent(new Component("time", true)));
		unbSegment.addField(new Field("controlRef",   true));
		unbSegment.addField(new Field("recipientRef",      false).
                addComponent(new Component("ref",          true)).
                addComponent(new Component("refQualifier", false)));
		unbSegment.addField(new Field("applicationRef", false));
		unbSegment.addField(new Field("processingPriorityCode", false));
		unbSegment.addField(new Field("ackRequest", false));
		unbSegment.addField(new Field("agreementId", false));
		unbSegment.addField(new Field("testIndicator", false));

		// UNZ Segment Definition...
		// http://www.gefeg.com/jswg/v41/se/se21.htm
		unzSegment = new Segment();
		unzSegment.setSegcode("UNZ");
		unzSegment.setXmltag("UNZ");
		unzSegment.setDescription("UNZ - Interchange Trailer");
		unzSegment.setTruncatable(true);
		unzSegment.addField(new Field("controlCount", true));
		unzSegment.addField(new Field("controlRef", true));
	}
}
