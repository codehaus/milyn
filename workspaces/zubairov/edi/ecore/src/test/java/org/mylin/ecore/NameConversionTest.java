package org.mylin.ecore;

import static org.mylin.ecore.ECoreConversionUtils.toJavaName;
import junit.framework.TestCase;

/**
 * Test for {@link ECoreConversionUtils#toJavaName(String)}
 * 
 * @author zubairov
 *
 */
public class NameConversionTest extends TestCase {

	public void testWrongNames() throws Exception {
		assertEquals("SegmentGroup3", toJavaName("Segment_group_3", true));
		assertEquals("SegmentGroup3", toJavaName("Segment_group__3", true));
		assertEquals("SegmentGroup3", toJavaName("Segment_group___3", true));
		assertEquals("segmentGroup3", toJavaName("Segment_group___3", false));
		
		assertEquals("BeginningOfMessage", toJavaName("Beginning_of_message", true));
		assertEquals("BeginningOfMessage", toJavaName("BEGINNING_OF_MESSAGE", true));
		assertEquals("beginningOfMessage", toJavaName("BEGINNING_OF_MESSAGE", false));
		assertEquals("documentMessageIdentification", toJavaName("DOCUMENT_MESSAGE_IDENTIFICATION", false));
		assertEquals("carrier", toJavaName("CARRIER", false));
	}
}
