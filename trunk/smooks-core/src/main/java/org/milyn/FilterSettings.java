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
package org.milyn;

import org.milyn.cdr.ParameterAccessor;
import org.milyn.delivery.Filter;

/**
 * Smooks filter settings for programmatic configuration of the {@link Smooks} instance.
 * 
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class FilterSettings {

    public static final FilterSettings DEFAULT_DOM = new FilterSettings(StreamFilterType.DOM);
    public static final FilterSettings DEFAULT_SAX = new FilterSettings(StreamFilterType.SAX);

    private StreamFilterType filterType = StreamFilterType.DOM;
    private boolean rewriteEntities = true;
    private boolean defaultSerializationOn = true;
    private boolean terminateOnException = true;
    private boolean maintainElementStack = true;
    private boolean closeSource = true;
    private boolean closeResult = true;

    public FilterSettings() {
    }

    public FilterSettings(StreamFilterType filterType) {
        this.filterType = filterType;
    }

    public FilterSettings setFilterType(StreamFilterType filterType) {
        this.filterType = filterType;
        return this;
    }

    public FilterSettings setRewriteEntities(boolean rewriteEntities) {
        this.rewriteEntities = rewriteEntities;
        return this;
    }

    public FilterSettings setDefaultSerializationOn(boolean defaultSerializationOn) {
        this.defaultSerializationOn = defaultSerializationOn;
        return this;
    }

    public FilterSettings setTerminateOnException(boolean terminateOnException) {
        this.terminateOnException = terminateOnException;
        return this;
    }

    public FilterSettings setMaintainElementStack(boolean maintainElementStack) {
        this.maintainElementStack = maintainElementStack;
        return this;
    }

    public FilterSettings setCloseSource(boolean closeSource) {
        this.closeSource = closeSource;
        return this;
    }

    public FilterSettings setCloseResult(boolean closeResult) {
        this.closeResult = closeResult;
        return this;
    }

    protected void applySettings(Smooks smooks) {
        ParameterAccessor.setParameter(Filter.STREAM_FILTER_TYPE, filterType.toString(), smooks);        
        ParameterAccessor.setParameter(Filter.ENTITIES_REWRITE, Boolean.toString(rewriteEntities), smooks);
        ParameterAccessor.setParameter(Filter.DEFAULT_SERIALIZATION_ON, Boolean.toString(defaultSerializationOn), smooks);
        ParameterAccessor.setParameter(Filter.TERMINATE_ON_VISITOR_EXCEPTION, Boolean.toString(terminateOnException), smooks);
        ParameterAccessor.setParameter(Filter.MAINTAIN_ELEMENT_STACK, Boolean.toString(maintainElementStack), smooks);
        ParameterAccessor.setParameter(Filter.CLOSE_SOURCE, Boolean.toString(closeSource), smooks);
        ParameterAccessor.setParameter(Filter.CLOSE_RESULT, Boolean.toString(closeResult), smooks);
    }
}

