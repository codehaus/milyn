// XmlUtilTest.java

package org.milyn.xml;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * XmlUtilTest
 * <p>
 * Relations: XmlUtil extends java.lang.Object <br>
 * 
 * @author Tom Fennelly
 * @see org.milyn.xml.XmlUtil
 */

public class XmlUtilTest extends TestCase {

	// private XmlUtil.UnitTest xmlutil;

	/**
	 * Constructor (needed for JTest)
	 * 
	 * @param name
	 *            Name of Object
	 */
	public XmlUtilTest(String name) {
		super(name);
	}

	/**
	 * Used by JUnit (called before each test method)
	 */
	protected void setUp() {
		// xmlutil = new XmlUtil.UnitTest(new XmlUtil());
	}

	/**
	 * Used by JUnit (called after each test method)
	 */
	protected void tearDown() {
		// xmlutil = null;
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_1() {
		try {
			XmlUtil.removeEntities(null, null);
			fail("no IllegalArgumentException on null reader and null writer");
		} catch (IllegalArgumentException excep) {
			// OK
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_2() {
		try {
			Reader reader = new StringReader("abcdefg");
			XmlUtil.removeEntities(reader, null);
			fail("no IllegalArgumentException on null writer");
		} catch (IllegalArgumentException excep) {
			// OK
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_3() {
		try {
			StringReader reader = new StringReader("abcdefg");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("abcdefg", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_4() {
		try {
			StringReader reader = new StringReader("a&bcdefg");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("a&bcdefg", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_5() {
		try {
			StringReader reader = new StringReader("a;bcdefg");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("a;bcdefg", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_6() {
		try {
			StringReader reader = new StringReader("&abcdefg;");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&abcdefg;", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_7() {
		try {
			StringReader reader = new StringReader("&&abcdefg");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&&abcdefg", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_8() {
		try {
			StringReader reader = new StringReader("&;abcdefg;");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&;abcdefg;", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_9() {
		try {
			StringReader reader = new StringReader("&amp;bcdefg");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&bcdefg", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_10() {
		try {
			StringReader reader = new StringReader("&amp;amp;");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&amp;", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_11() {
		try {
			StringReader reader = new StringReader("&amp;&;");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&&;", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_12() {
		try {
			StringReader reader = new StringReader("&");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_13() {
		try {
			StringReader reader = new StringReader("&;");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&;", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_14() {
		try {
			StringReader reader = new StringReader("");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_15() {
		try {
			StringReader reader = new StringReader("abc&#;");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("abc&#;", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_16() {
		try {
			StringReader reader = new StringReader("&#;");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&#;", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_17() {
		try {
			StringReader reader = new StringReader("abc&#");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("abc&#", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_18() {
		try {
			StringReader reader = new StringReader("&#");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&#", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_19() {
		try {
			StringReader reader = new StringReader("&#x");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&#x", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_20() {
		try {
			StringReader reader = new StringReader("&#x;");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("&#x;", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_21() {
		try {
			StringReader reader = new StringReader("&#60;");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("<", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_22() {
		try {
			StringReader reader = new StringReader("&#x3C;");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("<", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_23() {
		try {
			StringReader reader = new StringReader("a&#x3C;");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("a<", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_24() {
		try {
			StringReader reader = new StringReader("a&#x3C;a");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("a<a", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_25() {
		try {
			StringReader reader = new StringReader("a&#X3C;a");
			StringWriter writer = new StringWriter();

			XmlUtil.removeEntities(reader, writer);
			assertEquals("a<a", writer.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRemoveEntities_26() {
		try {
			assertEquals("a<a & & &g", XmlUtil
					.removeEntities("a&#X3C;a & &amp; &g"));
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * Test method: void removeEntities(Reader, Writer) removeEntities throws
	 * java.io.IOException
	 */
	public void testRewriteEntities_01() {
		try {
			StringReader reader = new StringReader(
					"a&yen;a  &#171; hh & ; jgha& &amp; gaa &j");
			StringWriter writer = new StringWriter();

			XmlUtil.rewriteEntities(reader, writer);
			assertEquals("a&#165;a  &#171; hh & ; jgha& &#38; gaa &j", writer
					.toString());
		} catch (Exception excep) {
			fail("Unhandled exception: " + excep.getMessage());
		}
	}

	/**
	 * This method is required because the tests can be run from either ANT or
	 * from within the IDE.
	 * 
	 * @return The DTD folder File.
	 */
	private File getDTDfolder() {
		// The DTDs are located in milyn/smooks/dtd
		try {
			File dtdFolder = new File("src/test/dtd");

			if (!dtdFolder.exists()) {
				fail("Can't run test - working dir must be 'commons' dir.");
			} else {
				return dtdFolder;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void testParseStream() {
		try {
			Document doc = XmlUtil.parseStream(getClass().getResourceAsStream(
					"/a.adf"), new LocalEntityResolver(getDTDfolder()), true,
					true);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			Document doc = XmlUtil.parseStream(getClass().getResourceAsStream(
					"/b.adf"), new LocalEntityResolver(getDTDfolder()), true,
					true);
			fail("No failure for invalid document.");
		} catch (SAXException e) {
			// OK
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private Document getXPathDocument() {
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(
					"<x><y attrib='attribval'/><z>zval</z></x> ".getBytes());

			return XmlUtil.parseStream(stream, new LocalEntityResolver(
					getDTDfolder()), false, true);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return null;
	}

	public void testGetString() {
		Document doc = getXPathDocument();

		assertEquals("attribval", XmlUtil.getString(doc, "/x/y/@attrib"));
		assertEquals("zval", XmlUtil.getString(doc, "/x/z/text()"));
		assertEquals("y", XmlUtil.getString(doc, "/x/*[1]/name()"));
		assertEquals("z", XmlUtil.getString(doc, "/x/*[2]/name()"));
		assertEquals("<x><y attrib=\"attribval\"/><z>zval</z></x>", XmlUtil
				.getString(doc, "/x"));
	}

	/**
	 * Main method needed to make a self runnable class
	 * 
	 * @param args
	 *            This is required for main method
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(XmlUtilTest.class));
	}
}

//
// The following code is a static inner class which will provide access to
// all the methods of the target test class XmlUtil. To use
// this class simply copy it into XmlUtil and construct instances of the
// UnitTest using its constructors - these are copies of the constructors in
// XmlUtil.
// In sumation, construct and use XmlUtil.UnitTest in your unit test in the same
// way as you'd expect to use
// XmlUtil.
//
/*
 * public static class UnitTest { XmlUtil testTargetInst = null;
 * 
 * public UnitTest() { testTargetInst = new XmlUtil(); }
 * 
 * public UnitTest(XmlUtil testTarget) { setTestTarget(testTarget); }
 * 
 * public void setTestTarget(XmlUtil testTarget) { testTargetInst = testTarget; }
 * 
 * //----------------------------------------------------------------------
 * 
 * public static void removeEntities (java.io.Reader param0,java.io.Writer
 * param1) throws java.io.IOException { XmlUtil.removeEntities( param0, param1); } }
 */

