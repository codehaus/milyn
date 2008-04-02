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

package org.milyn.delivery.dom.serialize;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Vector;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.ResourceConfigurationNotFoundException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentDeliveryUnitConfigMap;
import org.milyn.delivery.ContentDeliveryUnitConfigMapTable;
import org.milyn.delivery.dom.DOMContentDeliveryConfig;
import org.milyn.xml.DomUtils;
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
 * {@link org.milyn.delivery.dom.serialize.SerializationUnit} instances defined there on
 * to perform the serialization. 
 * @author tfennelly
 */
public class Serializer {
	
	/**
	 * Logger.
	 */
	private static Log logger = LogFactory.getLog(Serializer.class);
	/**
	 * Node to be serialized.
	 */
	private Node node;
	/**
	 * Target device context.
	 */
	private ExecutionContext executionContext;
	/**
	 * Target content delivery config.
	 */
	private DOMContentDeliveryConfig deliveryConfig;
	/**
	 * Target content delivery context SerializationUnit definitions.
	 */
	private ContentDeliveryUnitConfigMapTable serializationUnits;
	/**
	 * Default SerializationUnit.
	 */
	private List defaultSUs;

	/**
	 * Public constructor.
	 * @param node Node to be serialized.
	 * @param executionContext Target device context.
	 */
	public Serializer(Node node, ExecutionContext executionContext) {
		if(node == null) {
			throw new IllegalArgumentException("null 'node' arg passed in method call.");
		} else if(executionContext == null) {
			throw new IllegalArgumentException("null 'executionContext' arg passed in method call.");
		}
		this.node = node;
		this.executionContext = executionContext;
		// Get the delivery context for the device.
		deliveryConfig = (DOMContentDeliveryConfig) executionContext.getDeliveryConfig();
		// Initialise the serializationUnits member
		serializationUnits = deliveryConfig.getSerailizationUnits();
		// Set the default SerializationUnit
		defaultSUs = (List)serializationUnits.getMappings("*");
		if(defaultSUs == null) {
			SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration("*", "*", DefaultSerializationUnit.class.getName());
			defaultSUs = new Vector();
			defaultSUs.add(new ContentDeliveryUnitConfigMap(new DefaultSerializationUnit(resourceConfig), resourceConfig));
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
	 * @throws ResourceConfigurationNotFoundException DOM Serialiser exception.
	 * @throws IOException Unable to write to output writer.
	 */
	public void serailize(Writer writer) throws ResourceConfigurationNotFoundException, IOException {		
		List docTypeUDs;
		
		if(writer == null) {
			throw new IllegalArgumentException("null 'writer' arg passed in method call.");
		} 

		if(node instanceof Document) {
			Document doc = (Document)node;
			Element rootElement = doc.getDocumentElement();
			String rootElementName = DomUtils.getName(rootElement);
			SmooksResourceConfiguration docTypeSmooksResourceConfiguration = null;

			// Check for a DOCTYPE decl SmooksResourceConfigurations for this delivery context.
			docTypeUDs = deliveryConfig.getSmooksResourceConfigurations("doctype");
			if(docTypeUDs != null && docTypeUDs.size() > 0) {
				docTypeSmooksResourceConfiguration = (SmooksResourceConfiguration)docTypeUDs.get(0);
			}
			
			// Only use the cdrdef if the override flag is set.  The override flag will
			// cause this DOCTYPE to override any DOCYTPE decl from the source doc.
			if(docTypeSmooksResourceConfiguration != null && docTypeSmooksResourceConfiguration.getBoolParameter("override", true)) {
				String path = docTypeSmooksResourceConfiguration.getResource();
                
				if(path != null) {
					writer.write(new String(docTypeSmooksResourceConfiguration.getBytes()));
				} else {
					String publicId = docTypeSmooksResourceConfiguration.getStringParameter("publicId", "!!publicId undefined - fix smooks-resource!!");
					String systemId = docTypeSmooksResourceConfiguration.getStringParameter("systemId", "!!systemId undefined - fix smooks-resource!!");
					String xmlns = docTypeSmooksResourceConfiguration.getStringParameter("xmlns");

					serializeDoctype(publicId, systemId, rootElementName, writer);
					if(xmlns != null) {
						rootElement.setAttribute("xmlns", xmlns);
					} else {
						rootElement.removeAttribute("xmlns");
					}
				}
			} else {
				// If there's no SmooksResourceConfiguration doctypes defined, was there a DOCTYPE decl in 
				// the source Document?  If there was, write this out as the DOCTYPE.
				DocumentType docType = doc.getDoctype();
				
				if(docType != null) {
					serializeDoctype(docType.getPublicId(), docType.getSystemId(), rootElementName, writer);
				}
			}
            recursiveDOMWrite(rootElement, writer, true);
		} else {
            // Write the DOM, the child elements of the node
            NodeList deliveryNodes = node.getChildNodes();
            int nodeCount = deliveryNodes.getLength();
            boolean isRoot = (node == node.getOwnerDocument().getDocumentElement());
            for(int i = 0; i < nodeCount; i++) {
                Node childNode = deliveryNodes.item(i);
                if(childNode.getNodeType() == Node.ELEMENT_NODE) {
                    recursiveDOMWrite((Element)childNode, writer, isRoot);
                }
            }
        }
    }

	/**
	 * Serialize the DocumentType.
	 * <p/>
	 * Only called if a DOCTYPE was suppied in the original source and
	 * no doctype overrides were configured (in .cdrl) for the requesting
	 * device.
	 * @param publicId Docytype public ID.
     * @param systemId Docytype system ID.
	 * @param rootElement The root element name.
	 * @param writer The target writer.
	 * @throws IOException Exception writing to the output writer.
	 */
	private void serializeDoctype(String publicId, String systemId, String rootElement, Writer writer) throws IOException {

        writer.write("<!DOCTYPE ");
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
        writer.write('>');
		writer.write('\n');
	}

	/**
	 * Recursively write the DOM tree to the supplied writer.
	 * @param element Element to write.
	 * @param writer Writer to use.
     * @param isRoot Is the supplied element the document root element.
     * @throws IOException Exception writing to Writer.
	 */
	private void recursiveDOMWrite(Element element, Writer writer, boolean isRoot) throws IOException {
		SerializationUnit elementSU;
		NodeList children = element.getChildNodes();

		elementSU = getSerializationUnit(element, isRoot);
		try {
			elementSU.writeElementStart(element, writer, executionContext);
	
			if(children != null && children.getLength() > 0) {
				int childCount = children.getLength();
	
				for(int i = 0; i < childCount; i++) {
					Node childNode = children.item(i);
					
					switch(childNode.getNodeType()) {
						case Node.CDATA_SECTION_NODE: {
							elementSU.writeElementCDATA((CDATASection)childNode, writer, executionContext);
							break;
						}		
						case Node.COMMENT_NODE: { 
							elementSU.writeElementComment((Comment)childNode, writer, executionContext);
							break;
						}		
						case Node.ELEMENT_NODE: { 
							if(elementSU.writeChildElements()) {
								recursiveDOMWrite((Element)childNode, writer, false);
							}
							break;
						}		
						case Node.ENTITY_REFERENCE_NODE: { 
							elementSU.writeElementEntityRef((EntityReference)childNode, writer, executionContext);
							break;
						}		
						case Node.TEXT_NODE: { 
							elementSU.writeElementText((Text)childNode, writer, executionContext);
							break;
						}		
						default: {
							elementSU.writeElementNode(childNode, writer, executionContext);
							break;
						}
					}
				}
			}
			elementSU.writeElementEnd(element, writer, executionContext);
		} catch(Throwable thrown) {
			logger.error("Failed to apply serialization unit [" + elementSU.getClass().getName() + "] to [" + executionContext.getDocumentSource() + ":" + DomUtils.getXPath(element) + "].", thrown);
		}
	}

	/**
	 * Get the first matching serialisation unit for the supplied element.
	 * @param element Element to be serialized.
     * @param isRoot Is the supplied element the document root element.
     * @return SerializationUnit.
	 */
	private SerializationUnit getSerializationUnit(Element element, boolean isRoot) {
		String elementName = DomUtils.getName(element);
        List<ContentDeliveryUnitConfigMap> elementSUs;

        if(isRoot) {
            // The document as a whole (root node) can also be targeted through the "$document" selector.
            elementSUs = serializationUnits.getMappings(new String[] {elementName, SmooksResourceConfiguration.DOCUMENT_FRAGMENT_SELECTOR});
        } else {
            elementSUs = serializationUnits.getMappings(elementName);
        }
        
        if(elementSUs == null || elementSUs.isEmpty()) {
			elementSUs = defaultSUs;
		}
		int numSUs = elementSUs.size();
		
		for(int i = 0; i < numSUs; i++) {
            ContentDeliveryUnitConfigMap configMap = elementSUs.get(i);
            SmooksResourceConfiguration config = configMap.getResourceConfig(); 

            // Make sure the serialization unit is targeted at this element.
            if(!config.isTargetedAtElement(element)) {
                continue;
            }            

    		if(logger.isDebugEnabled()) {
    			logger.debug("Applying serialisation resource [" + config + "] to element [" + DomUtils.getXPath(element) + "].");
    		}
            
            // This is the one, return it...
            return (SerializationUnit)configMap.getContentDeliveryUnit();
		}
		
		throw new IllegalStateException("At least 1 SerializationUnit needs to be configured for an element. " + element.getTagName() + " has no configured SerializationUnit.");
	}
}