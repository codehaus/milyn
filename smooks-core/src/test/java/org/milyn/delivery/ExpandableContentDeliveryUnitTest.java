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
import org.milyn.delivery.dom.DOMContentDeliveryConfig;
import org.milyn.delivery.dom.serialize.DefaultSerializationUnit;
import org.milyn.delivery.dom.serialize.ContextObjectSerializationUnit;
import org.milyn.container.ExecutionContext;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * ExpandableContentDeliveryUnit tests.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ExpandableContentDeliveryUnitTest extends TestCase {

    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks();

        smooks.addConfigurations("expansion-config.xml", getClass().getResourceAsStream("expansion-config.xml"));
        ExecutionContext context = smooks.createExecutionContext();

        DOMContentDeliveryConfig config = (DOMContentDeliveryConfig) context.getDeliveryConfig();
        ContentDeliveryUnitConfigMapTable assemblyUnits = config.getAssemblyUnits();
        ContentDeliveryUnitConfigMapTable processingUnits = config.getProcessingUnits();
        ContentDeliveryUnitConfigMapTable serializationUnits = config.getSerailizationUnits();

        assertEquals(1, assemblyUnits.getCount());
        assertTrue(assemblyUnits.getMappings("a").get(0).getContentDeliveryUnit() instanceof Assembly1);

        assertEquals(2, processingUnits.getCount());
        assertTrue(processingUnits.getMappings("b").get(0).getContentDeliveryUnit() instanceof Processing1);

        assertEquals(2, serializationUnits.getCount());
        assertTrue(serializationUnits.getMappings("c").get(0).getContentDeliveryUnit() instanceof DefaultSerializationUnit);
        assertTrue(serializationUnits.getMappings("context-object").get(0).getContentDeliveryUnit() instanceof ContextObjectSerializationUnit);
    }
}
