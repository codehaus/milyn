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
 * {@link java.util.Date} data decoder.
 * <p/>
 * Decodes the supplied string into a {@link java.util.Date} value
 * based on the supplied "{@link java.text.SimpleDateFormat format}" parameter, or the default (see below).
 * <p/>
 * The default date format used is "<i>yyyy-MM-dd'T'HH:mm:ss</i>" (see {@link SimpleDateFormat}).
 * This format is based on the <a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#isoformats">ISO 8601</a>
 * standard as used by the XML Schema type "<a href="http://www.w3.org/TR/xmlschema-2/#dateTime">dateTime</a>".
 * <p/>
 * This decoder is synchronized on its underlying {@link SimpleDateFormat} instance.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DateDecoder implements DataDecoder {

    public static final String FORMAT_CONFIG_KEY = "format";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private String format = DEFAULT_DATE_FORMAT;
    private SimpleDateFormat decoder = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

    public void setConfiguration(SmooksResourceConfiguration resourceConfig) throws SmooksConfigurationException {
        format = resourceConfig.getStringParameter(FORMAT_CONFIG_KEY, DEFAULT_DATE_FORMAT);
        decoder = new SimpleDateFormat(format.trim());
    }

    public Object decode(String data) throws DataDecodeException {
        try {
            // Must be sync'd - DateFormat is not synchronized.
            synchronized(decoder) {
                return decoder.parse(data.trim());
            }
        } catch (ParseException e) {
            throw new DataDecodeException("Error decoding Date data value '" + data + "' with decode format '" + format + "'.", e);
        }
    }
}
