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

import org.apache.batik.css.engine.sac.CSSConditionFactory;
import org.apache.batik.css.engine.sac.CSSSelectorFactory;
import org.milyn.resource.ExternalResourceLocator;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.SACMediaList;
import org.w3c.flute.parser.Parser;

/**
 * CSS Parser.
 * @author tfennelly
 */
public class CSSParser {

	private ExternalResourceLocator resourceLocator;

	/**
	 * Public constructor.
	 * @param resourceLocator External resource locator. Used to get
	 * the CSS stream (including imports).
	 */
	public CSSParser(ExternalResourceLocator resourceLocator) {
		if(resourceLocator == null) {
			throw new IllegalArgumentException("null 'resourceLocator' arg in constructor call.");
		}
		this.resourceLocator = resourceLocator;
	}
	
	/**
	 * Parse the CSS specified by the supplied cssURI arg.
	 * <p/>
	 * Creates a new {@link CSSStylesheet} and calls {@link #parse(URI, SACMediaList, CSSStylesheet)}.
	 * @param cssURI The CSS URI.
	 * @param media The media list to be associated with the style rules loaded from
	 * the CSS stream. <code>Null</code> if the rules are to be associated with 
	 * any media.
	 * @return A CSS Stylesheet.
	 * @throws CSSException
	 * @throws IOException
	 */
	public CSSStylesheet parse(URI cssURI, SACMediaList media) throws CSSException, IOException {
		return parse(cssURI, media, new CSSStylesheet());
	}
	
	/**
	 * Parse the CSS specified by the supplied cssURI arg.
	 * @param cssURI The CSS URI.
	 * @param media The media list to be associated with the style rules loaded from
	 * the CSS stream. <code>Null</code> if the rules are to be associated with 
	 * any media.
	 * @param styleSheet The {@link CSSStylesheet} to which the style rules are added. 
	 * @return A CSS Stylesheet.
	 * @throws CSSException
	 * @throws IOException
	 */
	public CSSStylesheet parse(URI cssURI, SACMediaList media, CSSStylesheet styleSheet) throws CSSException, IOException {
		if(cssURI == null) {
			throw new IllegalArgumentException("null 'cssURI' arg in method call.");
		}

		CSSDocumentHandler docHandler = new CSSDocumentHandler(styleSheet, cssURI, media, resourceLocator);
		Parser flute = new Parser();
		InputSource cssSrc = new InputSource();
		CSSConditionFactory cssConditionFactory = new CSSConditionFactory(null, "class", null, "id");

		flute.setDocumentHandler(docHandler);
		flute.setSelectorFactory(CSSSelectorFactory.INSTANCE);
		flute.setConditionFactory(cssConditionFactory);
		cssSrc.setByteStream(resourceLocator.getResource(cssURI.toString()));
		flute.parseStyleSheet(cssSrc);
		
		return styleSheet;
	}
}
