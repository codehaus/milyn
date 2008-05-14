/**
 *
 */
package org.milyn.javabean.performance;

import java.io.IOException;

import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.w3c.dom.Element;

/**
 * @author maurice_zeijen
 *
 */
public class DummyVisitor implements DOMElementVisitor, SAXElementVisitor {

	/* (non-Javadoc)
	 * @see org.milyn.delivery.dom.DOMVisitBefore#visitBefore(org.w3c.dom.Element, org.milyn.container.ExecutionContext)
	 */
	public void visitBefore(Element element, ExecutionContext executionContext)
			throws SmooksException {
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.dom.DOMVisitAfter#visitAfter(org.w3c.dom.Element, org.milyn.container.ExecutionContext)
	 */
	public void visitAfter(Element element, ExecutionContext executionContext)
			throws SmooksException {
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.sax.SAXVisitBefore#visitBefore(org.milyn.delivery.sax.SAXElement, org.milyn.container.ExecutionContext)
	 */
	public void visitBefore(SAXElement element,
			ExecutionContext executionContext) throws SmooksException,
			IOException {
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.sax.SAXVisitChildren#onChildElement(org.milyn.delivery.sax.SAXElement, org.milyn.delivery.sax.SAXElement, org.milyn.container.ExecutionContext)
	 */
	public void onChildElement(SAXElement element, SAXElement childElement,
			ExecutionContext executionContext) throws SmooksException,
			IOException {
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.sax.SAXVisitChildren#onChildText(org.milyn.delivery.sax.SAXElement, org.milyn.delivery.sax.SAXText, org.milyn.container.ExecutionContext)
	 */
	public void onChildText(SAXElement element, SAXText childText,
			ExecutionContext executionContext) throws SmooksException,
			IOException {
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.sax.SAXVisitAfter#visitAfter(org.milyn.delivery.sax.SAXElement, org.milyn.container.ExecutionContext)
	 */
	public void visitAfter(SAXElement element, ExecutionContext executionContext)
			throws SmooksException, IOException {

	}

}
