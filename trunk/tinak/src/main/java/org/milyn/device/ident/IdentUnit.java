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

/**
 * Device identification data unit.
 * <p/>
 * These are units of information which direct Tinak as to how a device should be
 * identified e.g. a request header value to check for.
 * @see HttpIdentUnit
 * @author tfennelly
 */

public abstract class IdentUnit {

    /**
     * Ident unit id.
     */
    private String id = null;
    /**
     * Ident unit parameter name.
     */
    private String name = null;
    /**
     * Ident unit parameter value.
     */
    private String value = null;

    /**
     * Set the ident unit id.
     * @param id The id value.
     */
    public void setId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Null device ident id value.");
        }
        id = id.trim();
        if (id.equals("")) {
            throw new IllegalArgumentException("Blank device ident id value.");
        }

        this.id = id;
    }

    /**
     * Get the ident unit id.
     * @return The id value.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the ident unit name.
     * @param name The name value.
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null device ident name value.");
        }
        name = name.trim();
        if (name.equals("")) {
            throw new IllegalArgumentException("Blank device ident name value.");
        }

        this.name = name;
    }

    /**
     * Get the ident unit name.
     * @return The name value.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the ident unit value.
     * @param value The value value.
     */
    public void setValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Null device ident value value.");
        }
        value = value.trim();
        if (value.equals("")) {
            throw new IllegalArgumentException("Blank device ident value value.");
        }

        this.value = value;
    }

    /**
     * Get the ident unit value.
     * @return The ident value value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Is this device identification unit a match for the device.
     * @param request The device request.
     * @return true If the device identification unit is a match, otherwise false.
     */
    public abstract boolean isMatch(Request request);

    /**
     * Is the supplied object equal to this ident unit.
     * <p/>
     * If the object is a String it will be compared to the id value of the
     * ident unit, otherwise it is a direct reference comparison.
     * @param obj The object on which the comparison is to be made.
     */
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(obj instanceof String) {
            if(((String)obj).equals(getId())) {
                return true;
            }
        }
        return false;
    }

    //
    // The following code is a static inner class which will provide access to
    // all the methods of the target test class IdentUnit.  To use
    // this class simply copy it into IdentUnit and construct instances of the
    // UnitTest using its constructors - these are copies of the constructors in IdentUnit.
    // In sumation, construct and use IdentUnit.UnitTest in your unit test in the same way as you'd expect to use
    // IdentUnit.
    //
    static class UnitTest {
        IdentUnit testTargetInst = null;

        public UnitTest(IdentUnit testTarget) {
            setTestTarget(testTarget);
        }

        public void setTestTarget(IdentUnit testTarget) {
            testTargetInst = testTarget;
        }

        //----------------------------------------------------------------------

        public java.lang.String getName() {
            return testTargetInst.getName();
        }

        public java.lang.String getValue() {
            return testTargetInst.getValue();
        }

        public void setName(java.lang.String param0) {
            testTargetInst.setName(param0);
        }

        public void setValue(java.lang.String param0) {
            testTargetInst.setValue(param0);
        }

        public void setId(java.lang.String param0) {
            testTargetInst.setId(param0);
        }

        public java.lang.String getId() {
            return testTargetInst.getId();
        }

        public boolean equals(Object obj) {
            return testTargetInst.equals(obj);
        }
    }
}
