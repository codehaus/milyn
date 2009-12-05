package org.milyn.ect;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Edimap;
import org.milyn.edisax.model.internal.SegmentGroup;
import org.milyn.edisax.model.internal.Segment;
import org.milyn.edisax.EDIConfigurationException;
import org.xml.sax.SAXException;

public class ConfigReaderTest extends TestCase {

    public void test_Converting_UnEdifact_D08A() throws IOException, EdiParseException, EDIConfigurationException, SAXException {
        String directory = Thread.currentThread().getContextClassLoader().getResource("").getFile();

        /**
         * The zip-file containing the specifications.
         */
        String infile  = directory + "org" + File.separator + "milyn" + File.separator + "ect" + File.separator + "D08A.zip";

        /**
         * The directory where the config-files shall be written.
         */
        String outDir  = Thread.currentThread().getContextClassLoader().getResource("").getFile() + File.separator + "test-output";

        /**
         * Write out the INVOIC message. Try changing parameter to ALL for writing out all messages.
         */
        String message = "INVOIC";

        ConfigReader.convert(outDir, infile, message);

        // Assert that the generated configuration is correct.
        assertCorrectGeneratedConfiguration(outDir);

    }

    private void assertCorrectGeneratedConfiguration(String outDir) throws SAXException, EDIConfigurationException, IOException {
        assertTrue("The number of generated files should be 3.", new File(outDir).list().length == 3);

        for (String fileName : new File(outDir).list()) {
            assertTrue("Generated files does not match expected filename.",
                    fileName.equalsIgnoreCase("un-edifact-definition-D08A.xml") ||
                    fileName.equalsIgnoreCase("un-edifact-message-INVOIC_D.08A.xml") ||
                    fileName.equalsIgnoreCase("un-edifact-interchange-definition.xml"));
        }

        // Test reading the generated file into the Smooks edimap model.
        FileInputStream fis = new FileInputStream(outDir + File.separator + "un-edifact-message-INVOIC_D.08A.xml");
        EdifactModel model = new EdifactModel();
        model.parseSequence(fis);
        Edimap edimap = model.getEdimap();

        //Some assertions.
        assertTrue("The name of the message should be UN-EDIFACT but had the value " + edimap.getDescription().getName(), edimap.getDescription().getName().equals("UN-EDIFACT"));
        assertTrue("The version of the message should be D08A but had the value " + edimap.getDescription().getVersion(), edimap.getDescription().getVersion().equals("D08A"));
        assertTrue("The xmltag of the root segmentgroup should be INVOIC but had the value " + edimap.getSegments().getXmltag(), edimap.getSegments().getXmltag().equals("INVOIC"));
        assertTrue("The number of segment/segmentGroups in first level should be 32 but was " + model.getEdimap().getSegments().getSegments().size(), model.getEdimap().getSegments().getSegments().size() == 32);

        //The first segment should be UNH imported from interchange enveloe definition.
        Segment segment = (Segment)edimap.getSegments().getSegments().get(0);
        assertTrue("The first segment in INVOIC message should be UNH", segment.getSegcode().equals("UNH"));
        assertTrue("UNH segment should have 7 fields but had " + segment.getFields().size(), segment.getFields().size() == 7);
        assertTrue("The second Field should have 7 Components", segment.getFields().get(1).getComponent().size() == 7);

        //The second segment should be BGM imported from generated message definition file.
        segment = (Segment)edimap.getSegments().getSegments().get(1);
        assertTrue("The second segment in INVOIC message should be BGM", segment.getSegcode().equals("BGM"));
        assertTrue("BGM segment should have 4 fields but had " + segment.getFields().size(), segment.getFields().size() == 4);
        assertTrue("The first Field should have 4 Components", segment.getFields().get(0).getComponent().size() == 4);
    }
}
