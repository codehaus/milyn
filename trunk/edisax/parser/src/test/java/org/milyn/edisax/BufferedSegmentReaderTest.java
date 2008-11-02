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
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
	}
}
