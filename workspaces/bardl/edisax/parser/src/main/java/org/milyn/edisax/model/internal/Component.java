package org.milyn.edisax.model.internal;

import java.util.ArrayList;
import java.util.List;

public class Component extends MappingNode {

    private List<SubComponent> subComponent;
    private Boolean required;
    private Boolean truncatable;

    public List<SubComponent> getSubComponent() {
        if (subComponent == null) {
            subComponent = new ArrayList<SubComponent>();
        }
        return this.subComponent;
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
