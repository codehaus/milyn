/*
	Milyn - Copyright (C) 2006

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.milyn.useragent.request.HttpRequest;
import org.milyn.useragent.request.Request;

/**
 * HTTP Identification unit.
 * <p/>
 * Used to describe a HTTP identification unit in the document object model.  The supported
 * HTTP identification units are HTTP request headers and request query parameters.
 * <p/>
 * Regular expressions are supported on the HTTP identification unit values.
 * @author Tom Fennelly
 */

public class HttpIdentUnit extends IdentUnit {

    /**
     * HTTP request parameter ident unit type.
     */
    public final static int PARAM_UNIT = 0;
    /**
     * HTTP request header ident unit type.
     */
    public final static int HEADER_UNIT = 1;

    /**
     * HTTP ident unit type identifier.
     */
    private int type = -1;

    /**
     * Regular expression representation of the ident unit value.
     */
    private Pattern regexp = null;

    /**
     * Construct a HTTP ident unit.
     * @param type The type of ident unit.  Either {@link HttpIdentUnit#PARAM_UNIT} or
     * {@link HttpIdentUnit#HEADER_UNIT}. 
     */
    public HttpIdentUnit(int type) {
        switch (type) {
            case PARAM_UNIT:
            case HEADER_UNIT:
                break;
            default:
                throw new IllegalArgumentException("HttpIdentUnit construction using invalid ident unit type identifier.  Must be ome of: HttpIdentUnit.PARAM_UNIT, HttpIdentUnit.HEADER_UNIT");
        }

        this.type = type;
    }

    /**
     * Set the ident unit value.
     * @param value The value value.
     */
    public void setValue(String value) {
        super.setValue(value);
        try {
            // Force a match on the start and end of the line.
            regexp = Pattern.compile("^" + value + "$");
        } catch (Exception excep) {
            // OK, so don't treat it as a regular expression.
        }
    }

    /**
     * Is this device identification unit a match for the device.
     * @param request The device request.
     * @return true If the device identification unit is a match, otherwise false.
     */
    public boolean isMatch(Request request) {
        if (getName() == null) {
            throw new IllegalStateException("isMatch cannot be called before the ident unit name is set.");
        } else if (getValue() == null) {
            throw new IllegalStateException("isMatch cannot be called before the ident unit value is set.");
        }

        if(request instanceof HttpRequest) {
        	HttpRequest httpRequest = (HttpRequest)request; 
        	
            switch (type) {
                case PARAM_UNIT:
                    return isMatch(httpRequest.getParameter(getName()));
                case HEADER_UNIT:
                    return isMatch(httpRequest.getHeader(getName()));
                default:
                    throw new IllegalStateException("HttpIdentUnit type identifier has been unexpectedly changed to an illegal value.  Must be ome of: HttpIdentUnit.PARAM_UNIT, HttpIdentUnit.HEADER_UNIT");
            }
        } else {
            throw new IllegalArgumentException("request parameter not a HttpRequest implementation.");
        }
    }

    /**
     * Is this device identification unit a match for the supplied string.
     * <p/>
     * Perform a direct comparison first.  If that fails, a regular expression 
     * comparison is made.
     * @param value The value match against.
     * @return true If the device identification unit is a match, otherwise false.
     */
    private boolean isMatch(String value) {
        if (value != null) {
        	if(value.equals(getValue())) {
        		return true;
        	} else if (regexp != null) {
            	Matcher matcher = regexp.matcher(value);
                return matcher.matches();
            }
        }

        return false;
    }
    
    static class UnitTest {
        HttpIdentUnit testTargetInst = null;

        public UnitTest() {
            testTargetInst = new HttpIdentUnit(HttpIdentUnit.HEADER_UNIT);
        }

        public UnitTest(int type, String id, String name, String value) {

            testTargetInst = new HttpIdentUnit(type) {
                public boolean equals(Object anObject) {
                    if(super.equals(anObject)) {
                        return true;
                    }
                    if(anObject instanceof HttpIdentUnit) {
                        if(((HttpIdentUnit)anObject).getId().equals(getId())) {
                            return true;
                        }
                    }
                    return false;
                }
            };

            if(id != null) testTargetInst.setId(id);
            if(name != null) testTargetInst.setName(name);
            if(value != null) testTargetInst.setValue(value);
        }

        public UnitTest(HttpIdentUnit testTarget) {
            setTestTarget(testTarget);
        }

        public void setTestTarget(HttpIdentUnit testTarget) {
            testTargetInst = testTarget;
        }

        public IdentUnit getTestTarget() {
            return testTargetInst;
        }

        //----------------------------------------------------------------------

        public boolean isMatch(org.milyn.useragent.request.Request param0) {
            return testTargetInst.isMatch(param0);
        }
    }
}
