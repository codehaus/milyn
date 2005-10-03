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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.assemble.AbstractAssemblyUnit;
import org.milyn.dom.DomUtils;
import org.milyn.dom.Parser;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * sput element AssemblyUnit.
 * <p/>
 * Colaborates with 'scontent' elements to perform the content insert.
 * @author tfennelly
 */
public class SPutAU extends AbstractAssemblyUnit {

	/**
	 * Logger.
	 */
	private Log logger = LogFactory.getLog(SPutAU.class);

	/**
	 * Public Constructor
	 * @param unitDef
	 */
	public SPutAU(CDRDef unitDef) {
		super(unitDef);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ElementVisitor#visit(org.w3c.dom.Element, org.milyn.container.ContainerRequest)
	 */
	public void visit(Element element, ContainerRequest request) {
		String scontentIdRefVal = element.getAttribute("idref");
		
		if(scontentIdRefVal != null && !(scontentIdRefVal = scontentIdRefVal.trim()).equals("")) {
			int colonIndex = scontentIdRefVal.indexOf(':');
			Node src = null;
			
			// We may have an xpath ref
			if(colonIndex != -1 && colonIndex != scontentIdRefVal.length() - 1) {
				String scontentIdRef = scontentIdRefVal.substring(0, colonIndex);
				
				src = getContent(scontentIdRef, request);
				if(src != null) {
					String xpath = scontentIdRefVal.substring(colonIndex + 1);
					NodeList nodeList;
					
					if(xpath.charAt(0) == '/') {
						if(xpath.length() > 1) {
							xpath = xpath.substring(1);
						} else {
							xpath = "*";
						}
					}
					nodeList = XmlUtil.getNodeList(src, xpath);
					if(nodeList == null) {
						logger.warn("XPath reference [" + xpath + "] failed on [" + scontentIdRef + "]");
					} else {
						DomUtils.replaceNode(nodeList, element);
						return;
					}
				}
			} else {
				// insert from the root - insert it all
				src = getContent(scontentIdRefVal, request);
				if(src != null) {
					// Clone it incase another sput references part or all of this
					// content within this document.  It's an overhead but I don't think
					// it can be avoided.
					src = src.cloneNode(true);
					if(((Element)src).getTagName().equalsIgnoreCase("scontent")) {
						DomUtils.replaceNode(src.getChildNodes(), element);
					} else {
						DomUtils.replaceNode(src, element);
					}
					return;
				}
			}
			logger.warn("No content for idref [" + scontentIdRefVal + "].  No content inserted.");
			Document doc = element.getOwnerDocument();
			
			src = doc.createComment("sput:" + scontentIdRefVal);
			DomUtils.replaceNode(src, element);
		} else {
			logger.warn("null or empty idref attribute on sput.  No content inserted.");
		}
	}

	/**
	 * Get the source content identified by the supplied reference.
	 * @param scontentIdRef scontent reference.
	 * @param request Current request.
	 * @return W3C Node element containing the source content, or null if not available.
	 */
	private Node getContent(String scontentIdRef, ContainerRequest request) {
		Element scontent = request.getElementList("scontent").getElement(scontentIdRef);
			
		if(scontent != null && !scontent.hasChildNodes()) {
			String src = scontent.getAttribute("src");
			
			if(src != null && !(src = src.trim()).equals("")) {
		        InputStream srcStream;
				try {
					srcStream = request.getContext().getResourceLocator().getResource(src);
					if(srcStream != null) {
				        try {
				        	// Parse the fragment and merge into the scontent.
				        	// TODO: Encoding?? 
				        	Parser parser = new Parser(request);
				        	parser.append(new InputStreamReader(srcStream), scontent);
						} catch (SAXException e) {
							logger.error("SAXExcepting parsing scontent src [" + src + "]", e);
						} catch (IOException e) {
							logger.error("IOExcepting parsing scontent src [" + src + "]", e);
						}
			        }
				} catch (IOException e) {
					logger.error("IOExcepting sourcing scontent src [" + src + "]", e);
				}
			} else {
				logger.warn("null or empty src attribute on scontent [" + scontentIdRef + "].  No content sourced.");
			}
		}
		
        return scontent;
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryUnit#getShortDescription()
	 */
	public String getShortDescription() {
		return "Inserts content from the referenced 'scontent' element.";
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryUnit#getDetailDescription()
	 */
	public String getDetailDescription() {
		return "Inserts content from the referenced 'scontent' element.  The 'scontent' element sources the content and 'sput' elements insert parts (or all) of it in different locations in the document.";
	}
}
