/*
	Milyn - Copyright (C) 2003

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

package org.milyn.device.ident;

import org.milyn.device.request.Request;

import java.util.*;

/**
 * Device Identification Set.
 * <p/>
 * Represents a set of identification units for a specific device.  An identification
 * set exists per device.
 * <p/>
 * When talking in terms of the "device-ident" XML representation this class represents the
 * <code>device</code> element.  The attribute rules on which this class operates are easily
 * explained in relation to this XML definition but the same rules would apply if the objects
 * in this object model are popluated from a different data source e.g. a database.
 * <p/>
 * Matching is based on a number of criteria and is defined through the match attribute
 * on the device identification definition.  <code>match</code> defines combinations
 * of identification units that result in a positive match for the device.  "all",
 * "*" and "any" are predefined and  reserved where "all" is the default and "any"
 * and "*" are equivalent.  <code>match</code> may also contain a list of identification
 * unit "id" attribute value combinations.  Match "id" combinations are separated by the pipe
 * character "|".  A combination can be built from one or more id attribute values
 * from the contained identification units (separated by commas).  If an identification unit
 * doesn't have an "id" attribute value set a pseudo id value will be assigned based on it's
 * index in the identification set (base 1 index).
 * <p/>
 * XML Examples:
 * <ul>
 * 1.  Same as match="all".  Only a positive match on all the identification units result
 * in a positive match on the device.  This is the default.
 * <pre>
 *      &lt;device name="xxx"&gt;
 *          &lt;http-req-header name="AAA" value="aaa" /&gt;
 *          &lt;http-req-param name="BBB" value="bbb" /&gt;
 *      &lt;/device&gt;
 * </pre>
 * <p/>
 * 2.  A positive match on any of the identification units result in a positive
 * match on the device.  match="*" is equivalent to match="any".
 * <pre>
 *      &lt;device name="xxx" match="any"&gt;
 *          &lt;http-req-header name="AAA" value="aaa" /&gt;
 *          &lt;http-req-param name="BBB" value="bbb" /&gt;
 *      &lt;/device&gt;
 * </pre>
 * <p/>
 * 3.  A positive match on identification units (1) or (2 and 3) or (3 and 4)
 * result in a poitive match on the device.
 * <pre>
 *      &lt;device name="xxx" match="1|2,3|3,4"&gt;
 *          &lt;http-req-header id="1" name="AAA" value="aaa" /&gt;
 *          &lt;http-req-header id="2" name="BBB" value="bbb" /&gt;
 *          &lt;http-req-header id="3" name="CCC" value="ccc" /&gt;
 *          &lt;http-req-param id="4" name="DDD" value="ddd" /&gt;
 *      &lt;/device&gt;
 * </pre>
 * </ul>
 * <p/>
 * @author Tom Fennelly
 */

public class IdentSet {
    /**
     * Device Name.
     */
    private String deviceName = null;
    /**
     * This is a 2D String arrays which defines the device match
     * combination sets by their <code>id</code> attibute value.
     */
    private String[][] matchComboIdMatrix = null;
    /**
     * The device identification unit set.
     * <p/>
     * This is esentially the devices set of <code>identunit</code> elements
     * from the device definition module.
     */
    private IdentSetList identUnits = null;
    /**
     * This is a 2D IdentUnit arrays which defines the device match
     * combination sets by their identification units.
     * <p/>
     * This is derived from the matchComboIds and the
     */
    private IdentUnit[][] matchComboMatrix = null;
    /**
     * Is the identification set prepared for use in device identifrication.
     */
    private boolean prepared = false;
    /**
     * Match combination type identifier: MATCH_ALL
     */
    private static final int MATCH_ALL = 0;
    /**
     * Match combination type identifier: MATCH_ANY
     */
    private static final int MATCH_ANY = 1;
    /**
     * Match combination type identifier: MATCH_COMBO
     */
    private static final int MATCH_COMBO = 2;
    /**
     * Match type for this identfication set.
     */
    private int matchType = MATCH_ALL;

    /**
     * Set the device this identifier set represents.
     * @param name The device name.
     */
    public void setDeviceName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Illegal device name setting: null");
        }
        name = name.trim();
        if (name.equals("")) {
            throw new IllegalArgumentException("Illegal device name: empty");
        }
        deviceName = name.toLowerCase();
    }

    /**
     * Get the device this identifier set represents.
     * @return The device name.
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Set the match combinatiosn string for the device identification set.
     * @param match The match combinatation String.
     */
    public void setMatch(String match) {
        if (deviceName == null) {
            throw new IllegalStateException("Illegal call to setMatch before is setting the device name.");
        }
        if (identUnits != null && identUnits.size() > 0) {
            throw new IllegalStateException("Illegal call to setMatch after first IdentUnit has been added.");
        }
        if (match == null) {
            throw new IllegalArgumentException("Illegal device match string: null");
        }
        match = match.trim();
        if (match.equals("")) {
            throw new IllegalArgumentException("Illegal device match string: empty");
        }

        if (match.equalsIgnoreCase("all")) {
            matchType = MATCH_ALL;
        } else if (match.equalsIgnoreCase("any") || match.equalsIgnoreCase("*")) {
            matchType = MATCH_ANY;
        } else {
            matchType = MATCH_COMBO;
            parseMatchString(match);
        }
    }

    /**
     * Is this device definition a match based on the supplied device request
     * @param request The device request.
     * @return true If the device definition is a match, otherwise false.
     */
    public boolean isMatch(Request request) {
        if (deviceName == null) {
            throw new IllegalStateException("Call to isMatch before device name has been set.");
        }
        if (!prepared) {
            throw new IllegalStateException("Call to isMatch for device [" + deviceName + "] before IdentSet has been prepared.");
        }
        // Assumtion here is that if the set is prepared it is prepared without error
        // therefore the match matrix etc is in place.

        // Iterate through the match matrix.
        // -    If we get a match on all elements in one of the dimensions we have a
        //      match on the device.
        for (int y = 0; y < matchComboMatrix.length; y++) {
            int x = 0;

            for (; x < matchComboMatrix[y].length; x++) {
                if (!matchComboMatrix[y][x].isMatch(request)) {
                    break;
                }
            }

            // We have a macth if all the IdentUnits in this dimension match for the request.
            if (x == matchComboMatrix[y].length) {
                return true;
            }
        }

        return false;
    }

    /**
     * Parse the match combination string into the combination list.
     * @param match The match combination string.
     * @return True if the string was properly parsed.
     */
    private boolean parseMatchString(String match) {
        Enumeration matchTokens = null;
        Vector matchCombos = null;

        if (match == null) {
            throw new IllegalArgumentException("Illegal device match combination setting: null");
        }

        //
        // The string should be of the format "a|b|c,d,e|f|g,h" etc.
        // This needs to be parsed into a 2D String representation:
        // [a]
        // [b]
        // [c][d][e]
        // [f]
        // [g][h]
        //

        // Break out the combination sets first.
        matchTokens = new StringTokenizer(match, "|");

        // Throw an exception if there are no tokens (or it's just a string
        // of pipe characters).
        if (!matchTokens.hasMoreElements()) {
            throw new IllegalArgumentException("[Device: " + deviceName + "] Illegal match string format: [" + match + "]");
        }

        matchCombos = new Vector();
        // Iterate through the match string tokens.
        while (matchTokens.hasMoreElements()) {
            Vector comboVec = new Vector();
            String comboStr = (String) matchTokens.nextElement();
            Enumeration comboSetTokens = new StringTokenizer(comboStr, ",");
            String matchListEntry[] = null;

            // Throw an exception if there are no tokens (or it's just a string
            // of comma characters).
            if (!comboSetTokens.hasMoreElements()) {
                throw new IllegalArgumentException("[Device: " + deviceName + "] Illegal match substring format: [" + comboStr + "].  Match string: [" + match + "]");
            }

            // Iterate through the combo set.
            while (comboSetTokens.hasMoreElements()) {
                comboVec.addElement((String) comboSetTokens.nextElement());
            }

            // Add the combination set array to the match list.
            matchListEntry = new String[comboVec.size()];
            comboVec.copyInto(matchListEntry);
            matchCombos.addElement(matchListEntry);
        }

        // Create the 2D array.
        matchComboIdMatrix = new String[matchCombos.size()][0];
        matchCombos.copyInto(matchComboIdMatrix);

        return true;
    }

    /**
     * Get the device combinations sets.
     * @return 2D array of match combinations.
     */
    public String[][] getComboIdMatrix() {
        return matchComboIdMatrix;
    }

    /**
     * Get the identification unit combination set matrix.
     * <p/>
     * This is a 2D arry of IdentUnits
     * @return The identification unit combination set matrix.
     */
    private IdentUnit[][] getComboIdentUnitMatrix() {
        return matchComboMatrix;
    }

    /**
     * Add the identification unit to this device identification set.
     * @param unit The device identification unit to be added.
     */
    public void addIdentUnit(IdentUnit unit) {
        if (deviceName == null) {
            throw new IllegalStateException("Illegal call to addIdentUnit before setting the device name.");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Illegal device identification unit: null");
        }
        if (identUnits == null) {
            identUnits = new IdentSetList();
        }
        if (identUnits.contains(unit)) {
            throw new IllegalArgumentException("Illegal device identification unit: already a member of this identification set");
        }

        // If there's no id set on the identification unit set it to its
        // index within the set (base 1 index).
        String id = unit.getId();
        if (id == null) {
            id = Integer.toString(identUnits.size() + 1);
            unit.setId(id);
        }

        // Add the ident unit to this ident set.
        identUnits.addElement(unit);
    }

    /**
     * Prepare the identifications set for device matching.
     * <p/>
     * This method must be called before attempting to perform any match
     * operations.
     */
    public void prepare() {
        if (deviceName == null) {
            throw new IllegalStateException("Call to prepare before device name has been set.");
        }
        if (prepared) {
            throw new IllegalStateException("Call to prepare identification set for device [" + deviceName + "].  IdentSet allready prepared.");
        }

        // Generate the working set of match combination objects.
        switch (matchType) {
            case MATCH_ALL:
                prepareMatchAll();
                break;
            case MATCH_ANY:
                prepareMatchAny();
                break;
            case MATCH_COMBO:
                // Generate from the 2D array of combination set IDs.
                prepareMatchCombo();
                break;
            default:
                throw new IllegalStateException("**** Illegal alteration to identification set internal state - unsupported match type ****");
        }

        prepared = true;
    }

    /**
     * Perpare the match-all combination set.
     */
    private final void prepareMatchAll() {
        if (identUnits == null || identUnits.size() == 0) {
            throw new IllegalStateException("Call to prepare null or empty identification set for device [" + deviceName + "].");
        }
        // Create:
        // [a][b][c][d]
        matchComboMatrix = new IdentUnit[1][identUnits.size()];
        identUnits.copyInto(matchComboMatrix[0]);
    }

    /**
     * Perpare the match-any combination set.
     */
    private final void prepareMatchAny() {
        if (identUnits == null || identUnits.size() == 0) {
            throw new IllegalStateException("Call to prepare null or empty identification set for device [" + deviceName + "].");
        }
        // Create:
        // [a]
        // [b]
        // [c]
        // [d]
        matchComboMatrix = new IdentUnit[identUnits.size()][1];
        for (int i = 0; i < matchComboMatrix.length; i++) {
            matchComboMatrix[i][0] = (IdentUnit) identUnits.elementAt(i);
        }
    }

    /**
     * Perpare the match-combo combination set.
     */
    private final void prepareMatchCombo() {
        if (matchComboIdMatrix == null) {
            throw new IllegalStateException("Call to prepareMatchCombo - no matchComboId lists.");
        }
        if (identUnits == null || identUnits.size() == 0) {
            throw new IllegalStateException("Call to prepare null or empty identification set for device [" + deviceName + "].");
        }
        // Create in a format matching matchComboIds produced by parseMatchString:
        // [a]
        // [b]
        // [c][d][e]
        // [f]
        // [g][h]
        matchComboMatrix = new IdentUnit[matchComboIdMatrix.length][0];
        for (int iouter = 0; iouter < matchComboIdMatrix.length; iouter++) {
            int comboLen = matchComboIdMatrix[iouter].length;

            matchComboMatrix[iouter] = new IdentUnit[comboLen];
            for (int iinner = 0; iinner < comboLen; iinner++) {
                String unitId = matchComboIdMatrix[iouter][iinner];
                IdentUnit unit = (IdentUnit) identUnits.getIdentUnit(unitId);

                if (unit != null) {
                    matchComboMatrix[iouter][iinner] = unit;
                } else {
                    throw new IllegalArgumentException("Undefined device identication unit [" + unitId + "] in match.  Device [" + deviceName + "]");
                }
            }
        }
    }

    /**
     * Inner class for identification set list.
     */
    class IdentSetList extends Vector {
        /**
         * Get the identification unit form this set list.
         * @param id The identifier of the identification unit.
         * @return The IdentUnit having the supplied id, or null if not such
         * IdentUnit is present in the list.
         */
        private IdentUnit getIdentUnit(String id) {
            for (int i = 0; i < size(); i++) {
                IdentUnit unit = (IdentUnit) elementAt(i);
                String unitId = unit.getId();

                if (unitId != null && unitId.equals(id)) {
                    return unit;
                }
            }

            return null;
        }
    }

    /**
     * Unit test static inner class.
     */
    static class UnitTest {
        IdentSet testTargetInst = null;

        public UnitTest() {
            testTargetInst = new IdentSet();
        }

        public UnitTest(IdentSet testTarget) {
            setTestTarget(testTarget);
        }

        public void setTestTarget(IdentSet testTarget) {
            testTargetInst = testTarget;
        }

        //----------------------------------------------------------------------

        public void setDeviceName(java.lang.String param0) {
            testTargetInst.setDeviceName(param0);
        }

        public java.lang.String getDeviceName() {
            return testTargetInst.getDeviceName();
        }

        public void setMatch(java.lang.String param0) {
            testTargetInst.setMatch(param0);
        }

        public boolean isMatch(org.milyn.device.request.Request param0) {
            return testTargetInst.isMatch(param0);
        }

        public boolean parseMatchString(java.lang.String param0) {
            return testTargetInst.parseMatchString(param0);
        }

        public void addIdentUnit(IdentUnit unit) {
            testTargetInst.addIdentUnit(unit);
        }

        public void prepare() {
            testTargetInst.prepare();
        }

        public IdentUnit[][] getComboIdentUnitMatrix() {
            return testTargetInst.getComboIdentUnitMatrix();
        }

        public java.lang.String[][] getCombinations() {
            return testTargetInst.getComboIdMatrix();
        }
    }
}
