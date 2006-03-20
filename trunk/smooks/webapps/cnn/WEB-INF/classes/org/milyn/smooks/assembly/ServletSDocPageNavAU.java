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

package org.milyn.smooks.assembly;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.container.servlet.HttpServletContainerRequest;
import org.milyn.delivery.ElementList;
import org.milyn.delivery.assemble.AbstractAssemblyUnit;
import org.milyn.dom.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Page navigation assembly unit.
 * <p/>
 * Adds the interpage navigation links.
 * <p/>
 * <b>NB</b>: Only works in a servlet container.
 * @author tfennelly
 */
public class ServletSDocPageNavAU extends AbstractAssemblyUnit {

	private static Log logger = LogFactory.getLog(ServletSDocPageNavAU.class);
	
	/**
	 * Public constructor.
	 * @param unitDef Unit unit def.
	 */
	public ServletSDocPageNavAU(CDRDef unitDef) {
		super(unitDef);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ElementVisitor#visit(org.w3c.dom.Element, org.milyn.container.ContainerRequest)
	 */
	public void visit(Element sdoc, ContainerRequest request) {
		List pages = getPages(request);
		List spagelinks = getSPagesLinks(request);
		int spagelinkCount = spagelinks.size();
		
		if(spagelinkCount > 0) {
			HttpServletRequest servletRequest = ((HttpServletContainerRequest)request).getServletRequest();
			String requestUri = servletRequest.getRequestURI();
	
			for(int i = 0; i < spagelinkCount; i++) {
				Element spagelink = (Element)spagelinks.get(i);
				NodeList links = createLinks(spagelink, pages, requestUri);
				
				DomUtils.insertBefore(links, spagelink);
				spagelink.getParentNode().removeChild(spagelink);
			}
		}
	}

	/**
	 * Create the links (anchors) to replace the supplied spagelink.
	 * @param spagelink spagelink to be replaced by interpage links.
	 * @param pages Document pages.
	 * @param requestUri Container request URI.
	 * @return List of links (anchors).
	 */
	private NodeList createLinks(Element spagelink, List pages, String requestUri) {
		Element containerPage = DomUtils.getParentElement(spagelink, "spage");
		Document ownerDoc = spagelink.getOwnerDocument();
		int pageCount = pages.size();
		boolean hasParams = (requestUri.indexOf("?") != -1);
		
		for(int i = 0; i < pageCount; i++) {
			Element spage = (Element)pages.get(i);
			Element link = ownerDoc.createElement("a");
			
			if(spage != containerPage) {
				if(hasParams) {
					link.setAttribute("href", requestUri + "&spagenum=" + (i + 1));
				} else {
					link.setAttribute("href", requestUri + "?spagenum=" + (i + 1));
				}
			}
			link.appendChild(ownerDoc.createTextNode(spage.getAttribute("title")));
			spagelink.appendChild(link);
		}
		
		return spagelink.getChildNodes();
	}

	/**
	 * Get document page list from the request.
	 * @param request The request.
	 * @return List of pages.
	 */
	private List getPages(ContainerRequest request) {
		ElementList pages = request.getElementList("spage");
		
		return new Vector(pages.getElements());
	}

	/**
	 * Get the document "spagelinks" elements.
	 * @param request The request.
	 * @return List of spagelinks.
	 */
	private List getSPagesLinks(ContainerRequest request) {
		ElementList pages = request.getElementList("spagelinks");

		return new Vector(pages.getElements());
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.assemble.AbstractAssemblyUnit#visitBefore()
	 */
	public boolean visitBefore() {
		return false;
	}
	
	
}
