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
package org.milyn.general;

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ExecutionContext;
import org.milyn.xml.CloneableReader;
import org.milyn.xml.SmooksXMLReader;
import org.milyn.delivery.sax.ReadOnlySAXHandler;
import org.milyn.delivery.sax.SAXElementVisitorMap;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.ext.DefaultHandler2;

import javax.xml.stream.*;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class StAX2SAXReader implements SmooksXMLReader, CloneableReader {

    private static XMLInputFactory staxInputFactory = XMLInputFactory.newInstance();
    private ExecutionContext executionContext;
    private ReadOnlySAXHandler contentHandler;
    private AttributesImpl attributes = new AttributesImpl();
    private int elementCount = 0;
    
    static {
    	staxInputFactory.setProperty(XMLInputFactory.IS_COALESCING, false);
    }

	public XMLReader cloneReader(ExecutionContext executionContext, DefaultHandler2 handler) {
		StAX2SAXReader clone = new StAX2SAXReader();
		clone.executionContext = executionContext;
		clone.setContentHandler(handler);
		return clone;
	}

    public void setExecutionContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return false;
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return null;
    }

    public void setContentHandler(ContentHandler contentHandler) {
    	if(contentHandler instanceof ReadOnlySAXHandler) {
    		this.contentHandler = (ReadOnlySAXHandler) contentHandler;
    	} else {
    		throw new SmooksConfigurationException("Sorry, the " + getClass().getSimpleName() + " can only be used with the SAX Filter.");
    	}
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void parse(InputSource input) throws IOException, SAXException {
        XMLStreamReader staxReader = openStAXReader(input);

        contentHandler.startDocument();
        try {
            moveToNextElement(staxReader);
            parseElement(staxReader, false);
        } catch (XMLStreamException e) {
            throw new SAXException("Error reading XML Stream.", e);
        } finally {
            contentHandler.endDocument();            
        }
    }

    private static Attributes emptyAttributes = new AttributesImpl();
    private void parseElement(XMLStreamReader staxReader, boolean forwardEvents) throws XMLStreamException, SAXException {
        QName name = staxReader.getName();
        SAXElementVisitorMap elementVisitorConfig;
        boolean isRoot = (elementCount == 0);
        Attributes attrs;

        elementVisitorConfig = contentHandler.getElementVisitorConfig(name.getLocalPart().toLowerCase(), isRoot);
        forwardEvents  = (forwardEvents || elementVisitorConfig != null);
        
        if(forwardEvents) {
        	attrs = getAttributes(staxReader);
        	contentHandler.startElement(name, attrs, elementVisitorConfig);
        }        
        
        elementCount++;

        while(staxReader.hasNext()) {
            staxReader.next();
            switch (staxReader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    parseElement(staxReader, forwardEvents);
                    
                    break;
                case XMLStreamConstants.CHARACTERS:
                	if(elementVisitorConfig != null && !elementVisitorConfig.isBlank) {
                		contentHandler.characters(staxReader.getTextCharacters(), staxReader.getTextStart(), staxReader.getTextLength());
                	}
                    break;
                case XMLStreamConstants.END_ELEMENT:
                	if(forwardEvents) {
	                    contentHandler.endElement();
                	}
                    return;
            }
        }
    }

    private void moveToNextElement(XMLStreamReader staxReader) throws XMLStreamException {
        while(staxReader.hasNext()) {
            staxReader.next();
            switch (staxReader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    return;
            }
        }
    }

    private Attributes getAttributes(XMLStreamReader staxReader) {
        int attributeCount = staxReader.getAttributeCount();

        attributes.clear();
        for(int i = 0; i < attributeCount; i++) {
            String localPart = staxReader.getAttributeLocalName(i);
            String nsPrefix = staxReader.getAttributePrefix(i);

            if(nsPrefix != null) {
                attributes.addAttribute(staxReader.getAttributeNamespace(i), localPart, nsPrefix + ":" + localPart, null, staxReader.getAttributeValue(i));
            } else {
                attributes.addAttribute(staxReader.getAttributeNamespace(i), localPart, localPart, null, staxReader.getAttributeValue(i));
            }
        }

        return attributes;
    }

    private XMLStreamReader openStAXReader(InputSource input) throws SAXException, IOException {
        XMLStreamReader staxReader;

        Reader inputReader = input.getCharacterStream();
        if (inputReader != null) {
            try {
                staxReader = staxInputFactory.createXMLStreamReader(inputReader);
            } catch (XMLStreamException e) {
                throw new SAXException("Failed to create " + XMLStreamReader.class.getName() + " instance from " + inputReader.getClass().getName() + ".", e);
            }
        } else {
            InputStream inputStream = input.getByteStream();
            if (inputStream != null) {
                try {
                    staxReader = staxInputFactory.createXMLStreamReader(inputStream, executionContext.getContentEncoding());
                } catch (XMLStreamException e) {
                    throw new SAXException("Failed to create " + XMLStreamReader.class.getName() + " instance from " + inputStream.getClass().getName() + ".", e);
                }
            } else {
                throw new IOException("Invalid 'input' InputSource.  Not Character or Byte Stream set.");
            }
        }

        return staxReader;
    }

	/* (non-Javadoc)
	 * @see org.xml.sax.XMLReader#getDTDHandler()
	 */
	public DTDHandler getDTDHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.XMLReader#getEntityResolver()
	 */
	public EntityResolver getEntityResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.XMLReader#getErrorHandler()
	 */
	public ErrorHandler getErrorHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.XMLReader#parse(java.lang.String)
	 */
	public void parse(String systemId) throws IOException, SAXException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.XMLReader#setDTDHandler(org.xml.sax.DTDHandler)
	 */
	public void setDTDHandler(DTDHandler handler) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.XMLReader#setEntityResolver(org.xml.sax.EntityResolver)
	 */
	public void setEntityResolver(EntityResolver resolver) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)
	 */
	public void setErrorHandler(ErrorHandler handler) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.XMLReader#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		// TODO Auto-generated method stub
		
	}
}
