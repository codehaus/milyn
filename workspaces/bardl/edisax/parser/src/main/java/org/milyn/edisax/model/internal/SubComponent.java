package org.milyn.edisax.model.internal;

public class SubComponent extends MappingNode {

    private Boolean required;

    public boolean isRequired() {
        return required != null && required;
    }

    public void setRequired(Boolean value) {
        this.required = value;
    }

}
