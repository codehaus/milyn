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

package org.milyn.report.testcdus;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.report.AbstractReportingUnit;
import org.w3c.dom.Element;

public class SimpleReportingUnit extends AbstractReportingUnit {

	public SimpleReportingUnit(CDRDef cdrDef) {
		super(cdrDef);
	}

	public void visit(Element element, ContainerRequest containerRequest) {
		// Just report on it emmediately
		this.report(element, containerRequest);
	}

	public String getShortDescription() {
		return null;
	}

	public String getDetailDescription() {
		return null;
	}
}
