/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License (version 2.1) as published by the Free Software
 *  Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  See the GNU Lesser General Public License for more details:
 *  http://www.gnu.org/licenses/lgpl.txt
 */

package org.smooks.model.core;

import org.milyn.javabean.dynamic.serialize.DefaultNamespace;

import java.util.ArrayList;
import java.util.List;

/**
 * Smooks Model Root.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@DefaultNamespace(uri = "http://www.milyn.org/xsd/smooks-1.1.xsd")
public class SmooksModel {

    public static final String MODEL_DESCRIPTOR = "META-INF/org/smooks/model/descriptor.properties";

    private List<NamespaceRoot> modelComponents = new ArrayList<NamespaceRoot>();

    public List<NamespaceRoot> getModelComponents() {
        return modelComponents;
    }

    public void setModelComponents(List<NamespaceRoot> modelComponents) {
        this.modelComponents = modelComponents;
    }
}
