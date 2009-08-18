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

import org.milyn.useragent.UnknownUseragentException;
import org.milyn.useragent.request.Request;

import java.util.Vector;

/**
 * Device identification class.
 * <p/>
 * This class is used by adding {@link IdentSet} instances, preparing it for
 * device matching through a call to {@link #prepare} and then using it through
 * calls to the {@link #matchDevice} method.
 * @author tfennelly
 */

public final class DeviceIdent {

    /**
     * Intermediate IdentSet list.
     * <p/>
     * Set of devices this DeviceIdent object can identify.
     */
    private Vector iDevList = new Vector();
    /**
     * IdentSet list used during device matching.
     */
    private IdentSet[] devList = null;
    /**
     * List of matchable device names (Common Name).
     */
    private String[] deviceNames = null;
    /**
     * DeviceIdent for the VM.
     */
    private static DeviceIdent deviceIdent = null;

	/**
	 * Set the DeviceIdent instance for the VM.
	 * <p/>
	 * This method blocks all future calls to {@link #addIdentSet} or 
	 * {@link #prepare} throwing an IllegalStateException.
	 * @param deviceIdent The DeviceIdent instance for the VM.
	 */
	public static void setInstance(DeviceIdent deviceIdent) {
		if(deviceIdent == null) {
			throw new IllegalArgumentException("null 'deviceIdent' arg in method call.");
		}
		DeviceIdent.deviceIdent = deviceIdent;
	}
	
	/**
	 * Get the DeviceIdent instance for the VM. 
	 * @return The DeviceIdent instance.
	 */
	public static DeviceIdent getInstance() {
		return deviceIdent;
	}

    /**
     * Add a device identification set to this DeviceIdent object.
     * <p/>
     * All IdentSet instances must be added before the DeviceIdent instance is
     * prepared through a call to the {@link #prepare} method.
     * @param identSet Device identification set to be added.
     */
    public void addIdentSet(IdentSet identSet) {
        if(devList != null) {
            throw new IllegalStateException("Call to addIdentSet after DeviceIdent object has been prepared.");
        } else if(identSet == null) {
            throw new IllegalArgumentException("null identSet parameter in call to addIdentSet.");
        } else if(iDevList.contains(identSet)) {
            throw new IllegalArgumentException("IdentSet already added to DeviceIdent.");
        } else if(DeviceIdent.deviceIdent != null) {
			throw new IllegalStateException("Call to addIdentSet after VM DeviceIdent instance has been set.");
		}

        iDevList.addElement(identSet);
    }

    /**
     * Prepare the DeviceIdent object for device matching.
     * <p/>
     * This method must be called before attempting to perform any match
     * operations.
     */
    public void prepare() {
        if(devList != null) {
            throw new IllegalStateException("Call to prepare DeviceIdent object after object has already been prepared.");
        } else if(iDevList.size() == 0) {
            throw new IllegalStateException("Call to prepare DeviceIdent object before IdentSet objects have been added.");
		} else if(DeviceIdent.deviceIdent != null) {
			throw new IllegalStateException("Call to prepare after VM DeviceIdent instance has been set.");
		}

        devList = new IdentSet[iDevList.size()];
        iDevList.copyInto(devList);
		deviceNames = new String[devList.length];
        // Prepare each of the device IdentSet objects and initialise the 
        // deviceNames list.
        for(int i = 0; i < devList.length; i++) {
            devList[i].prepare();
			deviceNames[i] = devList[i].getDeviceName(); 
        }
    }

    /**
     * Match the device associated with the supplied request instance.
     * @param request The device request to be used to identify the requesting device.
     * @return The name of the requesting device.
     * @throws UnknownUseragentException Device cannot be matched from the supplied request.
     */
    public String matchDevice(Request request) throws UnknownUseragentException {
        if(devList == null) {
            throw new IllegalStateException("Call to matchDevice before object has been prepared.");
        }

        // Iterate through the IdentSet list checking for a match on each.
        for(int i = 0; i < devList.length; i++) {
            if(devList[i].isMatch(request)) {
                return devList[i].getDeviceName();
            }
        }

        throw new UnknownUseragentException();
    }

	/**
	 * Get the name list of matchable devices.
	 * @return List of matchable device names.
	 */
	public String[] getDeviceNames() {
		if(deviceNames == null) {
			throw new IllegalStateException("Call to getDeviceNames before object has been prepared.");
		}
		return deviceNames;
	}
}
