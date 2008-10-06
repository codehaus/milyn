package org.milyn.templating.xslt.MILYN_55;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author
 */
public class MILYN_55_Test extends TestCase {

    public void test() throws IOException, SAXException {
        try {
            Smooks smooks = new Smooks("/org/milyn/templating/xslt/MILYN_55/sample.xml");
            StandaloneExecutionContext executionContext = smooks.createExecutionContext();
            StringWriter resultWriter = new StringWriter();

            smooks.filter(new StreamSource(getClass().getResourceAsStream("source.xml")), new StreamResult(resultWriter), executionContext);
            assertEquals("<OrderLines>\n" +
                    "\t\t<OrderLine position=\"1\" quantity=\"1\">\n" +
                    "\t\t\t<Product price=\"29.98\" productId=\"364\" title=\"The 40-Year-Old Virgin \"></Product>\n" +
                    "\t\t</OrderLine>\n" +
                    "\t\t<OrderLine position=\"2\" quantity=\"1\">\n" +
                    "\t\t\t<Product price=\"29.99\" productId=\"299\" title=\"Pulp Fiction\"></Product>\n" +
                    "\t\t</OrderLine>\n" +
                    "\t</OrderLines>", resultWriter.toString());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
