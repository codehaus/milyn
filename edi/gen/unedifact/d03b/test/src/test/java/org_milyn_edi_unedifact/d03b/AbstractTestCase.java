package org_milyn_edi_unedifact.d03b;

import junit.framework.TestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.milyn.Smooks;
import org.milyn.edi.unedifact.d03b.D03BInterchangeFactory;
import org.milyn.io.StreamUtils;
import org.milyn.payload.StringResult;
import org.milyn.smooks.edi.unedifact.model.UNEdifactInterchange;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

public abstract class AbstractTestCase extends TestCase {

    public void testXMLSerialize(String messageInFile, String expectedResFile) throws IOException, SAXException {
        testXMLSerialize(messageInFile, expectedResFile, false);
    }

    public void testXMLSerialize(String messageInFile, String expectedResFile, boolean dumpResult) throws IOException, SAXException {
        Smooks smooks = new Smooks(AbstractTestCase.class.getResourceAsStream("smooks-config.xml"));
        StringResult result = new StringResult();

        smooks.filterSource(new StreamSource(getClass().getResourceAsStream(messageInFile)), result);

        if(dumpResult) {
            System.out.println(result);
        }

        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream(expectedResFile)), new StringReader(result.toString()));
    }

    public void testJavaBinding(String messageInFile, boolean dumpResult) throws IOException, SAXException {
        D03BInterchangeFactory factory = D03BInterchangeFactory.getInstance();

        // Deserialize the a UN/EDIFACT interchange stream to Java...
        UNEdifactInterchange interchange = factory.fromUNEdifact(getClass().getResourceAsStream(messageInFile));

        // Serialize it back to EDI....
        StringWriter writer = new StringWriter();
        factory.toUNEdifact(interchange, writer);

        if(dumpResult) {
            System.out.println(writer.toString());
        }

        // We expect the result to be the same as the input...
        String expected = StreamUtils.readStreamAsString(getClass().getResourceAsStream(messageInFile));
        assertEquals(StreamUtils.normalizeLines(expected, false), StreamUtils.normalizeLines(writer.toString(), false));
    }
}