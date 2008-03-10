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

package org.milyn.smooks.pagination;

import java.util.Collection;

import org.milyn.delivery.SmooksHtml;
import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.TransUnit;
import org.w3c.dom.Element;

/**
 * sdoc transformation unit.
 * @author tfennelly
 */
public class SDocTU implements TransUnit {

	/**
	 * Public Constructor
	 * @param unitDef Element CDRDef
	 */
	public SDocTU(CDRDef unitDef) {
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.delivery.trans.TransUnit#visitBefore()
	 */
	public boolean visitBefore() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.trans.TransUnit#visit(org.w3c.dom.Element, org.milyn.container.ContainerRequest)
	 */
	public void visit(Element sdocument, ContainerRequest request) {
		Collection pageList = request.getElementList("spage").getElements();
		
		if(pageList.size() > 0) {
			Element[] pages = new Element[pageList.size()];
			String pageNumParam = request.getParameter("spagenum");
			int pageNum = 1;
			
			pageList.toArray(pages);
			if(pageNumParam != null) {
				try {
					pageNum = Integer.parseInt(pageNumParam);
					pageNum = Math.max(pageNum, 1);
					pageNum = Math.min(pageNum, pages.length);
				} catch(NumberFormatException nfe) {
					pageNum = 1;
				}
			}
			
			pageNum--;
			request.setAttribute(SmooksHtml.DELIVERY_NODE_REQUEST_KEY, pages[pageNum]);
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryUnit#getShortDescription()
	 */
	public String getShortDescription() {
		return "Document pagination 'sdocument' element Transformation Unit.";
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryUnit#getDetailDescription()
	 */
	public String getDetailDescription() {
		return "Document pagination 'sdocument' element Transformation Unit.";
	}
}