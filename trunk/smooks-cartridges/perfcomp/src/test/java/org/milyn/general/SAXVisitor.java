package org.milyn.general;

import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.container.ExecutionContext;
import org.milyn.SmooksException;

import java.io.IOException;

/**
 * @author
 */
public class SAXVisitor implements SAXVisitAfter {
    public void visitBefore(SAXElement saxElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildText(SAXElement saxElement, SAXText saxText, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildElement(SAXElement saxElement, SAXElement saxElement1, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(SAXElement saxElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }
}
