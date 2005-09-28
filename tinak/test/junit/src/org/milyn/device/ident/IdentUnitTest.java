// IdentUnitTest.java

package org.milyn.device.ident;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.milyn.device.request.Request;

/**
 * IdentUnitTest
 * <p> Relations:
 *     IdentUnit extends java.lang.Object <br>
 *
 * @author Tom Fennelly
 * @see org.milyn.device.ident.IdentUnit
 */

public class IdentUnitTest extends TestCase {

    /**
     * Constructor (needed for JTest)
     * @param name    Name of Object
     */
    public IdentUnitTest(String name) {
        super(name);
    }

    /**
     * Used by JUnit (called before each test method)
     */
    protected void setUp() {
        identunit = new IdentUnit.UnitTest(new IdentUnit() {
            public boolean isMatch(Request request) {
                return false;
            }
        });
    }

    /**
     * Used by JUnit (called after each test method)
     */
    protected void tearDown() {
        identunit = null;
    }

    /**
     * Test method: String getName()
     */
    public void testGetName() {
        identunit.setName("x");
        assertEquals("getName() return value not equal to setName() param.", "x", identunit.getName());
    }

    /**
     * Test method: String getValue()
     */
    public void testGetValue() {
        identunit.setValue("x");
        assertEquals("getValue() return value not equal to setValue() param.", "x", identunit.getValue());
    }

    /**
     * Test method: void setName(String)
     */
    public void testSetName() {
        //Must test for the following parameters!
        String str [] = {null, "\u0000", " "};

        try {
            identunit.setName(null);
            fail("Failed to throw IllegalArgumentException for null String param.");
        } catch(IllegalArgumentException e) {}
        try {
            identunit.setName("");
            fail("Failed to throw IllegalArgumentException for blank String param.");
        } catch(IllegalArgumentException e) {}
        try {
            identunit.setName("  ");
            fail("Failed to throw IllegalArgumentException for whitespace String param.");
        } catch(IllegalArgumentException e) {}
    }

    /**
     * Test method: void setValue(String)
     */
    public void testSetValue() {
        //Must test for the following parameters!
        String str [] = {null, "\u0000", " "};

        try {
            identunit.setValue(null);
            fail("Failed to throw IllegalArgumentException for null String param.");
        } catch(IllegalArgumentException e) {}
        try {
            identunit.setValue("");
            fail("Failed to throw IllegalArgumentException for blank String param.");
        } catch(IllegalArgumentException e) {}
        try {
            identunit.setValue("  ");
            fail("Failed to throw IllegalArgumentException for whitespace String param.");
        } catch(IllegalArgumentException e) {}
    }

    /**
     * Test method: void setId(String)
     */
    public void testSetId() {
        try {
            identunit.setId(null);
            fail("Failed to throw IllegalArgumentException for null String param.");
        } catch(IllegalArgumentException e) {}
        try {
            identunit.setId("");
            fail("Failed to throw IllegalArgumentException for blank String param.");
        } catch(IllegalArgumentException e) {}
        try {
            identunit.setId("  ");
            fail("Failed to throw IllegalArgumentException for whitespace String param.");
        } catch(IllegalArgumentException e) {}
    }

    /**
     * Test method: String getId()
     */
    public void testGetId() {
        identunit.setId("x");
        assertEquals("getId() return value not equal to setId() param.", "x", identunit.getId());
    }


    /**
     * Test method: boolean equals(Object anObject)
     */
    public void testEquals() {
        identunit.setId("xxx");
        if(!identunit.equals("xxx")) {
            fail("equals failed when it shouldn't");
        }
        if(!identunit.equals(identunit.testTargetInst)) {
            fail("equals failed when it shouldn't");
        }
        if(identunit.equals("yyy")) {
            fail("equals didn't fail when it should");
        }
        if(identunit.equals(new Integer(22))) {
            fail("equals didn't fail when it should");
        }
    }

    /**
     * Main method needed to make a self runnable class
     *
     * @param args This is required for main method
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(IdentUnitTest.class));
    }

    private IdentUnit.UnitTest identunit;
}


//
// The following code is a static inner class which will provide access to
// all the methods of the target test class IdentUnit.  To use
// this class simply copy it into IdentUnit and construct instances of the
// UnitTest using its constructors - these are copies of the constructors in IdentUnit.
// In sumation, construct and use IdentUnit.UnitTest in your unit test in the same way as you'd expect to use
// IdentUnit.
//
/*
public static class UnitTest {
   IdentUnit testTargetInst = null;

   public UnitTest()  {
     testTargetInst = new IdentUnit();
   }

   public UnitTest(IdentUnit testTarget) {
     setTestTarget(testTarget);
   }

   public void setTestTarget(IdentUnit testTarget) {
     testTargetInst = testTarget;
   }

   //----------------------------------------------------------------------

   public java.lang.String getName ()  {
     return testTargetInst.getName();
   }
   public java.lang.String getValue ()  {
     return testTargetInst.getValue();
   }
   public void setName (java.lang.String param0)  {
     testTargetInst.setName( param0);
   }
   public void setValue (java.lang.String param0)  {
     testTargetInst.setValue( param0);
   }
   public void setId (java.lang.String param0)  {
     testTargetInst.setId( param0);
   }
   public java.lang.String getId ()  {
     return testTargetInst.getId();
   }
   public abstract boolean isMatch ()  {
     return testTargetInst.isMatch();
   }
}
*/

