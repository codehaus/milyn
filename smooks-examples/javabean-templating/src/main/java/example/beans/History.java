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
package example.beans;

/**
 * Shipping History bean.
 * <p/>
 * This bean is populated by the Smooks JavaBean Cartridge.
 * @author tfennelly
 */
public class History {

    private TrackingNumber[] trackingNumbers;

    public void setTrackingNumbers(TrackingNumber[] trackingNumbers) {
        this.trackingNumbers = trackingNumbers;
    }

    public TrackingNumber[] getTrackingNumbers() {
        return trackingNumbers;
    }
}
