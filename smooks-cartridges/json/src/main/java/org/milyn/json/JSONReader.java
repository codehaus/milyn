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

package org.milyn.json;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Stack;

import javax.xml.XMLConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.milyn.cdr.Parameter;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Config;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.xml.DomUtils;
import org.milyn.xml.SmooksXMLReader;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * JSON to SAX event reader.
 * <p/>
 * This JSON Reader can be plugged into the Smooks {@link org.milyn.delivery.dom.DOMParser} (for example) in order to convert a
 * JSON based message stream into a stream of SAX events to be consumed by the DOMBuilder.
 *
 * <h3>.cdrl Configuration</h3>
 *
 * @author <a href="mailto:maurice@zeijen.net">maurice@zeijen.net</a>
 */
public class JSONReader implements SmooksXMLReader {

	private static Log logger = LogFactory.getLog(JSONReader.class);

	private static final String CONFIG_PARAM_KEY_MAP = "keyMap";

	private static final String KEY_MAP_KEY_ELEMENT = "key";

	private static final String KEY_MAP_KEY_ELEMENT_FROM_ATTRIBUTE = "from";

	private static final String KEY_MAP_KEY_ELEMENT_TO_ATTRIBUTE = "to";

	private static final String XML_ROOT = "json";

	private static final String XML_ARRAY_ELEMENT_NAME = "element";

	private static final String DEFAULT_NULL_VALUE_REPLACEMENT = "";

    private static final Attributes EMPTY_ATTRIBS = new AttributesImpl();

    private static final JsonFactory jsonFactory = new JsonFactory();

    private ContentHandler contentHandler;

	private ExecutionContext request;


	@ConfigParam(defaultVal = XML_ROOT)
    private String rootName;

	@ConfigParam(defaultVal = XML_ARRAY_ELEMENT_NAME)
    private String arrayElementName;

	@ConfigParam(use = Use.OPTIONAL)
    private String keyWhitspaceReplacement;

	@ConfigParam(use = Use.OPTIONAL)
    private String keyPrefixOnNumeric;

	@ConfigParam(use = Use.OPTIONAL)
    private String illegalElementNameCharReplacement;

	@ConfigParam(defaultVal = DEFAULT_NULL_VALUE_REPLACEMENT)
    private String nullValueReplacement;

    @ConfigParam(defaultVal = "UTF-8")
    private Charset encoding;


    private boolean doKeyReplacement = false;

    private boolean doKeyWhitspaceReplacement = false;

    private boolean doPrefixOnNumericKey = false;

    private boolean doIllegalElementNameCharReplacement = false;

    private final HashMap<String, String> keyMap = new HashMap<String, String>();

	@Config
    private SmooksResourceConfiguration config;

    private static enum Type {
    	OBJECT,
    	ARRAY
    }

    @Initialize
    public void initialize() {
		initKeyMap();

		doKeyReplacement = !keyMap.isEmpty();
		doKeyWhitspaceReplacement = keyWhitspaceReplacement != null;
		doPrefixOnNumericKey = keyPrefixOnNumeric != null;
		doIllegalElementNameCharReplacement = illegalElementNameCharReplacement != null;
    }


    /*
     * (non-Javadoc)
     * @see org.milyn.xml.SmooksXMLReader#setExecutionContext(org.milyn.container.ExecutionContext)
     */
	public void setExecutionContext(ExecutionContext request) {
		this.request = request;
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
	 */
	public void parse(InputSource csvInputSource) throws IOException, SAXException {
        if(contentHandler == null) {
            throw new IllegalStateException("'contentHandler' not set.  Cannot parse JSON stream.");
        }
        if(request == null) {
            throw new IllegalStateException("Smooks container 'request' not set.  Cannot parse JSON stream.");
        }

		// Get a reader for the JSON source...
        Reader jsonStreamReader = csvInputSource.getCharacterStream();
        if(jsonStreamReader == null) {
            jsonStreamReader = new InputStreamReader(csvInputSource.getByteStream(), encoding);
        }

        // Create the JSON parser...
        JsonParser jp = null;
        try {

        	if(logger.isTraceEnabled()) {
        		logger.trace("Creating JSON parser");
        	}

        	jp = jsonFactory.createJsonParser(jsonStreamReader);

	        // Start the document and add the root "csv-set" element...
	        contentHandler.startDocument();
	        contentHandler.startElement(XMLConstants.NULL_NS_URI, rootName, "", EMPTY_ATTRIBS);

	        if(logger.isTraceEnabled()) {
	        	logger.trace("Starting JSON parsing");
	        }

	        boolean first = true;
	        Stack<String> elementStack = new Stack<String>();
	        Stack<Type> typeStack = new Stack<Type>();
	        JsonToken t;
	        while ((t = jp.nextToken()) != null) {

	        	if(logger.isTraceEnabled()) {
	        		logger.trace("Token: " + t.name());
	        	}

	        	switch(t) {

	        	case START_OBJECT:
	        	case START_ARRAY:
	        		if(!first) {
		        		if(!typeStack.empty() && typeStack.peek() == Type.ARRAY) {
		        			contentHandler.startElement(XMLConstants.NULL_NS_URI, arrayElementName, "", EMPTY_ATTRIBS);
		        		}
	        		}
	        		typeStack.push(t == JsonToken.START_ARRAY ? Type.ARRAY : Type.OBJECT);
	        		break;

	        	case END_OBJECT:
	        	case END_ARRAY:

	        		if(!elementStack.empty()) {
	        			contentHandler.endElement(XMLConstants.NULL_NS_URI, elementStack.pop(), "");
	        			typeStack.pop();
	        		}

	        		if(!typeStack.empty() && typeStack.peek() == Type.ARRAY) {
	        			contentHandler.endElement(XMLConstants.NULL_NS_URI, arrayElementName, "");
	        		}
	        		break;

	        	case FIELD_NAME:

	        		String name = getElementName(jp.getText());
	        		contentHandler.startElement(XMLConstants.NULL_NS_URI, name, "", EMPTY_ATTRIBS);
	        		elementStack.add(name);
	        		break;

	        	default:

	        		String value;

	        		if(t == JsonToken.VALUE_NULL) {
	        			value = nullValueReplacement;
	        		} else {
	        			value = jp.getText();
	        		}

	        		if(typeStack.peek() == Type.ARRAY) {

	        			contentHandler.startElement(XMLConstants.NULL_NS_URI, arrayElementName, "", EMPTY_ATTRIBS);
	        		}

	        		contentHandler.characters(value.toCharArray(), 0, value.length());

	        		if(typeStack.peek() == Type.ARRAY) {

	        			contentHandler.endElement(XMLConstants.NULL_NS_URI, arrayElementName, "");

	        		} else {

		        		contentHandler.endElement(XMLConstants.NULL_NS_URI, elementStack.pop(), "");

	        		}

	        		break;


	        	}

	        	first = false;
	        }
	        contentHandler.endElement(XMLConstants.NULL_NS_URI, rootName, "");
	        contentHandler.endDocument();
        } finally {

        	try {
        		jp.close();
        	} catch (Exception e) {
			}


        }
	}

    /**
	 * @param text
	 * @return
	 */
	private String getElementName(String text) {

		if(doKeyReplacement) {

			text = mapKey(text);

		} else {

			if(doKeyWhitspaceReplacement) {
				text = text.replace(" ", keyWhitspaceReplacement);
			}

			if(doPrefixOnNumericKey && Character.isDigit(text.charAt(0))) {
				text = keyPrefixOnNumeric + text;
			}

			if(doIllegalElementNameCharReplacement) {
				text = text.replaceAll("^[.]|[^a-zA-Z0-9_.-]", illegalElementNameCharReplacement);
			}

		}
		return text;
	}

	private String mapKey(String key) {

		String mappedKey = keyMap.get(key);
		if(mappedKey != null) {

			return mappedKey;

		} else {

			return key;

		}
	}

	/**
	 *
	 */
	private void initKeyMap() {
		Parameter keyMapParam = config.getParameter(CONFIG_PARAM_KEY_MAP);

       if (keyMapParam != null) {
           Element keyMapParamElement = keyMapParam.getXml();

           if(keyMapParamElement != null) {
               NodeList keys = keyMapParamElement.getElementsByTagName(KEY_MAP_KEY_ELEMENT);

               for (int i = 0; keys != null && i < keys.getLength(); i++) {
               	Element node = (Element)keys.item(i);

               	String name = DomUtils.getAttributeValue(node, KEY_MAP_KEY_ELEMENT_FROM_ATTRIBUTE);

               	if(StringUtils.isBlank(name)) {
               		throw new SmooksConfigurationException("The 'name' attribute isn't defined or is empty for the key name: " + node);
               	}
               	name = name.trim();

               	String value = DomUtils.getAttributeValue(node, KEY_MAP_KEY_ELEMENT_TO_ATTRIBUTE);
               	if(value == null) {
               		value = DomUtils.getAllText(node, true);
               		if(StringUtils.isBlank(value)) {
               			value = null;
               		}
               	}
               	keyMap.put(name, value);
               }

           } else {
           	logger.error("Sorry, the key properties must be available as XML DOM. Please configure using XML.");
           }
       }
	}

	public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    /****************************************************************************
     *
     * The following methods are currently unimplemnted...
     *
     ****************************************************************************/

    public void parse(String systemId) throws IOException, SAXException {
        throw new UnsupportedOperationException("Operation not supports by this reader.");
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        return false;
    }

    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
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

    public Object getProperty(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        return null;
    }

    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
    }
}
