package org.milyn.general;

import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.container.ExecutionContext;
import org.milyn.SmooksException;
import org.w3c.dom.Element;

/**
 * @author
 */
public class DOMVisitor implements DOMElementVisitor {
    
    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
    }
}
