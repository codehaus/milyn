package org.milyn.edisax.model.internal;

public class Import {

    private String name;
    private String namespace;
    private Boolean truncatableFields;
    private Boolean truncatableComponents;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
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

}
