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

import java.util.List;

/**
 * Interface to allow content delivery units expand the configuration by adding additional
 * configurations.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface ExpandableContentDeliveryUnit extends ContentDeliveryUnit {

    /**
     * Get the additional configurations to be added to the delivery config by
     * this ContentDeliveryUnit.
     *
     * @return A list of additional configurations, or an empty list if no configutations
     *         are to be added for this instance.
     */
    public List<SmooksResourceConfiguration> getExpansionConfigurations();
}
