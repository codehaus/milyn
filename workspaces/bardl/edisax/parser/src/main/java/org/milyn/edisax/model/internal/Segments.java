package org.milyn.edisax.model.internal;

import java.util.ArrayList;
import java.util.List;

public class Segments extends MappingNode {

    private List<Segment> segment;

    public List<Segment> getSegment() {
        if (segment == null) {
            segment = new ArrayList<Segment>();
        }
        return this.segment;
    }

}
