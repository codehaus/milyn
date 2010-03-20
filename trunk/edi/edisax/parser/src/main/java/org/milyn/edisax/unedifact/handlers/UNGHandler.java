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
import org.milyn.edisax.unedifact.UNEdifactUtil;
import org.xml.sax.SAXException;

/**
 * UNG Segment Handler.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNGHandler implements ControlBlockHandler {

	public void process(InterchangeContext interchangeContext) throws IOException, SAXException {
		BufferedSegmentReader segmentReader = interchangeContext.getSegmentReader();

		segmentReader.moveToNextSegment();
        while(true) {
	        String segCode = segmentReader.read(3);
	        
	        if(segCode.equals("UNE")) {
	    		segmentReader.moveToNextSegment();
	    		break;
	        } else {	        	
	        	ControlBlockHandler handler = UNEdifactUtil.getControlBlockHandler(segCode);
	        	handler.process(interchangeContext);
	        }
        }		
	}
}
