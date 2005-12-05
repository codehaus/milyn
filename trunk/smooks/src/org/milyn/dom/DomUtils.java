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

import java.util.List;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * W3C DOM utility methods.
 * @author tfennelly
 */
public abstract class DomUtils {

	/**
	 * Copy child node references from source to target.
	 * @param source Source Node.
	 * @param target Target Node.
	 */
	public static void copyChildNodes(Node source, Node target) {
		List nodeList = DomUtils.copyNodeList(source.getChildNodes());
		int childCount = nodeList.size();
		
		for(int i = 0; i < childCount; i++) {
			target.appendChild((Node)nodeList.get(i));
		}
	}
	
	/**
	 * Replace one node with another node.
	 * @param newNode New node - added in same location as oldNode.
	 * @param oldNode Old node - removed.
	 */
	public static void replaceNode(Node newNode, Node oldNode) {
		oldNode.getParentNode().replaceChild(newNode, oldNode);
	}

	/**
	 * Insert the supplied nodes before the supplied reference node (refNode).
	 * @param newNodes Nodes to be inserted.
	 * @param refNode Reference node before which the supplied nodes should
	 * be inserted.
	 */
	public static void insertBefore(NodeList newNodes, Node refNode) {
		Node parentNode = refNode.getParentNode();
		int nodeCount = newNodes.getLength();
		List nodeList = DomUtils.copyNodeList(newNodes);
		
		for(int i = 0; i < nodeCount; i++) {
			parentNode.insertBefore((Node)nodeList.get(i), refNode);
		}
	}
	
	/**
	 * Replace one node with a list of nodes.
	 * <p/>
	 * Clones the NodeList elements.
	 * @param newNodes New nodes - added in same location as oldNode.
	 * @param oldNode Old node - removed.
	 */
	public static void replaceNode(NodeList newNodes, Node oldNode) {
		replaceNode(newNodes, oldNode, true);
	}
	
	/**
	 * Replace one node with a list of nodes.
	 * @param newNodes New nodes - added in same location as oldNode.
	 * @param oldNode Old node - removed.
	 * @param clone Clone Nodelist Nodes.
	 */
	public static void replaceNode(NodeList newNodes, Node oldNode, boolean clone) {
		Node parentNode = oldNode.getParentNode();
		int nodeCount = newNodes.getLength();
		List nodeList = DomUtils.copyNodeList(newNodes);
		
		for(int i = 0; i < nodeCount; i++) {
			if(clone) {
				parentNode.insertBefore(((Node)nodeList.get(i)).cloneNode(true), oldNode);
			} else {
				parentNode.insertBefore((Node)nodeList.get(i), oldNode);
			}
		}
		oldNode.getParentNode().removeChild(oldNode);
	}

	/**
	 * Rename element.
	 * @param element The element to be renamed.
	 * @param replacementElement The tag name of the replacement element.
	 * @param keepChildContent <code>true</code> if the target element's child content
	 * is to be copied to the replacement element, false if not. Default <code>true</code>.
	 * @param keepAttributes <code>true</code> if the target element's attributes
	 * are to be copied to the replacement element, false if not. Default <code>true</code>.
	 * @return The renamed element.
	 */
	public static Element renameElement(Element element, String replacementElement, boolean keepChildContent, boolean keepAttributes) {
		Element replacement; 

		if(element == null) {
			throw new IllegalStateException("null 'element' arg in method call.");
		}
		if(replacementElement == null) {
			throw new IllegalStateException("null 'replacementElement' arg in method call.");
		}
		
		replacement = element.getOwnerDocument().createElement(replacementElement);
		if(keepChildContent) {
			DomUtils.copyChildNodes(element, replacement);
		}
		if(keepAttributes) { 
			NamedNodeMap attributes = element.getAttributes();
			int attributeCount = attributes.getLength();
			
			for(int i = 0; i < attributeCount; i++) {
				Attr attribute = (Attr)attributes.item(i);
				replacement.setAttribute(attribute.getName(), attribute.getValue());
			}
		}
		DomUtils.replaceNode(replacement, element);
		
		return replacement;
	}
	
	/**
	 * Remove all child nodes from the supplied node.
	 * @param node to be "cleared".
	 */
	public static void removeChildren(Node node) {
		NodeList children = node.getChildNodes();
		int nodeCount = children.getLength();
		
		for(int i = 0; i < nodeCount; i++) {
			node.removeChild(children.item(0));
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
	public static List copyNodeList(NodeList nodeList) {
		Vector copy = new Vector();
		
		if(nodeList != null) {
			int nodeCount = nodeList.getLength();
		
			for(int i = 0; i < nodeCount; i++) {
				copy.add(nodeList.item(i));
			}
		}
		
		return copy;
	}
	
	/**
	 * Append the nodes from the supplied list to the supplied node. 
	 * @param node Node to be appended to.
	 * @param nodes List of nodes to append.
	 */
	public static void appendList(Node node, List nodes) {
		int nodeCount = nodes.size();
	
		for(int i = 0; i < nodeCount; i++) {
			node.appendChild((Node)nodes.get(i));
		}
	}
	
	/**
	 * Get a boolean attribute from the supplied element.
	 * @param element The element.
	 * @param attribName The attribute name.
	 * @return True if the attribute value is "true" (case insensitive), otherwise false.
	 */
	public static boolean getBooleanAttrib(Element element, String attribName) {
		String attribVal = element.getAttribute(attribName);
		
		return (attribVal != null?attribVal.equalsIgnoreCase("true"):false);
	}
	
	/**
	 * Get the parent element of the supplied element having the
	 * specified tag name.
	 * @param child Child element. 
	 * @param parentName Parent element name.
	 * @return The first parent element of "child" having the tagname "parentName",
	 * or null if no such parent element exists.
	 */
	public static Element getParentElement(Element child, String parentName) {
		Element parent = (Element)child.getParentNode();
		
		while(parent != null) {
			if(parent.getTagName().equalsIgnoreCase(parentName)) {
				return parent;
			} 
			parent = (Element)parent.getParentNode();
		}
		
		return null;
	}
	
	/**
	 * Get attribute value, returning <code>null</code> if unset.
	 * <p/>
	 * Some DOM implementations return an empty string for an unset
	 * attribute.
	 * @param element The DOM element.
	 * @param attributeName The attribute to get.
	 * @return The attribute value, or <code>null</code> if unset.
	 */
	public static String getAttributeValue(Element element, String attributeName) {
		String attributeValue = element.getAttribute(attributeName);
		
		if(attributeValue.length() == 0 && !element.hasAttribute(attributeName)) {
			return null;
		}
		
		return attributeValue;
	}

	/**
	 * Count the DOM nodes of the supplied type (nodeType) before supplied
	 * node, not including the node itself.
	 * <p/>
	 * Counts the sibling nodes.
	 * @param node Node whose siblings are to be counted.
	 * @param nodeType The DOM {@link Node} type of the siblings to be counted. 
	 * @return The number of siblings of the supplied type before the supplied node.
	 */
	public static int countNodesBefore(Node node, short nodeType) {
		Node parent = node.getParentNode();
		NodeList siblings = parent.getChildNodes();
		int count = 0;
		int siblingCount = siblings.getLength();
		
		for(int i = 0; i < siblingCount; i++) {
			Node sibling = siblings.item(i);
			
			if(sibling == node) {
				break;
			}
			if(sibling.getNodeType() == nodeType) {
				count++;
			}			
		}
		
		return count;
	}

	/**
	 * Count the DOM nodes before the supplied node, not including the node itself.
	 * <p/>
	 * Counts the sibling nodes.
	 * @param node Node whose siblings are to be counted.
	 * @return The number of siblings before the supplied node.
	 */
	public static int countNodesBefore(Node node) {
		Node parent = node.getParentNode();
		NodeList siblings = parent.getChildNodes();
		int count = 0;
		int siblingCount = siblings.getLength();
		
		for(int i = 0; i < siblingCount; i++) {
			Node sibling = siblings.item(i);
			
			if(sibling == node) {
				break;
			}
			count++;
		}
		
		return count;
	}


	/**
	 * Count the DOM element nodes before the supplied node, having the specified 
	 * tag name, not including the node itself.
	 * <p/>
	 * Counts the sibling nodes.
	 * @param node Node whose element siblings are to be counted.
	 * @param tagName The tag name of the sibling elements to be counted. 
	 * @return The number of siblings elements before the supplied node with the 
	 * specified tag name.
	 */
	public static int countElementsBefore(Node node, String tagName) {
		Node parent = node.getParentNode();
		NodeList siblings = parent.getChildNodes();
		int count = 0;
		int siblingCount = siblings.getLength();
		
		for(int i = 0; i < siblingCount; i++) {
			Node sibling = siblings.item(i);
			
			if(sibling == node) {
				break;
			}
			if(sibling.getNodeType() == Node.ELEMENT_NODE && ((Element)sibling).getTagName().equals(tagName)) {
				count++;
			}			
		}
		
		return count;
	}

	/**
	 * Get all the text DOM sibling nodes before the supplied node and 
	 * concatenate them together into a single String.
	 * @param node Test node.
	 * @return String containing the concatentated text.
	 */
	public static String getTextBefore(Node node) {
		Node parent = node.getParentNode();
		NodeList siblings = parent.getChildNodes();
		StringBuffer text = new StringBuffer();
		int siblingCount = siblings.getLength();
		
		for(int i = 0; i < siblingCount; i++) {
			Node sibling = siblings.item(i);
			
			if(sibling == node) {
				break;
			}
			if(sibling.getNodeType() == Node.TEXT_NODE) {
				text.append(((Text)sibling).getData());
			}			
		}
		
		return text.toString();
	}
	
	/**
	 * Construct the XPath of the supplied DOM Node.
	 * <p/>
	 * Supports element, comment and cdata sections DOM Node types.
	 * @param node DOM node for XPath generation.
	 * @return XPath string representation of the supplied DOM Node.
	 */
	public static String getXPath(Node node) {
		StringBuffer xpath = new StringBuffer();
		Node parent = node.getParentNode();
		
		switch (node.getNodeType()) {
		case Node.ELEMENT_NODE:
			xpath.append(getXPathToken((Element)node));
			break;
		case Node.COMMENT_NODE:
			int commentNum = DomUtils.countNodesBefore(node, Node.COMMENT_NODE);
			xpath.append("/{COMMENT}[" + commentNum + 1 + "]");
			break;
		case Node.CDATA_SECTION_NODE:
			int cdataNum = DomUtils.countNodesBefore(node, Node.CDATA_SECTION_NODE);
			xpath.append("/{CDATA}[" + cdataNum + 1 + "]");
			break;
		default:
			throw new UnsupportedOperationException("XPath generation for supplied DOM Node type not supported.  Only supports element, comment and cdata section DOM nodes.");
		}

		while(parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
			xpath.insert(0, getXPathToken((Element)parent));			
			parent = parent.getParentNode();
		}

		return xpath.toString();
	}

	private static String getXPathToken(Element element) {
		String tagName = element.getTagName();
		int count = DomUtils.countElementsBefore(element, tagName);
		String xpathToken;
		
		if(count > 0) {
			xpathToken = "/" + tagName + "[" + (count + 1) + "]";
		} else {
			xpathToken = "/" + tagName;
		}
		
		return xpathToken;
	}
}
