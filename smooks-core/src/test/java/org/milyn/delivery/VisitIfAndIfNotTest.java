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
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.VisitAfterIf;
import org.milyn.delivery.annotation.VisitBeforeIf;
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

        resourceConfig = new SmooksResourceConfiguration ();
        assertFalse(ContentDeliveryConfigBuilder.visitBeforeAnnotationsOK(resourceConfig, new MySAXVisitBeforeVisitor1()));

        resourceConfig = new SmooksResourceConfiguration ();
        resourceConfig.setParameter("visitBefore", "false");
        assertFalse(ContentDeliveryConfigBuilder.visitBeforeAnnotationsOK(resourceConfig, new MySAXVisitBeforeVisitor1()));
    }

    public void test_sax_visitAfter() {
        SmooksResourceConfiguration resourceConfig;

        resourceConfig = new SmooksResourceConfiguration ();
        resourceConfig.setParameter("visitBefore", "true");
        assertFalse(ContentDeliveryConfigBuilder.visitAfterAnnotationsOK(resourceConfig, new MySAXVisitAfterVisitor1()));

        resourceConfig = new SmooksResourceConfiguration ();
        assertTrue(ContentDeliveryConfigBuilder.visitAfterAnnotationsOK(resourceConfig, new MySAXVisitAfterVisitor1()));

        resourceConfig = new SmooksResourceConfiguration ();
        resourceConfig.setParameter("visitBefore", "false");
        assertTrue(ContentDeliveryConfigBuilder.visitAfterAnnotationsOK(resourceConfig, new MySAXVisitAfterVisitor1()));
    }

    /* ====================================================================================================
             Test classes - visitor impls
       ==================================================================================================== */

    @VisitBeforeIf(condition = "parameters.containsKey('visitBefore') && parameters.visitBefore.value == 'true'")
    private class MySAXVisitBeforeVisitor1 implements SAXVisitBefore {
        public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        }
    }

    @VisitAfterIf(condition = "!parameters.containsKey('visitBefore') || parameters.visitBefore.value != 'true'")
    private class MySAXVisitAfterVisitor1 implements SAXVisitAfter {
        public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        }
    }
}
