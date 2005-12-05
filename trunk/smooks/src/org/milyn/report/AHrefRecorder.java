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

package org.milyn.report;

import java.net.URI;
import java.util.List;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.w3c.dom.Element;

/**
 * A Href (anchor) recorder {@link org.milyn.delivery.trans.TransUnit}.
 * <p/>
 * This {@link org.milyn.delivery.trans.AbstractTransUnit} implementation
 * simply records &lt;a href=".." /&gt; information from the request page and
 * stores it on the request session {@link org.milyn.report.SessionAHrefList}.
 * This is done in support of peforming "deep" test runs.
 * <p/>
 * hrefs are stored on the the request session {@link org.milyn.report.SessionAHrefList}
 * as instances of {@link java.net.URI}.
 * @author tfennelly
 */
public class AHrefRecorder extends AbstractTransUnit {

	public AHrefRecorder(CDRDef cdrDef) {
		super(cdrDef);
	}

	public void visit(Element element, ContainerRequest containerRequest) {
		String href = element.getAttribute("href");
		
		if(href != null && !href.trim().equals("")) {
			List ahrefList = SessionAHrefList.getList(containerRequest.getSession());
			URI absoluteURI = containerRequest.getRequestURI().resolve(href);
			
			if(!ahrefList.contains(href)) {
				ahrefList.add(absoluteURI);
			}
		}
	}

	public boolean visitBefore() {
		return true;
	}

	public String getShortDescription() {
		return "A-Href Visitor";
	}

	public String getDetailDescription() {
		return "Gathers a-href information from the current page and stores them for performing 'deep' page test runs.";
	}
}
