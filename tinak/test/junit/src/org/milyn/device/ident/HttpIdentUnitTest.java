// HttpIdentUnitTest.java

package org.milyn.device.ident;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.milyn.device.request.MockHttpRequest;
import org.milyn.device.request.Request;

/**
 * HttpIdentUnitTest
 * <p> Relations:
 *     HttpIdentUnit extends org.milyn.device.ident.IdentUnit <br>
 *
 * @author Tom Fennelly
 * @see org.milyn.device.ident.HttpIdentUnit
 */

public class HttpIdentUnitTest extends TestCase {

    private HttpIdentUnit httpidentunit;
    private MockHttpRequest request;

    /**
     * Constructor (needed for JTest)
     * @param name    Name of Object
     */
    public HttpIdentUnitTest(String name) {
        super(name);
    }

    /**
     * Used by JUnit (called before each test method)
     */
    protected void setUp() {
        request = new MockHttpRequest();
    }

    /**
     * Used by JUnit (called after each test method)
     */
    protected void tearDown() {
        httpidentunit = null;
    }

    /**
     * Test method: boolean isMatch(Request)
     */
    public void testIsMatch_1() {
        try {
            httpidentunit = new HttpIdentUnit(HttpIdentUnit.HEADER_UNIT);
            httpidentunit.isMatch(request);
        } catch(IllegalStateException excep) {
            //OK
        } catch(Exception excep) {
            fail("isMatch test failed - unhandled exception.");
        }
    }

    /**
     * Test method: boolean isMatch(Request)
     */
    public void testIsMatch_2() {
        try {
            httpidentunit = new HttpIdentUnit(HttpIdentUnit.HEADER_UNIT);
            httpidentunit.setName("name");
            httpidentunit.setValue("value");
            httpidentunit.isMatch(new Request(){});
        } catch(IllegalArgumentException excep) {
            // OK - Should throw exception if request implementation
            // not HttpRequest
        } catch(Exception excep) {
            fail("isMatch test failed - unhandled exception.");
        }
    }

    /**
     * Test method: boolean isMatch(Request)
     * Simple match
     */
    public void testIsMatch_3() {
        try {
            httpidentunit = new HttpIdentUnit(HttpIdentUnit.HEADER_UNIT);

            httpidentunit.setName("name");
            httpidentunit.setValue("value");

            request.setHeader("name", "value");
            assertTrue(httpidentunit.isMatch(request));

            request.setHeader("name", "not-value");
            assertTrue(!httpidentunit.isMatch(request));
        } catch(Exception excep) {
            fail("isMatch test failed unexpectedly.");
        }
    }

    /**
     * Test method: boolean isMatch(Request)
     * Simple match
     */
    public void testIsMatch_4() {
        try {
            httpidentunit = new HttpIdentUnit(HttpIdentUnit.HEADER_UNIT);

            httpidentunit.setName("name");
            httpidentunit.setValue("v.*ue");

            request.setHeader("name", "value");
            assertTrue(httpidentunit.isMatch(request));

            request.setHeader("name", "not-value");
            assertTrue(!httpidentunit.isMatch(request));

            httpidentunit.setValue(".*value");

            request.setHeader("name", "not-value");
            assertTrue(httpidentunit.isMatch(request));

            httpidentunit.setValue("va.*");

            request.setHeader("name", "value");
            assertTrue(httpidentunit.isMatch(request));
        } catch(Exception excep) {
            fail("isMatch test failed unexpectedly.");
        }
    }

	/**
	 * Test method: boolean isMatch(Request)
	 * Simple match
	 */
	public void testIsMatch_5() {
		try {
			httpidentunit = new HttpIdentUnit(HttpIdentUnit.PARAM_UNIT);

			httpidentunit.setName("name");
			httpidentunit.setValue("v.*ue");

			request.setParameter("name", "value");
			assertTrue(httpidentunit.isMatch(request));

			request.setParameter("name", "not-value");
			assertTrue(!httpidentunit.isMatch(request));

			httpidentunit.setValue(".*value");

			request.setParameter("name", "not-value");
			assertTrue(httpidentunit.isMatch(request));

			httpidentunit.setValue("va.*");

			request.setParameter("name", "value");
			assertTrue(httpidentunit.isMatch(request));
		} catch(Exception excep) {
			fail("isMatch test failed unexpectedly.");
		}
	}

    /**
     * Main method needed to make a self runnable class
     *
     * @param args This is required for main method
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(HttpIdentUnitTest.class));
    }
}


//
// The following code is a static inner class which will provide access to
// all the methods of the target test class HttpIdentUnit.  To use
// this class simply copy it into HttpIdentUnit and construct instances of the
// UnitTest using its constructors - these are copies of the constructors in HttpIdentUnit.
// In sumation, construct and use HttpIdentUnit.UnitTest in your unit test in the same way as you'd expect to use
// HttpIdentUnit.
//
/*
public static class UnitTest {
   HttpIdentUnit testTargetInst = null;

   public UnitTest()  {
     testTargetInst = new HttpIdentUnit();
   }
        public UnitTest(String id, String name, String value) {
            testTargetInst = new IdentUnit();
            testTargetInst.setId(id);
            testTargetInst.setName(name);
            testTargetInst.setValue(value);
        }

   public UnitTest(HttpIdentUnit testTarget) {
     setTestTarget(testTarget);
   }

   public void setTestTarget(HttpIdentUnit testTarget) {
     testTargetInst = testTarget;
   }

   //----------------------------------------------------------------------

   public boolean isMatch (org.milyn.device.request.Request param0)  {
     return testTargetInst.isMatch( param0);
   }
}
*/

