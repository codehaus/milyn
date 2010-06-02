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

public class MappingNode {

    private String xmltag;
    private String documentation;
    private MappingNode parent;
    
	public MappingNode() {
	}
    
	public MappingNode(String xmltag) {
		this.xmltag = xmltag;
	}

	public String getXmltag() {
        return xmltag;
    }

    public void setXmltag(String value) {
        this.xmltag = value;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public MappingNode getParent() {
        return parent;
    }

    public void setParent(MappingNode parent) {
        this.parent = parent;
    }

    public String getJavaName() {
        return xmltag;
    }
}
