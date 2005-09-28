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

import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.parser.Parser;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;

/**
 * Simple CSS Parser.
 * <p/>
 * Based on (extending) the Apache Batik "CSSEngine" class.  Uses the ValueManagers from
 * the Batik SVGCSSEngine dynamically adding Batik ValueManagers for properties not defined 
 * in the SVGCSSEngine ValueManagers.  Hope this doesn't have some hidden negative side effect!!
 * @author tfennelly
 */
public class SimpleCssParser extends CSSEngine {
	
	private static ValueManager[] SIMPLE_VALUE_MANAGERS = SVGCSSEngine.SVG_VALUE_MANAGERS;

	/**
	 * Public default constructor.
	 */
	public SimpleCssParser() {
		super(null, null, new Parser(), SIMPLE_VALUE_MANAGERS, 
				SVGCSSEngine.SVG_SHORTHAND_MANAGERS, null, null, "style",
	            null, "class", true, null, new SimpleCSSContext());
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.css.engine.CSSEngine#getPropertyIndex(java.lang.String)
	 */
	public int getPropertyIndex(String name) {
		int index = super.getPropertyIndex(name);
		
		if(index == -1) {
			index = getShorthandIndex(name);
			if(index == -1) {
				index = addSimpleManager(name);
			}
		}
		
		return index;
	}

	/**
	 * Add a new {@link SimpleValueManager} for the supplied property.
	 * @param propertyName The name of the property for which the ValueManager
	 * is to be added.
	 * @return The index of the new property.  This is the CSSEngine "index" value.  It
	 * uses an unusual mapping strategy for lopoking up property ValueManagers based on 
	 * an index.
	 */
	private int addSimpleManager(String propertyName) {
		synchronized (SIMPLE_VALUE_MANAGERS) {
			ValueManager[] newManagerList = new ValueManager[SIMPLE_VALUE_MANAGERS.length + 1];
			
			System.arraycopy(SIMPLE_VALUE_MANAGERS, 0, newManagerList, 0, SIMPLE_VALUE_MANAGERS.length);
			newManagerList[newManagerList.length - 1] = new SimpleValueManager(propertyName);
			SIMPLE_VALUE_MANAGERS = newManagerList;
			valueManagers = newManagerList;
			indexes.put(propertyName, newManagerList.length - 1);
			
			return newManagerList.length - 1;
		}
	}
	
	private static class SimpleCSSContext implements CSSContext {

		public Value getSystemColor(String arg0) {
			return null;
		}

		public Value getDefaultFontFamily() {
			return null;
		}

		public float getLighterFontWeight(float arg0) {
			return 0;
		}

		public float getBolderFontWeight(float arg0) {
			return 0;
		}

		public float getPixelUnitToMillimeter() {
			return 0;
		}

		public float getPixelToMillimeter() {
			return 0;
		}

		public float getMediumFontSize() {
			return 0;
		}

		public float getBlockWidth(Element arg0) {
			return 0;
		}

		public float getBlockHeight(Element arg0) {
			return 0;
		}

		public void checkLoadExternalResource(ParsedURL arg0, ParsedURL arg1) throws SecurityException {
		}

		public boolean isDynamic() {
			return false;
		}

		public boolean isInteractive() {
			return false;
		}

		public CSSEngine getCSSEngineForElement(Element arg0) {
			return null;
		}
	}
}
