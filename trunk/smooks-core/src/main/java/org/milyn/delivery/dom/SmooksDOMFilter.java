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

package org.milyn.delivery.dom;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.ResourceConfigurationNotFoundException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.ProcessingSet;
import org.milyn.delivery.dom.serialize.Serializer;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ContentDeliveryUnitConfigMap;
import org.milyn.delivery.ContentDeliveryUnitConfigMapTable;
import org.milyn.xml.DomUtils;
import org.milyn.xml.Parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Smooks DOM based content filtering class.
 * <p/>
 * This class is responsible for <b>Filtering</b> XML DOM streams
 * (XML/XHTML/HTML etc) through a process of iterating over the source XML DOM tree
 * and applying the {@link org.milyn.cdr.SmooksResourceConfiguration configured} Content Delivery Units
 * ({@link DOMElementVisitor DOMElementVisitors} and
 * {@link org.milyn.delivery.dom.serialize.SerializationUnit SerializationUnits}).
 * <p/>
 * This class doesn't get used directly.  See the {@link org.milyn.Smooks} class.
 * 
 * <h3 id="phases">XML/XHTML/HTML Filtering Process</h3>
 * SmooksDOMFilter markup processing (XML/XHTML/HTML) is a 2 phase filter, depending on what
 * needs to be done.  The first phase is called the "Visit Phase", and the second 
 * phase is called the "Serialisation Phase".  SmooksDOMFilter can be used to execute either or both of these
 * phases (depending on what needs to be done!).
 * <p/>
 * Through this filter, Smooks can be used to analyse and/or transform markup, and then
 * serialise it.
 * 
 * <p/>
 * So, in a little more detail, the 2 phases are:
 * <ol>
 * 	<li>
 * 		<b><u>Visit</u></b>: This phase is executed via either of the
 * 		{@link #filter(Document)} or {@link #filter(Reader)} methods. 
 * 		This phase is really 2 "sub" phases.
 * 		<ul>
 * 			<li>
 * 				<b>Assembly</b>: This is effectively a pre-processing phase.
 *              <p/>
 * 				This sub-phase involves iterating over the source XML DOM,
 * 				visiting all DOM elements that have {@link org.milyn.delivery.dom.VisitPhase ASSEMBLY} phase
 *              {@link org.milyn.delivery.dom.DOMElementVisitor DOMElementVisitors}
 *              {@link org.milyn.cdr.SmooksResourceConfiguration targeted} at them for the profile
 *              associated with the {@link org.milyn.container.ExecutionContext}. 
 * 				This phase can result in DOM elements being added to, or trimmed from, the DOM.
 * 				This phase is also very usefull for gathering data from the message in the DOM
 *              (and storing it in the {@link org.milyn.container.ExecutionContext}), 
 * 				which can be used during the processing phase (see below).  This phase is only
 * 				executed if there are 
 * 				{@link DOMElementVisitor DOMElementVisitors} targeted at this phase.
 * 			</li>
 * 			<li>
 * 				<b>Processing</b>: Processing takes the assembled DOM and 
 * 				iterates over it again, so as to perform transformation/analysis.
 *              <p/>
 * 				This sub-phase involves iterating over the source XML DOM again,
 * 				visiting all DOM elements that have {@link org.milyn.delivery.dom.VisitPhase PROCESSING} phase
 *              {@link org.milyn.delivery.dom.DOMElementVisitor DOMElementVisitors}
 *              {@link org.milyn.cdr.SmooksResourceConfiguration targeted} at them for the profile
 *              associated with the {@link org.milyn.container.ExecutionContext}.
 * 				This phase will only operate on DOM elements that were present in the assembled
 * 				document; {@link org.milyn.delivery.dom.DOMElementVisitor DOMElementVisitors} will not be applied
 * 				to elements that are introduced to the DOM during this phase.
 * 			</li>
 * 		</ul>
 * 	</li>
 * 	<li>
 * 		<b><u>Serialisation</u></b>: This phase is executed by the {@link #serialize(Node, Writer)} method (which uses the 
 * 		{@link org.milyn.delivery.dom.serialize.Serializer} class).  The serialisation phase takes the processed DOM and
 * 		iterates over it to apply all {@link org.milyn.delivery.dom.serialize.SerializationUnit SerializationUnits},
 * 		which write the document to the target output stream.
 * 		<p/>
 * 		Instead of using this serialisation mechanism, you may wish to perform
 * 		DOM Serialisation via some other mechanism e.g. XSL-FO via something like Apache FOP.
 * 	</li>
 * </ol>
 *
 * See the <a href="http://milyn.codehaus.org/flash/DOMProcess.html" target="DOMProcess">online flash demo</a> demonstrating this process.
 * 
 * <h3 id="threading">Threading Issues</h3>
 * This class processes the data associated with a single {@link org.milyn.container.ExecutionContext} instance.  This
 * {@link org.milyn.container.ExecutionContext} instance is bound to the current thread of execution for the lifetime of the
 * SmooksDOMFilter instance.  For this reason it is not recommended to execute more than one SmooksDOMFilter
 * instance concurrently within the scope of a single thread i.e. don't interleave them.  Of course it's perfectly fine to
 * create a SmooksDOMFilter instance, use it, "dump" it and create and use another instance all within a single thread of execution.
 * This also means that SmooksDOMFilter instances cannot be cached/pooled and reused.
 * <p/>
 * A {@link org.milyn.container.ExecutionContext} instance should only be bound to the thread for the lifetime of the SmooksDOMFilter instance
 * it is associated with (i.e. used to instantiate).  See the {@link #finalize()} method.
 * 
 * <h3>Other Documents</h3>
 * <ul>
 * 	<li>{@link org.milyn.Smooks}</li>
 * 	<li>{@link org.milyn.cdr.SmooksResourceConfiguration}</li>
 * 	<li>{@link org.milyn.cdr.SmooksResourceConfigurationSortComparator}</li>
 * </ul>
 * 
 * <!--
 * This whole filter sounds like a lot of processing.  Well, it is.  Three iterations
 * over the DOM.  However, the thinking on this is that:
 * <ul>
 * 	<li>
 * 		The assembly phase is bypassed if there are no assembly units configured for the target profile.
 * 	</li>
 * 	<li>
 * 		{@link DOMElementVisitor Assembly Units} are stateless, which means
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
 * 		{@link org.milyn.delivery.dom.DOMElementVisitor DOMElementVisitors} must be stateless, which means that
 *      multiple instances will not be instantiated.
 * 	</li>
 * 	<li>
 * 		A single instance of the {@link org.milyn.delivery.dom.serialize.DefaultSerializationUnit}
 * 		performs the vast majority of the work in the serialisation phase.  Only elements that require
 * 		special attention require a specialised {@link org.milyn.delivery.dom.serialize.SerializationUnit}.
 * 	</li>
 * </ul>
 * -->
 * 
 * @author tfennelly
 */
public class SmooksDOMFilter {

	/**
	 * Logger.
	 */
	private Log logger = LogFactory.getLog(SmooksDOMFilter.class);
    /**
	 * Container request for this Smooks content delivery instance.
	 */
	private ExecutionContext executionContext;
	/**
	 * Accessor to the ExecutionContext delivery config.
	 */
	private DOMContentDeliveryConfig deliveryConfig;
	/**
	 * Processing Units to be applied to all elements in the document.  
	 * Only applied to element present in the original document.
	 */
	private List<ContentDeliveryUnitConfigMap> globalProcessingUnits;
	/**
	 * Key under which a non-document content delivery node can be set in the 
	 * request.  This is needed because Xerces doesn't allow "overwriting" of
	 * the document root node.
	 */
	public static final String DELIVERY_NODE_REQUEST_KEY = ContentDeliveryConfig.class.getName() + "#DELIVERY_NODE_REQUEST_KEY";
	/**
	 * The Threadlocal storage instance for the ExecutionContext associated with the "current" SmooksDOMFilter thread instance.
	 */
	private static ThreadLocal<ExecutionContext> requestThreadLocal = new ThreadLocal<ExecutionContext>();

    /**
	 * Public constructor.
	 * <p/>
	 * Constructs a SmooksDOMFilter instance for delivering content for the supplied execution context.
	 * @param executionContext Execution context.  This instance
	 * is bound to the current Thread of execution.  See <a href="#threading">Threading Issues</a>. 
	 */
	public SmooksDOMFilter(ExecutionContext executionContext) {
		if(executionContext == null) {
			throw new IllegalArgumentException("null 'executionContext' arg passed in constructor call.");
		}
		this.executionContext = executionContext;
		synchronized (requestThreadLocal) {
			// Bind the container request to the current thread.
			requestThreadLocal.set(executionContext);

		}
		deliveryConfig = (DOMContentDeliveryConfig) executionContext.getDeliveryConfig();
	}
	
	/**
	 * Cleanup.
	 * <p/>
	 * Clears the current thread-bound {@link ExecutionContext} instance, but only if this SmooksDOMFilter instance
	 * "owns" the {@link ExecutionContext} instance that's bound to the current thread.
	 * See <a href="#threading">Threading Issues</a>.
	 */
	protected void finalize() throws Throwable {
		synchronized (requestThreadLocal) {
			try {
				ExecutionContext curExecutionContext = getContainerRequest();
				if(executionContext == curExecutionContext) {
					// Only reset if this instance "owns" the current thread bound instance.  It may not own
					// the instance if finalization happens after the next SmooksDOMFilter instance is created on
					// this thread.
					requestThreadLocal.remove();
				}
			} finally {
				// Make sure the super finalizer gets called!!
				super.finalize();
			}
		}
	}

	/**
	 * Get the {@link ExecutionContext} instance bound to the current thread.
	 * </p>
	 * The {@link ExecutionContext} used to instantiate the SmooksDOMFilter class is bound to the current Thread of execution.
	 * If should only be bound to the thread for the lifetime of the SmooksDOMFilter instance.
	 * See <a href="#threading">Threading Issues</a>.
	 * @return The thread-bound {@link ExecutionContext} instance.
	 */
	public static ExecutionContext getContainerRequest() {
		return requestThreadLocal.get();
	}

	/**
	 * Assertion method that tries to catch instances where this class is used in an interleaved manner on a single
	 * thread.  See <a href="#threading">Threading Issues</a>.  All public methods of this class should call this
	 * method (enter an aspect!!).
	 */
	private void assertNotInterleaved() {
		ExecutionContext curExecutionContext = getContainerRequest();
		if(executionContext != curExecutionContext) {
			// Stall the ball here!! There's code in place which interleaves the use of this class.
			throw new Error("Illegal interleaving of multiple instances of " + SmooksDOMFilter.class.getName() + ".  See class Javadocs.");
		}
	}
	
	/**
	 * Phase the supplied input reader.
	 * <p/>
	 * Simply parses the input reader into a W3C DOM and calls {@link #filter(Document)}.
	 * @param source The source of markup to be filtered.
	 * @return Node representing filtered document.
	 * @throws SmooksException
	 */
	public Node filter(Reader source) throws SmooksException {
		assertNotInterleaved();
		
		Node deliveryNode;		
		
		if(source == null) {
			throw new IllegalArgumentException("null 'source' arg passed in method call.");
		} 
		try {
			Parser parser = new Parser(executionContext);
			Document document = parser.parse(source); 
			
			deliveryNode = filter(document);
		} catch(Exception cause) {
			throw new SmooksException("Unable to filter InputStream for target profile [" + executionContext.getTargetProfiles().getBaseProfile() + "].", cause);
		}
		
		return deliveryNode;
	}

	/**
	 * Phase the supplied W3C Document.
	 * <p/>
	 * Executes the <a href="#phases">Assembly &amp Processing phases</a>.
	 * @param doc The W3C Document to be filtered.
	 * @return Node representing filtered document.
	 */
	public Node filter(Document doc) {
		assertNotInterleaved();
		
		Vector transList = new Vector();
		int transListLength;
		ProcessingSet globalProcessingSet;
		Node deliveryNode;

        // Apply assembly phase...
        if(doc.getDocumentElement() == null) {
			logger.warn("Empty Document [" + executionContext.getDocumentSource() + "].  Not performaing any processing.");
			return doc;
		}
		
        // Apply assembly phase, skipping it if there are no configured assembly units...
        ContentDeliveryUnitConfigMapTable assemblyUnits = deliveryConfig.getAssemblyUnits();
		if(!assemblyUnits.isEmpty()) {
			// Assemble
			if(logger.isDebugEnabled()) {
				logger.debug("Starting assembly phase [" + executionContext.getTargetProfiles().getBaseProfile() + "]");
			}
			assemble(doc.getDocumentElement(), true);
		} else {
			if(logger.isDebugEnabled()) {
				logger.debug("No assembly units configured for device [" + executionContext.getTargetProfiles().getBaseProfile() + "]");
			}
		}

        // Apply processing phase...
		if(logger.isDebugEnabled()) {
			logger.debug("Starting processing phase [" + executionContext.getTargetProfiles().getBaseProfile() + "]");
		}
        globalProcessingUnits = deliveryConfig.getProcessingUnits().getMappings("*");
		buildProcessingList(transList, doc.getDocumentElement(), true);
		transListLength = transList.size();
		for(int i = 0; i < transListLength; i++) {
			ElementProcessor elementTrans = (ElementProcessor)transList.get(i);			
			elementTrans.process(executionContext);
		}
		
		deliveryNode = (Node) executionContext.getAttribute(DELIVERY_NODE_REQUEST_KEY);
		if(deliveryNode == null) {
			deliveryNode = doc;
		}
		
		return deliveryNode;
	}
	
	/**
	 * Assemble the supplied element.
	 * <p/>
	 * Recursively iterate down into the elements children.
	 * @param element Next element to operate on and iterate over.
     * @param isRoot Is the supplied element the document root element.
	 */
	private void assemble(Element element, boolean isRoot) {
		List nodeListCopy = copyList(element.getChildNodes());
		int childCount = nodeListCopy.size();
		ContentDeliveryUnitConfigMapTable assemblyUnits = deliveryConfig.getAssemblyUnits();
		String elementName = DomUtils.getName(element);
		List<ContentDeliveryUnitConfigMap> elementAssemblyUnits;

        if(isRoot) {
            // The document as a whole (root node) can also be targeted through the "$document" selector.
            elementAssemblyUnits = assemblyUnits.getMappings(new String[] {elementName, SmooksResourceConfiguration.DOCUMENT_FRAGMENT_SELECTOR});
        } else {
            elementAssemblyUnits = assemblyUnits.getMappings(elementName);
        }

        // Visit element with its assembly units before visiting its child content.
		if(elementAssemblyUnits != null && !elementAssemblyUnits.isEmpty()) {
			for(int i = 0; i < elementAssemblyUnits.size(); i++) {
                ContentDeliveryUnitConfigMap configMap = elementAssemblyUnits.get(i);
                SmooksResourceConfiguration config = configMap.getResourceConfig(); 

                // Make sure the assembly unit is targeted at this element...
                if(!config.isTargetedAtElement(element)) {
                    continue;
                }            
                
                DOMElementVisitor assemblyUnit = (DOMElementVisitor)configMap.getContentDeliveryUnit();
                try {
                    if(logger.isDebugEnabled()) {
                        logger.debug("(Assembly) Calling visitBefore on element [" + DomUtils.getXPath(element) + "]. Config [" + config + "]");
                    }
                    assemblyUnit.visitBefore(element, executionContext);
                } catch(Throwable e) {
                    logger.error("(Assembly) visitBefore failed [" + assemblyUnit.getClass().getName() + "] on [" + executionContext.getDocumentSource() + ":" + DomUtils.getXPath(element) + "].", e);
                }
			}
		}
		
		// Recursively iterate the elements child content...
		for(int i = 0; i < childCount; i++) {
			Node child = (Node)nodeListCopy.get(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				assemble((Element)child, false);
			}
		}

        // Revisit the element with its assembly units after visiting its child content.
		if(elementAssemblyUnits != null && !elementAssemblyUnits.isEmpty()) {
			for(int i = 0; i < elementAssemblyUnits.size(); i++) {
                ContentDeliveryUnitConfigMap configMap = elementAssemblyUnits.get(i);
                SmooksResourceConfiguration config = configMap.getResourceConfig();
                
                // Make sure the assembly unit is targeted at this element...
                if(!config.isTargetedAtElement(element)) {
                    continue;
                }

                DOMElementVisitor assemblyUnit = (DOMElementVisitor)configMap.getContentDeliveryUnit();
                try {
                    if(logger.isDebugEnabled()) {
                        logger.debug("(Assembly) Calling visitAfter on element [" + DomUtils.getXPath(element) + "]. Config [" + config + "]");
                    }
                    assemblyUnit.visitAfter(element, executionContext);
                } catch(Throwable e) {
                    logger.error("(Assembly) visitAfter failed [" + assemblyUnit.getClass().getName() + "] on [" + executionContext.getDocumentSource() + ":" + DomUtils.getXPath(element) + "].", e);
                }
			}
		}
	}
	
	/**
	 * Recurcively build the processing list for the supplied element, iterating over the elements
	 * child content. 
	 * @param processingList List under construction.  List of ElementProcessor instances.
     * @param element Current element being tested.  Starts at the document root element.
     * @param isRoot Is the supplied element the document root element.
     */
	private void buildProcessingList(List processingList, Element element, boolean isRoot) {
		String elementName;
		List<ContentDeliveryUnitConfigMap> processingUnits = null;

		elementName = DomUtils.getName(element);
        if(isRoot) {
            // The document as a whole (root node) can also be targeted through the "$document" selector.
            processingUnits = deliveryConfig.getProcessingUnits().getMappings(new String[] {elementName, SmooksResourceConfiguration.DOCUMENT_FRAGMENT_SELECTOR});
        } else {
            processingUnits = deliveryConfig.getProcessingUnits().getMappings(elementName);
        }
		
        if(processingUnits != null && !processingUnits.isEmpty()) {
			processingList.add(new ElementProcessor(element, processingUnits, true));
		}
		if(globalProcessingUnits != null) {
			// TODO: Inefficient. Find a better way!
			processingList.add(new ElementProcessor(element, globalProcessingUnits, true));
		}
		
		// Iterate over the child elements, calling this method recurcively....
		NodeList children = element.getChildNodes();
		int childCount = children.getLength();
		for(int i = 0; i < childCount; i++) {
			Node child = children.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				buildProcessingList(processingList, (Element)child, false);
			}
		}
		
		if(processingUnits != null) {
			processingList.add(new ElementProcessor(element, processingUnits, false));
		}
        if(globalProcessingUnits != null) {
			// TODO: Inefficient. Find a better way!
			processingList.add(new ElementProcessor(element, globalProcessingUnits, false));
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
		assertNotInterleaved();
		
		Serializer serializer;
		
		if(node == null) {
			throw new IllegalArgumentException("null 'doc' arg passed in method call.");
		} else if(writer == null) {
			throw new IllegalArgumentException("null 'writer' arg passed in method call.");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("Starting serialization phase [" + executionContext.getTargetProfiles().getBaseProfile() + "]");
		}
		serializer = new Serializer(node, executionContext);
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
		private List<ContentDeliveryUnitConfigMap> processingUnits;
        /**
         * Call visitBefore (or visitAfter).
         */
        private boolean visitBefore;

        /**
		 * Constructor.
		 * @param element Element to be processed.
         * @param processingUnits ProcessingUnit instances to be applied.
         * @param visitBefore Call visitBefore (or visitAfter).
         */
		private ElementProcessor(Element element, List<ContentDeliveryUnitConfigMap> processingUnits, boolean visitBefore) {
			this.element = element;
			this.processingUnits = processingUnits;
            this.visitBefore = visitBefore;
        }
		
		/**
		 * Apply the ProcessingUnits.
		 * <p/>
		 * Iterate over the ProcessingUnit instances calling the visitAfter method.
		 * @param executionContext Container request instance.
		 */
		private void process(ExecutionContext executionContext) {
			int loopLength = processingUnits.size();
			
			for(int i = 0; i < loopLength; i++) {
                ContentDeliveryUnitConfigMap configMap = processingUnits.get(i);
                SmooksResourceConfiguration config = configMap.getResourceConfig(); 

                // Make sure the processing unit is targeted at this element...
                if(!config.isTargetedAtElement(element)) {
                    continue;
                }            

                DOMElementVisitor processingUnit = (DOMElementVisitor)configMap.getContentDeliveryUnit();
				// Could add an "is-element-in-document-tree" check here
				// but might not be valid.  Also, this check
				// would need to iterate back up to the document root
				// every time. Doing this for every element could be very 
				// costly.
				try {
					if(logger.isDebugEnabled()) {
						logger.debug("Applying processing resource [" + config + "] to element [" + DomUtils.getXPath(element) + "] " + (visitBefore?"before":"after") + " apply resources to its child elements.");
					}
                    if(visitBefore) {
                        processingUnit.visitBefore(element, executionContext);
                    } else {
                        processingUnit.visitAfter(element, executionContext);
                    }
                } catch(Throwable e) {
					logger.error("Failed to apply processing unit [" + processingUnit.getClass().getName() + "] to [" + executionContext.getDocumentSource() + ":" + DomUtils.getXPath(element) + "].", e);
				}
			}
		}
	}
}










