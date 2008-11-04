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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Simple test to verify differences between Java versions with regard to xml parsing.
 * <p/> 
 * 
 * @author <a href="mailto:dbevenius@jboss.com">Daniel Bevenius</a>
 *
 */
public class XmlParserTest
{
	@Test
	public void entityCallbackOrderJava() throws SAXException, IOException
	{
        final String input = "<element> &amp; some more text</element>";
        
		final MockContentHandler handler = new MockContentHandler();
		final XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		
		xmlReader.setContentHandler(handler);
        xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
		
		xmlReader.parse(new InputSource(new StringReader(input)));
		
		final List<String> events = handler.getEvents();
		//assertJava5CallbackOrder(events);
		assertJava6CallbackOrder(events);
	}
	
	private void assertJava6CallbackOrder(final List<String> events)
	{
		assertEquals("startDocument", events.get(0));
		assertEquals("startElement 'element'", events.get(1));
		assertEquals("characters ' '", events.get(2));
		assertEquals("startEntity 'amp'", events.get(3));
		assertEquals("endEntity 'amp'", events.get(4));
		assertEquals("characters '&'", events.get(5));
		assertEquals("characters ' some more text'", events.get(6));
		assertEquals("endElement 'element'", events.get(7));
		assertEquals("endDocument", events.get(8));
	}
	
	private void assertJava5CallbackOrder(final List<String> events)
	{
		assertEquals("startDocument", events.get(0));
		assertEquals("startElement 'element'", events.get(1));
		assertEquals("characters ' '", events.get(2));
		assertEquals("startEntity 'amp'", events.get(3));
		assertEquals("characters '&'", events.get(4));
		assertEquals("endEntity 'amp'", events.get(5));
		assertEquals("characters ' some more text'", events.get(6));
		assertEquals("endElement 'element'", events.get(7));
		assertEquals("endDocument", events.get(8));
	}
	
	private class MockContentHandler extends DefaultHandler2
	{
		private List<String> events;
		
		public List<String> getEvents()
		{
			return events;
		}

		/**
		 * Overrides DefaultHandlers impl of the ContentHandler
		 * interfaces startDocument method
		 */
		@Override
		public void startDocument() throws SAXException
		{
			events = new ArrayList<String>();
			events.add("startDocument");
		}
		
		@Override
		public void characters( char[] ch, int start, int length ) throws SAXException
		{
			events.add("characters '" + new String(ch, start, length) + "'");
		}
		
		@Override
		public void startElement( String uri, String localName, String name, Attributes atts ) throws SAXException
		{
			events.add("startElement '" + name + "'");
		}
		
		@Override
		public void endElement( String uri, String localName, String name ) throws SAXException
		{
			events.add("endElement '" + name +"'");
		}

		@Override
		public void endDocument() throws SAXException
		{
			events.add("endDocument");
		}
		
		@Override
		public void startEntity( String name ) throws SAXException
		{
			events.add("startEntity '" + name + "'");
		}
		
		@Override
		public void endEntity( String name ) throws SAXException
		{
			events.add("endEntity '" + name + "'");
		}
	}
}
