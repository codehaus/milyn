package org.milyn.edisax.model.internal;

import java.util.ArrayList;
import java.util.List;

public class Field extends MappingNode {

    private List<Component> component;
    private Boolean required;
    private Boolean truncatable;

    public List<Component> getComponent() {
        if (component == null) {
            component = new ArrayList<Component>();
        }
        return this.component;
    }

    public boolean isRequired() {
        return required != null && required;
    }

    public void setRequired(Boolean value) {
        this.required = value;
    }

    public boolean isTruncatable() {
        return truncatable != null && truncatable;
    }

    public void setTruncatable(Boolean value) {
        this.truncatable = value;
    }

}
