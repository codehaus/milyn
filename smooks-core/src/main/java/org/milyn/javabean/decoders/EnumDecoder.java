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
import org.milyn.util.ClassUtil;

/**
 * Enum instance decoder.
 * <p/>
 * The enumeration type is specified through the "<b>enumType</b>" configuration
 * param. Enum constant value mappings can be performed as per the
 * {@link org.milyn.javabean.decoders.MappingDecoder}.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class EnumDecoder implements DataDecoder {

    private Class enumType;
    private MappingDecoder mappingDecoder = new MappingDecoder();

    public void setConfiguration(SmooksResourceConfiguration resourceConfig) throws SmooksConfigurationException {
        String enumTypeName = resourceConfig.getStringParameter("enumType");

        if(enumTypeName == null || enumTypeName.trim().equals(""))
        {
            throw new SmooksConfigurationException("Invalid EnumDecoder configuration. 'enumType' param not specified.");
        }

        try {
            enumType = ClassUtil.forName(enumTypeName.trim(), EnumDecoder.class);
        } catch (ClassNotFoundException e) {
            throw new SmooksConfigurationException("Invalid Enum decoder configuration.  Failed to resolve '" + enumTypeName + "' as a Java Enum Class.", e);
        }

        if(!Enum.class.isAssignableFrom(enumType)) {
            throw new SmooksConfigurationException("Invalid Enum decoder configuration.  Resolved 'enumType' '" + enumTypeName + "' is not a Java Enum Class.");
        }

        mappingDecoder.setConfiguration(resourceConfig);
        mappingDecoder.setStrict(false);
    }

    public Object decode(String data) throws DataDecodeException {
        String mappedValue = (String) mappingDecoder.decode(data);

        try {
            return Enum.valueOf(enumType, mappedValue.trim());
        } catch(IllegalArgumentException e) {
            throw new DataDecodeException("Failed to decode '" + mappedValue + "' as a valid Enum constant of type '" + enumType.getName() + "'.");
        }
    }
}
