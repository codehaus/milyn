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

package org.milyn.delivery.serialize;

import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.milyn.cdr.CDRDef;
import org.milyn.cdr.cdrar.CDRArchiveEntry;
import org.milyn.cdr.cdrar.CDRArchiveEntryNotFoundException;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.dom.DomUtils;
import org.milyn.report.serialize.ReportSerializationUnit;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Node serializer.
 * <p/>
 * This class uses the {@link org.milyn.delivery.ContentDeliveryConfig} and the 
 * {@link org.milyn.delivery.serialize.SerializationUnit} instances defined there on
 * to perform the serialization. 
 * @author tfennelly
 */
public class Serializer {
	
	/**
	 * Node to be serialized.
	 */
	private Node node;
	/**
	 * Target device context.
	 */
	private ContainerRequest containerRequest;
	/**
	 * Target content delivery config.
	 */
	private ContentDeliveryConfig deliveryConfig;
	/**
	 * Target content delivery context SerializationUnit definitions.
	 */
	private Hashtable serializationUnits;
	/**
	 * Default SerializationUnit.
	 */
	private List defaultSUs;

	/**
	 * Public constructor.
	 * @param node Node to be serialized.
	 * @param containerRequest Target device context.
	 */
	public Serializer(Node node, ContainerRequest containerRequest) {
		if(node == null) {
			throw new IllegalArgumentException("null 'node' arg passed in method call.");
		} else if(containerRequest == null) {
			throw new IllegalArgumentException("null 'containerRequest' arg passed in method call.");
		}
		this.node = node;
		this.containerRequest = containerRequest;
		// Get the delivery context for the device.
		deliveryConfig = containerRequest.getDeliveryConfig();
		// Initialise the serializationUnits member
		serializationUnits = deliveryConfig.getSerailizationUnits();
		// Set the default SerializationUnit
		defaultSUs = (List)serializationUnits.get("*");
		if(defaultSUs == null) {
			CDRDef cdrDef = new CDRDef("*", "*", DefaultSerializationUnit.class.getName());
			defaultSUs = new Vector();
			defaultSUs.add(new DefaultSerializationUnit(cdrDef));
		}
	}
	
	/**
	 * Serialise the document to the supplied output writer instance.
	 * <p/>
	 * Adds the DOCTYPE decl if one defined in the Content Delivery Configuration.
	 * <p/>
	 * If the node is a Document (or DocumentFragment) node the whole node is serialised.  
	 * Otherwise, only the node child elements are serialised i.e. the node itself is skipped.
	 * @param writer Output writer.
	 * @throws CDRArchiveEntryNotFoundException DOM Serialiser exception.
	 * @throws IOException Unable to write to output writer.
	 */
	public void serailize(Writer writer) throws CDRArchiveEntryNotFoundException, IOException {		
		List docTypeUDs;
		
		if(writer == null) {
			throw new IllegalArgumentException("null 'writer' arg passed in method call.");
		} 

		if(node instanceof Document) {
			Document doc = (Document)node;
			Element rootElement = doc.getDocumentElement();
			String rootElementName = DomUtils.getName(rootElement);
			CDRDef docTypeCDRDef = null;

			// Check for a DOCTYPE decl CDRDefs for this delivery context.
			docTypeUDs = deliveryConfig.getCDRDefs("doctype");
			if(docTypeUDs != null && docTypeUDs.size() > 0) {
				docTypeCDRDef = (CDRDef)docTypeUDs.get(0);
			}
			
			// Only use the cdrdef if the override flag is set.  The override flag will
			// cause this DOCTYPE to override any DOCYTPE decl from the source doc.
			if(docTypeCDRDef != null && docTypeCDRDef.getBoolParameter("override", true)) {
				String path = docTypeCDRDef.getPath();
				
				if(path != null) {
					CDRArchiveEntry docTypeEntry = containerRequest.getContext().getCdrarStore().getEntry(docTypeCDRDef.getPath());
					
					writer.write(new String(docTypeEntry.getEntryBytes()));
				} else {
					String publicId = docTypeCDRDef.getStringParameter("publicId", "!!publicId undefined - fix cdres!!");
					String systemId = docTypeCDRDef.getStringParameter("systemId", "!!systemId undefined - fix cdres!!");
					String xmlns = docTypeCDRDef.getStringParameter("xmlns");

					serializeDoctype(publicId, systemId, rootElementName, writer);
					if(xmlns != null) {
						rootElement.setAttribute("xmlns", xmlns);
					} else {
						rootElement.removeAttribute("xmlns");
					}
				}
			} else {
				// If there's no CDRDef doctypes defined, was there a DOCTYPE decl in 
				// the source Document?  If there was, write this out as the DOCTYPE.
				DocumentType docType = doc.getDoctype();
				
				if(docType != null) {
					serializeDoctype(docType.getPublicId(), docType.getSystemId(), rootElementName, writer);
				}
			}
		}
		
		// Write the DOM, the child elements of the node
		NodeList deliveryNodes = node.getChildNodes();
		int nodeCount = deliveryNodes.getLength();
		for(int i = 0; i < nodeCount; i++) {
			Node childNode = deliveryNodes.item(i);
			if(childNode.getNodeType() == Node.ELEMENT_NODE) {
				recursiveDOMWrite((Element)childNode, writer);
			}
		}
	}

	/**
	 * Serialize the DocumentType.
	 * <p/>
	 * Only called if a DOCTYPE was suppied in the original source and
	 * no doctype overrides were configured (in .cdrl) for the requesting
	 * device.
	 * @param docType The DocumentType instance to serialize.
	 * @param rootElement The root element name.
	 * @param writer The target writer.
	 * @throws IOException Exception writing to the output writer.
	 */
	private void serializeDoctype(String publicId, String systemId, String rootElement, Writer writer) throws IOException {

		if(defaultSUs instanceof ReportSerializationUnit) {
			writer.write("&lt;!DOCTYPE ");
		} else {
			writer.write("<!DOCTYPE ");
		}
		writer.write(rootElement);
		writer.write(' ');
		if(publicId != null) {
			writer.write("PUBLIC \"");
			writer.write(publicId);
			writer.write("\" ");
		}
		if(systemId != null) {
			writer.write('"');
			writer.write(systemId);
			writer.write('"');
		}
		if(defaultSUs instanceof ReportSerializationUnit) {
			writer.write("&gt;");
		} else {
			writer.write('>');
		}
		writer.write('\n');
	}

	/**
	 * Recursively write the DOM tree to the supplied writer.
	 * @param element Element to write.
	 * @param writer Writer to use.
	 * @throws IOException Exception writing to Writer. 
	 */
	private void recursiveDOMWrite(Element element, Writer writer) throws IOException {
		SerializationUnit elementSU;
		NodeList children = element.getChildNodes();

		elementSU = getSerializationUnit(element);
		elementSU.writeElementStart(element, writer, containerRequest);
		if(children != null && children.getLength() > 0) {
			int childCount = children.getLength();

			for(int i = 0; i < childCount; i++) {
				Node childNode = children.item(i);
				
				switch(childNode.getNodeType()) {
					case Node.CDATA_SECTION_NODE: {
						elementSU.writeElementCDATA((CDATASection)childNode, writer, containerRequest);
						break;
					}		
					case Node.COMMENT_NODE: { 
						elementSU.writeElementComment((Comment)childNode, writer, containerRequest);
						break;
					}		
					case Node.ELEMENT_NODE: { 
						if(elementSU.writeChildElements()) {
							recursiveDOMWrite((Element)childNode, writer);
						}
						break;
					}		
					case Node.ENTITY_REFERENCE_NODE: { 
						elementSU.writeElementEntityRef((EntityReference)childNode, writer, containerRequest);
						break;
					}		
					case Node.TEXT_NODE: { 
						elementSU.writeElementText((Text)childNode, writer, containerRequest);
						break;
					}		
					default: {
						elementSU.writeElementNode(childNode, writer, containerRequest);
						break;
					}
				}
			}
		}
		elementSU.writeElementEnd(element, writer, containerRequest);
	}

	/**
	 * Get the first matching serialisation unit for the supplied element.
	 * @param element Element to be serialized.
	 * @return SerializationUnit.
	 */
	private SerializationUnit getSerializationUnit(Element element) {
		String elementName;
		elementName = element.getLocalName();
		if(elementName == null) {
			elementName = element.getTagName();
		}
		List elementSUs = (List)serializationUnits.get(elementName);
		if(elementSUs == null) {
			elementSUs = defaultSUs;
		}
		int numSUs = elementSUs.size();
		
		for(int i = 0; i < numSUs; i++) {
			SerializationUnit su = (SerializationUnit)elementSUs.get(i);
			if(su.isInNamespace(element)) {
				return su;
			}
		}
		
		throw new IllegalStateException("At least 1 SerializationUnit needs to be configured for an element. " + element.getTagName() + " has no configured SerializationUnit.");
	}
}