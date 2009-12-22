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
package org.milyn.javabean.decoders;

import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.DecodeType;

/**
 * Integer Decoder.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@DecodeType({Integer.class, int.class})
public class IntegerDecoder implements DataDecoder {

    public Object decode(String data) throws DataDecodeException {
        try {
            return Integer.parseInt(data.trim());
        } catch(NumberFormatException e) {
            throw new DataDecodeException("Failed to decode Integer value '" + data + "'.", e);
        }
    }
}
