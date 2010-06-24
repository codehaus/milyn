package org_milyn_edi_unedifact.d03b.PAXLST;

import org.xml.sax.SAXException;
import org_milyn_edi_unedifact.d03b.AbstractTestCase;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class PAKLSTTest extends AbstractTestCase {

    public void test_01() throws IOException, SAXException {
        runTest("message_01.edi", "expected_01.xml");
    }
}