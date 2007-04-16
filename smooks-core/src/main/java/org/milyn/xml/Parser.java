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

package org.milyn.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.dtd.DTDStore.DTDObjectContainer;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Smooks data stream parser.
 * <p/>
 * This parser can be configured to use a SAX Parser targeted at a specific data stream type.
 * This lets you parse a stream of any type, convert it to a stream of SAX event and so treat the stream
 * as an XML data stream, even when the stream is non-XML.
 * <p/>
 * If the configured parser implements the {@link org.milyn.xml.SmooksXMLReader}, the configuration will be
 * passed to the parser through the {@link org.milyn.xml.SmooksXMLReader#setConfiguration(SmooksResourceConfiguration)}
 * method.  This allows you to configure the parser. 
 * 
 * <h3 id="parserconfig">.cdrl Configuration</h3>
 * <pre>
 * &lt;smooks-resource selector="org.xml.sax.driver" path="org.milyn.protocolx.XParser" &gt;
 * 	&lt;!-- 
 * 		Optional list of driver parameters for {@link org.milyn.xml.SmooksXMLReader} implementations.
 * 		See {@link org.milyn.cdr.SmooksResourceConfiguration} for how to add configuration parameters. 
 * 	--&gt;
 * &lt;/smooks-resource&gt;
 * </pre>
 * 
 * @author tfennelly
 */
public class Parser {

	private static Log logger = LogFactory.getLog(Parser.class);
	private static DocumentBuilder documentBuilder;
	private ExecutionContext request;
	private HashSet emptyElements = new HashSet();
    private SmooksResourceConfiguration saxDriverConfig;
    
    static {
    	try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			IllegalStateException state = new IllegalStateException("XML DOM Parsing environment not configured properly.");
			state.initCause(e);
			throw state;
		}
    }

    /**
     * Default constructor.
     */
	public Parser() {
	}

	/**
	 * Public constructor.
	 * <p/>
	 * This constructor attempts to lookup a SAX Parser config under the "org.xml.sax.driver" selector string.
	 * See <a href="#parserconfig">.cdrl Configuration</a>.
	 * @param request The Smooks Container Request that the parser is being instantiated on behalf of.
	 */
	public Parser(ExecutionContext request) {
		if(request == null) {
			throw new IllegalArgumentException("null 'request' arg in method call.");
		}
		this.request = request;
		initialiseEmptyElements();
        
        // Allow the sax driver to be specified as a useragent config (under selector "org.xml.sax.driver").
		saxDriverConfig = getSAXParserConfiguration(request.getDeliveryConfig());
	}
    
	/**
	 * Public constructor.
	 * @param request The Smooks Container Request that the parser is being instantiated on behalf of.
	 * @param saxDriverConfig SAX Parser configuration. See <a href="#parserconfig">.cdrl Configuration</a>.
	 */
    public Parser(ExecutionContext request, SmooksResourceConfiguration saxDriverConfig) {
        this(request);
        this.saxDriverConfig = saxDriverConfig;
        if(saxDriverConfig.getResource() == null) {
            throw new IllegalStateException("Invalid SAX Parser configuration.  Must specify 'path' attribute to contain the parser class name.");
        }
    }
    
    /**
     * Get the SAX Parser configuration for the useragent associated with the supplied delivery configuration.
     * @param useragentConfig Useragent content delivery configuration.
     * @return Returns the SAX Parser configuration for the useragent associated with the supplied delivery 
     * configuration, or null if no parser configuration is specified.
     */
    public static SmooksResourceConfiguration getSAXParserConfiguration(ContentDeliveryConfig useragentConfig) {
    	if(useragentConfig == null) {
    		throw new IllegalArgumentException("null 'useragentConfig' arg in method call.");
    	}
    	
    	SmooksResourceConfiguration saxDriverConfig = null;
        List saxConfigs = useragentConfig.getSmooksResourceConfigurations("org.xml.sax.driver");
        
        if(saxConfigs != null && !saxConfigs.isEmpty()) {
            saxDriverConfig = (SmooksResourceConfiguration)saxConfigs.get(0);
            if(saxDriverConfig.getResource() == null) {
                throw new IllegalStateException("Invalid SAX Parser configuration.  Must specify 'path' attribute to contain the parser class name.");
            }
        }
        
        return saxDriverConfig;
    }
    
	private void initialiseEmptyElements() {
		DTDObjectContainer dtd = request.getDeliveryConfig().getDTD();
		if(dtd != null) {
			String[] emptyEls = dtd.getEmptyElements();
			
			if(emptyEls != null && emptyEls.length > 0) {
				for(int i = 0; i < emptyEls.length; i++) {
					emptyElements.add(emptyEls[i]);
				}
			}
		}
	}

	/**
	 * Document parser.
	 * @param source Source content stream to be parsed.
	 * @return W3C ownerDocument.
	 * @throws SAXException Unable to parse the content.
	 * @throws IOException Unable to read the input stream.
	 */
	public Document parse(Reader source) throws IOException, SAXException {
	   	SmooksContentHandler contentHandler = new SmooksContentHandler();
		
	   	parse(source, contentHandler);
		
		return contentHandler.getDocument();
	}

	/**
	 * Append the content, behind the supplied input stream, to suplied
	 * document element.
	 * <p/>
	 * Used to merge document fragments into a document.
	 * @param source Source content stream to be parsed.
	 * @param appendElement DOM element to which the content fragment is to 
	 * be added.
	 * @throws SAXException Unable to parse the content.
	 * @throws IOException Unable to read the input stream.
	 */
	public void append(Reader source, Element appendElement) throws IOException, SAXException {
	   	SmooksContentHandler contentHandler = new SmooksContentHandler();
		
		contentHandler.setAppendElement(appendElement);
	   	parse(source, contentHandler);
	}
	
	/**
	 * Perform the actual parse into the supplied content handler.
	 * @param source Source content stream to be parsed.
	 * @param contentHandler Content handler instance that will build/append-to the DOM.
	 * @throws SAXException Unable to parse the content.
	 * @throws IOException Unable to read the input stream.
	 */
	private void parse(Reader source, SmooksContentHandler contentHandler) throws SAXException, IOException {
        XMLReader reader;
        
        if(saxDriverConfig != null) {
            reader = XMLReaderFactory.createXMLReader(saxDriverConfig.getResource());
            if(reader instanceof SmooksXMLReader) {
            	((SmooksXMLReader)reader).setConfiguration(saxDriverConfig);
            	((SmooksXMLReader)reader).setExecutionContext(request);
            }
        } else {
            reader = XMLReaderFactory.createXMLReader();
        }

		reader.setContentHandler(contentHandler);
		reader.setProperty("http://xml.org/sax/properties/lexical-handler", contentHandler);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);

		reader.parse(new InputSource(source));
	}

	/**
	 * Content and Lexical Handler class for DOM construction.
	 * @see org.xml.sax.ContentHandler
	 * @see org.xml.sax.ext.LexicalHandler  
	 * @author tfennelly
	 */
	private class SmooksContentHandler implements ContentHandler, LexicalHandler {

		private Document ownerDocument;
		private Stack nodeStack = new Stack();
		private boolean inEntity = false;
		
		public void startDocument() throws SAXException {
			if(ownerDocument == null) {
				// Parsing a new ownerDocument from scratch - create the DOM Document
				// instance and set it as the startNode.
				ownerDocument = documentBuilder.newDocument();
				// Initialise the stack with the Document node.
				nodeStack.push(ownerDocument);
			} 
		}

		/**
		 * Get the Document node of the document into which this handler 
		 * is parsing.
		 * @return Returns the ownerDocument.
		 */
		public Document getDocument() {
			return ownerDocument;
		}
		
		/**
		 * Set the DOM Element node on which the parsed content it to be added.
		 * <p/>
		 * Used to merge ownerDocument fragments etc.
		 * @param appendElement The append DOM element.
		 */
		private void setAppendElement(Element appendElement) {
			ownerDocument = appendElement.getOwnerDocument();
			// Initialise the stack with the append element node.
			nodeStack.push(appendElement);
		}

		public void endDocument() throws SAXException {
		}

		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			Element newElement = null;
			int attsCount = atts.getLength();
			Node currentNode = (Node)nodeStack.peek();
			
			try {
                if(namespaceURI != null && qName != null && !qName.equals("")) {
                    newElement = ownerDocument.createElementNS(namespaceURI.intern(), qName);
                } else {
                    newElement = ownerDocument.createElement(localName.intern());
                }
                
				currentNode.appendChild(newElement);
				if(!emptyElements.contains(qName != null?qName.toLowerCase():localName.toLowerCase())) {
					nodeStack.push(newElement);
				}
			} catch(DOMException e) {
				logger.error("DOMException creating start element: namespaceURI=" + namespaceURI + ", localName=" + localName, e);
				throw e;
			}
			
			for(int i = 0; i < attsCount; i++) {
				String attNamespace = atts.getURI(i);
                String attQName = atts.getQName(i);
                String attLocalName = atts.getLocalName(i);
				String attValue = atts.getValue(i);
				try {
                    if(attNamespace != null && attQName != null) {
                    	attNamespace = attNamespace.intern();
                    	if(attNamespace.equals("")) {
                    		if(attQName.startsWith("xmlns:")) {
                    			attNamespace = Namespace.XMLNS_URI;
                    		} else if(attQName.startsWith("xml:")) {
                    			attNamespace = Namespace.XML_URI;
                    		}
                    	}
                        newElement.setAttributeNS(attNamespace, attQName, attValue);
                    } else {
                        newElement.setAttribute(attLocalName.intern(), attValue);
                    }
				} catch(DOMException e) {
					logger.error("DOMException setting element attribute " + attLocalName + "=" + attValue + "[namespaceURI=" + namespaceURI + ", localName=" + localName + "].", e);
					throw e;
				}
			}
		}

		public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
			String elName;
            
            if(qName != null && !qName.equals("")) {
                elName = qName.toLowerCase();
            }else {
                elName = localName.toLowerCase();
            }
            
			if(!emptyElements.contains(elName)) {
				int index = getIndex(elName);
				if(index != -1) {
					nodeStack.setSize(index);
				} else {
					logger.warn("Ignoring unexpected end [" + localName + "] element event. Request: [" + request.getDocumentSource() + "] - document location: [" + getCurPath() + "]");
				}
			}
		}

		private String getCurPath() {
			StringBuffer path = new StringBuffer();
			int stackSize = nodeStack.size();
			
			for(int i = 0; i < stackSize; i++) {
				Node node = (Node)nodeStack.elementAt(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					path.append('/').append(((Element)node).getTagName());
				}
			}
			
			return path.toString();
		}
		
		private int getIndex(String elName) {
			for(int i = nodeStack.size() - 1; i >= 0; i--) {
				Node node = (Node)nodeStack.elementAt(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element)node;
					if(element.getTagName().toLowerCase().equals(elName)) {
						return i;
					}
				}
			}
			
			return -1;
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			try {
				Node currentNode = (Node)nodeStack.peek();
	
				switch (currentNode.getNodeType()) {
				case Node.ELEMENT_NODE:
					if(inEntity) {
						currentNode.appendChild(ownerDocument.createTextNode("&#"+ (int)ch[0] + ";"));
					} else {
						currentNode.appendChild(ownerDocument.createTextNode(new String(ch, start, length)));
					}
					break;
				case Node.CDATA_SECTION_NODE:
					((CDATASection)currentNode).setData(new String(ch, start, length));
					break;
				default:
					break;
				} 
			} catch(DOMException e) {
				logger.error("DOMException appending character data [" + new String(ch, start, length) + "]", e);
				throw e;
			}
		}

		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
			characters(ch, start, length);
		}

		public void startCDATA() throws SAXException {
			CDATASection newCDATASection = ownerDocument.createCDATASection("dummy");
			Node currentNode;
			
			currentNode = (Node)nodeStack.peek();
			currentNode.appendChild(newCDATASection);
			nodeStack.push(newCDATASection);
		}

		public void endCDATA() throws SAXException {
			nodeStack.pop();
		}

		public void comment(char[] ch, int start, int length) throws SAXException {
			try {
				Node currentNode = (Node)nodeStack.peek();
				Comment newComment;
				
				newComment = ownerDocument.createComment(new String(ch, start, length));
				
				currentNode.appendChild(newComment);
			} catch(DOMException e) {
				logger.error("DOMException comment data [" + new String(ch, start, length) + "]", e);
				throw e;
			}
		}

		public void startEntity(String name) throws SAXException {
			inEntity = true;
		}

		public void endEntity(String name) throws SAXException {
			inEntity = false;
		}

		public void processingInstruction(String target, String data) throws SAXException {
		}

		public void startPrefixMapping(String prefix, String uri) throws SAXException {
		}

		public void endPrefixMapping(String prefix) throws SAXException {
		}

		public void skippedEntity(String name) throws SAXException {
		}

		public void setDocumentLocator(Locator locator) {
		}

		public void startDTD(String name, String publicId, String systemId) throws SAXException {
			DocumentType docType = documentBuilder.getDOMImplementation().createDocumentType(name, publicId, systemId);

			ownerDocument.appendChild(docType);
		}

		public void endDTD() throws SAXException {
			//ownerDocument.
		}
	}	
}
