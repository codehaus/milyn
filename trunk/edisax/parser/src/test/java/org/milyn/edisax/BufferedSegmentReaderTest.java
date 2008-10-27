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

import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import org.milyn.edisax.model.internal.Delimiters;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

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
	
	private class MockDelimiters extends Delimiters {

		private String segmentDelim;
		private String fieldDelim;
		
		public MockDelimiters(String segmentDelim, String fieldDelim) {
			this.segmentDelim = segmentDelim;
			this.fieldDelim = fieldDelim;
		}
		public String getSegment() { return segmentDelim; }
		public void setSegment(String arg0) { }
		public String getField() { return fieldDelim; }
		public void setField(String arg0) { }
		public String getComponent() { return null; }
		public void setComponent(String arg0) { }
		public String getSubComponent() { return null; }
		public void setSubComponent(String arg0) { }
		public boolean validate() { return false; }
		public boolean isNil() { return false; }
		public void setNil() { }
		public boolean isImmutable() { return false; }
		public int valueHashCode() { return 0; }
		public int compareTo(Object arg0) { return 0; }
		public Object monitor() { return null; }
		public String xmlText() { return null; }
		public InputStream newInputStream() { return null; }
		public Reader newReader() { return null; }
		public Node newDomNode() { return null; }
		public void save(ContentHandler arg0, LexicalHandler arg1) throws SAXException { 		}
		public void save(File arg0) throws IOException { }
		public void save(OutputStream arg0) throws IOException { 		}
		public void save(Writer arg0) throws IOException { 		}
		public void dump() { }
		public Node getDomNode() { return null; }
		public XMLStreamReader newXMLStreamReader() { return null; }

	}
}
