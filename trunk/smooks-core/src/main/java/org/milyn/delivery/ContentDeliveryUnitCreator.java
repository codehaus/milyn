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


/**
 * ContentDeliveryUnit factory method Creator interface.
 *
 * @author tfennelly
 */
public interface ContentDeliveryUnitCreator {

    /**
     * Name of the param used on a ContentDeliveryUnitCreator config that specifies
     * the resource type that the creator is adding support for.  This is different
     * from the type attribute on the resource element.  In the case of a ContentDeliveryUnitCreator
     * configuration, the ContentDeliveryUnitCreator impl resource type is "class", but it's adding
     * support for something else (e.g. "xsl").  This is why we can't use the type attribute for this
     * purpose.
     */
    public static final String PARAM_RESTYPE = "restype";

	/**
	 * Create the {@link ContentDeliveryUnit} instance. 
	 * @param resourceConfig The SmooksResourceConfiguration for the {@link ContentDeliveryUnit}
     * to be created.
	 * @return {@link ContentDeliveryUnit} instance.
	 * @throws InstantiationException Unable to create ContentDeliveryUnit instance.
	 */
	public ContentDeliveryUnit create(SmooksResourceConfiguration resourceConfig) throws InstantiationException;
}
