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
import org.milyn.edisax.model.internal.Description;
import org.milyn.edisax.unedifact.ControlBlockHandler;
import org.milyn.edisax.unedifact.UNEdifactUtil;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * UNG Segment Handler.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNGHandler implements ControlBlockHandler {

	/* (non-Javadoc)
	 * @see org.milyn.edisax.unedifact.ControlBlockHandler#process(org.milyn.edisax.BufferedSegmentReader, java.util.Map, org.xml.sax.ContentHandler)
	 */
	public void process(BufferedSegmentReader segmentReader, Map<Description, EdifactModel> mappingModels, ContentHandler contentHandler) throws IOException, SAXException {
		segmentReader.moveToNextSegment();
        while(true) {
	        String segCode = segmentReader.read(3);
	        
	        if(segCode.equals("UNE")) {
	    		segmentReader.moveToNextSegment();
	    		break;
	        } else {	        	
	        	ControlBlockHandler handler = UNEdifactUtil.getControlBlockHandler(segCode);
	        	handler.process(segmentReader, mappingModels, contentHandler);
	        }
        }		
	}
}
