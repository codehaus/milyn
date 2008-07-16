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

package org.milyn.cdr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.expression.ExpressionEvaluator;
import org.milyn.io.StreamUtils;
import org.milyn.net.URIUtil;
import org.milyn.profile.DefaultProfileSet;
import org.milyn.resource.URIResourceLocator;
import org.milyn.util.ClassUtil;
import org.milyn.xml.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Digester class for an XML {@link org.milyn.cdr.SmooksResourceConfiguration} file (.cdrl).
 *
 * @author tfennelly
 */
public final class XMLConfigDigester {

    public static final String DTD_V10 = "http://www.milyn.org/dtd/smooksres-list-1.0.dtd";
    public static final String XSD_V10 = "http://www.milyn.org/xsd/smooks-1.0.xsd";
    public static final String XSD_V11 = "http://www.milyn.org/xsd/smooks-1.1.xsd";

    private static Log logger = LogFactory.getLog(XMLConfigDigester.class);

    /**
     * Digest the XML Smooks configuration stream.
     *
     * @param stream  The stream.
     * @param baseURI The base URI to be associated with the configuration stream.
     * @return A {@link SmooksResourceConfigurationList} containing the list of
     *         {@link SmooksResourceConfiguration SmooksResourceConfigurations} defined in the
     *         XML configuration.
     * @throws SAXException Error parsing the XML stream.
     * @throws IOException  Error reading the XML stream.
     * @throws SmooksConfigurationException  Invalid configuration..
     */
    public static SmooksResourceConfigurationList digestConfig(InputStream stream, String baseURI) throws SAXException, IOException, URISyntaxException, SmooksConfigurationException {
        SmooksResourceConfigurationList list = new SmooksResourceConfigurationList(baseURI);

        digestConfig(stream, list, baseURI);

        return list;
    }

    private static void digestConfig(InputStream stream, SmooksResourceConfigurationList list, String baseURI) throws IOException, SAXException, URISyntaxException, SmooksConfigurationException {
        LocalEntityResolver entityResolver;
        Document configDoc;
        String docType;
        byte[] streamBuffer = StreamUtils.readStream(stream);

        try {
            entityResolver = getDTDEntityResolver();
            configDoc = XmlUtil.parseStream(new ByteArrayInputStream(streamBuffer), entityResolver, XmlUtil.VALIDATION_TYPE.DTD, true);
        } catch (Exception e) {
            entityResolver = getXSDEntityResolver();
            configDoc = XmlUtil.parseStream(new ByteArrayInputStream(streamBuffer), entityResolver, XmlUtil.VALIDATION_TYPE.XSD, true);
        }
        docType = entityResolver.getDocType();

        if (DTD_V10.equals(docType)) {
            logger.warn("Using a deprecated Smooks configuration DTD '" + DTD_V10 + "'.  Update configuration to use XSD '" + XSD_V10 + "'.");
            digestV10DTDValidatedConfig(configDoc, list);
            logger.warn("Using a deprecated Smooks configuration DTD '" + DTD_V10 + "'.  Update configuration to use XSD '" + XSD_V10 + "'.");
        } else if (XSD_V10.equals(docType)) {
            digestV10XSDValidatedConfig(baseURI, configDoc, list);
        } else {
            throw new SAXException("Invalid Content Delivery Resource archive definition file: Must be validated against one of - '" + DTD_V10 + "' or '" + XSD_V10 + "'.");
        }

        if (list.isEmpty()) {
            throw new SAXException("Invalid Content Delivery Resource archive definition file: 0 Content Delivery Resource definitions.");
        }
    }

    private static LocalEntityResolver getDTDEntityResolver() {
        return new LocalDTDEntityResolver();
    }

    private static LocalEntityResolver getXSDEntityResolver() {
        Source schemaSource = new StreamSource(ClassUtil.getResourceAsStream("/org/milyn/xsd/smooks-1.0.xsd", XMLConfigDigester.class), XSD_V10);
        return new LocalXSDEntityResolver(new Source[]{schemaSource});
    }

    private static void digestV10DTDValidatedConfig(Document configDoc, SmooksResourceConfigurationList list) throws SAXException {
        int cdrIndex = 1;
        Element currentElement;
        String resourceSelector;

        currentElement = (Element) XmlUtil.getNode(configDoc, "/smooks-resource-list");
        String defaultSelector = DomUtils.getAttributeValue(currentElement, "default-selector");
        String defaultNamespace = DomUtils.getAttributeValue(currentElement, "default-namespace");
        String defaultUseragent = DomUtils.getAttributeValue(currentElement, "default-useragent");
        String defaultPath = DomUtils.getAttributeValue(currentElement, "default-path");

        resourceSelector = "/smooks-resource-list/smooks-resource[" + cdrIndex + "]";
        while ((currentElement = (Element) XmlUtil.getNode(configDoc, resourceSelector)) != null) {
            String selector = DomUtils.getAttributeValue(currentElement, "selector");
            String namespace = DomUtils.getAttributeValue(currentElement, "namespace");
            String useragents = DomUtils.getAttributeValue(currentElement, "useragent");
            String path = DomUtils.getAttributeValue(currentElement, "path");
            SmooksResourceConfiguration resourceConfig;

            try {
                resourceConfig = new SmooksResourceConfiguration((selector != null ? selector : defaultSelector),
                        (namespace != null ? namespace : defaultNamespace),
                        (useragents != null ? useragents : defaultUseragent),
                        (path != null ? path : defaultPath));
            } catch (IllegalArgumentException e) {
                throw new SAXException("Invalid unit definition.", e);
            }

            // Add the parameters...
            digestParameters(currentElement, resourceConfig);

            list.add(resourceConfig);
            if (logger.isDebugEnabled()) {
                logger.debug("Adding smooks-resource config from [" + list.getName() + "]: " + resourceConfig);
            }

            cdrIndex++;
            resourceSelector = "/smooks-resource-list/smooks-resource[" + cdrIndex + "]";
        }
    }

    private static void digestV10XSDValidatedConfig(String baseURI, Document configDoc, SmooksResourceConfigurationList list) throws SAXException, URISyntaxException, SmooksConfigurationException {
        Element currentElement;

        currentElement = (Element) XmlUtil.getNode(configDoc, "/smooks-resource-list");
        String defaultSelector = DomUtils.getAttributeValue(currentElement, "default-selector");
        String defaultNamespace = DomUtils.getAttributeValue(currentElement, "default-selector-namespace");
        String defaultProfile = DomUtils.getAttributeValue(currentElement, "default-target-profile");

        NodeList configElements = XmlUtil.getNodeList(currentElement, "*");

        for (int i = 0; i < configElements.getLength(); i++) {
            Element configElement = (Element) configElements.item(i);

            if (DomUtils.getName(configElement).equals("profiles")) {
                digestProfiles(configElement, list);
            } else if (DomUtils.getName(configElement).equals("import")) {
                digestImport(configElement, new URI(baseURI), list);
            } else if (DomUtils.getName(configElement).equals("resource-config")) {
                digestResourceConfig(configElement, defaultSelector, defaultNamespace, defaultProfile, list);
            }
        }
    }

    private static void digestImport(Element configElement, URI baseURI, SmooksResourceConfigurationList list) throws SAXException, URISyntaxException, SmooksConfigurationException {
        String file = DomUtils.getAttributeValue(configElement, "file");
        URIResourceLocator resourceLocator;
        InputStream resourceStream;

        if (file == null) {
            throw new IllegalStateException("Invalid resource import.  'file' attribute must be specified.");
        }

        resourceLocator = new URIResourceLocator();
        resourceLocator.setBaseURI(baseURI);

        try {
            URI fileURI = resourceLocator.resolveURI(file);

            // Add the resource URI to the list.  Will fail if it was already loaded
            if(list.addSourceResourceURI(fileURI)) {
                resourceStream = resourceLocator.getResource(file);
                digestConfig(resourceStream, list, URIUtil.getParent(fileURI).toString()); // the file's parent URI becomes the new base URI.
            }
        } catch (IOException e) {
            throw new SmooksConfigurationException("Failed to load Smooks configuration resource <import> '" + file + "': " + e.getMessage(), e);
        }
    }

    private static void digestResourceConfig(Element configElement, String defaultSelector, String defaultNamespace, String defaultProfile, SmooksResourceConfigurationList list) throws SAXException {
        String selector = DomUtils.getAttributeValue(configElement, "selector");
        String namespace = DomUtils.getAttributeValue(configElement, "selector-namespace");
        String profiles = DomUtils.getAttributeValue(configElement, "target-profile");
        Element resourceElement = (Element) XmlUtil.getNode(configElement, "resource");
        Element conditionElement = (Element) XmlUtil.getNode(configElement, "condition");
        String resource = null;
        SmooksResourceConfiguration resourceConfig;

        try {
            if (resourceElement != null) {
                resource = DomUtils.getAllText(resourceElement, true);
            }
            resourceConfig = new SmooksResourceConfiguration((selector != null ? selector : defaultSelector),
                    (namespace != null ? namespace : defaultNamespace),
                    (profiles != null ? profiles : defaultProfile),
                    resource);
            if (resourceElement != null) {
                resourceConfig.setResourceType(DomUtils.getAttributeValue(resourceElement, "type"));
            }

            // And add the condition, if defined...
            if(conditionElement != null) {
                String evaluatorClassName = conditionElement.getAttribute("evaluator");
                if(evaluatorClassName == null || evaluatorClassName.trim().equals("")) {
                    evaluatorClassName = "org.milyn.javabean.expression.BeanMapExpressionEvaluator";
                }

                String evaluatorConditionExpression = DomUtils.getAllText(conditionElement, true);
                if(evaluatorConditionExpression == null || evaluatorConditionExpression.trim().equals("")) {
                    throw new SAXException("smooks-resource/condition must specify a condition expression as child text e.g. <condition evaluator=\"....\">A + B > C</condition>.");
                }

                // And construct it...
                ExpressionEvaluator evaluator = ExpressionEvaluator.Factory.createInstance(evaluatorClassName, evaluatorConditionExpression);

                // We have a valid ExpressionEvaluator, so set it on the resource...
                resourceConfig.setConditionEvaluator(evaluator);
            }
        } catch (IllegalArgumentException e) {
            throw new SAXException("Invalid unit definition.", e);
        }

        // Add the parameters...
        digestParameters(configElement, resourceConfig);

        list.add(resourceConfig);
        if (resource == null) {
            if (resourceConfig.getParameters(SmooksResourceConfiguration.PARAM_RESDATA) != null) {
                logger.warn("Resource 'null' for resource config: " + resourceConfig + ".  This is probably an error because the configuration contains a 'resdata' param, which suggests it is following the old DTD based configuration model.  The new model requires the resource to be specified in the <resource> element.");
            } else {
                logger.debug("Resource 'null' for resource config: " + resourceConfig + ". This is not invalid!");
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Adding smooks-resource config from [" + list.getName() + "]: " + resourceConfig);
        }
    }

    private static void digestProfiles(Element profilesElement, SmooksResourceConfigurationList list) {
        int profileIndex = 1;
        Element profileNode;
        String profileSelector = null;

        profileSelector = "profile[" + profileIndex + "]";
        while ((profileNode = (Element) XmlUtil.getNode(profilesElement, profileSelector)) != null) {
            String baseProfile = DomUtils.getAttributeValue(profileNode, "base-profile");
            String subProfiles = DomUtils.getAttributeValue(profileNode, "sub-profiles");
            DefaultProfileSet profileSet = new DefaultProfileSet(baseProfile);

            if (subProfiles != null) {
                profileSet.addProfiles(subProfiles.split(","));
            }

            list.add(profileSet);
            profileIndex++;
            profileSelector = "profile[" + profileIndex + "]";
        }
    }

    private static void digestParameters(Element resourceConfigElement, SmooksResourceConfiguration resourceConfig) {
        int paramIndex = 1;
        Element paramNode;
        String paramSelector = null;

        paramSelector = "param[" + paramIndex + "]";
        while ((paramNode = (Element) XmlUtil.getNode(resourceConfigElement, paramSelector)) != null) {
            String paramName = DomUtils.getAttributeValue(paramNode, "name");
            String paramType = DomUtils.getAttributeValue(paramNode, "type");
            String paramValue = DomUtils.getAllText(paramNode, true);
            Parameter paramInstance;

            paramInstance = resourceConfig.setParameter(paramName, paramType, paramValue);
            paramInstance.setXML(paramNode);
            paramIndex++;
            paramSelector = "param[" + paramIndex + "]";
        }
    }
}
