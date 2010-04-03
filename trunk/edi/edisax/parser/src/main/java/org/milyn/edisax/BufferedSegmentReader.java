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

package org.milyn.edisax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.edisax.model.internal.Delimiters;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Stack;

/**
 * Buffered EDI Stream Segment reader.
 * @author tfennelly
 */
public class BufferedSegmentReader {

    private static Log logger = LogFactory.getLog(BufferedSegmentReader.class);

    public static String IGNORE_CR_LF = "!$";

    private Reader reader;
    private StringBuffer segmentBuffer = new StringBuffer(512);
    private String[] currentSegmentFields = null;
	private int currentSegmentNumber = 0;
	private Stack<Delimiters> delimitersStack = new Stack<Delimiters>();
    private Delimiters currentDelimiters;
    private BufferedSegmentListener segmentListener;
	private boolean ignoreNewLines;

    /**
     * Construct the stream reader.
     * @param ediInputSource EDI Stream input source.
     * @param rootDelimiters Root currentDelimiters.  New currentDelimiters can be pushed and popped.
     */
    public BufferedSegmentReader(InputSource ediInputSource, Delimiters rootDelimiters) {
        reader = ediInputSource.getCharacterStream();
        if(reader == null) {
            reader = new InputStreamReader(ediInputSource.getByteStream());
        }
        this.currentDelimiters = rootDelimiters;
    }
    
    /**
     * Get the current delimiter set.
	 * @return the currentDelimiters The current delimiter set.
	 */
	public Delimiters getDelimiters() {
		return currentDelimiters;
	}
	
	/**
	 * Push in a new {@link Delimiters} set into the reader.
	 * @param delimiters New delimiters.
	 */
	public void pushDelimiters(Delimiters delimiters) {
		delimitersStack.push(currentDelimiters);
		currentDelimiters = delimiters;
	}
	
	/**
	 * Restore the parent delimiters set.
	 * <p/>
	 * Be sure to {@link #getDelimitersStack() get the delimiters stack} and check 
	 * that it is not empty before popping.
	 */
	public void popDelimiters() {
		currentDelimiters = delimitersStack.pop();
	}
	
	/**
	 * Get the 
	 * @return the delimitersStack
	 */
	public Stack<Delimiters> getDelimitersStack() {
		return delimitersStack;
	}
	
	/**
	 * Set ignore new lines in the EDI Stream.
	 * <p/>
	 * Some EDI messages are formatted with new lines for readability and so the
	 * new line characters should be ignored.
	 * 
	 * @param ignoreNewLines True if new line characters should be ignored, otherwise false.
	 */
	public void setIgnoreNewLines(boolean ignoreNewLines) {
		this.ignoreNewLines = ignoreNewLines;
	}
	
	/**
	 * Read a fixed number of characters from the input source.
	 * @param numChars The number of characters to read.
	 * @return The characters in a String.  If the end of the input source
	 * was reached, the length of the string will be less than the requested number
	 * of characters.
	 * @throws IOException Error reading from input source.
	 */
	public String read(int numChars) throws IOException {
		segmentBuffer.setLength(0);
        try {
        	return peek(numChars);
        } finally {
        	segmentBuffer.setLength(0);
        }
	}

	/**
	 * Peek a fixed number of characters from the input source.
	 * <p/>
	 * Peek differs from {@link #read(int)} in that it leaves the
	 * characters in the segment buffer.
	 * 
	 * @param numChars The number of characters to peeked.
	 * @return The characters in a String.  If the end of the input source
	 * was reached, the length of the string will be less than the requested number
	 * of characters.
	 * @throws IOException Error reading from input source.
	 */
	public String peek(int numChars) throws IOException {
        boolean ignoreCRLF;

        // Ignoring of new lines can be set as part of the segment delimiter, or
        // as a feature on the parser (the later is the preferred method)...
        ignoreCRLF = (currentDelimiters.ignoreCRLF() || ignoreNewLines);

		if(segmentBuffer.length() < numChars) {
			int c;
	        while((c = reader.read()) != -1) {
	            if (ignoreCRLF && (c == '\n' || c == '\r')) {
	                continue;
	            }

	        	segmentBuffer.append((char)c);
	        	if(segmentBuffer.length() == numChars) {
	        		break;
	        	}
	        }
    	}
    	
    	int endIndex = Math.min(numChars, segmentBuffer.length());
    	
    	return segmentBuffer.substring(0, endIndex);
	}

	/**
	 * Set the segment listener.
	 * @param segmentListener The segment listener.
	 */
	public void setSegmentListener(BufferedSegmentListener segmentListener) {
		this.segmentListener = segmentListener;
	}

	/**
     * Move to the next EDI segment.
     * <p/>
     * Simply reads and buffers the next EDI segment.  Clears the current contents of
     * the buffer before reading.
     * @return True if a "next" segment exists, otherwise false.
     * @throws IOException Error reading from EDI stream.
     */
    public boolean moveToNextSegment() throws IOException {
    	return moveToNextSegment(true);
    }

	/**
     * Move to the next EDI segment.
     * <p/>
     * Simply reads and buffers the next EDI segment.
     * @param clearBuffer Clear the segment buffer before reading.
     * @return True if a "next" segment exists, otherwise false.
     * @throws IOException Error reading from EDI stream.
     */
    public boolean moveToNextSegment(boolean clearBuffer) throws IOException {
        int c = reader.read();
        char[] segmentDelimiter = currentDelimiters.getSegmentDelimiter();
        int delimiterLen = segmentDelimiter.length;
        String escape = currentDelimiters.getEscape();
        int escapeLen = escape != null ? escape.length() : 0;
        boolean ignoreCRLF;

        // Ignoring of new lines can be set as part of the segment delimiter, or
        // as a feature on the parser (the later is the preferred method)...
        ignoreCRLF = (currentDelimiters.ignoreCRLF() || ignoreNewLines);
        
        if(clearBuffer) {
        	segmentBuffer.setLength(0);
        }
        currentSegmentFields = null;

        // We reached the end of the stream the last time this method was
        // called - see the while loop below...
        if(c == -1) {
            return false;
        }
        
        // Read the next segment...
        while(c != -1) {
        	char theChar = (char) c;

            if (ignoreCRLF && (theChar == '\n' || theChar == '\r')) {
                c = reader.read();
                continue;
            }

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

                    // Do not separate segment if escape character occurs.
                    if (segLen - 1 - i - escapeLen > -1 && escape != null) {
                        String escapeString = segmentBuffer.substring(segLen - 1 - i - escapeLen, segLen - 1 - i);
                        if (escape.equals(escapeString)) {
                            segmentBuffer = segmentBuffer.delete(segLen - 1 - i - escapeLen, segLen - 1 - i);
                            reachedSegEnd = false;
                            break;
                        }
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

        if(segmentListener != null) {
        	return segmentListener.onSegment(this);
        } else {
        	return true;
        }
    }
    
    /**
     * Does the read have a segment buffered and ready for processing.
     * @return True if a current segment exists, otherwise false.
     */
    public boolean hasCurrentSegment() {
        if(segmentListener != null) {
        	return segmentListener.onSegment(this);
        } else {
        	return segmentBuffer.length() != 0;
        }
    }

    /**
     * Get the segment buffer.
     * @return The segment buffer.
     */
    public StringBuffer getSegmentBuffer() {
        return segmentBuffer;
    }

    /**
     * Get the current EDI segment fields.
     * @return The current EDI segment fields array.
     * @throws IllegalStateException No current Segment.
     */
    public String[] getCurrentSegmentFields() throws IllegalStateException {
    	assertCurrentSegmentExists();

        if(currentSegmentFields == null) {
              currentSegmentFields = EDIUtils.split(segmentBuffer.toString(), currentDelimiters.getField(), currentDelimiters.getEscape());

              // If the segment delimiter is a LF, strip off any preceding CR characters...
              if(currentDelimiters.getSegment().equals("\n")) {
            	  int endIndex = currentSegmentFields.length - 1;
            	  if(currentSegmentFields[endIndex].endsWith("\r")) {
            		  int stringLen = currentSegmentFields[endIndex].length();
            		  currentSegmentFields[endIndex] = currentSegmentFields[endIndex].substring(0, stringLen - 1);
            	  }
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
		if(segmentBuffer.length() == 0) {
    		throw new IllegalStateException("No current segment available.  Possible conditions: \n" 
    									+ "\t\t1. A call to moveToNextSegment() was not made, or \n"
    									+ "\t\t2. The last call to moveToNextSegment() returned false.");
    	}
	}
}
