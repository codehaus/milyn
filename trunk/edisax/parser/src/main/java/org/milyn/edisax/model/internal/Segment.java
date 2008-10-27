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

package org.milyn.edisax.model.internal;

import java.util.ArrayList;
import java.util.List;

public class Segment extends MappingNode {

    private List<Field> field;
    private List<Segment> segment;
    private Integer minOccurs;
    private Integer maxOccurs;
    private String segcode;
    private String segref;
    private Boolean truncatable;

    public List<Field> getField() {
        if (field == null) {
            field = new ArrayList<Field>();
        }
        return this.field;
    }

    public List<Segment> getSegment() {
        if (segment == null) {
            segment = new ArrayList<Segment>();
        }
        return this.segment;
    }

    public int getMinOccurs() {
        if (minOccurs == null) {
            return  1;
        } else {
            return minOccurs;
        }
    }

    public void setMinOccurs(Integer value) {
        this.minOccurs = value;
    }

    public int getMaxOccurs() {
        if (maxOccurs == null) {
            return  1;
        } else {
            return maxOccurs;
        }
    }

    public void setMaxOccurs(Integer value) {
        this.maxOccurs = value;
    }

    public String getSegcode() {
        return segcode;
    }

    public void setSegcode(String value) {
        this.segcode = value;
    }

    public String getSegref() {
        return segref;
    }

    public void setSegref(String value) {
        this.segref = value;
    }

    public boolean isTruncatable() {
        return truncatable != null && truncatable;
    }

    public void setTruncatable(Boolean value) {
        this.truncatable = value;
    }

}
