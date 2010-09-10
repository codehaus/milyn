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

public class Field extends ValueNode implements IField {

    private List<IComponent> component;
    private Boolean required;
    private Boolean truncatable;
    
    public Field() {    	
    }

	public Field(String xmltag, Boolean required) {
		this(xmltag, null, required, true);
	}

	public Field(String xmltag, String namespace, Boolean required) {
		this(xmltag, namespace, required, true);
	}

	public Field(String xmltag, String namespace, Boolean required, Boolean truncatable) {
		super(xmltag, namespace);
		this.required = required;
		this.truncatable = truncatable;
	}

	/* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.IField#getComponents()
	 */
	public List<IComponent> getComponents() {
        if (component == null) {
            component = new ArrayList<IComponent>();
        }
        return this.component;
    }
    
    public Field addComponent(Component component) {
    	getComponents().add(component);
    	return this;
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.IField#isRequired()
	 */
    public boolean isRequired() {
        return required != null && required;
    }

    public void setRequired(Boolean value) {
        this.required = value;
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.IField#isTruncatable()
	 */
    public boolean isTruncatable() {
        return truncatable != null && truncatable;
    }

    public void setTruncatable(Boolean value) {
        this.truncatable = value;
    }

}
