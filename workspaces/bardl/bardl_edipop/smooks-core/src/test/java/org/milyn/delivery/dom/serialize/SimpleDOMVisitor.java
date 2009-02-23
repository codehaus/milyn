package org.milyn.delivery.dom.serialize;

import org.milyn.delivery.dom.DOMVisitAfter;
import org.milyn.container.ExecutionContext;
import org.milyn.SmooksException;
import org.w3c.dom.Element;

/**
 * @author
 */
public class SimpleDOMVisitor implements DOMVisitAfter {

    public static boolean visited = false;

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        visited = true;
    }
}
