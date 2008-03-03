/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/
package org.milyn.delivery;

import junit.framework.TestCase;
import org.milyn.SmooksException;
import org.milyn.cdr.annotation.VisitIf;
import org.milyn.cdr.annotation.VisitIfNot;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMVisitAfter;
import org.milyn.delivery.dom.DOMVisitBefore;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.w3c.dom.Element;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class VisitIfAndIfNotTest extends TestCase {

    public void test_sax_visitBefore() {
        SmooksResourceConfiguration resourceConfig;

        resourceConfig = new SmooksResourceConfiguration ();
        resourceConfig.setParameter("visitBefore", "true");
        assertTrue(ContentDeliveryConfigBuilder.visitBeforeAnnotationsOK(resourceConfig, new MySAXVisitBeforeVisitor1()));
        assertFalse(ContentDeliveryConfigBuilder.visitBeforeAnnotationsOK(resourceConfig, new MySAXVisitBeforeVisitor2()));

        resourceConfig = new SmooksResourceConfiguration ();
        resourceConfig.setParameter("visitBefore", "false");
        assertFalse(ContentDeliveryConfigBuilder.visitBeforeAnnotationsOK(resourceConfig, new MySAXVisitBeforeVisitor1()));
        assertTrue(ContentDeliveryConfigBuilder.visitBeforeAnnotationsOK(resourceConfig, new MySAXVisitBeforeVisitor2()));
    }

    public void test_sax_visitAfter() {
        SmooksResourceConfiguration resourceConfig;

        resourceConfig = new SmooksResourceConfiguration ();
        resourceConfig.setParameter("visitBefore", "true");
        assertTrue(ContentDeliveryConfigBuilder.visitAfterAnnotationsOK(resourceConfig, new MySAXVisitAfterVisitor1()));
        assertFalse(ContentDeliveryConfigBuilder.visitAfterAnnotationsOK(resourceConfig, new MySAXVisitAfterVisitor2()));

        resourceConfig = new SmooksResourceConfiguration ();
        resourceConfig.setParameter("visitBefore", "false");
        assertFalse(ContentDeliveryConfigBuilder.visitAfterAnnotationsOK(resourceConfig, new MySAXVisitAfterVisitor1()));
        assertTrue(ContentDeliveryConfigBuilder.visitAfterAnnotationsOK(resourceConfig, new MySAXVisitAfterVisitor2()));
    }

    public void test_dom_visitBefore() {
        SmooksResourceConfiguration resourceConfig;

        resourceConfig = new SmooksResourceConfiguration ();
        resourceConfig.setParameter("visitBefore", "true");
        assertTrue(ContentDeliveryConfigBuilder.visitBeforeAnnotationsOK(resourceConfig, new MyDOMVisitBeforeVisitor1()));
        assertFalse(ContentDeliveryConfigBuilder.visitBeforeAnnotationsOK(resourceConfig, new MyDOMVisitBeforeVisitor2()));

        resourceConfig = new SmooksResourceConfiguration ();
        resourceConfig.setParameter("visitBefore", "false");
        assertFalse(ContentDeliveryConfigBuilder.visitBeforeAnnotationsOK(resourceConfig, new MyDOMVisitBeforeVisitor1()));
        assertTrue(ContentDeliveryConfigBuilder.visitBeforeAnnotationsOK(resourceConfig, new MyDOMVisitBeforeVisitor2()));
    }

    public void test_dom_visitAfter() {
        SmooksResourceConfiguration resourceConfig;

        resourceConfig = new SmooksResourceConfiguration ();
        resourceConfig.setParameter("visitBefore", "true");
        assertTrue(ContentDeliveryConfigBuilder.visitAfterAnnotationsOK(resourceConfig, new MyDOMVisitAfterVisitor1()));
        assertFalse(ContentDeliveryConfigBuilder.visitAfterAnnotationsOK(resourceConfig, new MyDOMVisitAfterVisitor2()));

        resourceConfig = new SmooksResourceConfiguration ();
        resourceConfig.setParameter("visitBefore", "false");
        assertFalse(ContentDeliveryConfigBuilder.visitAfterAnnotationsOK(resourceConfig, new MyDOMVisitAfterVisitor1()));
        assertTrue(ContentDeliveryConfigBuilder.visitAfterAnnotationsOK(resourceConfig, new MyDOMVisitAfterVisitor2()));
    }

    /* ====================================================================================================
             Test classes - visitor impls
       ==================================================================================================== */

    private class MySAXVisitBeforeVisitor1 implements SAXVisitBefore {

        @VisitIf(param = "visitBefore", value = "true", defaultVal = "true")
        public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        }
    }

    private class MySAXVisitBeforeVisitor2 implements SAXVisitBefore {        

        @VisitIfNot(param = "visitBefore", value = "true", defaultVal = "true")
        public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        }
    }

    private class MySAXVisitAfterVisitor1 implements SAXVisitAfter {

        @VisitIf(param = "visitBefore", value = "true", defaultVal = "true")
        public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        }
    }

    private class MySAXVisitAfterVisitor2 implements SAXVisitAfter {

        @VisitIfNot(param = "visitBefore", value = "true", defaultVal = "true")
        public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        }
    }

    private class MyDOMVisitBeforeVisitor1 implements DOMVisitBefore {

        @VisitIf(param = "visitBefore", value = "true", defaultVal = "true")
        public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        }
    }

    private class MyDOMVisitBeforeVisitor2 implements DOMVisitBefore {

        @VisitIfNot(param = "visitBefore", value = "true", defaultVal = "true")
        public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        }
    }

    private class MyDOMVisitAfterVisitor1 implements DOMVisitAfter {

        @VisitIf(param = "visitBefore", value = "true", defaultVal = "true")
        public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        }
    }

    private class MyDOMVisitAfterVisitor2 implements DOMVisitAfter {

        @VisitIfNot(param = "visitBefore", value = "true", defaultVal = "true")
        public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        }
    }
}
