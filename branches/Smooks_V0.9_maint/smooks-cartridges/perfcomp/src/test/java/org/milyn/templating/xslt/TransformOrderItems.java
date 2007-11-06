package org.milyn.templating.xslt;

import org.milyn.delivery.process.ProcessingUnit;
import org.milyn.container.ContainerRequest;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.xml.XmlUtil;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformOrderItems implements ProcessingUnit {
    
    public void visit(Element order, ContainerRequest containerRequest) {
        List<Element> orderItems = TransformOrderItem.getOrderItems(containerRequest);
        Element orderLines = order.getOwnerDocument().createElement("OrderLines");

        // Add the OrderLines element
        order.appendChild(orderLines);
        // Readd the cached order items to the new OrderLines element
        DomUtils.appendList(orderLines, orderItems);

        /*
        Node firstOrderItem = orderItems.get(0);
        Document doc = order.getOwnerDocument();

        DomUtils.insertBefore(doc.createTextNode("<OrderLines>"), firstOrderItem);
        order.appendChild(doc.createTextNode("</OrderLines>"));
        */
    }

    public boolean visitBefore() {
        return false;
    }

    public void setConfiguration(SmooksResourceConfiguration smooksResourceConfiguration) throws SmooksConfigurationException {
    }
}
