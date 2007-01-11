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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.milyn.SmooksException;
import org.milyn.cdr.cdrar.CDRArchiveEntryNotFoundException;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.assemble.AssemblyUnit;
import org.milyn.delivery.serialize.Serializer;
import org.milyn.delivery.trans.TransSet;
import org.milyn.delivery.trans.TransUnit;
import org.milyn.delivery.trans.TransUnitPrototype;
import org.milyn.dom.Parser;
import org.milyn.logging.SmooksLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Smooks HTML content manipulation and delivery class.
 * <p/>
 * This executes the <a href="delivery/doc-files/delivery-phases.html">3 phases of HTML content delivery</a>.
 * The <b>Assembly</b> and <b>Transformation</b> phases are executed by the 
 * {@link #applyTransform(Document)} method and the <b>Serialisation</b> phase
 * is performed by the {@link #serailize(Node, Writer)} method.
 * <p/>
 * This class will be controlled by a container specific class.  At the moment Smooks is only
 * supported in the J2EE Servlet Container and this class is hooked into the process
 * through the {@link org.milyn.delivery.response.HtmlServletResponseWrapper}.
 * class.
 * 
 * @author tfennelly
 */
public class SmooksHtml {

	/**
	 * Logger.
	 */
	private Log logger = SmooksLogger.getLog();
	/**
	 * Container request for this Smooks content delivery instance.
	 */
	private ContainerRequest containerRequest;
	/**
	 * Accessor to the ContainerRequest delivery config.
	 */
	private ContentDeliveryConfig deliveryConfig;
	/**
	 * Visit before TransSet list to be applied to all elements in the document.  
	 * Only applied to element present in the original document.
	 */
	private List globalVisitBeforeTUs;
	/**
	 * Visit after TransSet list to be applied to all elements in the document.  
	 * Only applied to element present in the original document.
	 */
	private List globalVisitAfterTUs;
	/**
	 * Key under which a non-document content delivery node can be set in the 
	 * request.  This is needed because Xerces doesn't allow "overwriting" of
	 * the document root node.
	 */
	public static final String DELIVERY_NODE_REQUEST_KEY = ContentDeliveryConfig.class.getName() + "#DELIVERY_NODE_REQUEST_KEY";
	
	/**
	 * Public constructor.
	 * <p/>
	 * Constructs a Smooks instance for delivering content to the target device
	 * associated with the uaContext.
	 * @param containerRequest Container request for this Smooks content delivery instance.
	 */
	public SmooksHtml(ContainerRequest containerRequest) {
		if(containerRequest == null) {
			throw new IllegalArgumentException("null 'containerRequest' arg passed in constructor call.");
		}
		this.containerRequest = containerRequest;
		deliveryConfig = containerRequest.getDeliveryConfig();
	}
	
	/**
	 * Transform the supplied InputSource.
	 * <p/>
	 * Simply parses the InputSource into a W3C DOM and calls {@link #applyTransform(Document)}.
	 * @param source The source of markup to be transformed.
	 * @return Node representing transformed document; content to be delivered.
	 * @throws SmooksException
	 */
	public Node applyTransform(Reader source) throws SmooksException {
		Node deliveryNode;
		
		if(source == null) {
			throw new IllegalArgumentException("null 'source' arg passed in method call.");
		} 
		
		try {
			Parser parser = new Parser(deliveryConfig);
			Document document = parser.parse(source); 
			
			deliveryNode = applyTransform(document);
		} catch(Exception cause) {
			throw new SmooksException("Unable to transform InputStream for target device [" + containerRequest.getUseragentContext().getCommonName() + "].", cause);
		}
		
		return deliveryNode;
	}

	/**
	 * Transform the supplied W3C Document.
	 * <p/>
	 * Executes the <a href="delivery/doc-files/delivery-phases.html">Assembly and Transformation phases</a>.
	 * @param doc The W3C Document to be transformed.
	 * @return Node representing transformed document; content to be delivered.
	 */
	public Node applyTransform(Document doc) {
		Vector transList = new Vector();
		int transListLength;
		TransSet globalTransSet;
		Node deliveryNode;
		Hashtable deviceAssemblyUnits = deliveryConfig.getAssemblyUnits();
		
		// Skip the assembly phase if there are no configured assembly units.
		if(!deviceAssemblyUnits.isEmpty()) {
			// Assemble
			if(logger.isDebugEnabled()) {
				logger.debug("Starting assembly phase [" + containerRequest.getUseragentContext().getCommonName() + "]");
			}
			assemble(doc.getDocumentElement());
			containerRequest.clearElementLists();
		} else {
			if(logger.isDebugEnabled()) {
				logger.debug("No assembly units configured for device [" + containerRequest.getUseragentContext().getCommonName() + "]");
			}
		}

		// Transform
		if(logger.isDebugEnabled()) {
			logger.debug("Starting transformation phase [" + containerRequest.getUseragentContext().getCommonName() + "]");
		}
		globalTransSet = deliveryConfig.getTransSet("*");
		if(globalTransSet != null) {
			globalVisitBeforeTUs = globalTransSet.getVisitBeforeTransUnits();
			globalVisitAfterTUs = globalTransSet.getVisitAfterTransUnits();
		}
		buildTransformationList(transList, doc.getDocumentElement());
		transListLength = transList.size();
		for(int i = 0; i < transListLength; i++) {
			ElementTransformation elementTrans = (ElementTransformation)transList.get(i);			
			elementTrans.applyTransform(containerRequest);
		}
		containerRequest.clearElementLists();
		
		deliveryNode = (Node)containerRequest.getAttribute(DELIVERY_NODE_REQUEST_KEY);
		if(deliveryNode == null) {
			deliveryNode = doc;
		}
		
		return deliveryNode;
	}
	
	/**
	 * Assemble the document.
	 * <p/>
	 * Recursively iterate over the document and apply all AssemblyUnits.
	 * @param element Next element to operate on and iterate over.
	 */
	private void assemble(Element element) {
		List nodeListCopy = copyList(element.getChildNodes());
		int childCount = nodeListCopy.size();
		Hashtable deviceAssemblyUnits = deliveryConfig.getAssemblyUnits();
		List elementAssemblyUnits = (Vector)deviceAssemblyUnits.get(element.getTagName());

		// Visit elements with assembly units to be applied before iterating the
		// elements child content.
		if(elementAssemblyUnits != null && !elementAssemblyUnits.isEmpty()) {
			for(int i = 0; i < elementAssemblyUnits.size(); i++) {
				AssemblyUnit assemblyUnit = (AssemblyUnit)elementAssemblyUnits.get(i);
				if(assemblyUnit.visitBefore()) {
					try {
						assemblyUnit.visit(element, containerRequest);
					} catch(Exception e) {
						logger.error("Failed to apply assembly unit [" + assemblyUnit.getClass().getName() + "] to element [" + element.getTagName() + "].", e);
					}
				}
			}
		}
		
		// Recursively iterate the elements child content...
		for(int i = 0; i < childCount; i++) {
			Node child = (Node)nodeListCopy.get(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				assemble((Element)child);
			}
		}

		// Visit elements with assembly units to be applied after iterating the
		// elements child content.
		if(elementAssemblyUnits != null && !elementAssemblyUnits.isEmpty()) {
			for(int i = 0; i < elementAssemblyUnits.size(); i++) {
				AssemblyUnit assemblyUnit = (AssemblyUnit)elementAssemblyUnits.get(i);
				if(!assemblyUnit.visitBefore()) {
					try {
						assemblyUnit.visit(element, containerRequest);
					} catch(Exception e) {
						logger.error("Failed to apply assembly unit [" + assemblyUnit.getClass().getName() + "] to element [" + element.getTagName() + "].", e);
					}
				}
			}
		}
	}
	
	/**
	 * Recurcively build the document transformation list, iterating over the document 
	 * elements. 
	 * @param transList List under construction.  List of ElementTransformation instances.
	 * @param element Current element being tested.  Starts at the document root element.
	 */
	private void buildTransformationList(List transList, Element element) {
		String elementName = element.getTagName();
		TransSet transSet = deliveryConfig.getTransSet(elementName);
		List visitBeforeTUs = null;
		List visitAfterTUs = null;
		
		if(transSet != null) {
			visitBeforeTUs = transSet.getVisitBeforeTransUnits();
			visitAfterTUs = transSet.getVisitAfterTransUnits();			
		}
		
		if(visitBeforeTUs != null) {
			transList.add(new ElementTransformation(element, visitBeforeTUs));
		}
		if(globalVisitBeforeTUs != null) {
			// TODO: Inefficient. Find a better way!
			transList.add(new ElementTransformation(element, globalVisitBeforeTUs));
		}
		
		// Iterate over the child elements, calling this method recurcively....
		NodeList children = element.getChildNodes();
		int childCount = children.getLength();
		for(int i = 0; i < childCount; i++) {
			Node child = children.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				buildTransformationList(transList, (Element)child);
			}
		}
		
		if(visitAfterTUs != null) {
			transList.add(new ElementTransformation(element, visitAfterTUs));
		}
		if(globalVisitAfterTUs != null) {
			// TODO: Inefficient. Find a better way!
			transList.add(new ElementTransformation(element, globalVisitAfterTUs));
		}
	}
	
	/**
	 * Serialise the node to the supplied output writer instance.
	 * <p/>
	 * Executes the <a href="delivery/doc-files/delivery-phases.html">Serialisation phase</a>,
	 * using the {@link Serializer} class to perform the serialization.
	 * @param node Document to be serialised.
	 * @param writer Output writer.
	 * @throws CDRArchiveEntryNotFoundException DOM Serialiser exception.
	 * @throws IOException Unable to write to output writer.
	 * @throws SmooksException Unable to serialise do to bad Smooks environment.  Check cause.
	 */
	public void serailize(Node node, Writer writer) throws IOException, SmooksException {
		Serializer serializer;
		
		if(node == null) {
			throw new IllegalArgumentException("null 'doc' arg passed in method call.");
		} else if(writer == null) {
			throw new IllegalArgumentException("null 'writer' arg passed in method call.");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("Starting serialization phase [" + containerRequest.getUseragentContext().getCommonName() + "]");
		}
		serializer = new Serializer(node, containerRequest);
		try {
			serializer.serailize(writer);
		} catch (CDRArchiveEntryNotFoundException e) {
			throw new SmooksException("Unable to serialize document.", e);
		}
	}

	/**
	 * Copy the nodes of a NodeList into the supplied list.
	 * <p/>
	 * This is not a clone.  It's just a copy of the node references.
	 * <p/>
	 * Allows iteration over the Nodelist using the copy in the knowledge that
	 * the list will remain the same length.  Using the NodeList can result in problems
	 * because elements can get removed from the list while we're iterating over it.
	 * @param nodeList Nodelist to copy.
	 * @return List copy.
	 */
	private List copyList(NodeList nodeList) {
		Vector copy = new Vector(nodeList.getLength());
		int nodeCount = nodeList.getLength();
	
		for(int i = 0; i < nodeCount; i++) {
			copy.add(nodeList.item(i));
		}
		
		return copy;
	}
	
	/**
	 * Element Transformation class.
	 * <p/>
	 * Simple DOM Element to TransUnit[] transformation mapping and support functions.
	 * @author tfennelly
	 */
	private class ElementTransformation {
		/**
		 * The Element instance to be transformed.
		 */
		private Element element;
		/**
		 * The TransUnit instances to be applied 
		 */
		private List transUnits;
		
		/**
		 * Construct Transformation object.
		 * @param element Element to be transformed.
		 * @param transUnits TransUnit instances to be applied.
		 */
		private ElementTransformation(Element element, List transUnits) {
			this.element = element;
			this.transUnits = transUnits;
		}
		
		/**
		 * Apply the transformation.
		 * <p/>
		 * Iterate over the TransUnit instances calling the visit method.
		 * @param containerRequest Container request instance.
		 */
		private void applyTransform(ContainerRequest containerRequest) {
			int loopLength = transUnits.size();
			
			for(int i = 0; i < loopLength; i++) {
				TransUnit transUnit = (TransUnit)transUnits.get(i);
				
				if(transUnit instanceof TransUnitPrototype) {
					transUnit = ((TransUnitPrototype)transUnit).cloneTransUnit();
				}
				// Could add an "is-element-in-document-tree" check here
				// but might not be valid.  Also, this check
				// would need to iterate back up to the document root
				// every time. Doing this for every element could be very 
				// costly.
				try {
					transUnit.visit(element, containerRequest);
				} catch(Exception e) {
					logger.error("Failed to apply transformation unit [" + transUnit.getClass().getName() + "] to element [" + element.getTagName() + "].", e);
				}
			}
		}
	}
}










