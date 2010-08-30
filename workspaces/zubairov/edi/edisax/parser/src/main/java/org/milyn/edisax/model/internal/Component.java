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

public class Component extends ValueNode implements IComponent {

    private List<SubComponent> subComponent;
    private Boolean required;
    private Boolean truncatable;

	public Component() {
	}
    
	public Component(String xmltag, Boolean required) {
		super(xmltag);
		this.required = required;
		this.truncatable = true;
	}
    
	public Component(String xmltag, Boolean required, Boolean truncatable) {
		super(xmltag);
		this.required = required;
		this.truncatable = truncatable;
	}

	/* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.IComponent#getSubComponents()
	 */
	public List<SubComponent> getSubComponents() {
        if (subComponent == null) {
            subComponent = new ArrayList<SubComponent>();
        }
        return this.subComponent;
    }
    
    public IComponent addSubComponent(SubComponent subComponent) {
    	getSubComponents().add(subComponent);
    	return this;
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.IComponent#isRequired()
	 */
    public boolean isRequired() {
        return required != null && required;
    }

    public void setRequired(Boolean value) {
        this.required = value;
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.IComponent#isTruncatable()
	 */
    public boolean isTruncatable() {
        return truncatable != null && truncatable;
    }

    public void setTruncatable(Boolean value) {
        this.truncatable = value;
    }

}
