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
     * <p/>
     * The table is keyed by element name and the values are
     * {@link org.milyn.delivery.ContentDeliveryUnitConfigMap} instances where the contained
     * {@link org.milyn.delivery.ContentDeliveryUnit} is an {@link DOMElementVisitor}.
     * @return The AssemblyUnit table for this delivery context.
     */
    public abstract Map<String, List<ContentDeliveryUnitConfigMap>> getAssemblyUnits();

    /**
     * Get the ProcessingUnit configure instances, for the named tag, for the useragent
     * associated with this table.
     * @param tag The tag name for which the ProcessingUnits are being requested.
     * @return ProcessingSet for the specified tag name, or null if none is specified.
     */
    public abstract ProcessingSet getProcessingSet(String tag);

    /**
     * Get the SerializationUnit table for this delivery context.
     * <p/>
     * The table is keyed by element name and the values are
     * {@link org.milyn.delivery.ContentDeliveryUnitConfigMap} instances where the contained
     * {@link org.milyn.delivery.ContentDeliveryUnit} is a {@link org.milyn.delivery.dom.serialize.SerializationUnit}.
     * @return The SerializationUnit table for this delivery context.
     */
    public abstract Map<String, List<ContentDeliveryUnitConfigMap>> getSerailizationUnits();
}
