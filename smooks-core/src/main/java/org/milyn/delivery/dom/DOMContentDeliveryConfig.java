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

import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ContentDeliveryUnitConfigMap;
import org.milyn.delivery.ContentDeliveryUnitConfigMapTable;
import org.milyn.delivery.dom.ProcessingSet;

import java.util.List;
import java.util.Map;

/**
 * Content Delivery Configuration for DOM.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface DOMContentDeliveryConfig extends ContentDeliveryConfig {

    /**
     * Get the Assembly Unit table for this delivery context.
     * @return The Assembly Unit table for this delivery context.
     */
    public abstract ContentDeliveryUnitConfigMapTable getAssemblyUnits();

    /**
     * Get the Processing Unit table for this delivery context.
     * @return The Processing Unit table for this delivery context.
     */
    public abstract ContentDeliveryUnitConfigMapTable getProcessingUnits();

    /**
     * Get the SerializationUnit table for this delivery context.
     * @return The SerializationUnit table for this delivery context.
     */
    public abstract ContentDeliveryUnitConfigMapTable getSerailizationUnits();
}
