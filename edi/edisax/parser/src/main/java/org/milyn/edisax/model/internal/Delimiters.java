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

package org.milyn.edisax.model.internal;

public class Delimiters {

    private String segment;
    private String field;
    private String component;
    private String subComponent;
    private String escape;
    private volatile char[] segmentDelimiter;
    private boolean ignoreCRLF;

    public String getSegment() {
        return segment;
    }

    public Delimiters setSegment(String value) {
        this.segment = value;
		initSegmentDelimiter();
        return this;
    }

    public String getField() {
        return field;
    }

    public Delimiters setField(String value) {
        this.field = value;
        return this;
    }

    public String getComponent() {
        return component;
    }

    public Delimiters setComponent(String value) {
        this.component = value;
        return this;
    }

    public String getSubComponent() {
        return subComponent;
    }
    
    public Delimiters setSubComponent(String value) {
        this.subComponent = value;
        return this;
    }

    public String getEscape() {
        return escape;
    }

    public Delimiters setEscape(String escape) {
        this.escape = escape;
        return this;
    }

	public char[] getSegmentDelimiter() {
		if(segmentDelimiter == null) {
			initSegmentDelimiter();
		}
		return segmentDelimiter;
	}

	public boolean ignoreCRLF() {
		if(segmentDelimiter == null) {
			initSegmentDelimiter();
		}
		return ignoreCRLF;
	}
	
	private synchronized void initSegmentDelimiter() {
		if(segmentDelimiter != null) {
			return;
		}
		
        this.ignoreCRLF = segment.endsWith("!$");

        if (ignoreCRLF) {
            this.segmentDelimiter = segment.replace("!$", "").toCharArray();
        } else {
            this.segmentDelimiter = segment.toCharArray();
        }
	}
}
