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
package org.milyn.classpath;

import org.milyn.util.ClassUtil;
import org.milyn.assertion.AssertArgument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;

/**
 * Filter classpath classes based on their type.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class InstanceOfFilter implements Filter {
    
    private static Log logger = LogFactory.getLog(InstanceOfFilter.class);
    private Class searchType;
    private List<Class> classes = new ArrayList<Class>();
    private String[] includeList = null;
    private String[] igrnoreList = defaultIgnoreList;
    private static String[] defaultIgnoreList = new String[] {
            "java/", "javax/", "netscape/", "sun/", "com/sun", "org/omg", "org/xml", "org/w3c", "junit/", "org/apache/commons", "org/apache/log4j", 
    };

    public InstanceOfFilter(Class searchType) {
        AssertArgument.isNotNull(searchType, "searchType");
        this.searchType = searchType;
    }

    public InstanceOfFilter(Class searchType, String[] igrnoreList, String[] includeList) {
        AssertArgument.isNotNull(searchType, "searchType");
        this.searchType = searchType;
        if(igrnoreList != null) {
            this.igrnoreList = igrnoreList;
        }
        this.includeList = includeList;
    }

    public void filter(String resourceName) {
        if(resourceName.endsWith(".class") && !isIgnorable(resourceName)) {
            String className = ClasspathUtils.toClassName(resourceName);

            try {
                Class clazz = ClassUtil.forName(className, InstanceOfFilter.class);
                if(searchType.isAssignableFrom(clazz)) {
                    classes.add(clazz);
                }
            } catch (Throwable throwable) {
                logger.debug("Resource '" + resourceName + "' presented to '" + InstanceOfFilter.class.getName() + "', but not loadable by classloader.  Ignoring.", throwable);
            }
        }
    }

    private boolean isIgnorable(String resourceName) {
        if(includeList != null) {
            for(String include : includeList) {
                if(resourceName.startsWith(include)) {
                    return false;
                }
            }
            return true;
        }

        for(String ignore : igrnoreList) {
            if(resourceName.startsWith(ignore)) {
                return true;
            }
        }

        return false;
    }

    public List<Class> getClasses() {
        return classes;
    }
}
