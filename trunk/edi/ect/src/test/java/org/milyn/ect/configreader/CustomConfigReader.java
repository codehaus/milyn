package org.milyn.ect.configreader;

import org.milyn.ect.ConfigReader;
import org.milyn.ect.EdiParseException;
import org.milyn.edisax.model.internal.Edimap;
import org.milyn.edisax.model.internal.Description;
import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.edisax.model.internal.SegmentGroup;

import java.io.InputStream;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

public class CustomConfigReader implements ConfigReader {
    public void initialize(InputStream inputStream, boolean useImport) throws IOException, EdiParseException {
    }

    public Set<String> getMessageNames() {
        return new HashSet<String>();
    }

    public Edimap getMappingModelForMessage(String messageName) throws IOException {
        return createEdimap();
    }

    public Edimap getDefinitionModel() throws IOException {
        return createEdimap();
    }

    private Edimap createEdimap() {
        Edimap edimap = new Edimap();

        Description description = new Description();
        description.setName("Custom Config Reader");
        description.setVersion("1.0");
        edimap.setDescription(description);

        Delimiters delimiters = new Delimiters();
        delimiters.setSegment("'");
        delimiters.setField("+");
        delimiters.setComponent(":");
        delimiters.setSubComponent("^");
        delimiters.setEscape("?");
        edimap.setDelimiters(delimiters);

        SegmentGroup root = new SegmentGroup();
        root.setXmltag("Root");
        edimap.setSegments(root);

        return edimap;
    }
}
