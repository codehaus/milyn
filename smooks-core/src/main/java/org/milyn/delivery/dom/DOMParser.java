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

package org.milyn.delivery.dom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.Parameter;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.dom.DOMBuilder;
import org.milyn.xml.SmooksXMLReader;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Smooks DOM data stream parser.
 * <p/>
 * This parser can be configured to use a SAX Parser targeted at a specific data stream type.
 * This lets you parse a stream of any type, convert it to a stream of SAX event and so treat the stream
 * as an XML data stream, even when the stream is non-XML.
 * <p/>
 * If the configured parser implements the {@link org.milyn.xml.SmooksXMLReader}, the configuration will be
 * passed to the parser via {@link org.milyn.cdr.annotation.ConfigParam} annotaions on config properties
 * defined on the implementation.
 * 
 * <h3 id="parserconfig">.cdrl Configuration</h3>
 * <pre>
 * &lt;smooks-resource selector="org.xml.sax.driver" path="org.milyn.protocolx.XParser" &gt;
 * 	&lt;!-- 
 * 		Optional list of driver parameters for {@link org.milyn.xml.SmooksXMLReader} implementations.
 * 		See {@link org.milyn.cdr.SmooksResourceConfiguration} for how to add configuration parameters. 
 * 	--&gt;
 * &lt;/smooks-resource&gt;
 * </pre>
 * 
 * @author tfennelly
 */
public class DOMParser {

	private static Log logger = LogFactory.getLog(DOMParser.class);
    private ExecutionContext execContext;
    private SmooksResourceConfiguration saxDriverConfig;

    /**
     * Default constructor.
     */
	public DOMParser() {
	}

	/**
	 * Public constructor.
	 * <p/>
	 * This constructor attempts to lookup a SAX Parser config under the "org.xml.sax.driver" selector string.
	 * See <a href="#parserconfig">.cdrl Configuration</a>.
	 * @param execContext The execution context that the parser is being instantiated on behalf of.
	 */
	public DOMParser(ExecutionContext execContext) {
		if(execContext == null) {
			throw new IllegalArgumentException("null 'request' arg in method call.");
		}
		this.execContext = execContext;
        
        // Allow the sax driver to be specified as a resourcec config (under selector "org.xml.sax.driver").
		saxDriverConfig = getSAXParserConfiguration(execContext.getDeliveryConfig());
	}
    
	/**
	 * Public constructor.
	 * @param execContext The Smooks Container Request that the parser is being instantiated on behalf of.
	 * @param saxDriverConfig SAX Parser configuration. See <a href="#parserconfig">.cdrl Configuration</a>.
	 */
    public DOMParser(ExecutionContext execContext, SmooksResourceConfiguration saxDriverConfig) {
        this(execContext);
        this.saxDriverConfig = saxDriverConfig;
    }
    
    /**
     * Get the SAX Parser configuration for the profile associated with the supplied delivery configuration.
     * @param deliveryConfig Content delivery configuration.
     * @return Returns the SAX Parser configuration for the profile associated with the supplied delivery 
     * configuration, or null if no parser configuration is specified.
     */
    public static SmooksResourceConfiguration getSAXParserConfiguration(ContentDeliveryConfig deliveryConfig) {
    	if(deliveryConfig == null) {
    		throw new IllegalArgumentException("null 'deliveryConfig' arg in method call.");
    	}
    	
    	SmooksResourceConfiguration saxDriverConfig = null;
        List saxConfigs = deliveryConfig.getSmooksResourceConfigurations("org.xml.sax.driver");
        
        if(saxConfigs != null && !saxConfigs.isEmpty()) {
            saxDriverConfig = (SmooksResourceConfiguration)saxConfigs.get(0);
        }
        
        return saxDriverConfig;
    }

	/**
	 * Document parser.
	 * @param source Source content stream to be parsed.
	 * @return W3C ownerDocument.
	 * @throws SAXException Unable to parse the content.
	 * @throws IOException Unable to read the input stream.
	 */
	public Document parse(Reader source) throws IOException, SAXException {
	   	DOMBuilder contentHandler = new DOMBuilder(execContext);
		
	   	parse(source, contentHandler);
		
		return contentHandler.getDocument();
	}

	/**
	 * Append the content, behind the supplied input stream, to suplied
	 * document element.
	 * <p/>
	 * Used to merge document fragments into a document.
	 * @param source Source content stream to be parsed.
	 * @param appendElement DOM element to which the content fragment is to 
	 * be added.
	 * @throws SAXException Unable to parse the content.
	 * @throws IOException Unable to read the input stream.
	 */
	public void append(Reader source, Element appendElement) throws IOException, SAXException {
	   	DOMBuilder contentHandler = new DOMBuilder(execContext);
		
		contentHandler.setAppendElement(appendElement);
	   	parse(source, contentHandler);
	}
	
	/**
	 * Perform the actual parse into the supplied content handler.
	 * @param source Source content stream to be parsed.
	 * @param contentHandler Content handler instance that will build/append-to the DOM.
	 * @throws SAXException Unable to parse the content.
	 * @throws IOException Unable to read the input stream.
	 */
	private void parse(Reader source, DOMBuilder contentHandler) throws SAXException, IOException {
        XMLReader reader;
        
        if(saxDriverConfig != null) {
            String className = saxDriverConfig.getResource();

            if(className != null) {
                reader = XMLReaderFactory.createXMLReader(className);
            } else {
                reader = XMLReaderFactory.createXMLReader();
            }
            if(reader instanceof SmooksXMLReader) {
            	Configurator.configure((SmooksXMLReader)reader, saxDriverConfig);
            	((SmooksXMLReader)reader).setExecutionContext(execContext);
            }
        } else {
            reader = XMLReaderFactory.createXMLReader();
        }

		reader.setContentHandler(contentHandler);
		reader.setProperty("http://xml.org/sax/properties/lexical-handler", contentHandler);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);

        setImplSpecificFeatures(reader, saxDriverConfig);

        reader.parse(new InputSource(source));
	}

    private void setImplSpecificFeatures(XMLReader reader, SmooksResourceConfiguration saxDriverConfig) throws SAXNotSupportedException, SAXNotRecognizedException {
        // Try setting the xerces "notify-char-refs" feature, may fail if it's not Xerces but that's OK...
        try {
            reader.setFeature("http://apache.org/xml/features/scanner/notify-char-refs", true);
        } catch(Throwable t) {
            // Ignore
        }

        if(saxDriverConfig != null) {
            List<Parameter> features;

            features = saxDriverConfig.getParameters("feature-on");
            if(features != null) {
                for (Parameter feature : features) {
                    reader.setFeature(feature.getValue(), true);
                }
            }

            features = saxDriverConfig.getParameters("feature-off");
            if(features != null) {
                for (Parameter feature : features) {
                    reader.setFeature(feature.getValue(), false);
                }
            }
        }
    }

}
