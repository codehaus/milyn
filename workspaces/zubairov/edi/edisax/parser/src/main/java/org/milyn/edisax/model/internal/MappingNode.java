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

public class MappingNode implements IMappingNode {

    public static final String INDEXED_NODE_SEPARATOR = "_-_-";

    private String xmltag;
    private String nodeTypeRef;
    private String documentation;
    private IMappingNode parent;
    private String namespace;
    
	public MappingNode() {
	}
    
	public MappingNode(String xmltag, String namespace) {
		this.xmltag = xmltag;
		this.namespace = namespace;
	}

	/* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.IMappingNode#getXmltag()
	 */
	public String getXmltag() {
        return xmltag;
    }

    public void setXmltag(String value) {
        this.xmltag = value;
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.IMappingNode#getNodeTypeRef()
	 */
    public String getNodeTypeRef() {
        return nodeTypeRef;
    }

    public void setNodeTypeRef(String nodeTypeRef) {
        this.nodeTypeRef = nodeTypeRef;
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.IMappingNode#getDocumentation()
	 */
    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.IMappingNode#getParent()
	 */
    public IMappingNode getParent() {
        return parent;
    }

    public void setParent(IMappingNode parent) {
        this.parent = parent;
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.IMappingNode#getJavaName()
	 */
    public String getJavaName() {
        int separatorIndex = xmltag.indexOf(INDEXED_NODE_SEPARATOR);

        if(separatorIndex != -1) {
            return xmltag.substring(0, separatorIndex);
        } else {
            return xmltag;
        }
    }

	public String getNamespace() {
		return namespace;
	}
	
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}

