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
package org.milyn.event.types;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.VisitSequence;
import org.milyn.event.ElementProcessingEvent;
import org.milyn.event.ResourceBasedEvent;

/**
 * Element Visit Event.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ElementVisitEvent extends ElementProcessingEvent implements ResourceBasedEvent {
    
    private SmooksResourceConfiguration resourceConfig;
    private VisitSequence sequence;

    public ElementVisitEvent(Object element, SmooksResourceConfiguration resourceConfig, VisitSequence sequence) {
        super(element);
        this.resourceConfig = resourceConfig;
        this.sequence = sequence;
    }

    public SmooksResourceConfiguration getResourceConfig() {
        return resourceConfig;
    }

    public VisitSequence getSequence() {
        return sequence;
    }
}