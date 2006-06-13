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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.milyn.SmooksException;
import org.milyn.cdr.ResourceConfigurationNotFoundException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.assemble.AssemblyUnit;
import org.milyn.delivery.process.ProcessingSet;
import org.milyn.delivery.process.ProcessingUnit;
import org.milyn.delivery.process.ProcessingUnitPrototype;
import org.milyn.delivery.serialize.Serializer;
import org.milyn.dom.DomUtils;
import org.milyn.dom.Parser;
import org.milyn.logging.SmooksLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Smooks XML/XHTML/HTML/etc content filtering class.
 * <p/>
 * This class is responsible for <b>Filtering</b> and <b>Serialising</b> XML streams
 * (XML/XHTML/HTML etc) through a process of iterating over the source XML DOM tree
 * and applying the {@link org.milyn.cdr.SmooksResourceConfiguration configured} Content Delivery Units
 * ({@link org.milyn.delivery.assemble.AssemblyUnit AssemblyUnits}, 
 * {@link org.milyn.delivery.process.ProcessingUnit ProcessingUnits} and 
 * {@link org.milyn.delivery.serialize.SerializationUnit SerializationUnits}).
 * <p/>
 * This class doesn't get used directly.  See {@link org.milyn.SmooksServletFilter}
 * and {@link org.milyn.SmooksStandalone}.
 * 
 * <h3 id="phases">XML/XHTML/HTML Filtering and Serialisation</h3>
 * SmooksXML markup processing (XML/XHTML/HTML) is a 2 phase process, depending on what
 * needs to be done.  The first phase is called the "Filtering Phase", and the second 
 * phase is called the "Serialisation Phase".  SmooksXML can be used to execute either or both of these 
 * phases (depending on what needs to be done!).
 * <p/>
 * Through this process, Smooks can be used to analyse and/or transform markup, and then
 * serialise it.
 * 
 * <p/>
 * So, in a little more detail, the 2 phases are:
 * <ol>
 * 	<li>
 * 		<b><u>Filtering</u></b>: This phase is executed via either of the
 * 		{@link #filter(Document)} or {@link #filter(Reader)} methods. 
 * 		This phase is really 2 "sub" phases.
 * 		<ul>
 * 			<li>
 * 				<b>Assembly</b>: Assembly is the process of <u>assembling/analysing 
 * 				the content</u>.  This is effectively a pre-processing phase.
 *              <p/>
 * 				This process involves iterating over the source XML DOM, 
 * 				{@link org.milyn.delivery.ElementVisitor visiting} all the
 * 				DOM elements with {@link org.milyn.delivery.assemble.AssemblyUnit AssemblyUnits}
 * 				that are {@link org.milyn.cdr.SmooksResourceConfiguration configured} for the SmooksXML processing context
 * 				(e.g. for the requesting browser). 
 * 				This phase can result in DOM elements getting added to, or trimmed from, the DOM.  
 * 				This phase is also very usefull for gathering information about the DOM, 
 * 				which can be used during the processing phase (see below).  This phase is only
 * 				executed if there are {@link org.milyn.cdr.SmooksResourceConfiguration configured}
 * 				{@link org.milyn.delivery.assemble.AssemblyUnit} for the SmooksXML processing context
 * 				(e.g. for the requesting browser).
 * 			</li>
 * 			<li>
 * 				<b>Processing</b>: Processing takes the assembled DOM and 
 * 				iterates over it again to "process" it (analyse/transform), {@link org.milyn.delivery.ElementVisitor visiting} all the
 * 				DOM elements with {@link org.milyn.delivery.process.ProcessingUnit}
 * 				that are {@link org.milyn.cdr.SmooksResourceConfiguration configured} for the SmooksXML processing context
 * 				(e.g. for the requesting browser). 
 * 				This phase will only operate on DOM elements that were present in the assembled
 * 				document; {@link org.milyn.delivery.process.ProcessingUnit ProcessingUnits} will not be applied
 * 				to elements that are introduced to the DOM during this phase.
 * 				class.
 * 			</li>
 * 		</ul>
 * 	</li>
 * 	<li>
 * 		<b><u>Serialisation</u></b>: This phase is executed by the {@link #serialize(Node, Writer)} method (which uses the 
 * 		{@link org.milyn.delivery.serialize.Serializer} class).  The serialisation phase takes the processed DOM and 
 * 		iterates over it to apply all {@link org.milyn.delivery.serialize.SerializationUnit SerializationUnits},
 * 		which write the document to the target output stream.
 * 		<p/>
 * 		Instead of using this serialisation mechanism, you may wish to perform
 * 		DOM Serialisation via some other mechanism e.g. XSL-FO via something like Apache FOP.
 * 	</li>
 * </ol>
 * 
 * It is expected that the vast majority of Smooks usecases will involve implementing the
 * {@link org.milyn.delivery.process.ProcessingUnit ProcessingUnit} interface (most likely via
 * {@link org.milyn.delivery.process.AbstractProcessingUnit}).  It is not expected that many usecases will
 * require implementation of the {@link org.milyn.delivery.assemble.AssemblyUnit} interface,
 * and even less will require implementation of the
 * {@link org.milyn.delivery.serialize.SerializationUnit} interface. 
 * A {@link org.milyn.delivery.serialize.DefaultSerializationUnit} exists and this should solve the
 * vast majority of XML serialisation requirements.
 * 
 * <h4>Other Documents</h4>
 * <ul>
 * 	<li>{@link org.milyn.SmooksStandalone}</li>
 * 	<li>{@link org.milyn.cdr.SmooksResourceConfiguration}</li>
 * 	<li>{@link org.milyn.cdr.SmooksResourceConfigurationSortComparator}</li>
 * </ul>
 * 
 * 
 * <!--
 * This whole process sounds like a lot of processing.  Well, it is.  Three iterations
 * over the DOM.  However, the thinking on this is that:
 * <ul>
 * 	<li>
 * 		The assembly phase is bypassed if there are no {@link org.milyn.delivery.assemble.AssemblyUnit AssemblyUnits}
 * 		configured for the requesting device.
 * 	</li>
 * 	<li>
 * 		{@link org.milyn.delivery.assemble.AssemblyUnit AssemblyUnits} are stateless, which means
 * 		that only a single instance needs to be created.
 * 	</li>
 * 	<li>
 * 		The processing phase is likely to be the most processor intensive of the three 
 * 		phases but the DOM to be processed should have been reduced as much as possible by the
 * 		assembly phase.  Remember, assembly doesn't just mean "adding" to the DOM.
 * 	</li>
 * 	<li>
 * 		The processing phase is only applied to elements that were in the DOM 
 * 		at the start of that phase.
 * 	</li>
 * 	<li>
 * 		{@link org.milyn.delivery.process.ProcessingUnit ProcessingUnits} are normally stateless (see {@link org.milyn.delivery.process.ProcessingUnitPrototype}) 
 * 		which means that multiple instances shouldn't need to be instanciated.
 * 	</li>
 * 	<li>
 * 		A single instance of the {@link org.milyn.delivery.serialize.DefaultSerializationUnit}
 * 		performs the vast majority of the work in the serialisation phase.  Only elements that require
 * 		special attention require a specialised {@link org.milyn.delivery.serialize.SerializationUnit}.
 * 	</li>
 * </ul>
 * -->
 * 
 * @author tfennelly
 */
public class SmooksXML {

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
	 * Visit before ProcessingSet list to be applied to all elements in the document.  
	 * Only applied to element present in the original document.
	 */
	private List globalVisitBeforePUs;
	/**
	 * Visit after ProcessingSet list to be applied to all elements in the document.  
	 * Only applied to element present in the original document.
	 */
	private List globalVisitAfterPUs;
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
	public SmooksXML(ContainerRequest containerRequest) {
		if(containerRequest == null) {
			throw new IllegalArgumentException("null 'containerRequest' arg passed in constructor call.");
		}
		this.containerRequest = containerRequest;
		deliveryConfig = containerRequest.getDeliveryConfig();
	}
	
	/**
	 * Filter the supplied input reader.
	 * <p/>
	 * Simply parses the input reader into a W3C DOM and calls {@link #filter(Document)}.
	 * @param source The source of markup to be filtered.
	 * @return Node representing filtered document.
	 * @throws SmooksException
	 */
	public Node filter(Reader source) throws SmooksException {
		Node deliveryNode;		
		
		if(source == null) {
			throw new IllegalArgumentException("null 'source' arg passed in method call.");
		} 
		try {
			Parser parser = new Parser(containerRequest);
			Document document = parser.parse(source); 
			
			deliveryNode = filter(document);
		} catch(Exception cause) {
			throw new SmooksException("Unable to filter InputStream for target useragent [" + containerRequest.getUseragentContext().getCommonName() + "].", cause);
		}
		
		return deliveryNode;
	}

	/**
	 * Filter the supplied W3C Document.
	 * <p/>
	 * Executes the <a href="#phases">Assembly &amp Processing phases</a>.
	 * @param doc The W3C Document to be filtered.
	 * @return Node representing filtered document.
	 */
	public Node filter(Document doc) {
		Vector transList = new Vector();
		int transListLength;
		ProcessingSet globalProcessingSet;
		Node deliveryNode;
		Hashtable deviceAssemblyUnits = deliveryConfig.getAssemblyUnits();
		
		if(doc.getDocumentElement() == null) {
			logger.warn("Empty Document [" + containerRequest.getRequestURI() + "].  Not performaing any processing.");
			return doc;
		}
		
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

		// process
		if(logger.isDebugEnabled()) {
			logger.debug("Starting processing phase [" + containerRequest.getUseragentContext().getCommonName() + "]");
		}
		globalProcessingSet = deliveryConfig.getProcessingSet("*");
		if(globalProcessingSet != null) {
			globalVisitBeforePUs = globalProcessingSet.getVisitBeforeProcessingUnits();
			globalVisitAfterPUs = globalProcessingSet.getVisitAfterProcessingUnits();
		}
		buildProcessingList(transList, doc.getDocumentElement());
		transListLength = transList.size();
		for(int i = 0; i < transListLength; i++) {
			ElementProcessor elementTrans = (ElementProcessor)transList.get(i);			
			elementTrans.process(containerRequest);
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
		String elementName = DomUtils.getName(element);
		List elementAssemblyUnits;
		
		elementAssemblyUnits = (List)deviceAssemblyUnits.get(elementName.toLowerCase());

		// Visit elements with assembly units to be applied before iterating the
		// elements child content.
		if(elementAssemblyUnits != null && !elementAssemblyUnits.isEmpty()) {
			for(int i = 0; i < elementAssemblyUnits.size(); i++) {
                ContentDeliveryUnitConfigMap configMap = (ContentDeliveryUnitConfigMap) elementAssemblyUnits.get(i);;
                SmooksResourceConfiguration config = configMap.getResourceConfig(); 

                // Make sure the assembly unit is targeted at this element.
                // Check the namespace and context (if the selector is contextual)...
                if(!config.isTargetedAtElementNamespace(element)) {
                    continue;
                } else if(config.isSelectorContextual() && !config.isTargetedAtElementContext(element)) {
                    // Note: If the selector is not contextual, there's no need to perform the
                    // isTargetedAtElementContext check because we already know the unit is targetd at the
                    // element by name - because we looked it up by name in the 1st place.
                    continue;
                }            
                
                AssemblyUnit assemblyUnit = (AssemblyUnit)configMap.getContentDeliveryUnit();
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
                ContentDeliveryUnitConfigMap configMap = (ContentDeliveryUnitConfigMap) elementAssemblyUnits.get(i);
                AssemblyUnit assemblyUnit = (AssemblyUnit)configMap.getContentDeliveryUnit();
                
				if(!assemblyUnit.visitBefore() && configMap.getResourceConfig().isTargetedAtElementContext(element)) {
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
	 * Recurcively build the document processing list, iterating over the document 
	 * elements. 
	 * @param processingList List under construction.  List of ElementProcessor instances.
	 * @param element Current element being tested.  Starts at the document root element.
	 */
	private void buildProcessingList(List processingList, Element element) {
		String elementName;
		ProcessingSet processingSet;
		List visitBeforeTUs = null;
		List visitAfterTUs = null;
		
		elementName = DomUtils.getName(element);
		processingSet = deliveryConfig.getProcessingSet(elementName);
		
		if(processingSet != null) {
			visitBeforeTUs = processingSet.getVisitBeforeProcessingUnits();
			visitAfterTUs = processingSet.getVisitAfterProcessingUnits();			
		}
		
		if(visitBeforeTUs != null) {
			processingList.add(new ElementProcessor(element, visitBeforeTUs));
		}
		if(globalVisitBeforePUs != null) {
			// TODO: Inefficient. Find a better way!
			processingList.add(new ElementProcessor(element, globalVisitBeforePUs));
		}
		
		// Iterate over the child elements, calling this method recurcively....
		NodeList children = element.getChildNodes();
		int childCount = children.getLength();
		for(int i = 0; i < childCount; i++) {
			Node child = children.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				buildProcessingList(processingList, (Element)child);
			}
		}
		
		if(visitAfterTUs != null) {
			processingList.add(new ElementProcessor(element, visitAfterTUs));
		}
		if(globalVisitAfterPUs != null) {
			// TODO: Inefficient. Find a better way!
			processingList.add(new ElementProcessor(element, globalVisitAfterPUs));
		}
	}
	
	/**
	 * Serialise the node to the supplied output writer instance.
	 * <p/>
	 * Executes the <a href="#phases">Serialisation phase</a>,
	 * using the {@link Serializer} class to perform the serialization.
	 * @param node Document to be serialised.
	 * @param writer Output writer.
	 * @throws ResourceConfigurationNotFoundException DOM Serialiser exception.
	 * @throws IOException Unable to write to output writer.
	 * @throws SmooksException Unable to serialise due to bad Smooks environment.  Check cause.
	 */
	public void serialize(Node node, Writer writer) throws IOException, SmooksException {
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
		} catch (ResourceConfigurationNotFoundException e) {
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
	 * Element Prcessor class.
	 * <p/>
	 * Simple DOM Element to ProcessingUnit[] processing mapping and support functions.
	 * @author tfennelly
	 */
	private class ElementProcessor {
		/**
		 * The Element instance to be processed.
		 */
		private Element element;
		/**
		 * The processing list. 
		 */
		private List processingUnits;
		
		/**
		 * Constructor.
		 * @param element Element to be processed.
		 * @param processingUnits ProcessingUnit instances to be applied.
		 */
		private ElementProcessor(Element element, List processingUnits) {
			this.element = element;
			this.processingUnits = processingUnits;
		}
		
		/**
		 * Apply the ProcessingUnits.
		 * <p/>
		 * Iterate over the ProcessingUnit instances calling the visit method.
		 * @param containerRequest Container request instance.
		 */
		private void process(ContainerRequest containerRequest) {
			int loopLength = processingUnits.size();
			
			for(int i = 0; i < loopLength; i++) {
                ContentDeliveryUnitConfigMap configMap = (ContentDeliveryUnitConfigMap)processingUnits.get(i);
                SmooksResourceConfiguration config = configMap.getResourceConfig(); 

                // Make sure the processing unit is targeted at this element.
                // Check the namespace and context (if the selector is contextual)...
                if(!config.isTargetedAtElementNamespace(element)) {
                    continue;
                } else if(config.isSelectorContextual() && !config.isTargetedAtElementContext(element)) {
                    // Note: If the selector is not contextual, there's no need to perform the
                    // isTargetedAtElementContext check because we already know the unit is targetd at the
                    // element by name - because we looked it up by name in the 1st place.
                    continue;
                }            

                ProcessingUnit processingUnit = (ProcessingUnit)configMap.getContentDeliveryUnit();
				if(processingUnit instanceof ProcessingUnitPrototype) {
					processingUnit = ((ProcessingUnitPrototype)processingUnit).cloneProcessingUnit();
				}
				// Could add an "is-element-in-document-tree" check here
				// but might not be valid.  Also, this check
				// would need to iterate back up to the document root
				// every time. Doing this for every element could be very 
				// costly.
				try {
					processingUnit.visit(element, containerRequest);
				} catch(Exception e) {
					logger.error("Failed to apply processing unit [" + processingUnit.getClass().getName() + "] to element [" + element.getTagName() + "].", e);
				}
			}
		}
	}
}










