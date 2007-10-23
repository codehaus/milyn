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

import org.milyn.delivery.AbstractContentDeliveryConfig;
import org.milyn.delivery.ContentDeliveryUnitConfigMapTable;

/**
 * DOM specific {@link org.milyn.delivery.ContentDeliveryConfig} implementation.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DOMContentDeliveryConfig extends AbstractContentDeliveryConfig {

    private ContentDeliveryUnitConfigMapTable assemblyUnits;
    private ContentDeliveryUnitConfigMapTable processingUnits;
    private ContentDeliveryUnitConfigMapTable serailizationUnits;

    public ContentDeliveryUnitConfigMapTable getAssemblyUnits() {
        return assemblyUnits;
    }

    public void setAssemblyUnits(ContentDeliveryUnitConfigMapTable assemblyUnits) {
        this.assemblyUnits = assemblyUnits;
    }

    public ContentDeliveryUnitConfigMapTable getProcessingUnits() {
        return processingUnits;
    }

    public void setProcessingUnits(ContentDeliveryUnitConfigMapTable processingUnits) {
        this.processingUnits = processingUnits;
    }

    public ContentDeliveryUnitConfigMapTable getSerailizationUnits() {
        return serailizationUnits;
    }

    public void setSerailizationUnits(ContentDeliveryUnitConfigMapTable serailizationUnits) {
        this.serailizationUnits = serailizationUnits;
    }
}
