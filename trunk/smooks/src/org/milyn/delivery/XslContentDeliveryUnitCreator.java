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

package org.milyn.delivery;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.milyn.cdr.CDRStore;
import org.milyn.cdr.CDRDef;
import org.milyn.cdr.cdrar.CDRArchiveEntryNotFoundException;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.milyn.delivery.trans.TransUnit;
import org.milyn.io.StreamUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * XSL TransUnit ConcreteCreator class (GoF - Factory Method).
 * <p/>
 * This Content Delivery Unit creator needs to be reviewed by someone that knows more about XSL etc.
 * Not convinced that it's implemented correctly.  Works, but?  Not even convinced that it makes sense!
 * May be dumped.
 * 
 * @see JavaContentDeliveryUnitCreator 
 * @author tfennelly
 */
public class XslContentDeliveryUnitCreator implements ContentDeliveryUnitCreator {

	/**
	 * Create an XSL based ContentDeliveryUnit instance ie from an XSL byte stream.
	 * <p/>
	 * @see JavaContentDeliveryUnitCreator 
	 */
	public synchronized ContentDeliveryUnit create(CDRDef cdrDef, CDRStore cdrarStore) throws InstantiationException {
		try {
			byte[] xslBytes = null;

			try {
				// Try load it from the cdrars.
				xslBytes = cdrarStore.getEntry(cdrDef).getEntryBytes();
			} catch (CDRArchiveEntryNotFoundException e) {
				// OK, try get the xsl file from the classpath.
				xslBytes = StreamUtils.readStream(getClass().getResourceAsStream("/" + cdrDef.getPath()));
			}
			return new XslTransUnit(cdrDef, xslBytes, cdrDef.getBoolParameter("visitBefore", false));
		} catch (TransformerConfigurationException e) {
			InstantiationException instanceException = new InstantiationException("XSL TransUnit class resource [" + cdrDef.getPath() + "] not loadable.  XSL resource invalid.");
			instanceException.initCause(e);
			throw instanceException;
		} catch (IOException e) {
			InstantiationException instanceException = new InstantiationException("XSL TransUnit class resource [" + cdrDef.getPath() + "] not loadable.  XSL resource not found.");
			instanceException.initCause(e);
			throw instanceException;
		}
	}

	/**
	 * XSLT template application TransUnit.
	 * @author tfennelly
	 */
	private static class XslTransUnit extends AbstractTransUnit {

		/**
		 * XSL template to be applied to the visited element.
		 */
		private Templates xslTemplate;
		/**
		 * Visit before visiting child elements.
		 */
		private boolean visitBefore = false;
		
		/**
		 * Constructor.
		 * <p/>
		 * Create the XSL Template from the Content Delivery Resource bytes.
		 * @param cdrDef Config. 
		 * @param transUnitRes The XSL file contents to be parsed into an XSL Template.
		 * @param visitBefore True if XSL is to be applied before visiting child elements.
		 * @throws TransformerConfigurationException Unable to parse XSL.
		 */
		private XslTransUnit(CDRDef cdrDef, byte[] transUnitRes, boolean visitBefore) throws TransformerConfigurationException {
			super(cdrDef);
			StreamSource xslStreamSource = new StreamSource(new ByteArrayInputStream(transUnitRes));
			TransformerFactory transformer = TransformerFactory.newInstance();

			xslTemplate = transformer.newTemplates(xslStreamSource);
			this.visitBefore = visitBefore;
		}

		/* (non-Javadoc)
		 * @see org.milyn.trans.TransUnit#visitBefore()
		 */
		public boolean visitBefore() {
			return visitBefore;
		}

		/* (non-Javadoc)
		 * @see org.milyn.trans.TransUnit#visit(org.w3c.dom.Element, org.milyn.device.UAContext)
		 */
		public void visit(Element element, ContainerRequest containerRequest) {
			Node transRes = element.getOwnerDocument().createElement("xsltrans");
			Node parent = element.getParentNode();
			NodeList children = null;

			// TODO:  This is probably completely wrong.  Need to find someone that knows something about XSLT.
			try {
				xslTemplate.newTransformer().transform(new DOMSource(element), new DOMResult(transRes));
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			children = transRes.getChildNodes();
			if(children != null) {
				for(int i = 0; i < children.getLength(); i++) {
					parent.insertBefore(children.item(i), element);
				}
			}
			parent.removeChild(element);
		}

		/* (non-Javadoc)
		 * @see org.milyn.trans.TransUnit#getShortDescription()
		 */
		public String getShortDescription() {
			return "XSL Transformation Unit";
		}

		/* (non-Javadoc)
		 * @see org.milyn.trans.TransUnit#getDetailDescription()
		 */
		public String getDetailDescription() {
			return "XSL Transformation Unit";
		}		
	}
}
