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
package org.milyn.csv;

import org.milyn.assertion.AssertArgument;

/**
 * CSV Binding configuration.
 * <p/>
 * For more complex bindings, use the main java binding framwework.
 * 
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class CSVBinding {

    private String beanId;
    private Class beanClass;
    private boolean createList;

    public CSVBinding(String beanId, Class beanClass, boolean createList) {
        AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
        AssertArgument.isNotNull(beanClass, "beanClass");
        this.beanId = beanId;
        this.beanClass = beanClass;
        this.createList = createList;
    }

    public String getBeanId() {
        return beanId;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public boolean isCreateList() {
        return createList;
    }
}
