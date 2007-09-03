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

import org.milyn.cdr.SmooksResourceConfiguration;

import java.util.*;

/**
 * Simple table for storing {@link ContentDeliveryUnitConfigMap} lists against a selector string.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ContentDeliveryUnitConfigMapTable {

    private Map<String, List<ContentDeliveryUnitConfigMap>> targetMapTable = new Hashtable<String, List<ContentDeliveryUnitConfigMap>>();
    private int count = 0;

    /**
     * Add a delivery unit mapping for the specified selector.
     *
     * @param selector The resource/delivery-unit selector.
     * @param resourceConfig Resource configuration.
     * @param deliveryUnit The delivery unit.
     */
    public void addMapping(String selector, SmooksResourceConfiguration resourceConfig, ContentDeliveryUnit deliveryUnit) {
        List<ContentDeliveryUnitConfigMap> selectorMappings = targetMapTable.get(selector.toLowerCase());

        if(selectorMappings == null) {
            selectorMappings = new Vector<ContentDeliveryUnitConfigMap>();
            targetMapTable.put(selector.toLowerCase(), selectorMappings);
        }
        ContentDeliveryUnitConfigMap mapInst = new ContentDeliveryUnitConfigMap(deliveryUnit, resourceConfig);
        selectorMappings.add(mapInst);
        count++;
    }

    /**
     * Get the {@link ContentDeliveryUnitConfigMap} list for the supplied selector string.
     * @param selector The lookup selector.
     * @return It's list of {@link ContentDeliveryUnitConfigMap} instances, or null if there are none.
     */
    public List<ContentDeliveryUnitConfigMap> getMappings(String selector) {
        return targetMapTable.get(selector.toLowerCase());
    }

    /**
     * Get the combined {@link ContentDeliveryUnitConfigMap} list for the supplied list of selector strings.
     * @param selectors The lookup selectors.
     * @return The combined {@link ContentDeliveryUnitConfigMap} list for the supplied list of selector strings,
     * or an empty list if there are none.
     */
    public List<ContentDeliveryUnitConfigMap> getMappings(String[] selectors) {
        List<ContentDeliveryUnitConfigMap> combinedList = new ArrayList<ContentDeliveryUnitConfigMap>();

        for(String selector : selectors) {
            List<ContentDeliveryUnitConfigMap> selectorList = targetMapTable.get(selector.toLowerCase());
            if(selectorList != null) {
                combinedList.addAll(selectorList);
            }
        }

        return combinedList;
    }

    /**
     * Is the table empty.
     * @return True if the table is empty, otherwise false.
     */
    public boolean isEmpty() {
        return targetMapTable.isEmpty();
    }

    /**
     * Get the total number of mappings on this table.
     * @return The total number of mappings on this table.
     */
    public int getCount() {
        return count;
    }
}
