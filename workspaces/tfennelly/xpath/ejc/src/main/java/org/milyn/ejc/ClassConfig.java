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

import java.util.List;
import java.util.ArrayList;

/**
 * ClassConfig
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @author bardl
 */
public class ClassConfig {

    private Class beanClass;
    private String beanId;
    private List<BindingConfig> bindings = new ArrayList<BindingConfig>();
    private boolean isArray;
    private String elementName;

    public ClassConfig(Class beanClass, String elementName) {
        this.beanClass = beanClass;
        this.elementName = elementName;
    }

    public ClassConfig(Class beanClass, String beanId, String elementName) {
        this.beanClass = beanClass;
        if (beanId == null) {
            beanId = getClassName(beanClass.getName());
            beanId = beanId.substring(0,1).toLowerCase() + beanId.substring(1);
        }
        this.beanId = beanId;
        this.elementName = elementName;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public String getElementName() {
        return elementName;
    }

    public List<BindingConfig> getBindings() {
        return bindings;
    }

    public void setBindings(List<BindingConfig> bindings) {
        this.bindings = bindings;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    public String getBeanId() {
        if(beanId != null) {
            return beanId;
        } else {
            StringBuilder beanId = new StringBuilder(beanClass.getSimpleName());
            beanId.setCharAt(0, Character.toLowerCase(beanId.charAt(0)));
            return beanId.toString();
        }
    }

    private String getClassName(String name) {
        return name.substring(name.lastIndexOf('.')+1);
    }
}