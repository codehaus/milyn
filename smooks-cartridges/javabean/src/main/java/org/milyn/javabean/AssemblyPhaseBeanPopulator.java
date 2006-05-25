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

package org.milyn.javabean;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.assemble.AssemblyUnit;

/**
 * Javabean populator for the {@link org.milyn.delivery.SmooksXML assembly phase}.
 * <p/>
 * See the {@link org.milyn.javabean.BeanPopulator} class for configuration details.
 * @author tfennelly
 */
public class AssemblyPhaseBeanPopulator extends BeanPopulator implements AssemblyUnit {

    /**
     * Public constructor.
     * @param config Configuration.
     */
    public AssemblyPhaseBeanPopulator(SmooksResourceConfiguration config) {
        super(config);
    }

}
