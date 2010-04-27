/*
	Milyn - Copyright (C) 2006 - 2010

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

package org.milyn.edisax.model.internal;

import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.DecodeType;
import org.milyn.javabean.DataDecodeException;
import org.milyn.config.Configurable;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * ValueNode.
 *
 * @author bardl
 */
public class ValueNode extends MappingNode {

    private String type;
    private List<Map.Entry<String,String>> parameters;
    private Integer minLength;
    private Integer maxLength;
    private DataDecoder decoder;
    private Class<?> typeClass;
    private Properties decodeParams;
    
	public ValueNode() {
	}
    
	public ValueNode(String xmltag) {
		super(xmltag);
		minLength = 0;
		maxLength = 1;
	}

	public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
        if(type != null) {
            decoder = DataDecoder.Factory.create(type);

            DecodeType decodeType = decoder.getClass().getAnnotation(DecodeType.class);
            if(decodeType != null) {
                typeClass = decodeType.value()[0];
            }
        }
    }

    public DataDecoder getDecoder() {
        return decoder;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public List<Map.Entry<String,String>> getTypeParameters() {
        return parameters;
    }

    public void setTypeParameters(List<Map.Entry<String,String>> parameters) {
        this.parameters = parameters;

        if(decoder instanceof Configurable) {
            if(decoder == null) {
                throw new IllegalStateException("Illegal call to set parameters before 'type' has been configured on the " + getClass().getName());
            }

            decodeParams = new Properties();
            for (Map.Entry<String,String> entry : parameters) {
                decodeParams.setProperty(entry.getKey(), entry.getValue());
            }
            ((Configurable)decoder).setConfiguration(decodeParams);
        }
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public void isValidForType(String value) throws DataDecodeException {
        decoder.decode(value);
    }        
}
