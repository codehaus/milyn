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

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.process.AbstractProcessingUnit;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * XSL ProcessingUnit ConcreteCreator class (GoF - Factory Method).
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
     * Public constructor.
     * @param config Configuration details for this ContentDeliveryUnitCreator.
     */
    public XslContentDeliveryUnitCreator(SmooksResourceConfiguration config) {        
    }
    
	/**
	 * Create an XSL based ContentDeliveryUnit instance ie from an XSL byte stream.
     * @param resourceConfig The SmooksResourceConfiguration for the XSL {@link ContentDeliveryUnit}
     * to be created.
     * @return XSL {@link ContentDeliveryUnit} instance.
	 * @see JavaContentDeliveryUnitCreator 
	 */
	public synchronized ContentDeliveryUnit create(SmooksResourceConfiguration resourceConfig) throws InstantiationException {
		try {
			byte[] xslBytes = resourceConfig.getBytes();

			return new XslProcessingUnit(resourceConfig, xslBytes, resourceConfig.getBoolParameter("visitBefore", false));
		} catch (TransformerConfigurationException e) {
			InstantiationException instanceException = new InstantiationException("XSL ProcessingUnit class resource [" + resourceConfig.getPath() + "] not loadable.  XSL resource invalid.");
			instanceException.initCause(e);
			throw instanceException;
		} catch (IOException e) {
			InstantiationException instanceException = new InstantiationException("XSL ProcessingUnit class resource [" + resourceConfig.getPath() + "] not loadable.  XSL resource not found.");
			instanceException.initCause(e);
			throw instanceException;
		}
	}

	/**
	 * XSLT template application ProcessingUnit.
	 * @author tfennelly
	 */
	private static class XslProcessingUnit extends AbstractProcessingUnit {

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
		 * @param resourceConfig Config. 
		 * @param processingUnitRes The XSL file contents to be parsed into an XSL Template.
		 * @param visitBefore True if XSL is to be applied before visiting child elements.
		 * @throws TransformerConfigurationException Unable to parse XSL.
		 */
		private XslProcessingUnit(SmooksResourceConfiguration resourceConfig, byte[] processingUnitRes, boolean visitBefore) throws TransformerConfigurationException {
			super(resourceConfig);
			StreamSource xslStreamSource = new StreamSource(new ByteArrayInputStream(processingUnitRes));
			TransformerFactory transformer = TransformerFactory.newInstance();

			xslTemplate = transformer.newTemplates(xslStreamSource);
			this.visitBefore = visitBefore;
		}

		/* (non-Javadoc)
		 * @see org.milyn.delivery.ElementVisitor#visitBefore()
		 */
		public boolean visitBefore() {
			return visitBefore;
		}

		/* (non-Javadoc)
		 * @see org.milyn.delivery.ElementVisitor#visit(org.w3c.dom.Element, org.milyn.device.UAContext)
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
	}
}
