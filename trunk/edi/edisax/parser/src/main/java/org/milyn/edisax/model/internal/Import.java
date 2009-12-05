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

public class Import {

    private String resource;
    private String namespace;
    private Boolean truncatableSegments;
    private Boolean truncatableFields;
    private Boolean truncatableComponents;

    public String getResource() {
        return resource;
    }

    public void setResource(String value) {
        this.resource = value;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String value) {
        this.namespace = value;
    }

    public Boolean isTruncatableFields() {
        return truncatableFields;
    }

    public void setTruncatableFields(Boolean value) {
        this.truncatableFields = value;
    }

    public Boolean isTruncatableComponents() {
        return truncatableComponents;
    }

    public void setTruncatableComponents(Boolean value) {
        this.truncatableComponents = value;
    }

    public Boolean isTruncatableSegments() {
        return truncatableSegments;
    }

    public void setTruncatableSegments(Boolean truncatableSegments) {
        this.truncatableSegments = truncatableSegments;
    }
}
