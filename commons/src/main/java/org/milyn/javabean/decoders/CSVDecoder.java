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

import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DecodeType;

/**
 * Comma Separated Value (CSV) decoder.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@DecodeType(String[].class)
public class CSVDecoder implements DataDecoder {
    public Object decode(String data) throws DataDecodeException {
        String[] values = data.split(",");

        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();            
        }

        return values;
    }
}
