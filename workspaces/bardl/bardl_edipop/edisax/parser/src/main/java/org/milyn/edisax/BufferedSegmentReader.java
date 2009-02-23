/*
	Milyn - Copyright (C) 2006

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

package org.milyn.edisax;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.schema.edi_message_mapping_1_0.Delimiters;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Buffered EDI Stream Segment reader.
 * @author tfennelly
 */
class BufferedSegmentReader {

    private static Log logger = LogFactory.getLog(BufferedSegmentReader.class);

    private Reader reader;
    private StringBuffer segmentBuffer = new StringBuffer(512);
    private String[] currentSegmentFields = null;
	private int currentSegmentNumber = 0;
    private Delimiters delimiters;
	private char[] segmentDelimiter;

    /**
     * Construct the stream reader.
     * @param ediInputSource EDI Stream input source.
     * @param segmentDelimiter Segment delimiter String.
     */
    protected BufferedSegmentReader(InputSource ediInputSource, Delimiters delimiters) {
        reader = ediInputSource.getCharacterStream();
        if(reader == null) {
            reader = new InputStreamReader(ediInputSource.getByteStream());
        }
        this.delimiters = delimiters;
        this.segmentDelimiter = delimiters.getSegment().toCharArray();
    }
    
    /**
     * Move to the next EDI segment.
     * <p/>
     * Simply reads and buffers the next EDI segment.
     * @return True if a "next" segment exists, otherwise false.
     * @throws IOException Error reading from EDI stream.
     */
    protected boolean moveToNextSegment() throws IOException {
        int c = reader.read();
        int delimiterLen = segmentDelimiter.length;

        segmentBuffer.setLength(0);
        currentSegmentFields = null;

        // We reached the end of the stream the last time this method was
        // called - see the while loop below...
        if(c == -1) {
            return false;
        }
        
        // Read the next segment...
        while(c != -1) {
            segmentBuffer.append((char)c);
            
            int segLen = segmentBuffer.length();
            if(segLen >= delimiterLen) {
            	boolean reachedSegEnd = true;
            	
	            for(int i = 0; i < delimiterLen; i++) {
	            	char segChar = segmentBuffer.charAt(segLen - 1 - i);
	            	char delimChar = segmentDelimiter[delimiterLen - 1 - i];
	            	
	            	if(segChar != delimChar) {
	            		// Not the end of a segment
	            		reachedSegEnd = false;
	            		break;
	            	}
	            }
	            
	            // We've reached the end of a segment...
	            if(reachedSegEnd) {
	            	// Trim off the delimiter and break out...
	            	segmentBuffer.setLength(segLen - delimiterLen);
	            	break;
	            }
            }
            
            c = reader.read();
        }

        if(logger.isDebugEnabled()) {
            logger.debug(segmentBuffer.toString());
        }
        
        currentSegmentNumber++;
        
        return true;
    }
    
    /**
     * Does the read have a segment buffered and ready for processing.
     * @return True if a current segment exists, otherwise false.
     */
    public boolean hasCurrentSegment() {
    	return segmentBuffer.length() != 0;
    }

    /**
     * Get the current EDI segment.
     * @return The current EDI segment.
     * @throws IllegalStateException No current Segment.
     */
    protected String getCurrentSegment() throws IllegalStateException {
    	assertCurrentSegmentExists();
        return segmentBuffer.toString();
    }

    /**
     * Get the current EDI segment fields.
     * @return The current EDI segment fields array.
     * @throws IllegalStateException No current Segment.
     */
    protected String[] getCurrentSegmentFields() throws IllegalStateException {
    	assertCurrentSegmentExists();
    	
    	if(currentSegmentFields == null) {
    		currentSegmentFields = StringUtils.splitPreserveAllTokens(segmentBuffer.toString(), delimiters.getField());
    	}
    	
    	// If the segment delimiter is a LF, strip off any preceeding CR characters...
    	if(delimiters.getSegment().equals("\n")) {
    		int endIndex = currentSegmentFields.length - 1;
    		if(currentSegmentFields[endIndex].endsWith("\r")) {
    			int stringLen = currentSegmentFields[endIndex].length();
    			currentSegmentFields[endIndex] = currentSegmentFields[endIndex].substring(0, stringLen - 1);
    		}
    	}
    	
        return currentSegmentFields;
    }

    /**
     * Get the current segment "number".
     * <p/>
     * The first segment is "segment number 1".
     * @return The "number" of the current segment.
     */
	public int getCurrentSegmentNumber() {
		return currentSegmentNumber;
	}

	/**
	 * Assert that there is a current segment.
	 */
	private void assertCurrentSegmentExists() {
		if(!hasCurrentSegment()) {
    		throw new IllegalStateException("No current segment available.  Possible conditions: \n" 
    									+ "\t\t1. A call to moveToNextSegment() was not made, or \n"
    									+ "\t\t2. The last call to moveToNextSegment() returned false.");
    	}
	}
}
