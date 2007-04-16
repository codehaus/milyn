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

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XMl utility methods.
 * 
 * @author Tom Fennelly
 */

public class XmlUtil {

	/**
	 * Remove all entities from the supplied <code>Reader</code> stream
	 * replacing them with their actual character values. <p/> Both the read and
	 * write streams are returned unclosed.
	 * 
	 * @param reader
	 *            The read stream.
	 * @param writer
	 *            The write stream.
	 */
	public static void removeEntities(Reader reader, Writer writer)
			throws IOException {
		int curChar = -1;
		StringBuffer ent = null;

		if (reader == null) {
			throw new IllegalArgumentException("null reader arg");
		} else if (writer == null) {
			throw new IllegalArgumentException("null writer arg");
		}

		ent = new StringBuffer(50);
		while ((curChar = reader.read()) != -1) {
			if (curChar == '&') {
				if (ent.length() > 0) {
					writer.write(ent.toString());
					ent.setLength(0);
				}
				ent.append((char) curChar);
			} else if (curChar == ';' && ent.length() > 0) {
				int entLen = ent.length();

				if (entLen > 1) {
					if (ent.charAt(1) == '#') {
						if (entLen > 2) {
							char char2 = ent.charAt(2);

							try {
								if (char2 == 'x' || char2 == 'X') {
									if (entLen > 3) {
										writer.write(Integer.parseInt(ent
												.substring(3), 16));
									} else {
										writer.write(ent.toString());
										writer.write(curChar);
									}
								} else {
									writer.write(Integer.parseInt(ent
											.substring(2)));
								}
							} catch (NumberFormatException nfe) {
								// bogus character ref - leave as is.
								writer.write(ent.toString());
								writer.write(curChar);
							}
						} else {
							writer.write("&#;");
						}
					} else {
						Character character = HTMLEntityLookup
								.getCharacterCode(ent.substring(1));

						if (character != null) {
							writer.write(character.charValue());
						} else {
							// bogus entity ref - leave as is.
							writer.write(ent.toString());
							writer.write(curChar);
						}
					}
				} else {
					writer.write("&;");
				}

				ent.setLength(0);
			} else if (ent.length() > 0) {
				ent.append((char) curChar);
			} else {
				writer.write(curChar);
			}
		}

		if (ent.length() > 0) {
			writer.write(ent.toString());
		}
	}

	/**
	 * Remove all entities from the supplied <code>String</code> stream
	 * replacing them with there actual character values.
	 * 
	 * @param string
	 *            The string on which the operation is to be carried out.
	 * @return The string with its entities rewriten.
	 */
	public static String removeEntities(String string) {
		if (string == null) {
			throw new IllegalArgumentException("null string arg");
		}

		try {
			StringReader reader = new StringReader(string);
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);

			return writer.toString();
		} catch (Exception excep) {
			excep.printStackTrace();
			return string;
		}
	}

	/**
	 * Rewrite all entities from the supplied <code>Reader</code> stream
	 * replacing them with their character reference equivalents. <p/> Example:
	 * <b>&ampnbsp;</b> is rewriten as <b>&amp#160;</b> <p/> Both the read and
	 * write streams are returned unclosed.
	 * 
	 * @param reader
	 *            The read stream.
	 * @param writer
	 *            The write stream.
	 */
	public static void rewriteEntities(Reader reader, Writer writer)
			throws IOException {
		int curChar = -1;
		StringBuffer ent;
		char[] entBuf;

		if (reader == null) {
			throw new IllegalArgumentException("null reader arg");
		} else if (writer == null) {
			throw new IllegalArgumentException("null writer arg");
		}

		ent = new StringBuffer(50);
		entBuf = new char[50];
		while ((curChar = reader.read()) != -1) {
			if (curChar == '&') {
				if (ent.length() > 0) {
					writer.write(ent.toString());
					ent.setLength(0);
				}
				ent.append((char) curChar);
			} else if (curChar == ';' && ent.length() > 0) {
				int entLen = ent.length();

				if (entLen > 1) {
					if (ent.charAt(1) == '#') {
						// Already a character ref.
						ent.getChars(0, ent.length(), entBuf, 0);
						writer.write(entBuf, 0, ent.length());
						writer.write(';');
					} else {
						Character character = HTMLEntityLookup
								.getCharacterCode(ent.substring(1));

						if (character != null) {
							writer.write("&#");
							writer.write(String.valueOf((int) character
									.charValue()));
							writer.write(";");
						} else {
							// bogus entity ref - leave as is.
							writer.write(ent.toString());
							writer.write(curChar);
						}
					}
				} else {
					writer.write("&;");
				}

				ent.setLength(0);
			} else if (ent.length() > 0) {
				ent.append((char) curChar);
			} else {
				writer.write(curChar);
			}
		}

		if (ent.length() > 0) {
			writer.write(ent.toString());
		}
	}

	/**
	 * Parse the XML stream and return the associated W3C Document object.
	 * 
	 * @param stream
	 *            The stream to be parsed.
	 * @param validate
	 *            True if the document is to be validated, otherwise false.
	 * @param expandEntityRefs
	 *            Expand entity References as per
	 *            {@link DocumentBuilderFactory#setExpandEntityReferences(boolean)}.
	 * @return The W3C Document object associated with the input stream.
	 */
	public static Document parseStream(InputStream stream, boolean validate,
			boolean expandEntityRefs) throws SAXException, IOException {
		return parseStream(stream, new LocalEntityResolver(), validate,
				expandEntityRefs);
	}

	/**
	 * Parse the XML stream and return the associated W3C Document object.
	 * 
	 * @param stream
	 *            The stream to be parsed.
	 * @param entityResolver
	 *            Entity resolver to be used during the parse.
	 * @param validate
	 *            True if the document is to be validated, otherwise false.
	 * @param expandEntityRefs
	 *            Expand entity References as per
	 *            {@link DocumentBuilderFactory#setExpandEntityReferences(boolean)}.
	 * @return The W3C Document object associated with the input stream.
	 */
	public static Document parseStream(InputStream stream,
			EntityResolver entityResolver, boolean validate,
			boolean expandEntityRefs) throws SAXException, IOException {
		if (stream == null) {
			throw new IllegalArgumentException(
					"null 'stream' arg in method call.");
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = null;

			factory.setValidating(validate);
			factory.setExpandEntityReferences(expandEntityRefs);
            docBuilder = factory.newDocumentBuilder();
			docBuilder.setEntityResolver(entityResolver);
			docBuilder.setErrorHandler(XMLParseErrorHandler.getInstance());

            return docBuilder.parse(stream);
		} catch (ParserConfigurationException e) {
			IllegalStateException state = new IllegalStateException(
					"Unable to parse XML stream - XML Parser not configured correctly.");
			state.initCause(e);
			throw state;
		} catch (FactoryConfigurationError e) {
			IllegalStateException state = new IllegalStateException(
					"Unable to parse XML stream - DocumentBuilderFactory not configured correctly.");
			state.initCause(e);
			throw state;
		}
	}

	private static String ELEMENT_NAME_FUNC = "/name()";

	private static XPathFactory xPathFactory = XPathFactory.newInstance();

	/**
	 * Get the W3C NodeList instance associated with the XPath selection
	 * supplied.
	 * 
	 * @param node
	 *            The document node to be searched.
	 * @param xpath
	 *            The XPath String to be used in the selection.
	 * @return The W3C NodeList instance at the specified location in the
	 *         document, or null.
	 */
	public static NodeList getNodeList(Node node, String xpath) {
		if (node == null) {
			throw new IllegalArgumentException(
					"null 'document' arg in method call.");
		} else if (xpath == null) {
			throw new IllegalArgumentException(
					"null 'xpath' arg in method call.");
		}
		try {
			XPath xpathEvaluater = xPathFactory.newXPath();

			if (xpath.endsWith(ELEMENT_NAME_FUNC)) {
				return (NodeList) xpathEvaluater.evaluate(xpath.substring(0,
						xpath.length() - ELEMENT_NAME_FUNC.length()), node,
						XPathConstants.NODESET);
			} else {
				return (NodeList) xpathEvaluater.evaluate(xpath, node,
						XPathConstants.NODESET);
			}
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("bad 'xpath' expression ["
					+ xpath + "].");
		}
	}

	/**
	 * Get the W3C Node instance associated with the XPath selection supplied.
	 * 
	 * @param node
	 *            The document node to be searched.
	 * @param xpath
	 *            The XPath String to be used in the selection.
	 * @return The W3C Node instance at the specified location in the document,
	 *         or null.
	 */
	public static Node getNode(Node node, String xpath) {
		NodeList nodeList = getNodeList(node, xpath);

		if (nodeList == null || nodeList.getLength() == 0) {
			return null;
		} else {
			return nodeList.item(0);
		}
	}

	/**
	 * Get the String data associated with the XPath selection supplied.
	 * 
	 * @param node
	 *            The node to be searched.
	 * @param xpath
	 *            The XPath String to be used in the selection.
	 * @return The string data located at the specified location in the
	 *         document, or an empty string for an empty resultset query.
	 */
	public static String getString(Node node, String xpath) {
		NodeList nodeList = getNodeList(node, xpath);

		if (nodeList == null || nodeList.getLength() == 0) {
			return "";
		}

		if (xpath.endsWith(ELEMENT_NAME_FUNC)) {
			if (nodeList.getLength() > 0) {
				return nodeList.item(0).getNodeName();
			} else {
				return "";
			}
		} else {
			return serialize(nodeList);
		}
	}

    private static TransformerFactory factory = TransformerFactory.newInstance();
    
    /**
	 * Serialise the supplied W3C DOM subtree.
	 * 
	 * @param nodeList
	 *            The DOM subtree as a NodeList.
	 * @return The subtree in serailised form.
	 * @throws DOMException
	 *             Unable to serialise the DOM.
	 */
	public static String serialize(NodeList nodeList) throws DOMException {
		if (nodeList == null) {
			throw new IllegalArgumentException(
					"null 'subtree' NodeIterator arg in method call.");
		}

		try {
            Transformer transformer;

            transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			StringWriter writer = new StringWriter();
			int listLength = nodeList.getLength();

			// Iterate through the Node List.
			for (int i = 0; i < listLength; i++) {
				Node node = nodeList.item(i);

                if (XmlUtil.isTextNode(node)) {
					writer.write(node.getNodeValue());
				} else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
					writer.write(((Attr) node).getValue());
				} else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    transformer.transform(new DOMSource(node), new StreamResult(writer));
                }
            }

			return writer.toString();
		} catch (Exception e) {
			DOMException domExcep = new DOMException(
					DOMException.INVALID_ACCESS_ERR,
					"Unable to serailise DOM subtree.");
			domExcep.initCause(e);
			throw domExcep;
		}
	}

	/**
	 * Is the supplied W3C DOM Node a text node.
	 * 
	 * @param node
	 *            The node to be tested.
	 * @return True if the node is a text node, otherwise false.
	 */
	public static boolean isTextNode(Node node) {
		short nodeType;

		if (node == null) {
			return false;
		}
		nodeType = node.getNodeType();

		return nodeType == Node.CDATA_SECTION_NODE
				|| nodeType == Node.TEXT_NODE;
	}

	/**
	 * XML Parse error handler.
	 * 
	 * @author tfennelly
	 */
	static class XMLParseErrorHandler implements ErrorHandler {

		/**
		 * Singleton instance reference of this class.
		 */
		private static XMLParseErrorHandler singleton = new XMLParseErrorHandler();

		/**
		 * Private constructor.
		 */
		private XMLParseErrorHandler() {
		}

		/**
		 * Get this classes singleton reference.
		 * 
		 * @return This classes singleton reference.
		 */
		private static XMLParseErrorHandler getInstance() {
			return singleton;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
		 */
		public void warning(SAXParseException arg0) throws SAXException {
			throw arg0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
		 */
		public void error(SAXParseException arg0) throws SAXException {
			throw arg0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
		 */
		public void fatalError(SAXParseException arg0) throws SAXException {
			throw arg0;
		}
	}
}
