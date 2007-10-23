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
import org.milyn.container.ApplicationContext;
import org.milyn.dtd.DTDStore;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Abstract {@link ContentDeliveryConfig}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class AbstractContentDeliveryConfig implements ContentDeliveryConfig {

    /**
     * Container context.
     */
    private ApplicationContext applicationContext;
    /**
     * Table of SmooksResourceConfiguration instances keyed by selector value. Each table entry
     * contains a List of SmooksResourceConfiguration instances.
     */
    private Hashtable<String, List<SmooksResourceConfiguration>> resourceConfigTable = new Hashtable<String, List<SmooksResourceConfiguration>>();
    /**
     * Table of Object instance lists keyed by selector. Each table entry
     * contains a List of Objects.
     */
    private Map objectsTable = new Hashtable();
    /**
     * DTD for the associated device.
     */
    private DTDStore.DTDObjectContainer dtd;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Get the list of {@link org.milyn.cdr.SmooksResourceConfiguration}s for the specified selector definition.
     * @param selector The configuration "selector" attribute value from the .cdrl file in the .cdrar.
     * @return List of SmooksResourceConfiguration instances, or null.
     */
    public List getSmooksResourceConfigurations(String selector) {
        return resourceConfigTable.get(selector.toLowerCase());
    }

    public void setSmooksResourceConfigurations(Hashtable<String, List<SmooksResourceConfiguration>> resourceConfigTable) {
        this.resourceConfigTable = resourceConfigTable;
    }

    /**
     * Get the {@link org.milyn.cdr.SmooksResourceConfiguration} map for the target execution context.
     * <p/>
     * This Map will be {@link org.milyn.cdr.SmooksResourceConfigurationSortComparator preordered}
     * for the target execution context.
     *
     * @return {@link org.milyn.cdr.SmooksResourceConfiguration} map for the target execution context, keyed by the configuration
     * {@link org.milyn.cdr.SmooksResourceConfiguration#getSelector() selector}, with each value being a
     * {@link List} of preordered {@link org.milyn.cdr.SmooksResourceConfiguration} instances.
     */
    public Map getSmooksResourceConfigurations() {
        return resourceConfigTable;
    }

    private static final Vector EMPTY_LIST = new Vector();

    /**
     * Get a list {@link Object}s from the supplied {@link org.milyn.cdr.SmooksResourceConfiguration} selector value.
     * <p/>
     * Uses {@link org.milyn.cdr.SmooksResourceConfigurationStore#getObject(org.milyn.cdr.SmooksResourceConfiguration)} to construct the object.
     * @param selector selector attribute value from the .cdrl file in the .cdrar.
     * @return List of Object instances.  An empty list is returned where no
     * selectors exist.
     */
    public List getObjects(String selector) {
        Vector objects;

        selector = selector.toLowerCase();
        objects = (Vector)objectsTable.get(selector);
        if(objects == null) {
            List unitDefs = resourceConfigTable.get(selector);

            if(unitDefs != null && unitDefs.size() > 0) {
                objects = new Vector(unitDefs.size());

                if(applicationContext == null) {
                    throw new IllegalStateException("Call to getObjects() before the setApplicationContext() was called.");
                }

                for(int i = 0; i < unitDefs.size(); i++) {
                    SmooksResourceConfiguration resConfig = (SmooksResourceConfiguration)unitDefs.get(i);
                    objects.add(applicationContext.getStore().getObject(resConfig));
                }
            } else {
                objects = EMPTY_LIST;
            }

            objectsTable.put(selector, objects);
        }

        return objects;
    }

    /* (non-Javadoc)
     * @see org.milyn.delivery.ContentDeliveryConfig#getDTD()
     */
    public DTDStore.DTDObjectContainer getDTD() {
        return dtd;
    }

    public void setDtd(DTDStore.DTDObjectContainer dtd) {
        this.dtd = dtd;
    }
}
