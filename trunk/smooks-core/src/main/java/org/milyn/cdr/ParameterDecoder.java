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

import org.milyn.delivery.AbstractContentDeliveryUnit;

/**
 * Abstract Parameter Decoder.
 * <p/>
 * Decodes a parameter {@link java.lang.String} value to an {@link java.lang.Object}.
 * The actual decoded {@link java.lang.Object} type depends on the implementation.
 * @author tfennelly
 */
public abstract class ParameterDecoder extends AbstractContentDeliveryUnit {

	/**
	 * Parameter Decoder Constructor.
	 * @param resourceConfig smooks-resource Configuration.
	 */
	public ParameterDecoder(SmooksResourceConfiguration resourceConfig) {
		super(resourceConfig);
	}

	/**
	 * Decode the supplied parameter value.
	 * @param value The value to be decoded.
	 * @return The decode value Object.
	 * @throws ParameterDecodeException Unable to decode parameter value.
	 */
	public abstract Object decodeValue(String value) throws ParameterDecodeException;
}
