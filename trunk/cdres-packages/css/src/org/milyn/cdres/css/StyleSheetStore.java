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

import java.util.Iterator;
import java.util.Vector;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.StyleSheet;
import org.milyn.container.ContainerRequest;
import org.w3c.dom.Element;

/**
 * CSS Store class for storing a list of Stylesheets associated with a single
 * page - the current page.
 * <p/>
 * Note this is store class for a single page and is stored in the request.
 * @author tfennelly
 */
class StyleSheetStore {
	
	/**
	 * Store request lookup key.
	 */
	private static final String STORE_REQUEST_KEY = StyleSheetStore.class.getName() + "#STORE_REQUEST_KEY";
	/**
	 * Stylesheets - the store.
	 */
	private Vector storeEntries = new Vector();
	/**
	 * Request parser.
	 */
	private CSSEngine cssParser = new SimpleCssParser();

	/**
	 * Hidden default constructor.
	 */
	private StyleSheetStore() {
	}
	
	/**
	 * StyleSheetStore static accessor method.
	 * @param request Container request.  The store is stored on the request.
	 * @return The StyleSheetStore instance associate with this request.
	 */
	protected static StyleSheetStore getStore(ContainerRequest request) {
		StyleSheetStore store = (StyleSheetStore)request.getAttribute(STORE_REQUEST_KEY);
		
		if(store == null) {
			store = new StyleSheetStore();
			request.setAttribute(STORE_REQUEST_KEY, store);
		}
		
		return store;
	}
	
	/**
	 * Add a new Store entry.
	 * @param stylesheet The StyleSheet to be added.
	 * @param styleElement The style element associated with the entry
	 */
	protected void add(StyleSheet stylesheet, Element styleElement) {
		StoreEntry storeEntry = new StoreEntry();
		
		storeEntry.stylesheet = stylesheet;
		storeEntry.styleElement = styleElement;
		if(styleElement != null && styleElement.getTagName().equals("link")) {
			storeEntry.isLinked = true;
		}
		
		// Add of the list.
		storeEntries.add(storeEntry);
	}
	
	/**
	 * Get an iterator for the store {@link StoreEntry} instances.
	 * @return An Iterator of the store {@link StoreEntry} instances.
	 */
	protected Iterator iterator() {
		return storeEntries.iterator();
	}

	/**
	 * Get the CSSEngine parser to be used for parsing StyleSheets
	 * for the associated request.
	 * @return Returns the cssParser.
	 */
	public CSSEngine getCssParser() {
		return cssParser;
	}
	
	/**
	 * Simple Store entry class. 
	 * @author tfennelly
	 */
	protected class StoreEntry {
		private StyleSheet stylesheet;
		private Element styleElement;
		private boolean isLinked = false;

		/**
		 * Is the associated style linked in the document i.e. referenced via
		 * a link element.
		 * @return True if the element is linked via a link element, otherwise false.
		 */
		public boolean isLinked() {
			return isLinked;
		}
		
		/**
		 * Get the style element source for this StyleSheet entry.
		 * @return The style DOM Element - or null if not associated with
		 * a DOM element.
		 */
		public Element getStyleElement() {
			return styleElement;
		}
		
		/**
		 * Get the actual StyleSheet associated with this entry.
		 * @return The StyleSheet.
		 */
		public StyleSheet getStylesheet() {
			return stylesheet;
		} 
	}

}
