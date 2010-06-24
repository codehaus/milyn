package org_milyn_edi_unedifact.d03b.CUSCAR;

import org.xml.sax.SAXException;
import org_milyn_edi_unedifact.d03b.AbstractTestCase;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CUSCARTest extends AbstractTestCase {

    public void test() throws IOException, SAXException {

        // Seems to me as though the sample message is invalid.  The message gets to the SEL
        // segment in Segment Group 5.  The next segment in the message is the RFF segment,
        // which is defined in Segment Group 8.  But segment Group 8 is nested inside
        // Segment Group 7.. what about all th mandatory stuff to get you there?
        //

        // Scroll to end of http://www.stylusstudio.com/edifact/D03B/CUSCAR.htm
        // Got the message from http://www.cbp.gov/linkhandler/cgov/trade/automated/modernization/carrier_info/etruck_tech_info/ace_edi_drafts/edi_messages/unedifact_message_standard/cuscar_sample_message.ctt/cuscar_012_30.txt

//        runTest("message_01.edi", "expected_01.xml");
    }
}
