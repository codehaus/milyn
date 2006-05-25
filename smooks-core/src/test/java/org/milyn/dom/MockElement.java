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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MockElement extends MockNode implements Element {
	
	private String tagName;

	public MockElement(String tagName) {
		this.tagName = tagName;
	}

	public short getNodeType() {
		return Node.ELEMENT_NODE;
	}
	
	public String getTagName() {
		return tagName;
	}

	public void removeAttribute(String name) throws DOMException {
	}

	public boolean hasAttribute(String name) {
		return false;
	}

	public String getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
		// TODO Auto-generated method stub
		
	}

	public void setAttribute(String name, String value) throws DOMException {
		// TODO Auto-generated method stub
		
	}

	public boolean hasAttributeNS(String namespaceURI, String localName) {
		// TODO Auto-generated method stub
		return false;
	}

	public Attr getAttributeNode(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeList getElementsByTagName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAttributeNS(String namespaceURI, String localName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
		// TODO Auto-generated method stub
		
	}

	public Attr getAttributeNodeNS(String namespaceURI, String localName) {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
		// TODO Auto-generated method stub
		return null;
	}
}
