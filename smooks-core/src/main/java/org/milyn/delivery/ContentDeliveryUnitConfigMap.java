/*
	Milyn - Copyright (C) 2003

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

/**
 * Mapping between a resource configuration and its corresponding resource
 * configuration.
 * <p/>
 * Obviously this class is only relevant when the resource configuration refers to
 * a {@link org.milyn.delivery.ContentDeliveryUnit}.
 * @author tfennelly
 */
public class ContentDeliveryUnitConfigMap {

    private ContentDeliveryUnit contentDeliveryUnit;
    private SmooksResourceConfiguration resourceConfig;

    /**
     * Public constructor.
     * @param contentDeliveryUnit The content delivery unit instance.
     * @param resourceConfig The defining resource configuration.
     */
    public ContentDeliveryUnitConfigMap(ContentDeliveryUnit contentDeliveryUnit, SmooksResourceConfiguration resourceConfig) {
        this.contentDeliveryUnit = contentDeliveryUnit;
        this.resourceConfig = resourceConfig;
    }

    /**
     * Get the content delivery unit.
     * @return The {@link org.milyn.delivery.ContentDeliveryUnit}.
     */
    public ContentDeliveryUnit getContentDeliveryUnit() {
        return contentDeliveryUnit;
    }

    /**
     * Get the resource configuration.
     * @return The {@link SmooksResourceConfiguration}.
     */
    public SmooksResourceConfiguration getResourceConfig() {
        return resourceConfig;
    }
}
