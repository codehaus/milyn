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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.Parameter;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaSource;
import org.milyn.payload.FilterSource;
import org.milyn.delivery.java.JavaXMLReader;
import org.milyn.delivery.java.XStreamXMLReader;
import org.milyn.util.ClassUtil;
import org.milyn.xml.NullSourceXMLReader;
import org.milyn.xml.SmooksXMLReader;
import org.xml.sax.*;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.Source;
import java.util.List;

/**
 * Abstract Parser.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class AbstractParser {
    
    private static Log logger = LogFactory.getLog(AbstractParser.class);
    
    private ExecutionContext execContext;
    private SmooksResourceConfiguration saxDriverConfig;
    public static final String ORG_XML_SAX_DRIVER = "org.xml.sax.driver";
    public static final String FEATURE_ON = "feature-on";
    public static final String FEATURE_OFF = "feature-off";

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
        List<SmooksResourceConfiguration> saxConfigs = deliveryConfig.getSmooksResourceConfigurations(ORG_XML_SAX_DRIVER);

        if(saxConfigs != null && !saxConfigs.isEmpty()) {
            saxDriverConfig = saxConfigs.get(0);
        }

        return saxDriverConfig;
    }

    protected XMLReader createXMLReader(DefaultHandler2 handler) throws SAXException {
        XMLReader reader;
        ExecutionContext execContext = getExecContext();
        Source source = FilterSource.getSource(execContext);

        if(saxDriverConfig != null && saxDriverConfig.getResource() != null) {
            String className = saxDriverConfig.getResource();

            reader = XMLReaderFactory.createXMLReader(className);
        } else if(source instanceof JavaSource) {
            JavaSource javaSource = (JavaSource) source;

            if(isFeatureOn(JavaSource.FEATURE_GENERATE_EVENT_STREAM, saxDriverConfig) && !javaSource.isEventStreamRequired()) {
                throw new SAXException("Invalid Smooks configuration.  Feature '" + JavaSource.FEATURE_GENERATE_EVENT_STREAM + "' is explicitly configured 'on' in the Smooks configuration, while the supplied JavaSource has explicitly configured event streaming to be off (through a call to JavaSource.setEventStreamRequired).");
            }

            // Event streaming must be explicitly turned off.  If is on as long as it is (a) not configured "off" in
            // the smooks config (via the reader features) and (b) not turned off via the supplied JavaSource...
            boolean eventStreamingOn = (!isFeatureOff(JavaSource.FEATURE_GENERATE_EVENT_STREAM, saxDriverConfig) && javaSource.isEventStreamRequired());
            if(eventStreamingOn && javaSource.getSourceObjects() != null) {
                reader = new XStreamXMLReader();
            } else {
                reader = new NullSourceXMLReader();
            }
        } else {
            reader = XMLReaderFactory.createXMLReader();
        }

        if(reader instanceof SmooksXMLReader) {
            if(saxDriverConfig != null) {
                Configurator.configure((SmooksXMLReader)reader, saxDriverConfig, execContext.getContext());
            }
            ((SmooksXMLReader)reader).setExecutionContext(execContext);
        }

        if(reader instanceof JavaXMLReader) {
            if(!(source instanceof JavaSource)) {
                throw new SAXException("A " + JavaSource.class.getName() + " source must be supplied for " + JavaXMLReader.class.getName() + " implementations.");
            }
            ((JavaXMLReader)reader).setSourceObjects(((JavaSource)source).getSourceObjects());
        }

        reader.setContentHandler(handler);
        try {
            reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        } catch (SAXNotRecognizedException e) {
            logger.debug("XMLReader property 'http://xml.org/sax/properties/lexical-handler' not recognized by XMLReader '" + reader.getClass().getName() + "'.");
        }
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);

        setHandlers(reader);
        setFeatures(reader);

        return reader;
    }

    private void setHandlers(XMLReader reader) throws SAXException {
        if(saxDriverConfig != null) {
            List<Parameter> handlers;

            handlers = saxDriverConfig.getParameters("sax-handler");
            if(handlers != null) {
                for (Parameter handler : handlers) {
                    Object handlerObj = createHandler(handler.getValue());

                    if(handlerObj instanceof EntityResolver) {
                        reader.setEntityResolver((EntityResolver) handlerObj);
                    }
                    if(handlerObj instanceof DTDHandler) {
                        reader.setDTDHandler((DTDHandler) handlerObj);
                    }
                    if(handlerObj instanceof ErrorHandler) {
                        reader.setErrorHandler((ErrorHandler) handlerObj);
                    }
                }
            }
        }
    }

    private Object createHandler(String handlerName) throws SAXException {
        try {
            Class handlerClass = ClassUtil.forName(handlerName, getClass());
            return handlerClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new SAXException("Failed to create SAX Handler '" + handlerName + "'.", e);
        } catch (IllegalAccessException e) {
            throw new SAXException("Failed to create SAX Handler '" + handlerName + "'.", e);
        } catch (InstantiationException e) {
            throw new SAXException("Failed to create SAX Handler '" + handlerName + "'.", e);
        }
    }

    private void setFeatures(XMLReader reader) throws SAXNotSupportedException, SAXNotRecognizedException {
        // Try setting the xerces "notify-char-refs" feature, may fail if it's not Xerces but that's OK...
        try {
            reader.setFeature("http://apache.org/xml/features/scanner/notify-char-refs", true);
        } catch(Throwable t) {
            // Ignore
        }

        if(saxDriverConfig != null) {
            List<Parameter> features;

            features = saxDriverConfig.getParameters(FEATURE_ON);
            if(features != null) {
                for (Parameter feature : features) {
                    reader.setFeature(feature.getValue(), true);
                }
            }

            features = saxDriverConfig.getParameters(FEATURE_OFF);
            if(features != null) {
                for (Parameter feature : features) {
                    reader.setFeature(feature.getValue(), false);
                }
            }
        }
    }

    public static boolean isFeatureOn(String name, SmooksResourceConfiguration saxDriverConfig) throws SAXException {
        boolean featureOn = isFeature(name, FeatureValue.ON, saxDriverConfig);

        // Make sure the same feature is not also configured off...
        if(featureOn && isFeature(name, FeatureValue.OFF, saxDriverConfig)) {
            throw new SAXException("Invalid Smooks configuration.  Feature '" + name + "' is explicitly configured 'on' and 'off'.  Must be one or the other!");
        }

        return featureOn;
    }

    public static boolean isFeatureOff(String name, SmooksResourceConfiguration saxDriverConfig) throws SAXException {
        boolean featureOff = isFeature(name, FeatureValue.OFF, saxDriverConfig);

        // Make sure the same feature is not also configured on...
        if(featureOff && isFeature(name, FeatureValue.ON, saxDriverConfig)) {
            throw new SAXException("Invalid Smooks configuration.  Feature '" + name + "' is explicitly configured 'on' and 'off'.  Must be one or the other!");
        }

        return featureOff;
    }

    private static enum FeatureValue {
        ON,
        OFF;
    }

    private static boolean isFeature(String name, FeatureValue featureValue, SmooksResourceConfiguration saxDriverConfig) {
        if(saxDriverConfig != null) {
            List<Parameter> features;

            if(featureValue == FeatureValue.ON) {
                features = saxDriverConfig.getParameters(FEATURE_ON);
            } else {
                features = saxDriverConfig.getParameters(FEATURE_OFF);
            }
            if(features != null) {
                for (Parameter feature : features) {
                    if(feature.getValue().equals(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
                                                    