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
package org.milyn.edisax.unedifact;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import org.apache.commons.lang.StringUtils;
import org.milyn.assertion.AssertArgument;
import org.milyn.edisax.BufferedSegmentReader;
import org.milyn.edisax.EDIParser;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.edisax.model.internal.Description;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * UN/EDIFACT Interchange Envelope parser.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class InterchangeParser implements XMLReader {

	private static final Delimiters defaultUNEdifactDelimiters = new Delimiters().setSegment("'").setField("+").setComponent(":").setEscape("?");
	
	private Map<Description, EdifactModel> mappingModels = new HashMap<Description, EdifactModel>();
	private ContentHandler contentHandler;

	public void parse(InputSource unedifactInterchange) throws IOException, SAXException {
		AssertArgument.isNotNull(unedifactInterchange, "unedifactInterchange");

        if(contentHandler == null) {
            throw new IllegalStateException("'contentHandler' not set.  Cannot parse EDI stream.");
        }

        if(mappingModels == null || mappingModels.isEmpty()) {
            throw new IllegalStateException("'mappingModels' not set.  Cannot parse EDI stream.");
        }
		
        try {
	        BufferedSegmentReader segmentReader = new BufferedSegmentReader(unedifactInterchange, defaultUNEdifactDelimiters);
	        ControlBlockHandler handler;
	        String segCode;
	        
	        contentHandler.startDocument();
	        contentHandler.startElement(XMLConstants.NULL_NS_URI, "unEdifact", StringUtils.EMPTY, new AttributesImpl());
	
	        while(true) {
		        segCode = segmentReader.read(3);
		        if(segCode.length() == 3) {
		        	handler = UNEdifactUtil.getControlBlockHandler(segCode);
		        	handler.process(segmentReader, mappingModels, contentHandler);
		        } else {
		        	break;
		        }
	        }
	        
	        contentHandler.endElement(XMLConstants.NULL_NS_URI, "unEdifact", StringUtils.EMPTY);
	        contentHandler.endDocument();
        } finally {
        	contentHandler = null;
        }
	}

	/**
	 * Add EDI mapping models to be used in all subsequent parse operations.
	 * <p/>
	 * The models can be generated through a call to the {@link EDIParser}.
	 * 
	 * @param mappingModels The mapping models.
	 * @return 
	 */
	public InterchangeParser addMappingModels(Map<Description, EdifactModel> mappingModels) {
		AssertArgument.isNotNullAndNotEmpty(mappingModels, "mappingModels");		
		this.mappingModels.putAll(mappingModels);
		return this;
	}

	/**
	 * Add EDI mapping model to be used in all subsequent parse operations.
	 * <p/>
	 * The models can be generated through a call to the {@link EDIParser}.
	 * 
	 * @param mappingModels The mapping models.
	 * @return 
	 */
	public InterchangeParser addMappingModel(EdifactModel mappingModel) {
		AssertArgument.isNotNull(mappingModel, "mappingModel");		
		this.mappingModels.put(mappingModel.getEdimap().getDescription(), mappingModel);
		return this;
	}

	public ContentHandler getContentHandler() {
		return contentHandler;
	}

	public void setContentHandler(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}
    
    /****************************************************************************
     *
     * The following methods are currently unimplemnted...
     *
     ****************************************************************************/
    
    public void parse(String systemId) throws IOException, SAXException {
    	throw new UnsupportedOperationException("Operation not supports by this reader.");
    }
    
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    	return false;
    }
    
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    }
    
    public DTDHandler getDTDHandler() {
    	return null;
    }
    
    public void setDTDHandler(DTDHandler arg0) {
    }
    
    public EntityResolver getEntityResolver() {
    	return null;
    }
    
    public void setEntityResolver(EntityResolver arg0) {
    }
    
    public ErrorHandler getErrorHandler() {
    	return null;
    }
    
    public void setErrorHandler(ErrorHandler arg0) {
    }
    
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    	return null;
    }
    
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    }
}
