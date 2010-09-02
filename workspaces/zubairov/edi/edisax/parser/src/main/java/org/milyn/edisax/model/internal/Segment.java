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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Segment extends SegmentGroup implements ContainerNode, ISegment {

    private List<IField> fields;
    private String segcode;
    private Pattern segcodePattern;
    private Boolean truncatable;
    private Boolean ignoreUnmappedFields;
    private String description;

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.ISegment#getFields()
	 */
    public List<IField> getFields() {
        if (fields == null) {
            fields = new ArrayList<IField>();
        }
        return this.fields;
    }
    
    public ISegment addField(Field field) {
    	getFields().add(field);
    	return this;
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.ISegment#getSegcode()
	 */
    public String getSegcode() {
        return segcode;
    }

    public void setSegcode(String value) {
        this.segcode = value;
        segcodePattern = Pattern.compile("^" + segcode, Pattern.DOTALL);
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.ISegment#getSegcodePattern()
	 */
    public Pattern getSegcodePattern() {
        return segcodePattern;
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.ISegment#getJavaName()
	 */
    @Override
    public String getJavaName() {
        if(getNodeTypeRef() != null) {
            return getNodeTypeRef();
        } else {
            return super.getJavaName();
        }
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.ISegment#isTruncatable()
	 */
    public boolean isTruncatable() {
        return truncatable != null && truncatable;
    }

    public void setTruncatable(Boolean value) {
        this.truncatable = value;
    }
    
    public void setIgnoreUnmappedFields(Boolean value) {
    	this.ignoreUnmappedFields = value;
    }
    
    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.ISegment#isIgnoreUnmappedFields()
	 */
    public boolean isIgnoreUnmappedFields() {
    	return ignoreUnmappedFields != null && ignoreUnmappedFields;
    }

    /* (non-Javadoc)
	 * @see org.milyn.edisax.model.internal.ISegment#getDescription()
	 */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
