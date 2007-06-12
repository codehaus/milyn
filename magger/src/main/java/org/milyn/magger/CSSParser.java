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

package org.milyn.magger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
 * <p/>
 * This is a very simple CSS Parser that uses Apache Flute for the SAC parsing
 * and Apache Batik for the resulting CSS 
 * {@link org.w3c.css.sac.Selector}/{@link org.w3c.css.sac.Condition} model.  
 * <p/>
 * <h3>Why did we do this?</h3>
 * We originally used Apache Batik on its own (for both the parsing and the resulting CSS DOM).
 * Batik implements an "extended" CSS {@link org.w3c.css.sac.Selector} object model that provides some very useful
 * features such as CSS selector/condition specificity, as well as XML DOM "match" capabilities (see {@link org.apache.batik.css.engine.sac.ExtendedSelector}).
 * However, Batik seems to have problems parsing some CSS where Flute seems to be able to
 * parse without difficulty.
 * <p/>
 * This parser simply combines Batik and Flute capabilities by using Flute to perform the
 * SAC parsing.  The Flute parser is initialised with Batik 
 * {@link org.apache.batik.css.engine.sac.CSSSelectorFactory} and 
 * {@link org.apache.batik.css.engine.sac.CSSConditionFactory} instances.
 * 
 * <h3>Sample Code</h3>
 * <pre>
	{@link org.milyn.resource.URIResourceLocator} resLocator = new URIResourceLocator();
	CSSParser parser = new CSSParser(resLocator);
	{@link org.milyn.magger.CSSStylesheet} styleSheet;
	Iterator rules;
	
	try {
		styleSheet = {@link #parse(URI, SACMediaList) parser.parse}(URI.create("http://www.acme.com/style.css"), null);
	} catch (CSSException e) {
		// Do something...
	} catch (IOException e) {
		// Do something...
	}
	
	rules = {@link org.milyn.magger.CSSStylesheet#getRules() styleSheet.getRules()}.iterator();
	while(rules.hasNext()) {
		{@link org.milyn.magger.CSSRule} rule = (CSSRule)rules.next();
		
		// Use the rules for whatever...
	}
 * </pre>
 * 
 * @author tfennelly
 */
public class CSSParser {

	private ExternalResourceLocator resourceLocator;

	/**
	 * Public constructor.
	 * @param resourceLocator External resource locator. Used to locate and read
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
     * @throws IOException Unable to read SCC stream.
	 * @throws CSSException Invalid CSS stream.
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
     * @throws IOException Unable to read SCC stream.
	 * @throws CSSException Invalid CSS stream.
	 */
	public CSSStylesheet parse(URI cssURI, SACMediaList media, CSSStylesheet styleSheet) throws CSSException, IOException {
		if(cssURI == null) {
			throw new IllegalArgumentException("null 'cssURI' arg in method call.");
		}

		InputStream cssStream = resourceLocator.getResource(cssURI.toString());

		if(cssStream == null) {
			// Shouldn't get this - should be getting an IOException???
			throw new IOException("Failed to read CSS resource: " + cssURI);
		}

		InputSource cssSrc = new InputSource();
		
		cssSrc.setByteStream(cssStream);

		return parse(cssSrc, cssURI, media, styleSheet);
	}

	/**
	 * Parse the supplied string into a {@link CSSStylesheet}.
	 * @param cssString CSS container String.
	 * @param baseURI The base URI to be used when parsing this cssString.  This
	 * URI will be used for resolving import URLs.
	 * @param media The media list to be associated with the style rules loaded from
	 * the CSS stream. <code>Null</code> if the rules are to be associated with 
	 * any media.
	 * @return A CSS Stylesheet.
     * @throws IOException Unable to read SCC stream.
	 * @throws CSSException Invalid CSS stream.
	 */
	public CSSStylesheet parse(String cssString, URI baseURI, SACMediaList media) throws CSSException, IOException {
		if(cssString == null) {
			throw new IllegalArgumentException("null 'cssString' arg in method call.");
		}
		if(baseURI == null) {
			throw new IllegalArgumentException("null 'baseURI' arg in method call.");
		}
		
		InputSource cssSrc = new InputSource();
		
		cssSrc.setByteStream(new ByteArrayInputStream(cssString.getBytes()));

		return parse(cssSrc, baseURI, media, null);
	}


	/**
	 * Parse the supplied {@link InputSource} into a {@link CSSStylesheet}.
	 * @param cssSrc CSS {@link InputSource}.
	 * @param baseURI The base URI to be used when parsing this cssString.  This
	 * URI will be used for resolving import URLs.
	 * @param media The media list to be associated with the style rules loaded from
	 * the CSS stream. <code>Null</code> if the rules are to be associated with 
	 * any media.
	 * @param styleSheet The {@link CSSStylesheet} to which the style rules are added.  <code>null</code>
	 * you want the method to construct a new {@link CSSStylesheet} instance;
	 * @return A CSS Stylesheet.
     * @throws IOException Unable to read SCC stream.
	 * @throws CSSException Invalid CSS stream.
	 */
	public CSSStylesheet parse(InputSource cssSrc, URI baseURI, SACMediaList media, CSSStylesheet styleSheet) throws CSSException, IOException {
		if(cssSrc == null) {
			throw new IllegalArgumentException("null 'cssSrc' arg in method call.");
		}
		if(baseURI == null) {
			throw new IllegalArgumentException("null 'baseURI' arg in method call.");
		}
		
		if(styleSheet == null) {
			styleSheet = new CSSStylesheet();
		}
		
		CSSDocumentHandler docHandler = new CSSDocumentHandler(styleSheet, baseURI, media, resourceLocator);
		Parser flute = new Parser();
		CSSConditionFactory cssConditionFactory = new CSSConditionFactory(null, "class", null, "id");
		
		flute.setDocumentHandler(docHandler);
		flute.setSelectorFactory(CSSSelectorFactory.INSTANCE);
		flute.setConditionFactory(cssConditionFactory);
		flute.parseStyleSheet(cssSrc);
		
		return styleSheet;
	}
}
