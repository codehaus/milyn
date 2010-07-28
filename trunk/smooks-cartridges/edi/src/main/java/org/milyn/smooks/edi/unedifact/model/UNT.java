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
package org.milyn.smooks.edi.unedifact.model;

import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.smooks.edi.EDIWritable;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

/**
 * Message Trailer.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNT implements Serializable, EDIWritable {

	private static final long serialVersionUID = 1L;

	private int segmentCount;
	private String messageRefNum;

    public void write(Writer writer, Delimiters delimiters) throws IOException {
        writer.write("UNT");
        writer.write(delimiters.getField());
        writer.write(segmentCount);
        writer.write(delimiters.getField());
        if(messageRefNum != null) {
            writer.write(messageRefNum);
        }
        writer.write(delimiters.getSegment());
    }

    public int getSegmentCount() {
		return segmentCount;
	}

    public void setSegmentCount(int segmentCount) {
		this.segmentCount = segmentCount;
	}

    public String getMessageRefNum() {
		return messageRefNum;
	}

    public void setMessageRefNum(String messageRefNum) {
		this.messageRefNum = messageRefNum;
	}
}
