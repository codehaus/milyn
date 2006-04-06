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

package org.milyn.dom;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.crimson.tree.XmlDocument;
import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;
import org.milyn.container.ContainerRequest;
import org.milyn.dtd.DTDStore.DTDObjectContainer;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class Parser {

	private static Log logger = LogFactory.getLog(Parser.class);
	private ContainerRequest request;
	HashSet emptyElements = new HashSet();
	
	public Parser() {
	}
	
	public Parser(ContainerRequest request) {
		if(request == null) {
			throw new IllegalArgumentException("null 'request' arg in method call.");
		}
		this.request = request;
		initialiseEmptyElements();
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
		XMLReader reader = new HTMLSAXParser();

		reader.setContentHandler(contentHandler);
		reader.setProperty("http://xml.org/sax/properties/lexical-handler", contentHandler);
		reader.setFeature("http://cyberneko.org/html/features/balance-tags", false);
		reader.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
		reader.setFeature("http://apache.org/xml/features/scanner/notify-char-refs", true);
		reader.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);
		reader.setFeature("http://cyberneko.org/html/features/scanner/notify-builtin-refs", true);
		reader.parse(new InputSource(source));
	}

	/**
	 * HTML parser using the cyberneko HTML configuration.
	 * @author tfennelly
	 */
	public static class HTMLSAXParser extends AbstractSAXParser {
	    public HTMLSAXParser() {
	        super(new HTMLConfiguration());
	    }
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
				ownerDocument = new XmlDocument();
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
			String elName = localName.toLowerCase();
			Node currentNode = (Node)nodeStack.peek();
			
			try {
				// TODO: What about namespaces?
				newElement = ownerDocument.createElement(elName);
				currentNode.appendChild(newElement);
				if(!emptyElements.contains(elName)) {
					nodeStack.push(newElement);
				}
			} catch(DOMException e) {
				logger.error("DOMException creating start element: namespaceURI=" + namespaceURI + ", localName=" + elName, e);
				throw e;
			}
			
			for(int i = 0; i < attsCount; i++) {
				String attName = atts.getLocalName(i).toLowerCase();
				String attValue = atts.getValue(i);				
				try {
					newElement.setAttribute(attName, attValue);
				} catch(DOMException e) {
					logger.error("DOMException setting element attribute " + attName + "=" + attValue + "[namespaceURI=" + namespaceURI + ", localName=" + elName + "].", e);
					throw e;
				}
			}
		}

		public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
			String elName = localName.toLowerCase();
			if(!emptyElements.contains(elName)) {
				int index = getIndex(elName);
				if(index != -1) {
					nodeStack.setSize(index);
				} else {
					logger.warn("Ignoring unexpected end [" + localName + "] element event. Request: [" + request.getRequestURI() + "] - document location: [" + getCurPath() + "]");
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
					if(element.getTagName().equals(elName)) {
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
			if(ownerDocument instanceof XmlDocument) {
				((XmlDocument)ownerDocument).setDoctype(publicId, systemId, null);
			}
		}

		public void endDTD() throws SAXException {
			//ownerDocument.
		}
	}	
}
