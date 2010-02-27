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
package org.milyn.ejc;

import org.milyn.javabean.pojogen.JNamedType;
import org.milyn.javabean.DataDecoder;

import java.util.Map;
import java.util.List;

/**
 * BindingConfig
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @author bardl
 */
public class BindingConfig {

    private JNamedType property;
    private String wireBeanId;
    private String selector;
    private List<Map.Entry<String, String>> decoderConfigs;

    public BindingConfig(JNamedType property, String selector, List<Map.Entry<String, String>> decoderConfigs) {
        this.property = property;
        this.selector = selector;
        this.decoderConfigs = decoderConfigs;
    }

    public BindingConfig(String wireBeanId, String selector) {
        this.wireBeanId = wireBeanId;
        this.selector = selector;
    }

    public BindingConfig(JNamedType property, String wireBeanId, String selector) {
        this.property = property;
        this.wireBeanId = wireBeanId;
        this.selector = selector;
    }

    public JNamedType getProperty() {
        return property;
    }

    public String getSelector() {
        return selector;
    }

    public boolean isWiring() {
        return (wireBeanId != null);
    }

    public boolean isBoundToProperty() {
        return (property != null);
    }

    public String getWireBeanId() {
        return wireBeanId;
    }

    public List<Map.Entry<String, String>> getDecoderConfigs() {
        return decoderConfigs;
    }

    public String getType() {
        Class type = property.getType().getType();

        if(type.isArray()) {
            return "$DELETE:NOT-APPLICABLE$";
        }

        Class<? extends DataDecoder> decoder = DataDecoder.Factory.getInstance(type);

        if(type.isPrimitive() || type.getPackage().equals(String.class.getPackage())) {
            String typeAlias = decoder.getSimpleName();

            if(typeAlias.endsWith("Decoder")) {
                return typeAlias.substring(0, typeAlias.length() - "Decoder".length());
            }
        }

        return decoder.getName();
    }
}