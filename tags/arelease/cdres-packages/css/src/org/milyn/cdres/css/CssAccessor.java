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

package org.milyn.cdres.css;

import java.util.Hashtable;
import java.util.Iterator;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.Rule;
import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.batik.css.engine.StyleRule;
import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.css.engine.sac.ExtendedSelector;
import org.apache.batik.css.engine.value.Value;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdres.css.StyleSheetStore.StoreEntry;
import org.milyn.container.ContainerRequest;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.Element;

/**
 * Page CSS accessor class.
 * <p/>
 * Transformation Units use this class to access CSS information for the 
 * current page.  The CSS info is "pre-gathered" by the {@link org.milyn.cdres.css.CssStyleScraper}
 * Assembly Unit, if configured for the requesting device.  
 * @author tfennelly
 */
public class CssAccessor {

	/**
	 * Logger.
	 */
	private static Log logger = LogFactory.getLog(CssAccessor.class);
	/**
	 * Request stylesheet store.
	 */
	private StyleSheetStore stylesheetStore;
	/**
	 * Associated container request.
	 */
	private ContainerRequest request;
	/**
	 * Element level style cache key.
	 */
	private static final String ELEMENT_STYLE_DECL_REQUESTKEY = CssAccessor.class + "#elementStyleDecls";
	
	/**
	 * Public constructor.
	 * @param request The container request associated with the current page 
	 * being delivered.
	 */
	public CssAccessor(ContainerRequest request) {
		if(request == null) {
			throw new IllegalArgumentException("null 'request' arg in constructor call.");
		}
		this.request = request;
		stylesheetStore = StyleSheetStore.getStore(request);
	}

	/**
	 * Get the named CSS property value for the supplied DOM element, if one exists.
	 * @param domElement The DOM element.
	 * @param propertyName The required CSS property.
	 * @return The value associated with the named CSS property for the supplied element,
	 * if a value for this property is defined for this element, otherwise null. 
	 */
	public Value getPropertyValue(Element domElement, String propertyName) {
		Iterator ssIterator = stylesheetStore.iterator();
		Value propValue = null;
		int highestSpecificity = -1;
		StyleDeclaration elementStyleDecl = getElementStyle(domElement);
		
		// if the element defines the property, return this value.
		if(elementStyleDecl != null) {
			Value value = getPropertyValue(propertyName, elementStyleDecl);
			if(value != null) {
				return value;
			}
		}
		
		// Iterate over the StyleSheets
		while(ssIterator.hasNext()) {
			StoreEntry storeEntry = (StoreEntry)ssIterator.next();
			StyleSheet styleSheet = storeEntry.getStylesheet();
			int ruleCount = styleSheet.getSize();
			
			// Iterate over the rules
			for(int i = 0; i < ruleCount; i++) {
				Rule rule = styleSheet.getRule(i);
				
				// If it's a StyleRule, search for a matching rule in the declaration.
				if(rule instanceof StyleRule) {
					StyleRule styleRule = (StyleRule)rule;
					StyleDeclaration decl = styleRule.getStyleDeclaration();
					Value value = getPropertyValue(propertyName, decl);

					// If we find a matching rule, check is it applicable to this
					// element and is it more specific than the current value of
					// highestSpecificity.
					if(value != null) {
						int specificity = getSpecificity(domElement, styleRule.getSelectorList());
						if(specificity >= highestSpecificity) {
							propValue = value;
							highestSpecificity = specificity;
						}
					}
				}
			}
		}
		
		return propValue;
	}

	/**
	 * Parse and return the elements style declaration.
	 * @param domElement The element.
	 * @return The elements style in a StyleDeclaration, or null if no style defined.
	 */
	private StyleDeclaration getElementStyle(Element domElement) {
		StyleDeclaration decl = null;
		String styleAttrib = domElement.getAttribute("style");
		
		if(styleAttrib != null) {
			Hashtable elementStyleDecls = getElementStyleDeclCache(request);
			
			decl = (StyleDeclaration)elementStyleDecls.get(domElement);
			if(decl == null) {
				CSSEngine cssParser = stylesheetStore.getCssParser();
				StyleSheet styleSheet;
				
				try {
					String styleToParse = domElement.getTagName() + " {" + styleAttrib + "}";
					styleSheet = cssParser.parseStyleSheet(styleToParse, request.getRequestURI().toURL(), "media");
					decl = ((StyleRule)styleSheet.getRule(0)).getStyleDeclaration();
					// and cache the parsed decl
					elementStyleDecls.put(domElement, decl);
				} catch(Throwable throwable) {
					logger.warn("Unable to parse element CSS: " + styleAttrib, throwable);
				}
			}
		}
		return decl;
	}

	private Hashtable getElementStyleDeclCache(ContainerRequest request2) {
		Hashtable elementStyleDecls = (Hashtable)request.getAttribute(ELEMENT_STYLE_DECL_REQUESTKEY);
		if(elementStyleDecls == null) {
			elementStyleDecls = new Hashtable();
			request.setAttribute(ELEMENT_STYLE_DECL_REQUESTKEY, elementStyleDecls);
		}
		return elementStyleDecls;
	}

	private Value getPropertyValue(String propertyName, StyleDeclaration decl) {
		int valueCount = decl.size();
		CSSEngine cssParser = stylesheetStore.getCssParser();
		
		for(int i = 0; i < valueCount; i ++) {
			String nextPropertyName = cssParser.getPropertyName(decl.getIndex(i));
			if(nextPropertyName.equals(propertyName)) {
				return decl.getValue(i);
			}
		}
		
		return null;
	}

	/**
	 * Check the selector list for a selector matching the supplied element,
	 * returning the specificity of the highest specificity match.
	 * @param domElement Element to test.
	 * @param selectorList List of Selectors to test.
	 * @return The specificity of the highest specificity match, or -1 if no
	 * match is found in the selector list.
	 */
	private int getSpecificity(Element domElement, SelectorList selectorList) {
		int selectorCount = selectorList.getLength();
		int highestSpecificity = -1;
		
		for(int i = 0; i < selectorCount; i++) {
			Selector selector = selectorList.item(i);
			if(selector instanceof ExtendedSelector) {
				ExtendedSelector extendedSelector = (ExtendedSelector)selector;
				if(extendedSelector.getSpecificity() >= highestSpecificity &&
						extendedSelector.match(domElement, null)) {
					highestSpecificity = extendedSelector.getSpecificity();
				}
			}
		}
		
		return highestSpecificity;
	}
}
