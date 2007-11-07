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
 * Simple table for storing {@link ContentHandlerConfigMap} lists against a selector string.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ContentHandlerConfigMapTable<T extends ContentHandler> {

    private Map<String, List<ContentHandlerConfigMap<T>>> targetMapTable = new Hashtable<String, List<ContentHandlerConfigMap<T>>>();
    private int count = 0;

    /**
     * Add a delivery unit mapping for the specified selector.
     *
     * @param selector The resource/delivery-unit selector.
     * @param resourceConfig Resource configuration.
     * @param deliveryUnit The delivery unit.
     */
    public void addMapping(String selector, SmooksResourceConfiguration resourceConfig, T deliveryUnit) {
        List<ContentHandlerConfigMap<T>> selectorMappings = targetMapTable.get(selector.toLowerCase());

        if(selectorMappings == null) {
            selectorMappings = new Vector<ContentHandlerConfigMap<T>>();
            targetMapTable.put(selector.toLowerCase(), selectorMappings);
        }
        ContentHandlerConfigMap<T> mapInst = new ContentHandlerConfigMap<T>(deliveryUnit, resourceConfig);
        selectorMappings.add(mapInst);
        count++;
    }

    /**
     * Get the {@link ContentHandlerConfigMap} list for the supplied selector string.
     * @param selector The lookup selector.
     * @return It's list of {@link ContentHandlerConfigMap} instances, or null if there are none.
     */
    public List<ContentHandlerConfigMap<T>> getMappings(String selector) {
        return targetMapTable.get(selector.toLowerCase());
    }

    /**
     * Get the combined {@link ContentHandlerConfigMap} list for the supplied list of selector strings.
     * @param selectors The lookup selectors.
     * @return The combined {@link ContentHandlerConfigMap} list for the supplied list of selector strings,
     * or an empty list if there are none.
     */
    public List<ContentHandlerConfigMap<T>> getMappings(String[] selectors) {
        List<ContentHandlerConfigMap<T>> combinedList = new ArrayList<ContentHandlerConfigMap<T>>();

        for(String selector : selectors) {
            List<ContentHandlerConfigMap<T>> selectorList = targetMapTable.get(selector.toLowerCase());
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
