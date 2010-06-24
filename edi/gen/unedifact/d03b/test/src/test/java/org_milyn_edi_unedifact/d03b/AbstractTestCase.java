package org_milyn_edi_unedifact.d03b;

import junit.framework.TestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.milyn.Smooks;
import org.milyn.payload.StringResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

public abstract class AbstractTestCase extends TestCase {

    public void runTest(String messageInFile, String expectedResFile) throws IOException, SAXException {
        Smooks smooks = new Smooks(AbstractTestCase.class.getResourceAsStream("smooks-config.xml"));
        StringResult result = new StringResult();

        smooks.filterSource(new StreamSource(getClass().getResourceAsStream(messageInFile)), result);

        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream(expectedResFile)), new StringReader(result.toString()));
    }
}