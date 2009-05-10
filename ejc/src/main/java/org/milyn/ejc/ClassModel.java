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

import org.milyn.ejc.classes.JClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.HashMap;

/**
 * ClassModel contains a Map of {@link org.milyn.ejc.classes.JClass} for easy lookup when
 * {@link org.milyn.ejc.BeanWriter} and {@link org.milyn.ejc.BindingWriter} needs to access the
 * classes.
 *
 * @see org.milyn.ejc.BeanWriter
 * @see org.milyn.ejc.BindingWriter
 * @author bardl
 */
public class ClassModel {

    private static Log LOG = EJCLogFactory.getLog(ClassModel.class);

    private JClass root;
    private Map<String, JClass> createdClasses;

    public JClass getRoot() {
        return root;
    }

    public void setRoot(JClass root) {
        this.root = root;
    }

    public Map<String, JClass> getCreatedClasses() {
        if ( createdClasses == null ) {
            this.createdClasses = new HashMap<String, JClass>();
        }
        return createdClasses;
    }

    public void addClass(JClass aClass) {
        getCreatedClasses().put(aClass.toString(), aClass);
        LOG.info("Added class " + aClass + " to model.");        
    }
}
