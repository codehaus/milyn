// DeviceIdentTest.java

package org.milyn.device.ident;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.milyn.device.request.MockHttpRequest;

/**
 * DeviceIdentTest
 * <p> Relations:
 *     DeviceIdent extends java.lang.Object <br>
 *
 * @author Tom Fennelly
 * @see org.milyn.device.ident.DeviceIdent
 */

public class DeviceIdentTest extends TestCase {

    //private DeviceIdent.UnitTest deviceident;

    /**
     * Constructor (needed for JTest)
     * @param name    Name of Object
     */
    public DeviceIdentTest(String name) {
        super(name);
    }

    /**
     * Used by JUnit (called before each test method)
     */
    protected void setUp() {
        //deviceident = new DeviceIdent.UnitTest(new DeviceIdent());
    }

    /**
     * Used by JUnit (called after each test method)
     */
    protected void tearDown() {
        //deviceident = null;
    }

    /**
     * Test method: void addIdentSet(IdentSet)
     */
    public void testAddIdentSet_1() {
        DeviceIdent deviceIdent = new DeviceIdent();
        try {
            deviceIdent.addIdentSet(null);
            fail("failed to throw IllegalArgumentException on null IdentSet.");
        } catch(IllegalArgumentException arg) {
            //OK
        }
    }

    /**
     * Test method: void addIdentSet(IdentSet)
     */
    public void testAddIdentSet_2() {
        DeviceIdent deviceIdent = new DeviceIdent();
        IdentSet identset = new IdentSet();

        deviceIdent.addIdentSet(identset);
        try {
            deviceIdent.addIdentSet(identset);
            fail("failed to throw IllegArgumentException for adding the same IdentSet more than once.");
        } catch(IllegalArgumentException arg) {
            //OK
        }
    }

    /**
     * Test method: void addIdentSet(IdentSet)
     */
    public void testAddIdentSet_3() {
        DeviceIdent deviceIdent = new DeviceIdent();
        IdentSet identset = new IdentSet();
        IdentUnit unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "n-1", "v-1").getTestTarget();

        identset.setDeviceName("devicename");
        identset.addIdentUnit(unit);
        deviceIdent.addIdentSet(identset);
        deviceIdent.prepare();
        try {
            deviceIdent.addIdentSet(identset);
            fail("Failed to throw IllegalStateException for calling addIdentSet after calling prepare.");
        } catch(IllegalStateException state) {
            //OK
        }
    }

    /**
     * Test method: void prepare()
     */
    public void testPrepare_1() {
        DeviceIdent deviceIdent = new DeviceIdent();
        IdentSet identset = new IdentSet();
        IdentUnit unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "n-1", "v-1").getTestTarget();

        identset.setDeviceName("devicename");
        identset.addIdentUnit(unit);
        deviceIdent.addIdentSet(identset);
        deviceIdent.prepare();
        try {
            deviceIdent.prepare();
            fail("Failed to throw IllegalStateException for calling prepare a second time.");
        } catch(IllegalStateException state) {
            //OK
        }
    }

    /**
     * Test method: void prepare()
     */
    public void testPrepare_2() {
        DeviceIdent deviceIdent = new DeviceIdent();
        try {
            deviceIdent.prepare();
            fail("Failed to throw IllegalStateException for calling prepare before adding an IdentSet.");
        } catch(IllegalStateException state) {
            //OK
        }
    }

    /**
     * Test method: String matchDevice(Request)
     * matchDevice throws org.milyn.device.ident.UnknownDeviceException
     */
    public void testMatchDevice_1() {
        DeviceIdent deviceIdent = new DeviceIdent();
        MockHttpRequest request = new MockHttpRequest();

        try {
            deviceIdent.matchDevice(request);
            fail("failed to throw IllegalState Exception after calling matchDevice before DeviceIdent has been prepared.");
        } catch(IllegalStateException state) {
            //OK
        } catch(UnknownDeviceException ude) {
            fail("Unexpected UnknownDeviceException.");
        }
    }

    /**
     * Test method: String matchDevice(Request)
     * matchDevice throws org.milyn.device.ident.UnknownDeviceException
     */
    public void testMatchDevice_2() {
        DeviceIdent deviceIdent = new DeviceIdent();
        IdentSet identset = new IdentSet();
        IdentUnit unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "n-1", "v-1").getTestTarget();
        MockHttpRequest request = new MockHttpRequest();

        request.setHeader("n-4", "v-4");

        identset.setDeviceName("device1");
        identset.addIdentUnit(unit);
        deviceIdent.addIdentSet(identset);
        deviceIdent.prepare();
        try {
            deviceIdent.matchDevice(request);
            fail("Failed to throw UnknownDeviceException for unknown device.");
        } catch(UnknownDeviceException ude) {
            //OK
        }
    }

    /**
     * Test method: String matchDevice(Request)
     * matchDevice throws org.milyn.device.ident.UnknownDeviceException
     */
    public void testMatchDevice_3() {
        DeviceIdent deviceIdent = new DeviceIdent();
        IdentSet identset = new IdentSet();
        IdentUnit unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "n-1", "v-1").getTestTarget();
        MockHttpRequest request = new MockHttpRequest();

        request.setHeader("n-1", "v-1");

        identset.setDeviceName("device1");
        identset.addIdentUnit(unit);
        deviceIdent.addIdentSet(identset);
        deviceIdent.prepare();
        try {
            assertEquals("device1", deviceIdent.matchDevice(request));
        } catch(UnknownDeviceException ude) {
            fail("Unexpected UnknownDeviceException.");
        }
    }

    /**
     * Test method: String matchDevice(Request)
     * matchDevice throws org.milyn.device.ident.UnknownDeviceException
     */
    public void testMatchDevice_4() {
        DeviceIdent deviceIdent = new DeviceIdent();
        IdentSet identset = new IdentSet();
        IdentUnit unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "n-1", "v-1").getTestTarget();
        MockHttpRequest request = new MockHttpRequest();

        request.setParameter("n-1", "v-1");

        identset.setDeviceName("device1");
        identset.addIdentUnit(unit);
        deviceIdent.addIdentSet(identset);
        deviceIdent.prepare();
        try {
            deviceIdent.matchDevice(request);
            fail("Failed to throw UnknownDeviceException for unknown device.");
        } catch(UnknownDeviceException ude) {
            //OK
        }
    }

    /**
     * Test method: String matchDevice(Request)
     * matchDevice throws org.milyn.device.ident.UnknownDeviceException
     */
    public void testMatchDevice_5() {
        DeviceIdent deviceIdent = new DeviceIdent();
        IdentSet identset = null;
        IdentUnit unit = null;
        MockHttpRequest request = new MockHttpRequest();

        request.setParameter("n-2", "v-2");

        identset = new IdentSet();
        unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.HEADER_UNIT, null, "n-2", "v-2").getTestTarget();
        identset.setDeviceName("device1");
        identset.addIdentUnit(unit);
        deviceIdent.addIdentSet(identset);

        identset = new IdentSet();
        unit = new HttpIdentUnit.UnitTest(HttpIdentUnit.PARAM_UNIT, null, "n-2", "v-2").getTestTarget();
        identset.setDeviceName("device2");
        identset.addIdentUnit(unit);
        deviceIdent.addIdentSet(identset);

        deviceIdent.prepare();
        try {
            assertEquals("device2", deviceIdent.matchDevice(request));
        } catch(UnknownDeviceException ude) {
            fail("Unexpected UnknownDeviceException.");
        }
    }

    /**
     * Main method needed to make a self runnable class
     *
     * @param args This is required for main method
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(DeviceIdentTest.class));
    }
}


//
// The following code is a static inner class which will provide access to
// all the methods of the target test class DeviceIdent.  To use
// this class simply copy it into DeviceIdent and construct instances of the
// UnitTest using its constructors - these are copies of the constructors in DeviceIdent.
// In sumation, construct and use DeviceIdent.UnitTest in your unit test in the same way as you'd expect to use
// DeviceIdent.
//
/*
public static class UnitTest {
   DeviceIdent testTargetInst = null;

   public UnitTest()  {
     testTargetInst = new DeviceIdent();
   }

   public UnitTest(DeviceIdent testTarget) {
     setTestTarget(testTarget);
   }

   public void setTestTarget(DeviceIdent testTarget) {
     testTargetInst = testTarget;
   }

   //----------------------------------------------------------------------

   public void addIdentSet (org.milyn.device.ident.IdentSet param0)  {
     testTargetInst.addIdentSet( param0);
   }
   public void prepare ()  {
     testTargetInst.prepare();
   }
   public java.lang.String matchDevice (org.milyn.device.request.Request param0)  throws org.milyn.device.ident.UnknownDeviceException {
     return testTargetInst.matchDevice( param0);
   }
}
*/

