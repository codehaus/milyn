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
package org.milyn.delivery;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.Parameter;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.xml.SmooksXMLReader;
import org.milyn.container.ExecutionContext;
import org.milyn.assertion.AssertArgument;
import org.xml.sax.*;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

import java.util.List;

/**
 * Abstract Parser.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class AbstractParser {
    
    private ExecutionContext execContext;
    private SmooksResourceConfiguration saxDriverConfig;

    /**
     * Public constructor.
     * @param execContext The Smooks Container Request that the parser is being instantiated on behalf of.
     * @param saxDriverConfig SAX Parser configuration. See <a href="#parserconfig">.cdrl Configuration</a>.
     */
    public AbstractParser(ExecutionContext execContext, SmooksResourceConfiguration saxDriverConfig) {
        AssertArgument.isNotNull(execContext, "execContext");
        this.execContext = execContext;
        this.saxDriverConfig = saxDriverConfig;
    }

    public AbstractParser(ExecutionContext execContext) {
        this(execContext, getSAXParserConfiguration(execContext.getDeliveryConfig()));
    }

    protected ExecutionContext getExecContext() {
        return execContext;
    }

    protected SmooksResourceConfiguration getSaxDriverConfig() {
        return saxDriverConfig;
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

    protected XMLReader createXMLReader(DefaultHandler2 handler) throws SAXException {
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

        reader.setContentHandler(handler);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);

        setImplSpecificFeatures(reader, saxDriverConfig);

        return reader;
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
