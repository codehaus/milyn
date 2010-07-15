package org.milyn.delivery.nested;

import junit.framework.TestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class NestedExecutionVisitorTest extends TestCase {

    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config-01.xml"));
        StringResult result = new StringResult();
        JavaResult beans = new JavaResult();

        smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order-message.xml")), result, beans);

        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream("order-message.xml")), new StringReader(result.toString()));

        assertEquals("header", beans.getBean("header"));
        assertEquals("trailer", beans.getBean("trailer"));
        assertEquals("orderItem-orderItem", beans.getBean("orderItem"));
    }
}
