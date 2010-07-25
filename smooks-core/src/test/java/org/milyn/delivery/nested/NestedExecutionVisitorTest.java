package org.milyn.delivery.nested;

import junit.framework.TestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.lifecycle.BeanContextLifecycleEvent;
import org.milyn.javabean.lifecycle.BeanContextLifecycleObserver;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class NestedExecutionVisitorTest extends TestCase {

    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config-01.xml"));
        StringResult result = new StringResult();
        JavaResult beans = new JavaResult();
        ExecutionContext executionContext = smooks.createExecutionContext();
        final List<String> orderItems = new ArrayList<String>();

        executionContext.getBeanContext().addObserver(new BeanContextLifecycleObserver() {
            public void onBeanLifecycleEvent(BeanContextLifecycleEvent event) {
                if(event.getLifecycle() == BeanLifecycle.END && event.getBeanId().getName().equals("orderItem")) {
                    orderItems.add((String) event.getBean());
                }
            }
        });

        smooks.filterSource(executionContext, new StreamSource(getClass().getResourceAsStream("order-message.xml")), result, beans);

        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream("order-message.xml")), new StringReader(result.toString()));

        assertEquals("header", beans.getBean("header"));
        assertEquals("trailer", beans.getBean("trailer"));
        assertEquals(2, orderItems.size());
    }
}
