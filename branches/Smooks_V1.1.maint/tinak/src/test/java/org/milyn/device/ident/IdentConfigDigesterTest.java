// IdentConfigDigesterTest.java

package org.milyn.device.ident;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.InputStream;

import org.milyn.device.ident.DeviceIdent;
import org.milyn.useragent.UnknownUseragentException;
import org.milyn.useragent.request.MockHttpRequest;

/**
 * IdentConfigDigesterTest
 * <p> Relations:
 *     IdentConfigDigester extends org.apache.commons.digester.Digester <br>
 *
 * @author Tom Fennelly
 * @see org.milyn.device.ident.IdentConfigDigester
 */

public class IdentConfigDigesterTest extends TestCase {

    //private IdentConfigDigester.UnitTest identconfigdigester;


    /**
     * Constructor (needed for JTest)
     * @param name    Name of Object
     */
    public IdentConfigDigesterTest(String name) {
        super(name);
    }

    /**
     * Used by JUnit (called before each test method)
     */
    protected void setUp() {
        //identconfigdigester = new IdentConfigDigester.UnitTest(new IdentConfigDigester());
    }

    /**
     * Used by JUnit (called after each test method)
     */
    protected void tearDown() {
        //identconfigdigester = null;
    }

    /**
     * Test method: java.lang.Object parse(InputStream)
     * parse throws java.io.IOException
     * parse throws org.xml.sax.SAXException
     */
    public void testParse_1() {
        InputStream is = getClass().getResourceAsStream("IdentConfigDigesterTest.xml");
        IdentConfigDigester digester = new IdentConfigDigester();

        try {
			DeviceIdent deviceIdent = digester.parse(is);
			MockHttpRequest request = new MockHttpRequest();

			/*
			    <device name="Nokia7210">
			        <http-req-param name="ua" value="Nokia 7210" />
			    </device>
			 */
			request.setParameter("ua", "Nokia 7210");
			assertEquals("Nokia7210".toLowerCase(), deviceIdent.matchDevice(request));
			request.reset();

			/*
			    <device name="www" match="*">
			        <http-req-header name="User-Agent" value="xxx"/>
			        <http-req-param name="ua" value="xxx" />
			    </device>
			 */
			request.setHeader("User-Agent", "xxx");
			assertEquals("www", deviceIdent.matchDevice(request));
			request.reset();
			request.setParameter("ua", "xxx");
			assertEquals("www", deviceIdent.matchDevice(request));
			request.reset();
			
			/*
			    <device name="zzz">
			        <http-req-param name="User-Agent" value="zzz" />
			        <http-req-param name="ua" value="zzz" />
			    </device>
			 */			
			request.setParameter("User-Agent", "zzz");
			try {
				deviceIdent.matchDevice(request);
				fail("Failed to throw UnknownDeviceException.");
			} catch(UnknownUseragentException ude) {
				// OK
			}
			request.setParameter("ua", "zzz");
			assertEquals("zzz", deviceIdent.matchDevice(request));
			request.reset();

        }catch(Exception excep) {
            excep.printStackTrace();
            fail(excep.getMessage());
        }
    }

    /**
     * Test method: java.lang.Object parse(InputStream)
     * parse throws java.io.IOException
     * parse throws org.xml.sax.SAXException
     */
    /*
    public void testParse_2() {
        InputStream is = getClass().getResourceAsStream("/ident1.xml");
        IdentConfigDigester digester = new IdentConfigDigester();

        try {
            DeviceIdent deviceIdent = digester.parseDeviceIdent(is);
        }catch(Exception excep) {
            excep.printStackTrace();
            fail(excep.getMessage());
        }
    }
    */

    /**
     * Main method needed to make a self runnable class
     *
     * @param args This is required for main method
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(IdentConfigDigesterTest.class));
    }
}


//
// The following code is a static inner class which will provide access to
// all the methods of the target test class IdentConfigDigester.  To use
// this class simply copy it into IdentConfigDigester and construct instances of the
// UnitTest using its constructors - these are copies of the constructors in IdentConfigDigester.
// In sumation, construct and use IdentConfigDigester.UnitTest in your unit test in the same way as you'd expect to use
// IdentConfigDigester.
//
/*
public static class UnitTest {
   IdentConfigDigester testTargetInst = null;

   public UnitTest()  {
     testTargetInst = new IdentConfigDigester();
   }

   public UnitTest(IdentConfigDigester testTarget) {
     setTestTarget(testTarget);
   }

   public void setTestTarget(IdentConfigDigester testTarget) {
     testTargetInst = testTarget;
   }

   //----------------------------------------------------------------------

   public java.lang.Object parse (java.io.File param0)  throws java.io.IOException,org.xml.sax.SAXException {
     return testTargetInst.parse( param0);
   }
   public java.lang.Object parse (org.xml.sax.InputSource param0)  throws java.io.IOException,org.xml.sax.SAXException {
     return testTargetInst.parse( param0);
   }
   public java.lang.Object parse (java.io.InputStream param0)  throws java.io.IOException,org.xml.sax.SAXException {
     return testTargetInst.parse( param0);
   }
   protected void configure ()  {
     testTargetInst.configure();
   }
}
*/

