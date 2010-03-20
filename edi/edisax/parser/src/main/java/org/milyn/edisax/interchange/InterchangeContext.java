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
package org.milyn.edisax.interchange;

import java.util.Map;

import org.milyn.assertion.AssertArgument;
import org.milyn.edisax.BufferedSegmentReader;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Description;
import org.milyn.lang.MutableInt;
import org.xml.sax.ContentHandler;

/**
 * EDI message interchange context object.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class InterchangeContext {

	private BufferedSegmentReader segmentReader; 
	private Map<Description, EdifactModel> mappingModels; 
	private ContentHandler contentHandler;
    public MutableInt indentDepth = new MutableInt(0);
	
	/**
	 * Public constructor.
	 * 
	 * @param segmentReader The interchange {@link BufferedSegmentReader} instance.
	 * @param mappingModels The {@link EdifactModel Mapping Models} to be used for translating the interchange.
	 * @param contentHandler The {@link ContentHandler content handler} instance to receive the interchange events.
	 */
	public InterchangeContext(BufferedSegmentReader segmentReader, Map<Description, EdifactModel> mappingModels, ContentHandler contentHandler) {
		AssertArgument.isNotNull(segmentReader, "segmentReader");
		AssertArgument.isNotNull(mappingModels, "mappingModels");
		AssertArgument.isNotNull(contentHandler, "contentHandler");
		this.segmentReader = segmentReader;
		this.mappingModels = mappingModels;
		this.contentHandler = contentHandler;
	}

	public BufferedSegmentReader getSegmentReader() {
		return segmentReader;
	}

	public Map<Description, EdifactModel> getMappingModels() {
		return mappingModels;
	}

	public ContentHandler getContentHandler() {
		return contentHandler;
	}
}
