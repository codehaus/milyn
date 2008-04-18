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
package org.milyn.payload;

import org.milyn.assertion.AssertArgument;
import org.milyn.payload.FilterSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Java Filtration/Transformation {@link javax.xml.transform.Source}.
 *  
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class JavaSource extends FilterSource {

    private List<Object> sourceObjects;
    private Map<String, Object> beans;

    /**
     * Default Contructor.
     * <p/>
     * Allows the instance to be constructed without a set of
     * "source objects" i.e. a set of objects from which a stream
     * of SAX events will be generated.  In this case a
     * {@link org.milyn.xml.NullSourceXMLReader} will be put in place
     * by Smooks and used by the underlying filter (DOM/SAX).
     * <p/>
     * This constructor can be used in conjunction with the
     * {@link #setBeans} method to (for example) apply a template directly
     * to a bean map by targeting the template resource at the "$document"
     * selector.
     */
    public JavaSource() {
    }

    /**
     * Construct a stream of SAX document/message events from the
     * supplied source object.
     *
     * @param sourceObject The source object.
     */
    public JavaSource(Object sourceObject) {
        AssertArgument.isNotNull(sourceObject, "sourceObject");
        sourceObjects = new ArrayList<Object>();
        sourceObjects.add(sourceObject);
    }

    /**
     * Construct a stream of SAX document/message events from the
     * supplied source object list.
     *
     * @param sourceObjects The source object list.
     */
    public JavaSource(List<Object> sourceObjects) {
        AssertArgument.isNotNull(sourceObjects, "sourceObjects");
        this.sourceObjects = sourceObjects;
    }

    /**
     * Get the source object list.
     * @return The source object list.
     */
    public List<Object> getSourceObjects() {
        return sourceObjects;
    }

    /**
     * Get the input bean map for the transform.
     * <p/>
     * See the {@link JavaSource#JavaSource} constructor.
     *
     * @return The bean map.
     */
    public Map<String, Object> getBeans() {
        return beans;
    }

    /**
     * Set the input bean map for the transform.
     * <p/>
     * See the {@link JavaSource#JavaSource} constructor.
     * 
     * @param beans The bean map.
     */
    public void setBeans(Map<String, Object> beans) {
        this.beans = beans;
    }
}
