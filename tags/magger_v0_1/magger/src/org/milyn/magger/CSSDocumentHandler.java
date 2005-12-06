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

package org.milyn.magger;

import java.io.IOException;
import java.net.URI;
import java.util.Stack;

import org.apache.batik.css.engine.sac.ExtendedSelector;
import org.milyn.resource.ExternalResourceLocator;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

class CSSDocumentHandler implements org.w3c.css.sac.DocumentHandler {

	private URI baseURI;
	private Stack mediaStack = new Stack();
	private CSSStylesheet stylesheet;
	private SelectorList curSelectors;
	private ExternalResourceLocator resourceLocator;
	
	CSSDocumentHandler(CSSStylesheet stylesheet, URI baseURI, SACMediaList media, ExternalResourceLocator resourceLocator) {
		this.stylesheet = stylesheet;
		this.baseURI = baseURI;
		if(media != null) {
			mediaStack.push(media);
		}
		this.resourceLocator = resourceLocator;
	}
	
	public void startMedia(SACMediaList media) throws CSSException {
		mediaStack.push(media);
	}

	public void endMedia(SACMediaList media) throws CSSException {
		mediaStack.pop();
	}

	public void startSelector(SelectorList selectors) throws CSSException {
		curSelectors = selectors;
	}

	public void endSelector(SelectorList selectors) throws CSSException {
		curSelectors = null;
	}

	public void importStyle(String uri, SACMediaList media, String defaultNamespaceURI) throws CSSException {
		URI importURI = baseURI.resolve(uri);
		CSSParser parser = new CSSParser(resourceLocator);
		
		try {
			if(media != null) {
				parser.parse(importURI, media, stylesheet);
			} else {
				parser.parse(importURI, getCurrentMedia(), stylesheet);
			}
		} catch(IOException e) {
			throw new CSSException(e);
		}
	}

	public void property(String name, LexicalUnit value, boolean important) throws CSSException {
		CSSProperty property = new CSSProperty(name.intern(), value, important);
		SACMediaList mediaList = getCurrentMedia();
		int selectorCount = curSelectors.getLength();
		
		for(int i = 0; i < selectorCount; i++) {
			ExtendedSelector selector = (ExtendedSelector)curSelectors.item(i);
			stylesheet.addRule(new CSSRule(selector, property, mediaList));
		}
	}

	private SACMediaList getCurrentMedia() {
		SACMediaList mediaList;
		if(!mediaStack.isEmpty()) {
			mediaList = (SACMediaList)mediaStack.peek();
		} else {
			mediaList = null;
		}
		return mediaList;
	}

	public void startDocument(InputSource source) throws CSSException {
	}
	public void endDocument(InputSource source) throws CSSException {
	}
	public void comment(String text) throws CSSException {
	}
	public void ignorableAtRule(String atRule) throws CSSException {
	}
	public void startPage(String name, String pseudo_page) throws CSSException {
	}
	public void endPage(String name, String pseudo_page) throws CSSException {
	}
	public void startFontFace() throws CSSException {
	}
	public void endFontFace() throws CSSException {
	}
	public void namespaceDeclaration(String prefix, String uri) throws CSSException {
	}
}
