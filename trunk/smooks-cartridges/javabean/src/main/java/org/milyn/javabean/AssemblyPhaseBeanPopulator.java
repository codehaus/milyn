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

package org.milyn.javabean;

import org.milyn.delivery.dom.Phase;
import org.milyn.delivery.dom.VisitPhase;

/**
 * Javabean populator for the {@link org.milyn.delivery.dom.SmooksDOMFilter assembly phase}.
 * <p/>
 * See the {@link org.milyn.javabean.AbstractBeanPopulator} class for configuration details.
 * @author tfennelly
 */
@Phase(VisitPhase.ASSEMBLY)
public class AssemblyPhaseBeanPopulator extends AbstractBeanPopulator {
}
