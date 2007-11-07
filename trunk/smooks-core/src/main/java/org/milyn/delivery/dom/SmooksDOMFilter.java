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

import java.io.*;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.ResourceConfigurationNotFoundException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.delivery.dom.serialize.Serializer;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ContentHandlerConfigMap;
import org.milyn.delivery.ContentHandlerConfigMapTable;
import org.milyn.delivery.Filter;
import org.milyn.xml.DomUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

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
 * <h3>Other Documents</h3>
 * <ul>
 * 	<li>{@link org.milyn.Smooks}</li>
 * 	<li>{@link org.milyn.cdr.SmooksResourceConfiguration}</li>
 * 	<li>{@link org.milyn.cdr.SmooksResourceConfigurationSortComparator}</li>
 * </ul>
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksDOMFilter extends Filter {

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
	private List<ContentHandlerConfigMap<DOMElementVisitor>> globalProcessingUnits;
	/**
	 * Key under which a non-document content delivery node can be set in the 
	 * request.  This is needed because Xerces doesn't allow "overwriting" of
	 * the document root node.
	 */
	public static final String DELIVERY_NODE_REQUEST_KEY = ContentDeliveryConfig.class.getName() + "#DELIVERY_NODE_REQUEST_KEY";

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
        deliveryConfig = (DOMContentDeliveryConfig) executionContext.getDeliveryConfig();
	}

    public void doFilter(Source source, Result result) throws SmooksException {

        if (!(source instanceof StreamSource) && !(source instanceof DOMSource)) {
            throw new IllegalArgumentException(source.getClass().getName() + " Source types not yet supported by the DOM Filter.");
        }
        if (!(result instanceof StreamResult) && !(result instanceof DOMResult)) {
            throw new IllegalArgumentException(result.getClass().getName() + " Result types not yet supported by the DOM Filter.");
        }

        try {
            Node resultNode;

            // Filter the Source....
            if (source instanceof StreamSource) {
                StreamSource streamSource = (StreamSource) source;
                resultNode = filter(getReader(streamSource, executionContext));
            } else {
                Node node = ((DOMSource) source).getNode();
                if (!(node instanceof Document)) {
                    throw new IllegalArgumentException("DOMSource Source types must contain a Document node.");
                }
                resultNode = filter((Document) node);
            }

            // Populate the Result
            if (result instanceof StreamResult) {
                StreamResult streamResult = ((StreamResult) result);
                Writer writer;

                writer = getWriter(streamResult, executionContext);
                try {
                    serialize(resultNode, writer);
                } catch (IOException e) {
                    logger.error("Error writing result to output stream.", e);
                }
            } else {
                ((DOMResult) result).setNode(resultNode);
            }
        } finally {
            close(source);
            close(result);
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
		Node deliveryNode;
		
		if(source == null) {
			throw new IllegalArgumentException("null 'source' arg passed in method call.");
		} 
		try {
			DOMParser parser = new DOMParser(executionContext);
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
        ContentHandlerConfigMapTable<DOMElementVisitor> assemblyUnits = deliveryConfig.getAssemblyUnits();
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
		ContentHandlerConfigMapTable<DOMElementVisitor> assemblyUnits = deliveryConfig.getAssemblyUnits();
		String elementName = DomUtils.getName(element);
		List<ContentHandlerConfigMap<DOMElementVisitor>> elementAssemblyUnits;

        if(isRoot) {
            // The document as a whole (root node) can also be targeted through the "$document" selector.
            elementAssemblyUnits = assemblyUnits.getMappings(new String[] {elementName, SmooksResourceConfiguration.DOCUMENT_FRAGMENT_SELECTOR});
        } else {
            elementAssemblyUnits = assemblyUnits.getMappings(elementName);
        }

        // Visit element with its assembly units before visiting its child content.
		if(elementAssemblyUnits != null && !elementAssemblyUnits.isEmpty()) {
			for(int i = 0; i < elementAssemblyUnits.size(); i++) {
                ContentHandlerConfigMap<DOMElementVisitor> configMap = elementAssemblyUnits.get(i);
                SmooksResourceConfiguration config = configMap.getResourceConfig(); 

                // Make sure the assembly unit is targeted at this element...
                if(!config.isTargetedAtElement(element)) {
                    continue;
                }            
                
                DOMElementVisitor assemblyUnit = configMap.getContentHandler();
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
                ContentHandlerConfigMap configMap = elementAssemblyUnits.get(i);
                SmooksResourceConfiguration config = configMap.getResourceConfig();
                
                // Make sure the assembly unit is targeted at this element...
                if(!config.isTargetedAtElement(element)) {
                    continue;
                }

                DOMElementVisitor assemblyUnit = (DOMElementVisitor)configMap.getContentHandler();
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
		List<ContentHandlerConfigMap<DOMElementVisitor>> processingUnits = null;

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
		private List<ContentHandlerConfigMap<DOMElementVisitor>> processingUnits;
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
		private ElementProcessor(Element element, List<ContentHandlerConfigMap<DOMElementVisitor>> processingUnits, boolean visitBefore) {
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
                ContentHandlerConfigMap configMap = processingUnits.get(i);
                SmooksResourceConfiguration config = configMap.getResourceConfig(); 

                // Make sure the processing unit is targeted at this element...
                if(!config.isTargetedAtElement(element)) {
                    continue;
                }            

                DOMElementVisitor processingUnit = (DOMElementVisitor)configMap.getContentHandler();
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










