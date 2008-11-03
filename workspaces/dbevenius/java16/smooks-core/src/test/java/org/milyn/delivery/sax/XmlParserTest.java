/*
 * Milyn - Copyright (C) 2006
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.milyn.delivery.sax;

import java.io.StringReader;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Simple test to verify differences between Java versions with regard
 * to xml parsing.
 * <p/> 
 * Really only indended for manual testing and visual insecting the SAX events
 * generated.
 * 
 * @author <a href="mailto:dbevenius@redhat.com">Daniel Bevenius</a>
 *
 */
public class XmlParserTest
{
	@Test
	public void test() throws SAXException
	{
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        String input = "<element> &amp; some more text &amp;</element>";
		
		MockContentHandler handler = new MockContentHandler();
		xmlReader.setContentHandler(handler);
		
		// Validate the xml document. Only if a grammer is specified.
        xmlReader.setFeature("http://xml.org/sax/features/validation", false);
        
        // Include external paramter entities.
        xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        
        // Tells the parser to ignore the external dtd. Don't want to have this for this test.
        xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        
        xmlReader.setFeature("http://xml.org/sax/features/lexical-handler/parameter-entities", true);
        xmlReader.setFeature("http://apache.org/xml/features/scanner/notify-char-refs", true);
        xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        
        try
        {
    		xmlReader.parse(new InputSource(new StringReader(input)));
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
	}
	
	private class MockContentHandler extends DefaultHandler2
	{
		/**
		 * Overrides DefaultHandlers impl of the ContentHandler
		 * interfaces startDocument method
		 */
		@Override
		public void startDocument() throws SAXException
		{
			System.out.println("startDocument");
		}
		
		/**
		 * Overrides DefaultHandlers impl of the ContentHandler interfaces characters method
		 * 
		 * Different parser implementation handle events differently. For example, a string of characters ending in a newline 
		 * character (e.g abcdefg\n) 
		 * will cause different numbers of character events depending on the parser. 
		 * If Crimson (the default for JDK 1.4.x) is used, only one character event will occur. However, Xerces-J creates 
		 * two consecutive events, one for the letters (abcdefg in the example above) and one for the newline character at the end. 
		 * It is important to be aware of this when writing XMLFilters to be used with Xerces-J.
		 * Individual parser documentation should give you information on these specifications.
		 */
		@Override
		public void characters( char[] ch, int start, int length ) throws SAXException
		{
			System.out.println("characters '" + new String(ch, start, length) + "'");
			
			/*
			String characters = new String(ch, start, length);
			int indexOf = characters.indexOf( '\n' );
			if ( indexOf != -1 && indexOf != 0 )
			{
				String withoutNewlineChar = characters.substring(0, indexOf);
				System.out.println("newline '" + withoutNewlineChar + "'");
				System.out.println("newline '" + "\n" + "'");
				//System.out.println("newline->" + characters + "<-");
			}
			else
			{
    			System.out.println("characters '" + characters + "'");
			}
			*/
		}
		
		/**
		 * Overrides DefaultHandlers impl of the ContentHandler
		 * interfaces characters method
		 */
		@Override
		public void ignorableWhitespace( char[] ch, int start, int length ) throws SAXException
		{
			System.out.println("ignoreableWhitespace");
		}
		
		/**
		 * Overrides DefaultHandlers impl of the ContentHandler
		 * interfaces startPrefixMapping method
		 */
		@Override
		public void startPrefixMapping( String prefix, String uri ) throws SAXException
		{
			System.out.println("startPrefixMapping");
		}
		
		/**
		 * Overrides DefaultHandlers impl of the ContentHandler
		 * interfaces endDocument method
		 */
		@Override
		public void startElement( String uri, String localName, String name, Attributes atts ) throws SAXException
		{
			System.out.println("startElement: " + name);
		}
		
		/**
		 * Overrides DefaultHandlers impl of the ContentHandler
		 * interfaces skippedEntity method
		 */
		@Override
		public void skippedEntity( String name ) throws SAXException
		{
			System.out.println("skippedEntity : " + name);
		}
		
		/**
		 * Overrides DefaultHandlers impl of the ContentHandler
		 * interfaces endPrefixMapping method
		 */
		@Override
		public void endPrefixMapping( String prefix ) throws SAXException
		{
			System.out.println("endPrefixMapping : " + prefix);
		}
		
		@Override
		public void processingInstruction( String target, String data ) throws SAXException
		{
			System.out.println("processingInstruction : " + target + ", data : " + data);
		}
		
		@Override
		public void setDocumentLocator( Locator locator )
		{
		}
		
		/**
		 * Overrides DefaultHandlers impl of the ContentHandler
		 * interfaces endElement method
		 */
		@Override
		public void endElement( String uri, String localName, String name ) throws SAXException
		{
			System.out.println("endElement: " + name);
		}

		/**
		 * Overrides DefaultHandlers impl of the ContentHandler
		 * interfaces endDocument method
		 */
		@Override
		public void endDocument() throws SAXException
		{
			System.out.println("endDocument: ");
		}
		
		/**
		 * Overrides DefaultHandlers impl of LexicalHandler
		 * interface startEntity
		 */
		@Override
		public void startEntity( String name ) throws SAXException
		{
			System.out.println("startEntity : " + name);
		}
		
		/**
		 * Overrides DefaultHandlers impl of LexicalHandler
		 * interface startEntity
		 */
		@Override
		public void endEntity( String name ) throws SAXException
		{
			System.out.println("endEntity : " + name);
		}

	}

}
