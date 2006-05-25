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

package org.milyn.delivery.assemble;

import org.milyn.delivery.ElementVisitor;

/**
 * Assembly Unit interface definition.
 * <p/>
 * Implementations of this interface are applied to the content during the
 * Assembly Phase (see {@link org.milyn.delivery.SmooksXML}).
 * <p/>
 * Multiple AssemblyUnit implementations can be applied to a single element.  The order in which 
 * they are applied is determined by the {@link org.milyn.cdr.SmooksResourceConfiguration} specificity. 
 * See {@link org.milyn.cdr.SmooksResourceConfigurationSortComparator}.
 * <p/>
 * All implementations must contain a public constructor that takes a {@link org.milyn.cdr.SmooksResourceConfiguration} instance as 
 * a parameter.  For this reason condsider extending {@link org.milyn.delivery.assemble.AbstractAssemblyUnit} for
 * convienience (your IDE should auto-add the constructor).
 * <p/>
 * Assembly Units are defined in the .cdrl file (in .cdrar file(s)) in the very
 * same way as any other content delivery resource (Trans Units, Serialization Units, 
 * DTDs etc).
 * <p/>
 * Assembly units always visit target elements <b>after</b> Smooks iterates over the child
 * content of the target element. See <a href="../package-summary.html">Delivery Overview</a>.
 * @author tfennelly
 */
public interface AssemblyUnit extends ElementVisitor {
}
