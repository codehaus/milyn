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
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMContentDeliveryConfig;
import org.milyn.delivery.dom.DOMVisitAfter;
import org.milyn.delivery.dom.DOMVisitBefore;
import org.milyn.delivery.dom.serialize.ContextObjectSerializationUnit;
import org.milyn.delivery.dom.serialize.DefaultSerializationUnit;
import org.milyn.delivery.dom.serialize.SerializationUnit;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * ConfigurationExpander tests.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ExpandableContentDeliveryUnitTest extends TestCase {

    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks();

        smooks.addConfigurations("expansion-config.xml", getClass().getResourceAsStream("expansion-config.xml"));
        ExecutionContext context = smooks.createExecutionContext();

        DOMContentDeliveryConfig config = (DOMContentDeliveryConfig) context.getDeliveryConfig();
        ContentHandlerConfigMapTable<DOMVisitBefore> assemblyVisitBefores = config.getAssemblyVisitBefores();
        ContentHandlerConfigMapTable<DOMVisitAfter> assemblyVisitAfters = config.getAssemblyVisitAfters();
        ContentHandlerConfigMapTable<DOMVisitBefore> processingVisitBefores = config.getProcessingVisitBefores();
        ContentHandlerConfigMapTable<DOMVisitAfter> processingVisitAfters = config.getProcessingVisitAfters();
        ContentHandlerConfigMapTable<SerializationUnit> serializationUnits = config.getSerailizationVisitors();

        assertEquals(1, assemblyVisitBefores.getCount());
        assertTrue(assemblyVisitBefores.getMappings("a").get(0).getContentHandler() instanceof Assembly1);
        assertEquals(1, assemblyVisitAfters.getCount());
        assertTrue(assemblyVisitAfters.getMappings("a").get(0).getContentHandler() instanceof Assembly1);

        assertEquals(2, processingVisitBefores.getCount());
        assertTrue(processingVisitBefores.getMappings("b").get(0).getContentHandler() instanceof Processing1);
        assertEquals(2, processingVisitAfters.getCount());
        assertTrue(processingVisitAfters.getMappings("b").get(0).getContentHandler() instanceof Processing1);

        assertEquals(2, serializationUnits.getCount());
        assertTrue(serializationUnits.getMappings("c").get(0).getContentHandler() instanceof DefaultSerializationUnit);
        assertTrue(serializationUnits.getMappings("context-object").get(0).getContentHandler() instanceof ContextObjectSerializationUnit);
    }
}
