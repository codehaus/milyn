package org.milyn.templating.xslt;

import org.milyn.delivery.process.ProcessingUnit;
import org.milyn.container.ContainerRequest;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.javabean.BeanAccessor;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;
import java.util.ArrayList;

/**
 * Transform the order-item node using the current populated OrderItem bean.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformOrderItem implements ProcessingUnit {
    private boolean removeItem = true;

    public void visit(Element orderItemElement, ContainerRequest containerRequest) {
        OrderItem orderItem = (OrderItem) BeanAccessor.getBean("order-item", containerRequest);

        // Remove the order items child content
        DomUtils.removeChildren(orderItemElement);
        // Readd the order item data in the form required by the target
        orderItem.addToElement(orderItemElement);
        if(removeItem) {
            // Remove the order item from the message
            DomUtils.removeElement(orderItemElement, false);
            // cache the order item - readded to the OrderLines at the end.
            cacheOrderItem(orderItemElement, containerRequest);
        }
    }

    private static final String KEY = TransformOrderItem.class.getName() + "#order-items";

    private void cacheOrderItem(Element orderItemElement, ContainerRequest containerRequest) {
        List<Element> orderItems = getOrderItems(containerRequest);
        orderItems.add(orderItemElement);
    }

    public static List<Element> getOrderItems(ContainerRequest containerRequest) {
        List<Element> orderItems = (List<Element>) containerRequest.getAttribute(KEY);

        if(orderItems == null) {
            orderItems = new ArrayList<Element>();
            containerRequest.setAttribute(KEY, orderItems);
        }
        return orderItems;
    }

    public boolean visitBefore() {
        return false;
    }

    public void setConfiguration(SmooksResourceConfiguration smooksResourceConfiguration) throws SmooksConfigurationException {
        removeItem = smooksResourceConfiguration.getBoolParameter("removeItem", false);
    }
}
