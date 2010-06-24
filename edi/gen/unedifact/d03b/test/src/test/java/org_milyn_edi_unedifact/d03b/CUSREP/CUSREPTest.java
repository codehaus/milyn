package org_milyn_edi_unedifact.d03b.CUSREP;

import org.xml.sax.SAXException;
import org_milyn_edi_unedifact.d03b.AbstractTestCase;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CUSREPTest extends AbstractTestCase {

    public void test_01() throws IOException, SAXException {


        // Same sort of issue as with the CUSCAR test... same message seems to be invalid???
        //

        // Scroll to end of http://www.stylusstudio.com/edifact/D03B/CUSREP.htm
        // Got the message from http://www.cbp.gov/linkhandler/cgov/trade/automated/modernization/carrier_info/etruck_tech_info/ace_edi_drafts/edi_messages/unedifact_message_standard/cusrep_sample_message.ctt/cusrep012_31.txt

//        runTest("message_01.edi", "expected_01.xml");
    }
}