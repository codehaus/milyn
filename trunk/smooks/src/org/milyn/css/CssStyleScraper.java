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

package org.milyn.css;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.assemble.AbstractAssemblyUnit;
import org.milyn.device.profile.ProfileSet;
import org.milyn.dom.DomUtils;
import org.milyn.magger.CSSParser;
import org.milyn.magger.CSSStylesheet;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.Element;

/**
 * CSS scraping Assembly Unit.
 * <p/>
 * Gathers CSS information during the Assembly phase.  This information is then
 * available to transformation units during the Transformation phase.
 * <p/>
 * Triggered on &lt;style&gt; and &lt;link&gt; elements. Reads and parses the referenced CSS
 * using Apache Batik.  Makes the gathered CSS data available to 
 * transformation units via the {@link org.milyn.cdres.css.CssAccessor} class.
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;cdres	uatarget="<i>device/profile</i>" selector="style" 
 * 	path="org.milyn.cdres.css.CssStyleScraper" &gt;
 * 
 * 	&lt;!-- (Optional) Only process the CSS if the 'media' attribute lists
 * 		one of the requesting devices profiles. Default true. --&gt;
 * 	&lt;param name="<b>checkMediaAttribute</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Only process the CSS if the 'type' attribute equals
 * 		'text/css'. Default true. --&gt;
 * 	&lt;param name="<b>checkTypeAttribute</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * &lt;/cdres&gt;
 * 
 * &lt;cdres	uatarget="<i>device/profile</i>" selector="link" 
 * 	path="org/milyn/cdres/css/CssStyleScraper.class" &gt;
 * 
 * 	&lt;!-- (Optional) Only process the CSS if the 'media' attribute, if present, lists
 * 		one of the requesting devices profiles. Default true. --&gt;
 * 	&lt;param name="<b>checkMediaAttribute</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Only process the CSS if the 'type' attribute, if present, equals
 * 		'text/css'. Default true. --&gt;
 * 	&lt;param name="<b>checkTypeAttribute</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Only process the CSS if the 'rel' attribute, if present,
 * 		contains the keyword 'stylesheet'. Default true. --&gt;
 * 	&lt;param name="<b>checkRelAttributeForStylesheet</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Only process the CSS if the 'rel' attribute, if present, 
 * 		<b>does not</b> contains the keyword 'alternate'. Default true. --&gt;
 * 	&lt;param name="<b>checkRelAttributeForAlternate</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * &lt;/cdres&gt;</pre>
 * 
 * See {@link org.milyn.cdr.CDRDef}.
 * @author tfennelly
 */
public class CssStyleScraper extends AbstractAssemblyUnit {

	private static Log logger = LogFactory.getLog(CssStyleScraper.class);
	private boolean checkMediaAttribute = true;
	private boolean checkTypeAttribute = true;
	private boolean checkRelAttributeForStylesheet = true;
	private boolean checkRelAttributeForAlternate = true;
	
	public CssStyleScraper(CDRDef cdres) {
		super(cdres);
		checkMediaAttribute = cdres.getBoolParameter("checkMediaAttribute", true);
		checkTypeAttribute = cdres.getBoolParameter("checkTypeAttribute", true);
		checkRelAttributeForStylesheet = cdres.getBoolParameter("checkRelAttributeForStylesheet", true);
		checkRelAttributeForAlternate = cdres.getBoolParameter("checkRelAttributeForAlternate", true);		
	}

	public boolean visitBefore() {
		return false;
	}
	
	public void visit(Element element, ContainerRequest request) {
		String media = DomUtils.getAttributeValue(element, "media");
		String type = DomUtils.getAttributeValue(element, "type");
		
		if(checkMediaAttribute && media != null && !hasMediaProfile(media, request.getUseragentContext().getProfileSet())) {
			logger.info("Bypassing style. [" + request.getRequestURI() + "]. Requesting device [" + request.getUseragentContext() + "] does not have required media profile [" + media + "].");
			return;
		} else if(checkTypeAttribute && type != null) {
			// Check the type attribute - contains "text/css".
			type = type.trim().toLowerCase();
			if(!type.equals("text/css")) {
				logger.info("Bypassing style. [" + request.getRequestURI() + "]. 'type' attribute set but value not 'text/css'.");
				return;
			}
		}

		if(element.getTagName().equals("style")) {
			visitStyle(element, request, media);
		} else if(element.getTagName().equals("link")) {
			visitLink(element, request, media);
		}
	}

	private boolean hasMediaProfile(String media, ProfileSet profileSet) {
		StringTokenizer tokenizer = new StringTokenizer(media, ",");
		
		while(tokenizer.hasMoreTokens()) {
			if(profileSet.isMember(tokenizer.nextToken().trim())) {
				return true;
			}
		}
		
		return false;
	}

	private void visitStyle(Element element, ContainerRequest request, String media) {
		// The style may be enclosed in comment or cdata section nodes.
		// Extract all "character" data!
		String style = DomUtils.getAllText(element, false);
		
		if(!style.trim().equals("")) {
			try {
				CharArrayReader reader;
				
				reader = new CharArrayReader(style.toCharArray());
				parseCSS(element, request, media, new InputSource(reader));
			} catch(Throwable throwable) {
				logger.warn("Unable to parse inline style element css. [" + request.getRequestURI() + "]", throwable);
			}
		}
	}

	private void visitLink(Element element, ContainerRequest request, String media) {
		String href = DomUtils.getAttributeValue(element, "href");
		String rel = DomUtils.getAttributeValue(element, "rel");
		URI cssURI;
		InputStream cssStream;

		// Check the rel and href attributes.
		if(href == null || href.trim().equals("")) {
			return;
		} else if(rel != null) {
			// Check the rel attribute contains "stylesheet" and doesn't contain "alternate".
			rel = rel.trim().toLowerCase();
			if(checkRelAttributeForStylesheet && rel.indexOf("stylesheet") == -1) {
				logger.info("Bypassing link element. [" + request.getRequestURI() + "]. 'rel' attribute set but 'stylesheet' not in value.");
				return;
			} else if(checkRelAttributeForAlternate && rel.indexOf("alternate") != -1) {
				logger.info("Bypassing linked style element css. [" + request.getRequestURI() + "]. 'rel' attribute declares css as being 'alternate'.");
				return;
			}
		}

		// Resolve the CSS href against the current request.
		try {
			cssURI = request.getRequestURI().resolve(new URI(href));
		} catch (URISyntaxException e) {
			logger.warn("Bypassing linked style element css. [" + request.getRequestURI() + "]. Invalid css link 'href' [" + href + "].");
			return;
		}
		
		// Get the CSS stream.
		try {
			cssStream = request.getContext().getResourceLocator().getResource(cssURI.toString());
		} catch (IOException e) {
			logger.warn("Bypassing linked style element css. [" + request.getRequestURI() + "]. CSS stream read failure.", e);
			return;
		}

		// Parse the CSS stream - stores the parsed CSS in the requests StyleSheetStore.
		try {
			parseCSS(element, request, media, new InputSource(new InputStreamReader(cssStream)));
		} catch(Throwable throwable) {
			logger.warn("Unable to parse linked css. [" + request.getRequestURI() + "]", throwable);
		}
	}
	
	private void parseCSS(Element element, ContainerRequest request, String media, InputSource inputSource) throws CSSException, IOException {
		CSSParser parser = new CSSParser(request.getContext().getResourceLocator());
		CSSStylesheet styleSheet;
		StyleSheetStore store;
		
		store = StyleSheetStore.getStore(request);
		styleSheet = parser.parse(inputSource, request.getRequestURI(), null, null);
		store.add(styleSheet, element);
	}
}
