package org_milyn_edi_unedifact.d03b.PAXLST;

import org.milyn.edi.unedifact.d03b.D03BInterchangeFactory;
import org.milyn.smooks.edi.unedifact.model.UNEdifactInterchange;
import org.xml.sax.SAXException;
import org_milyn_edi_unedifact.d03b.AbstractTestCase;

import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class PAXLSTTest extends AbstractTestCase {

    public void test_read() throws IOException, SAXException {
        runTest("message_01.edi", "expected_01.xml");
    }

    public void test_bind() throws IOException, SAXException {
        D03BInterchangeFactory factory = D03BInterchangeFactory.getInstance();

        // Deserialize the a UN/EDIFACT interchange stream to Java...
        UNEdifactInterchange interchange = factory.fromUNEdifact(getClass().getResourceAsStream("message_01.edi"), new StreamResult(System.out));

        // Do stuff with the message....

        // Serialize it back to EDI....
//        factory.toUNEdifact(interchange, outStream);

        StringWriter writer = new StringWriter();
        interchange.write(writer);

        System.out.println("");
        System.out.println(writer);
    }
}
