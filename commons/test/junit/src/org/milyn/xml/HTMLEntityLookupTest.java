// HTMLEntityLookupTest.java

package org.milyn.xml;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * HTMLEntityLookupTest
 * <p> Relations:
 *     HTMLEntityLookup extends java.lang.Object <br>
 *
 * @author Tom Fennelly
 * @see org.milyn.xml.HTMLEntityLookup
 */

public class HTMLEntityLookupTest extends TestCase {

    //private HTMLEntityLookup.UnitTest htmlentitylookup;


    /**
     * Constructor (needed for JTest)
     * @param name    Name of Object
     */
    public HTMLEntityLookupTest(String name) {
        super(name);
    }

    /**
     * Used by JUnit (called before each test method)
     */
    protected void setUp() {
        //htmlentitylookup = new HTMLEntityLookup.UnitTest(new HTMLEntityLookup());
    }

    /**
     * Used by JUnit (called after each test method)
     */
    protected void tearDown() {
        //htmlentitylookup = null;
    }

    /**
     * Test method: java.lang.Character getCharacterCode(String)
     */
    public void testGetCharacterCode() {
        assertEquals('\u00A4', HTMLEntityLookup.getCharacterCode("curren").charValue());
        assertEquals('\u0026', HTMLEntityLookup.getCharacterCode("amp").charValue());
        assertEquals('\u00A0', HTMLEntityLookup.getCharacterCode("nbsp").charValue());
    }

    /**
     * Test method: String getEntityRef(char)
     */
    public void testGetEntityRef() {
        assertEquals("curren", HTMLEntityLookup.getEntityRef('\u00A4'));
        assertEquals("amp", HTMLEntityLookup.getEntityRef('\u0026'));
        assertEquals("nbsp", HTMLEntityLookup.getEntityRef('\u00A0'));
    }

    /**
     * Main method needed to make a self runnable class
     *
     * @param args This is required for main method
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(HTMLEntityLookupTest.class));
    }
}


//
// The following code is a static inner class which will provide access to
// all the methods of the target test class HTMLEntityLookup.  To use
// this class simply copy it into HTMLEntityLookup and construct instances of the
// UnitTest using its constructors - these are copies of the constructors in HTMLEntityLookup.
// In sumation, construct and use HTMLEntityLookup.UnitTest in your unit test in the same way as you'd expect to use
// HTMLEntityLookup.
//
/*
public static class UnitTest {
   HTMLEntityLookup testTargetInst = null;

   public UnitTest()  {
     testTargetInst = new HTMLEntityLookup();
   }

   public UnitTest(HTMLEntityLookup testTarget) {
     setTestTarget(testTarget);
   }

   public void setTestTarget(HTMLEntityLookup testTarget) {
     testTargetInst = testTarget;
   }

   //----------------------------------------------------------------------

   public static void defineEntity (java.lang.String param0,char param1)  {
     HTMLEntityLookup.defineEntity( param0, param1);
   }
   public static java.lang.Character getCharacterCode (java.lang.String param0)  {
     return HTMLEntityLookup.getCharacterCode( param0);
   }
   public static java.lang.String getEntityRef (char param0)  {
     return HTMLEntityLookup.getEntityRef( param0);
   }
}
*/

