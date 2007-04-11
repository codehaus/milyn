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
package org.milyn.delivery.dom;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.delivery.ContentDeliveryUnitConfigMap;
import org.milyn.container.ExecutionContext;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Tests to make sure the phase annotations work properly.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksVisitorPhaseTest extends TestCase {

    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config1.xml"));
        ExecutionContext execContext;
        DOMContentDeliveryConfig config;

        execContext = smooks.createExecutionContext();
        config = (DOMContentDeliveryConfig) execContext.getDeliveryConfig();

        // Check the assembly units...
        List<ContentDeliveryUnitConfigMap> assemblyUnits = config.getAssemblyUnits().get("a");
        assertEquals(1, assemblyUnits.size());
        assertTrue(assemblyUnits.get(0).getContentDeliveryUnit() instanceof AssemblyVisitor1);

        List<ContentDeliveryUnitConfigMap> processingUnits = config.getProcessingSet("a").getProcessingUnits();
        assertEquals(2, processingUnits.size());
        assertTrue(processingUnits.get(0).getContentDeliveryUnit() instanceof ProcessorVisitor1);
        assertTrue(processingUnits.get(1).getContentDeliveryUnit() instanceof ProcessorVisitor2);
    }
}
