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

package org.milyn.css;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.milyn.SmooksStandalone;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.MockContainerResourceLocator;
import org.milyn.container.standalone.StandaloneContainerContext;
import org.milyn.container.standalone.StandaloneContainerRequest;
import org.milyn.magger.CSSProperty;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

public class CssStyleScraperTest extends TestCase {

    private SmooksStandalone smooks;
    private StandaloneContainerRequest request;
    private StandaloneContainerContext context;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        smooks = new SmooksStandalone("ISO-8859-1");
        smooks.registerUseragent("device1", new String[] {"blah"});
        request = new StandaloneContainerRequest(URI.create("http://www.milyn.org/myapp/aaa/mypage.html"), null, smooks.getSession("device1"));
        context = smooks.getSession("device1").getContext();
    }
		
	public void testProcessPageCSS() {
		assertTrue("Expected CSS to be processed - href only.", 
				isCSSProcessed("href='mycss.css'"));
		assertTrue("Expected CSS to be processed - href + type.", 
				isCSSProcessed("href='mycss.css' type='text/css'"));
		assertTrue("Expected CSS to be processed - href + rel.", 
				isCSSProcessed("href='mycss.css' rel='stylesheet'"));
		assertTrue("Expected CSS to be processed - href + rel.", 
				isCSSProcessed("href='mycss.css' rel='xxx stylesheet'"));

        // register a new useragent and recreate the request.
        smooks.registerUseragent("device1", new String[] {"screen"});
        request = new StandaloneContainerRequest(URI.create("http://x.com"), null, smooks.getSession("device1"));
		
        assertTrue("Expected CSS to be processed - href + media.", 
				isCSSProcessed("href='mycss.css' media='screen'"));
		
		assertFalse("Expected CSS not to be processed - href + invalid media.", 
				isCSSProcessed("href='mycss.css' media='audio'"));
		assertFalse("Expected CSS not to be processed - href + invalid type.", 
				isCSSProcessed("href='mycss.css' type='xxx'"));
		assertFalse("Expected CSS not to be processed - href + alternate stylesheet rel.", 
				isCSSProcessed("href='mycss.css' rel='alternate stylesheet'"));
	}
	
	public void test_link_href_resolution() {
		String requestUri = "http://www.milyn.org/myapp/aaa/mypage.html";
		
		assertEquals("http://www.milyn.org/xxx/yyy/mycss.css", 
				getResolvedUri("/xxx/yyy/mycss.css", requestUri));

		assertEquals("http://www.milyn.org/myapp/aaa/mycss.css", 
				getResolvedUri("mycss.css", requestUri));

		assertEquals("http://www.milyn.org/myapp/mycss.css", 
				getResolvedUri("../mycss.css", requestUri));

		assertEquals("http://www.milyn.org/mycss.css", 
				getResolvedUri("../../mycss.css", requestUri));

		assertEquals("http://www.milyn.org/../mycss.css", 
				getResolvedUri("../../../mycss.css", requestUri));
	}
	
	public void testInlineStyle() {
		Document doc = CssTestUtil.parseXMLString("<x><style>p {background-color: white}</style><p/></x>"); 
		Element style = (Element)XmlUtil.getNode(doc, "/x/style");
		Element paragraph = (Element)XmlUtil.getNode(doc, "/x/p");
		SmooksResourceConfiguration cdrDef = new SmooksResourceConfiguration("link", "device", "xxx");
		CSSStyleScraper delivUnit = new CSSStyleScraper(cdrDef);
		CssMockResLocator mockrl = new CssMockResLocator();
		
        context.setResourceLocator(mockrl);
		delivUnit.visit(style, request);
		CSSAccessor accessor = CSSAccessor.getInstance(request);
		
		CSSProperty property = accessor.getProperty(paragraph, "background-color");
		assertNotNull("Expected CSS property.", property);
	}
	
	public String getResolvedUri(String href, String requestUri) {
		Document doc = CssTestUtil.parseXMLString("<x><link href='" + href + "' /></x>"); 
		Element link = (Element)XmlUtil.getNode(doc, "/x/link");
		SmooksResourceConfiguration cdrDef = new SmooksResourceConfiguration("link", "device", "xxx");
		CSSStyleScraper delivUnit = new CSSStyleScraper(cdrDef);
		CssMockResLocator mockrl = new CssMockResLocator();
		
        context.setResourceLocator(mockrl);
		delivUnit.visit(link, request);
		
		return mockrl.uri;
	}

	
	public boolean isCSSProcessed(String attribs) {
		Document doc = CssTestUtil.parseXMLString("<x><link " + attribs + " /></x>"); 
		Element link = (Element)XmlUtil.getNode(doc, "/x/link");
		SmooksResourceConfiguration cdrDef = new SmooksResourceConfiguration("link", "device", "xxx");
		CSSStyleScraper delivUnit = new CSSStyleScraper(cdrDef);
		CssMockResLocator mockrl = new CssMockResLocator();
		
        context.setResourceLocator(mockrl);
		delivUnit.visit(link, request);
		
		return (mockrl.uri != null);
	}
	
	private class CssMockResLocator extends MockContainerResourceLocator {

		private InputStream stream = CssStyleScraperTest.class.getResourceAsStream("style1.css");
		private String uri;

		/* (non-Javadoc)
		 * @see org.milyn.container.MockContainerResourceLocator#getResource(java.lang.String)
		 */
		public InputStream getResource(String uri) throws IllegalArgumentException, IOException {
			this.uri = uri;
			return stream;
		}
		
	}
}
