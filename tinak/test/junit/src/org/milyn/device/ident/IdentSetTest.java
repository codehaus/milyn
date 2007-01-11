// IdentSetTest.java

package org.milyn.device.ident;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.Arrays;

import org.milyn.device.request.MockHttpRequest;

/**
 * IdentSetTest
 * <p> Relations:
 *     IdentSet extends java.lang.Object <br>
 *
 * @author Tom Fennelly
 * @see org.milyn.device.ident.IdentSet
 */

public class IdentSetTest extends TestCase {

    private IdentSet.UnitTest identset;


    /**
     * Constructor (needed for JTest)
     * @param name    Name of Object
     */
    public IdentSetTest(String name) {
        super(name);
    }

    /**
     * Used by JUnit (called before each test method)
     */
    protected void setUp() {
        identset = new IdentSet.UnitTest(new IdentSet());
    }

    /**
     * Used by JUnit (called after each test method)
     */
    protected void tearDown() {
        identset = null;
    }

    /**
     * Test method: void setDeviceName(String)
     */
    public void testSetDeviceName_1() {
        try {
            identset.setDeviceName(null);
            fail("didn't fail on null device name");
        } catch(IllegalArgumentException ex) {
            // OK
        }
    }

    /**
     * Test method: void setDeviceName(String)
     */
    public void testSetDeviceName_2() {
        try {
            identset.setDeviceName("");
            fail("didn't fail on empty device name");
        } catch(IllegalArgumentException ex) {
            // OK
        }
    }

    /**
     * Test method: void setMatch(String)
     */
    public void testSetMatch_1() {
        try {
            // shouldn't call setMatch before calling deviceName
            identset.setMatch("blah");
            fail("didn't fail on null device match string");
        } catch(IllegalStateException ex) {
            // OK
        }
    }

    /**
     * Test method: void setMatch(String)
     */
    public void testSetMatch_2() {
        try {
            identset.setDeviceName("devname");
            identset.setMatch(null);
            fail("didn't fail on null device match string");
        } catch(IllegalArgumentException ex) {
            // OK
        }
    }

    /**
     * Test method: void setMatch(String)
     */
    public void testSetMatch_3() {
        try {
            identset.setDeviceName("devname");
            identset.setMatch("");
            fail("didn't fail on empty device match string");
        } catch(IllegalArgumentException ex) {
            // OK
        }
    }

    /**
     * Test method: void setMatch(String)
     */
    public void testSetMatch_4() {
        try {
            identset.setDeviceName("devname");
            IdentUnit unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "name", "value").getTestTarget();
            identset.addIdentUnit(unit);
            identset.setMatch("asd");
            fail("Call to setMatch after 1st ident unit has been added.");
        } catch(IllegalStateException ex) {
            // OK
        }
    }

    /**
     * Test method: boolean isMatch(Request)
     */
    public void testIsMatch_1() {
        identset.setDeviceName("devname");
        identset.setMatch("1,third|4|2");
        addIdentUnitsForTest();
        // Prepare should produce:
        // [1],[third]
        // [4]
        // [2]
        identset.prepare();

        // Setup the request
        MockHttpRequest request = new MockHttpRequest();

        // Try a few requests
        request.setHeader("n-4", "v-4");
        assertTrue(identset.isMatch(request));
        request.reset();

        request.setParameter("n-4", "v-4");
        assertTrue(!identset.isMatch(request));
        request.reset();

        request.setHeader("n-1", "v-1");
        request.setHeader("n-third", "v-3");
        assertTrue(identset.isMatch(request));
        request.reset();

        request.setHeader("n-1", "v-1");
        assertTrue(!identset.isMatch(request));
        request.reset();

        request.setHeader("n-third", "v-3");
        assertTrue(!identset.isMatch(request));
        request.reset();
    }

    /**
     * Test method: boolean isMatch(Request)
     */
    public void testIsMatch_2() {
        identset.setDeviceName("devname");
        identset.setMatch("1,third,fifth|4,7|2,sixth");
        addIdentUnitsForTest();
        addMoreIdentUnitsForTest();
        // Prepare should produce:
        // [1],[third],[fifth]
        // [4],[7]
        // [2],[sixth]
        identset.prepare();

        // Setup the request
        MockHttpRequest request = new MockHttpRequest();

        // Try a few requests
        request.setHeader("n-4", "v-4");
        assertTrue(!identset.isMatch(request));
        request.reset();

        request.setHeader("n-4", "v-4");
        request.setParameter("n-7", "v-7");
        assertTrue(identset.isMatch(request));
        request.reset();

        request.setHeader("n-2", "v-2");
        request.setHeader("n-6", "v-6");
        assertTrue(identset.isMatch(request));
        request.reset();

        request.setHeader("n-1", "v-1");
        request.setHeader("n-third", "v-1");
        request.setParameter("n-5", "v-5");
        assertTrue(!identset.isMatch(request));
        request.reset();

        request.setHeader("n-1", "v-1");
        request.setParameter("n-5", "v-5");
        assertTrue(!identset.isMatch(request));
        request.reset();

        request.setHeader("n-1", "v-1");
        request.setHeader("n-third", "v-3");
        request.setParameter("n-5", "v-5");
        assertTrue(identset.isMatch(request));
        request.reset();
    }

    /**
     * Test method: boolean parseMatchString(String)
     */
    public void testParseMatchString_1() {
        try {
            identset.parseMatchString(",,,");
            fail("didn't fail on empty device match string");
        } catch(IllegalArgumentException ex) {
            // OK
        }
    }

    /**
     * Test method: boolean parseMatchString(String)
     */
    public void testParseMatchString_2() {
        try {
            identset.parseMatchString("|||");
            fail("didn't fail on empty device match string");
        } catch(IllegalArgumentException ex) {
            // OK
        }
    }

    /**
     * Test method: boolean parseMatchString(String)
     */
    public void testParseMatchString_3() {
        try {
            identset.parseMatchString("a,|,|");
            fail("didn't fail on empty device match string");
        } catch(IllegalArgumentException ex) {
            // OK
        }
    }

    /**
     * Test method: boolean parseMatchString(String)
     */
    public void testParseMatchString_4() {
        // All these should be OK
        identset.parseMatchString("a");
        identset.parseMatchString("a||");
        identset.parseMatchString("a,b,c");
        identset.parseMatchString("a,b,c,");
        identset.parseMatchString("a,b,c|d|e,f");
        identset.parseMatchString("a,b,c|");
    }

    /**
     * Test method: boolean parseMatchString(String)
     */
    public void testParseMatchString_5() {
        // All these should be OK
        // Check the combinations
        identset.parseMatchString("a");
        assertEquals(identset.getCombinations(), new String[][] {{"a"}});
        identset.parseMatchString("a||");
        assertEquals(identset.getCombinations(), new String[][] {{"a"}});
        identset.parseMatchString("a,b,c");
        assertEquals(identset.getCombinations(), new String[][] {{"a", "b", "c"}});
        identset.parseMatchString("a,b,c,");
        assertEquals(identset.getCombinations(), new String[][] {{"a", "b", "c"}});
        identset.parseMatchString("a,b,c|d|e,f");
        assertEquals(identset.getCombinations(), new String[][] {{"a", "b", "c"}, {"d"}, {"e", "f"}});
        identset.parseMatchString("a,b,c|");
        assertEquals(identset.getCombinations(), new String[][] {{"a", "b", "c"}});
        identset.parseMatchString("a|b,c|d|e,f");
        assertEquals(identset.getCombinations(), new String[][] {{"a"}, {"b", "c"}, {"d"}, {"e", "f"}});
    }

    /**
     * Assert that the 2 arrays are equal.
     * @param array1 First 2D array
     * @param array2 Second 2D array.
     */
    private void assertEquals(Object[][] array1, Object[][] array2) {
        if(array1 == array2) {
            return;
        }
        if(array1 != null && array2 != null) {
            if(array1.length == array2.length) {
                int i = 0;
                for(; i < array1.length; i++) {
                    if(!Arrays.equals(array1[i], array2[i])) {
                        break;
                    }
                }
                if(i == array1.length) {
                    return;
                }
            }
        }

        fail("2D arrays not equal.");
    }

    /**
     * Test method: void addIdentUnit(IdentUnit unit)
     */
    public void testAddIdentUnit_1() {
        try {
            identset.addIdentUnit(null);
            fail("didn't fail on null device ident unit");
        } catch(IllegalStateException ex) {
            // OK
        }
    }

    /**
     * Test method: void addIdentUnit(IdentUnit unit)
     */
    public void testAddIdentUnit_2() {
        try {
            identset.setDeviceName("devname");
            identset.addIdentUnit(null);
            fail("didn't fail on null device ident unit");
        } catch(IllegalArgumentException ex) {
            // OK
        }
    }

    /**
     * Test method: void addIdentUnit(IdentUnit unit)
     */
    public void testAddIdentUnit_3() {
        identset.setDeviceName("devname");
        IdentUnit unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "name", "value").getTestTarget();
        identset.addIdentUnit(unit);
        if(unit.getId() == null || !unit.getId().equals("1")) {
            fail("didn't assign device ident id to pseudo value. Value: " + unit.getId());
        }
        unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "name", "value").getTestTarget();
        identset.addIdentUnit(unit);
        if(unit.getId() == null || !unit.getId().equals("2")) {
            fail("didn't assign device ident id to pseudo value. Value: " + unit.getId());
        }
        unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, "identid", "name", "value").getTestTarget();
        identset.addIdentUnit(unit);
        if(unit.getId() == null || !unit.getId().equals("identid")) {
            fail("didn't assign device ident id to pseudo value. Value: " + unit.getId());
        }
        unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "name", "value").getTestTarget();
        identset.addIdentUnit(unit);
        if(unit.getId() == null || !unit.getId().equals("4")) {
            fail("didn't assign device ident id to pseudo value. Value: " + unit.getId());
        }
    }

    /**
     * Test method: void addIdentUnit(IdentUnit unit)
     */
    public void testAddIdentUnit_4() {
        try {
            identset.setDeviceName("devname");
            IdentUnit unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "name", "value").getTestTarget();
            identset.addIdentUnit(unit);
            identset.addIdentUnit(unit);
            fail("didn't throw exception on duplicate ident unit");
        } catch(IllegalArgumentException ex) {
            // OK
        }
    }

    /**
     * Test method: void prepare() throws UndefinedIdentUnitException
     */
    public void testPrepare_1() {
        try {
            identset.prepare();
            fail("Didn't fail on call to prepare before setting deviceName.");
        } catch(IllegalStateException excep) {
            // OK
        } catch(Exception excep) {
            fail("Unhandled exception");
        }
    }

    private void addIdentUnitsForTest() {
        IdentUnit unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "n-1", "v-1").getTestTarget();
        identset.addIdentUnit(unit);
        unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "n-2", "v-2").getTestTarget();
        identset.addIdentUnit(unit);
        unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, "third", "n-third", "v-3").getTestTarget();
        identset.addIdentUnit(unit);
        unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "n-4", "v.*4").getTestTarget();
        identset.addIdentUnit(unit);
    }

    private void addMoreIdentUnitsForTest() {
        IdentUnit unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.PARAM_UNIT, "fifth", "n-5", "v-5").getTestTarget();
        identset.addIdentUnit(unit);
        unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, "sixth", "n-6", "v-6").getTestTarget();
        identset.addIdentUnit(unit);
        unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.PARAM_UNIT, null, "n-7", "v-7").getTestTarget();
        identset.addIdentUnit(unit);
    }

    /**
     * Test method: void prepare() throws UndefinedIdentUnitException
     */
    public void testPrepare_2_1() {
        identset.setDeviceName("devname");
        // Not setting match - defaulting to "all"

        // Add some ident units
        addIdentUnitsForTest();

        // Prepare should produce:
        // [1][2][third][4]
        identset.prepare();
        assertEquals(identset.getComboIdentUnitMatrix(), new String[][] {{"1", "2", "third", "4"}});
    }

    /**
     * Test method: void prepare() throws UndefinedIdentUnitException
     */
    public void testPrepare_2_2() {
        identset.setDeviceName("devname");
        identset.setMatch("all");

        // Add some ident units
        addIdentUnitsForTest();

        // Prepare should produce:
        // [1][2][third][4]
        identset.prepare();
        assertEquals(identset.getComboIdentUnitMatrix(), new String[][] {{"1", "2", "third", "4"}});
    }

    /**
     * Test method: void prepare() throws UndefinedIdentUnitException
     */
    public void testPrepare_2_3() {
        identset.setDeviceName("devname");
        identset.setMatch("All");

        // Add some ident units
        addIdentUnitsForTest();

        // Prepare should produce:
        // [1][2][third][4]
        identset.prepare();
        assertEquals(identset.getComboIdentUnitMatrix(), new String[][] {{"1", "2", "third", "4"}});
    }

    /**
     * Test method: void prepare() throws UndefinedIdentUnitException
     */
    public void testPrepare_3_1() {
        identset.setDeviceName("devname");
        identset.setMatch("*");

        // Add some ident units
        addIdentUnitsForTest();

        // Prepare should produce:
        // [1]
        // [2]
        // [third]
        // [4]
        identset.prepare();
        assertEquals(identset.getComboIdentUnitMatrix(), new String[][] {{"1"}, {"2"}, {"third"}, {"4"}});
    }

    /**
     * Test method: void prepare() throws UndefinedIdentUnitException
     */
    public void testPrepare_3_2() {
        identset.setDeviceName("devname");
        identset.setMatch("any");

        // Add some ident units
        addIdentUnitsForTest();

        // Prepare should produce:
        // [1]
        // [2]
        // [third]
        // [4]
        identset.prepare();
        assertEquals(identset.getComboIdentUnitMatrix(), new String[][] {{"1"}, {"2"}, {"third"}, {"4"}});
    }

    /**
     * Test method: void prepare() throws UndefinedIdentUnitException
     */
    public void testPrepare_3_3() {
        identset.setDeviceName("devname");
        identset.setMatch("Any");

        // Add some ident units
        addIdentUnitsForTest();

        // Prepare should produce:
        // [1]
        // [2]
        // [third]
        // [4]
        identset.prepare();
        assertEquals(identset.getComboIdentUnitMatrix(), new String[][] {{"1"}, {"2"}, {"third"}, {"4"}});
    }

    /**
     * Test method: void prepare() throws UndefinedIdentUnitException
     */
    public void testPrepare_3_4() {
        identset.setDeviceName("devname");
        identset.setMatch("1|2,third|4");

        // Add some ident units
        addIdentUnitsForTest();

        // Prepare should produce:
        // [1]
        // [2],[third]
        // [4]
        identset.prepare();
        assertEquals(identset.getComboIdentUnitMatrix(), new String[][] {{"1"}, {"2", "third"}, {"4"}});
    }

    /**
     * Test method: void prepare() throws UndefinedIdentUnitException
     */
    public void testPrepare_3_5() {
        identset.setDeviceName("devname");
        identset.setMatch("1,third|4|2");

        // Add some ident units
        addIdentUnitsForTest();

        // Prepare should produce:
        // [1],[third]
        // [4]
        // [2]
        identset.prepare();
        assertEquals(identset.getComboIdentUnitMatrix(), new String[][] {{"1", "third"}, {"4"}, {"2"}});
    }

    /**
     * Test method: void prepare() throws UndefinedIdentUnitException
     */
    public void testPrepare_3_6() {
        identset.setDeviceName("devname");
        // set a match with an undefined ident unit id "xxx"
        identset.setMatch("1,third|4|2,xxx");

        // Add some ident units
        addIdentUnitsForTest();

        // Prepare should produce:
        // [1],[third]
        // [4]
        // [2]
        try {
            identset.prepare();
            fail("No exception thrown for undefined ident unit \"xxx\"");
        } catch(IllegalArgumentException excep) {
            // OK
        }
    }

    /**
     * Main method needed to make a self runnable class
     *
     * @param args This is required for main method
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(IdentSetTest.class));
    }
}


//
// The following code is a static inner class which will provide access to
// all the methods of the target test class IdentSet.  To use
// this class simply copy it into IdentSet and construct instances of the
// UnitTest using its constructors - these are copies of the constructors in IdentSet.
// In sumation, construct and use IdentSet.UnitTest in your unit test in the same way as you'd expect to use
// IdentSet.
//
/*
public static class UnitTest {
   IdentSet testTargetInst = null;

   public UnitTest()  {
     testTargetInst = new IdentSet();
   }

   public UnitTest(IdentSet testTarget) {
     setTestTarget(testTarget);
   }

   public void setTestTarget(IdentSet testTarget) {
     testTargetInst = testTarget;
   }

   //----------------------------------------------------------------------

   public void setDeviceName (java.lang.String param0)  {
     testTargetInst.setDeviceName( param0);
   }
   public java.lang.String getDeviceName ()  {
     return testTargetInst.getDeviceName();
   }
   public void setMatch (java.lang.String param0)  {
     testTargetInst.setMatch( param0);
   }
   public boolean isMatch (org.milyn.device.request.Request param0)  {
     return testTargetInst.isMatch( param0);
   }
   public boolean parseMatchString (java.lang.String param0)  {
     return testTargetInst.parseMatchString( param0);
   }
   public [[Ljava.lang.String; getCombinations ()  {
     return testTargetInst.getCombinations();
   }
}
*/

