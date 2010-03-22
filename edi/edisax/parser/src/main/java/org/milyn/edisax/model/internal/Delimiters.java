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

    public String getSegment() {
        return segment;
    }

    public void setSegment(String value) {
        this.segment = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String value) {
        this.field = value;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String value) {
        this.component = value;
    }

    public String getSubComponent() {
        return subComponent;
    }
    
    public void setSubComponent(String value) {
        this.subComponent = value;
    }

    public String getEscape() {
        return escape;
    }

    public void setEscape(String escape) {
        this.escape = escape;
    }
}
