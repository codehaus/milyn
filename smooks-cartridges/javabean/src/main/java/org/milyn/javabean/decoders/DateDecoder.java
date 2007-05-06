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
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksConfigurationException;

import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Date data decoder.
 * <p/>
 * Decodes the supplied string into a {@link java.util.Date} value
 * based on the supplied "{@link java.text.SimpleDateFormat format}" parameter.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DateDecoder implements DataDecoder {

    private String format;
    private SimpleDateFormat decoder;

    public void setConfiguration(SmooksResourceConfiguration resourceConfig) throws SmooksConfigurationException {
        format = resourceConfig.getStringParameter("format");
        if (format == null) {
            throw new SmooksConfigurationException("Date Decoder must specify a 'format' parameter.");
        }
        decoder = new SimpleDateFormat(format);
    }

    public Object decode(String data) throws DataDecodeException {
        if (decoder == null) {
            throw new IllegalStateException("Date decoder not initialised.  A decoder for this type (" + getClass().getName() + ") must be explicitly configured (unlike the primitive type decoders) with a date 'format'. See Javadoc.");
        }
        try {
            return decoder.parse(data);
        } catch (ParseException e) {
            throw new DataDecodeException("Error decoding Date data value '" + data + "' with decode format '" + format + "'.", e);
        }
    }
}
