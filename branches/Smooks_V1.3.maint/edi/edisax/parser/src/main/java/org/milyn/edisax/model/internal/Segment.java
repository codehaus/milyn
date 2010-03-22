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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Segment extends SegmentGroup {

    private List<Field> fields;
    private String segcode;
    private Pattern segcodePattern;
    private String segref;
    private Boolean truncatable;
    private String description;

    public List<Field> getFields() {
        if (fields == null) {
            fields = new ArrayList<Field>();
        }
        return this.fields;
    }

    public String getSegcode() {
        return segcode;
    }

    public void setSegcode(String value) {
        this.segcode = value;
        segcodePattern = Pattern.compile("^" + segcode, Pattern.DOTALL);
    }

    public Pattern getSegcodePattern() {
        return segcodePattern;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
