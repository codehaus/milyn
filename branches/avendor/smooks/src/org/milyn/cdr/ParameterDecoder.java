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

package org.milyn.cdr;

import org.milyn.delivery.ContentDeliveryUnit;

/**
 * Abstract Parameter Decoder.
 * <p/>
 * Decodes a parameter {@link java.lang.String} value to an {@link java.lang.Object}.
 * The actual decoded {@link java.lang.Object} type depends on the implementation.
 * @author tfennelly
 */
public abstract class ParameterDecoder implements ContentDeliveryUnit {

	/**
	 * Parameter Decoder Constructor.
	 * @param cdrDef cdres Configuration.
	 */
	public ParameterDecoder(CDRDef cdrDef) {
	}

	/**
	 * Decode the supplied parameter value.
	 * @param value The value to be decoded.
	 * @return The decode value Object.
	 * @throws ParameterDecodeException Unable to decode parameter value.
	 */
	public abstract Object decodeValue(String value) throws ParameterDecodeException;

	public String getShortDescription() {
		return "cdres configuration parameter decoder";
	}

	public String getDetailDescription() {
		return "cdres configuration parameter decoder";
	}
}
