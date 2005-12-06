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

import org.milyn.cdr.CDRDef;
import org.milyn.cdr.cdrar.CDRArchiveEntry;
import org.milyn.cdr.cdrar.CDRArchiveEntryNotFoundException;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.ContentDeliveryConfig;
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
	private SerializationUnit defaultSU;
	/**
	 * Carriage-Return Line-Feed static def.
	 */
	private static final String CRLF = "\r\n";

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
		defaultSU = (SerializationUnit)serializationUnits.get("*");
		if(defaultSU == null) {
			defaultSU = new DefaultSerializationUnit(null);
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
			// Get the DOCTYPE decl CDRDefs for this delivery context and write the 1st one.
			docTypeUDs = deliveryConfig.getCDRDefs("doctype");
			if(docTypeUDs != null && docTypeUDs.size() > 0) {
				CDRDef docTypeCDRDef = (CDRDef)docTypeUDs.get(0);			
				CDRArchiveEntry docTypeEntry = containerRequest.getContext().getCdrarStore().getEntry(docTypeCDRDef.getPath());
				
				writer.write(new String(docTypeEntry.getEntryBytes()));
			} else {
				// If there's no CDRDef doctypes defined, was there a DOCTYPE decl in 
				// the source Document?  If there was, write this out as the DOCTYPE.
				Document doc = (Document)node;
				DocumentType docType = doc.getDoctype();
				if(docType != null) {
					String rootElement = doc.getDocumentElement().getTagName();
					serializeDoctype(docType, rootElement, writer);
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
	private void serializeDoctype(DocumentType docType, String rootElement, Writer writer) throws IOException {
		String publicId = docType.getPublicId();
		String systemId = docType.getSystemId();
		
		if(defaultSU instanceof ReportSerializationUnit) {
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
		if(defaultSU instanceof ReportSerializationUnit) {
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
		SerializationUnit elementSU = (SerializationUnit)serializationUnits.get(element.getTagName());
		NodeList children = element.getChildNodes();
		
		if(elementSU == null) {
			elementSU = defaultSU;
		}

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
}