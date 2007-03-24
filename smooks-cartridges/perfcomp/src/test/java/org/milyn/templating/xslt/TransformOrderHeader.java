package org.milyn.templating.xslt;

import org.milyn.delivery.process.ProcessingUnit;
import org.milyn.container.ContainerRequest;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.xml.XmlUtil;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * @author
 */
public class TransformOrderHeader implements ProcessingUnit {
    public void visit(Element header, ContainerRequest containerRequest) {
        Element order = header.getOwnerDocument().getDocumentElement();

        // Map all the header fields onto the <Order> header...
        order.setAttribute("orderId", XmlUtil.getString(header, "order-id/text()"));
        order.setAttribute("statusCode", XmlUtil.getString(header, "status-code/text()"));
        order.setAttribute("netAmount", XmlUtil.getString(header, "net-amount/text()"));
        order.setAttribute("totalAmount", XmlUtil.getString(header, "total-amount/text()"));
        order.setAttribute("tax", XmlUtil.getString(header, "tax/text()"));
        order.setAttribute("date", XmlUtil.getString(header, "date/month/text()") + "-" +
                                   XmlUtil.getString(header, "date/day/text()") + "-" +
                                   XmlUtil.getString(header, "date/year/text()"));

        // Remove the header from the message...
        DomUtils.removeElement(header, false);
    }

    public boolean visitBefore() {
        return false;
    }

    public void setConfiguration(SmooksResourceConfiguration smooksResourceConfiguration) throws SmooksConfigurationException {
    }
}
