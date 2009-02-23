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

package org.milyn.report;

import org.milyn.cdr.CDRDef;
import org.milyn.cdr.Parameter;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.w3c.dom.Node;

/**
 * Abstract Reporting Unit. 
 * <p/>
 * This {@link org.milyn.delivery.trans.TransUnit} implementation adds reporting
 * type methods that can be used by implementations of this class for adding
 * individual report entries.
 * @author tfennelly
 */
public abstract class AbstractReportingUnit extends AbstractTransUnit {
	
	private CDRDef cdrDef;

	/**
	 * Public Constructor.
	 * @param cdrDef Unit Configuration.
	 */
	public AbstractReportingUnit(CDRDef cdrDef) {
		super(cdrDef);
		if(cdrDef == null) {
			throw new IllegalArgumentException("null 'cdrDef' arg in constructor call.");
		}
		this.cdrDef = cdrDef;
	}
	
	/**
	 * Default for test unit implementations is to visit after visiting
	 * the elements child content.  This is so because these units are not
	 * supposed to perform any manipulations of the content, in which case
	 * the visit order is not important.
	 */
	public boolean visitBefore() {
		return false;
	}

	/**
	 * Add the supplied report entry message for the supplied DOM element to the report 
	 * associated with the supplied request.
	 * <p/>
	 * The test unit {@link CDRDef configuration} will contain 
	 * {@link Parameter parameters} used in the report.
	 * @param node The node to be associated with the report entry.
	 * @param containerRequest The request associated with the page being tested.
	 */
	protected final void report(Node node, ContainerRequest containerRequest) {
		PageReport testReport = PageReport.getInstance(containerRequest);
		testReport.report(node, cdrDef);
	}
}
