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

package org.milyn.edisax;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters;
import org.milyn.schema.ediMessageMapping10.impl.DelimitersDocumentImpl.DelimitersImpl;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import junit.framework.TestCase;

/**
 * @author tfennelly
 */
public class BufferedSegmentReaderTest extends TestCase {

	public void test() throws IOException {
		test("111111111ff22222222222ff333333333ff4444444444f4444444fff5555555555", "ff", "|",
			new String[] {"111111111", "22222222222", "333333333", "4444444444f4444444", "f5555555555"});

		test("a", "ff", "|", new String[] {"a"});

		test("ff", "ff", "*",  new String[] {});

		test("111111111\n22222222222\n333333333\n4444444444f4444444\nf5555555555", "\n", "*",
				new String[] {"111111111", "22222222222", "333333333", "4444444444f4444444", "f5555555555"});
	}

	private void test(String input, String segmentDelim, String fieldDelim, String[] segments) throws IOException {
		InputSource inputSource = new InputSource(new ByteArrayInputStream(input.getBytes()));
		Delimiters delimiters = new MockDelimiters(segmentDelim, fieldDelim);
		BufferedSegmentReader reader = new BufferedSegmentReader(inputSource, delimiters);
		int segIndex = 0;
		
		while(segIndex < segments.length && reader.moveToNextSegment()) {
			String segment = reader.getCurrentSegment();
			assertEquals("Segment comparison failure.", segments[segIndex], segment);
			segIndex++;
		}
		
		assertEquals("All segments not read.", segments.length, segIndex);
	}
	
	public void test_split() {
		System.out.println(Arrays.asList(StringUtils.splitPreserveAllTokens("a*b***C*d", "*")));
	}
	
	private class MockDelimiters implements Delimiters {

		private String segmentDelim;
		private String fieldDelim;
		
		public MockDelimiters(String segmentDelim, String fieldDelim) {
			this.segmentDelim = segmentDelim;
			this.fieldDelim = fieldDelim;
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#getSegment()
		 */
		public String getSegment() {
			return segmentDelim;
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#xgetSegment()
		 */
		public XmlString xgetSegment() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#setSegment(java.lang.String)
		 */
		public void setSegment(String arg0) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#xsetSegment(org.apache.xmlbeans.XmlString)
		 */
		public void xsetSegment(XmlString arg0) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#getField()
		 */
		public String getField() {
			return fieldDelim;
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#xgetField()
		 */
		public XmlString xgetField() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#setField(java.lang.String)
		 */
		public void setField(String arg0) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#xsetField(org.apache.xmlbeans.XmlString)
		 */
		public void xsetField(XmlString arg0) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#getComponent()
		 */
		public String getComponent() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#xgetComponent()
		 */
		public XmlString xgetComponent() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#setComponent(java.lang.String)
		 */
		public void setComponent(String arg0) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#xsetComponent(org.apache.xmlbeans.XmlString)
		 */
		public void xsetComponent(XmlString arg0) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#getSubComponent()
		 */
		public String getSubComponent() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#xgetSubComponent()
		 */
		public XmlString xgetSubComponent() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#setSubComponent(java.lang.String)
		 */
		public void setSubComponent(String arg0) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters#xsetSubComponent(org.apache.xmlbeans.XmlString)
		 */
		public void xsetSubComponent(XmlString arg0) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#schemaType()
		 */
		public SchemaType schemaType() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#validate()
		 */
		public boolean validate() {
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#validate(org.apache.xmlbeans.XmlOptions)
		 */
		public boolean validate(XmlOptions arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#selectPath(java.lang.String)
		 */
		public XmlObject[] selectPath(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#selectPath(java.lang.String, org.apache.xmlbeans.XmlOptions)
		 */
		public XmlObject[] selectPath(String arg0, XmlOptions arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#execQuery(java.lang.String)
		 */
		public XmlObject[] execQuery(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#execQuery(java.lang.String, org.apache.xmlbeans.XmlOptions)
		 */
		public XmlObject[] execQuery(String arg0, XmlOptions arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#changeType(org.apache.xmlbeans.SchemaType)
		 */
		public XmlObject changeType(SchemaType arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#isNil()
		 */
		public boolean isNil() {
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#setNil()
		 */
		public void setNil() {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#isImmutable()
		 */
		public boolean isImmutable() {
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#set(org.apache.xmlbeans.XmlObject)
		 */
		public XmlObject set(XmlObject arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#copy()
		 */
		public XmlObject copy() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#valueEquals(org.apache.xmlbeans.XmlObject)
		 */
		public boolean valueEquals(XmlObject arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#valueHashCode()
		 */
		public int valueHashCode() {
			// TODO Auto-generated method stub
			return 0;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#compareTo(java.lang.Object)
		 */
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlObject#compareValue(org.apache.xmlbeans.XmlObject)
		 */
		public int compareValue(XmlObject arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#monitor()
		 */
		public Object monitor() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#documentProperties()
		 */
		public XmlDocumentProperties documentProperties() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#newCursor()
		 */
		public XmlCursor newCursor() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#newXMLInputStream()
		 */
		public XMLInputStream newXMLInputStream() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#xmlText()
		 */
		public String xmlText() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#newInputStream()
		 */
		public InputStream newInputStream() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#newReader()
		 */
		public Reader newReader() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#newDomNode()
		 */
		public Node newDomNode() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#save(org.xml.sax.ContentHandler, org.xml.sax.ext.LexicalHandler)
		 */
		public void save(ContentHandler arg0, LexicalHandler arg1) throws SAXException {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#save(java.io.File)
		 */
		public void save(File arg0) throws IOException {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#save(java.io.OutputStream)
		 */
		public void save(OutputStream arg0) throws IOException {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#save(java.io.Writer)
		 */
		public void save(Writer arg0) throws IOException {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#newXMLInputStream(org.apache.xmlbeans.XmlOptions)
		 */
		public XMLInputStream newXMLInputStream(XmlOptions arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#xmlText(org.apache.xmlbeans.XmlOptions)
		 */
		public String xmlText(XmlOptions arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#newInputStream(org.apache.xmlbeans.XmlOptions)
		 */
		public InputStream newInputStream(XmlOptions arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#newReader(org.apache.xmlbeans.XmlOptions)
		 */
		public Reader newReader(XmlOptions arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#newDomNode(org.apache.xmlbeans.XmlOptions)
		 */
		public Node newDomNode(XmlOptions arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#save(org.xml.sax.ContentHandler, org.xml.sax.ext.LexicalHandler, org.apache.xmlbeans.XmlOptions)
		 */
		public void save(ContentHandler arg0, LexicalHandler arg1, XmlOptions arg2) throws SAXException {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#save(java.io.File, org.apache.xmlbeans.XmlOptions)
		 */
		public void save(File arg0, XmlOptions arg1) throws IOException {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#save(java.io.OutputStream, org.apache.xmlbeans.XmlOptions)
		 */
		public void save(OutputStream arg0, XmlOptions arg1) throws IOException {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.apache.xmlbeans.XmlTokenSource#save(java.io.Writer, org.apache.xmlbeans.XmlOptions)
		 */
		public void save(Writer arg0, XmlOptions arg1) throws IOException {
			// TODO Auto-generated method stub
			
		}

		public XmlObject selectAttribute(QName arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public XmlObject selectAttribute(String arg0, String arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		public XmlObject[] selectAttributes(QNameSet arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public XmlObject[] selectChildren(QName arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public XmlObject[] selectChildren(QNameSet arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public XmlObject[] selectChildren(String arg0, String arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		public XmlObject substitute(QName arg0, SchemaType arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		public void dump() {
			// TODO Auto-generated method stub
			
		}

		public Node getDomNode() {
			// TODO Auto-generated method stub
			return null;
		}

		public XMLStreamReader newXMLStreamReader() {
			// TODO Auto-generated method stub
			return null;
		}

		public XMLStreamReader newXMLStreamReader(XmlOptions arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
