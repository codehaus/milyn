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

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;

/**
 * Mapping decoder.
 * <p/>
 * Decodes the supplied data by using it to lookup the decode mapping from the
 * decoders configuration parameters.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MappingDecoder implements DataDecoder {
    
    private SmooksResourceConfiguration resourceConfig;

    public void setConfiguration(SmooksResourceConfiguration resourceConfig) throws SmooksConfigurationException {
        this.resourceConfig = resourceConfig;
    }

    public Object decode(String data) throws DataDecodeException {
        String mappingValue = resourceConfig.getStringParameter(data);

        if(mappingValue == null) {
            throw new DataDecodeException("Mapping <param> for data '" + data + "' not defined.");
        }

        return mappingValue;
    }
}
