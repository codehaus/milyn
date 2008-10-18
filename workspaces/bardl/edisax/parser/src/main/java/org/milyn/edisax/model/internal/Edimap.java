package org.milyn.edisax.model.internal;

import java.util.ArrayList;
import java.util.List;

public class Edimap {

    private List<Import> imports;
    private Description description;
    private Delimiters delimiters;
    private Segments segments;

    public List<Import> getImport() {
        if (imports == null) {
            imports = new ArrayList<Import>();
        }
        return this.imports;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description value) {
        this.description = value;
    }

    public Delimiters getDelimiters() {
        return delimiters;
    }

    public void setDelimiters(Delimiters value) {
        this.delimiters = value;
    }

    public Segments getSegments() {
        return segments;
    }

    public void setSegments(Segments value) {
        this.segments = value;
    }

}
