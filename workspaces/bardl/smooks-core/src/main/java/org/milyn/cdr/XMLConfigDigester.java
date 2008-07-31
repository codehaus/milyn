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
import org.milyn.xml.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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

    private SmooksResourceConfigurationList list;
    private Stack<SmooksConfig> configStack = new Stack<SmooksConfig>();
    private String currentSchema;

    private XMLConfigDigester(SmooksResourceConfigurationList list) {
        this.list = list;
        configStack.push(new SmooksConfig("root-config"));
    }

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

        XMLConfigDigester digester = new XMLConfigDigester(list);

        digester.digestConfigRecursively(stream, baseURI);

        return list;
    }

    private void digestConfigRecursively(InputStream stream, String baseURI) throws IOException, SAXException, URISyntaxException, SmooksConfigurationException {
        Document configDoc;
        byte[] streamBuffer = StreamUtils.readStream(stream);

        try {
            configDoc = XmlUtil.parseStream(new ByteArrayInputStream(streamBuffer), getDTDEntityResolver(), XmlUtil.VALIDATION_TYPE.DTD, true);
            logger.warn("Using a deprecated Smooks configuration DTD '" + DTD_V10 + "'.  Update configuration to use XSD '" + XSD_V10 + "'.");
            digestV10DTDValidatedConfig(configDoc);
            logger.warn("Using a deprecated Smooks configuration DTD '" + DTD_V10 + "'.  Update configuration to use XSD '" + XSD_V10 + "'.");
        } catch (Exception e) {
            // Must be an XSD based config...
            try {
                configDoc = XmlUtil.parseStream(new ByteArrayInputStream(streamBuffer));
            } catch (ParserConfigurationException ee) {
                throw new SAXException("Unable to parse Smooks configuration.", ee);
            }

            XsdDOMValidator validator = new XsdDOMValidator(configDoc);
            String defaultNS = validator.getDefaultNamespace().toString();

            validator.validate();
            if(XSD_V10.equals(defaultNS)) {
                if(validator.getNamespaces().size() > 1) {
                    throw new SmooksConfigurationException("Unsupported use of multiple configuration namespaces from inside a v1.0 Smooks configuration. Configuration extension not supported from a v1.0 configuration.  Use the v1.1 configuration namespace.");
                }
                digestV10XSDValidatedConfig(baseURI, configDoc);
            } else if(XSD_V11.equals(defaultNS)) {
                digestV11XSDValidatedConfig(baseURI, configDoc);
            } else {
                throw new SAXException("Cannot parse Smooks configuration.  Unsupported default Namespace '" + defaultNS + "'.");
            }
        }

        if (list.isEmpty()) {
            throw new SAXException("Invalid Content Delivery Resource archive definition file: 0 Content Delivery Resource definitions.");
        }
    }

    private void digestV10DTDValidatedConfig(Document configDoc) throws SAXException {
        int cdrIndex = 1;
        Element currentElement;
        String resourceSelector;

        currentSchema = DTD_V10;

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

    private void digestV10XSDValidatedConfig(String baseURI, Document configDoc) throws SAXException, URISyntaxException, SmooksConfigurationException {
        Element currentElement = configDoc.getDocumentElement();

        currentSchema = XSD_V10;

        String defaultSelector = DomUtils.getAttributeValue(currentElement, "default-selector");
        String defaultNamespace = DomUtils.getAttributeValue(currentElement, "default-selector-namespace");
        String defaultProfile = DomUtils.getAttributeValue(currentElement, "default-target-profile");

        NodeList configNodes = currentElement.getChildNodes();

        for (int i = 0; i < configNodes.getLength(); i++) {
            if(configNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element configElement = (Element) configNodes.item(i);

                if (DomUtils.getName(configElement).equals("profiles")) {
                    digestProfiles(configElement);
                } else if (DomUtils.getName(configElement).equals("import")) {
                    digestImport(configElement, new URI(baseURI));
                } else if (DomUtils.getName(configElement).equals("resource-config")) {
                    digestResourceConfig(configElement, defaultSelector, defaultNamespace, defaultProfile, null);
                }
            }
        }
    }

    private void digestV11XSDValidatedConfig(String baseURI, Document configDoc) throws SAXException, URISyntaxException, SmooksConfigurationException {
        Element currentElement = configDoc.getDocumentElement();

        if(currentSchema == XSD_V10) {
            // This must be an import of a v1.1 schema based config from inside a v1.0 schema config.  Not allowed!!
            throw new SmooksConfigurationException("Unsupported import of a v1.1 configuration from inside a v1.0 configuration.  Path to configuration: '" + getCurrentPath() + "'.");
        }

        currentSchema = XSD_V11;

        String defaultSelector = DomUtils.getAttributeValue(currentElement, "default-selector");
        String defaultNamespace = DomUtils.getAttributeValue(currentElement, "default-selector-namespace");
        String defaultProfile = DomUtils.getAttributeValue(currentElement, "default-target-profile");
        String defaultConditionRef = DomUtils.getAttributeValue(currentElement, "default-condition-ref");

        NodeList configNodes = currentElement.getChildNodes();

        for (int i = 0; i < configNodes.getLength(); i++) {
            if(configNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element configElement = (Element) configNodes.item(i);

                if (DomUtils.getName(configElement).equals("condition")) {
                    String id = DomUtils.getAttributeValue(configElement, "id");
                    if(id != null) {
                        addConditionEvaluator(id, digestCondition(configElement));
                    }
                } else if (DomUtils.getName(configElement).equals("profiles")) {
                    digestProfiles(configElement);
                } else if (DomUtils.getName(configElement).equals("import")) {
                    digestImport(configElement, new URI(baseURI));
                } else if (DomUtils.getName(configElement).equals("resource-config")) {
                    digestResourceConfig(configElement, defaultSelector, defaultNamespace, defaultProfile, defaultConditionRef);
                }
            }
        }
    }

    private void digestImport(Element configElement, URI baseURI) throws SAXException, URISyntaxException, SmooksConfigurationException {
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
                if (logger.isDebugEnabled()) {
                    logger.debug("Importing resource configuration '" + file + "' from inside '" + configStack.peek().configFile + "'.");
                }

                resourceStream = resourceLocator.getResource(file);
                pushConfig(file);
                digestConfigRecursively(resourceStream, URIUtil.getParent(fileURI).toString()); // the file's parent URI becomes the new base URI.
                popConfig();
            }
        } catch (IOException e) {
            throw new SmooksConfigurationException("Failed to load Smooks configuration resource <import> '" + file + "': " + e.getMessage(), e);
        }
    }

    private void digestResourceConfig(Element configElement, String defaultSelector, String defaultNamespace, String defaultProfile, String defaultConditionRef) throws SAXException {
        String selector = DomUtils.getAttributeValue(configElement, "selector");
        String namespace = DomUtils.getAttributeValue(configElement, "selector-namespace");
        String profiles = DomUtils.getAttributeValue(configElement, "target-profile");
        Element resourceElement = getElementByTagName(configElement, "resource");
        Element conditionElement = getElementByTagName(configElement, "condition");
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
                ExpressionEvaluator evaluator = digestCondition(conditionElement);
                resourceConfig.setConditionEvaluator(evaluator);
            } else if(defaultConditionRef != null) {
                ExpressionEvaluator evaluator = getConditionEvaluator(defaultConditionRef);
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

    private static Element getElementByTagName(Element configElement, String name) {
        NodeList elements = configElement.getElementsByTagName(name);
        if(elements.getLength() != 0) {
            return (Element) elements.item(0);
        }
        return null;
    }

    private ExpressionEvaluator digestCondition(Element conditionElement) throws SAXException {
        String idRef = DomUtils.getAttributeValue(conditionElement, "idRef");

        if(idRef != null) {
            return getConditionEvaluator(idRef);
        } else {
            String evaluatorClassName = DomUtils.getAttributeValue(conditionElement, "evaluator");

            if(evaluatorClassName == null || evaluatorClassName.trim().equals("")) {
                evaluatorClassName = "org.milyn.javabean.expression.BeanMapExpressionEvaluator";
            }

            String evaluatorConditionExpression = DomUtils.getAllText(conditionElement, true);
            if(evaluatorConditionExpression == null || evaluatorConditionExpression.trim().equals("")) {
                throw new SAXException("smooks-resource/condition must specify a condition expression as child text e.g. <condition evaluator=\"....\">A + B > C</condition>.");
            }

            // And construct it...
            return ExpressionEvaluator.Factory.createInstance(evaluatorClassName, evaluatorConditionExpression);
        }
    }

    private void digestProfiles(Element profilesElement) {
        NodeList configNodes = profilesElement.getChildNodes();

        for (int i = 0; i < configNodes.getLength(); i++) {
            if(configNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element profileNode = (Element) configNodes.item(i);
                String baseProfile = DomUtils.getAttributeValue(profileNode, "base-profile");
                String subProfiles = DomUtils.getAttributeValue(profileNode, "sub-profiles");
                DefaultProfileSet profileSet = new DefaultProfileSet(baseProfile);

                if (subProfiles != null) {
                    profileSet.addProfiles(subProfiles.split(","));
                }

                list.add(profileSet);
            }
        }
    }

    private void digestParameters(Element resourceConfigElement, SmooksResourceConfiguration resourceConfig) {
        NodeList configNodes = resourceConfigElement.getElementsByTagName("param");

        for (int i = 0; i < configNodes.getLength(); i++) {
            Element paramNode = (Element) configNodes.item(i);
            String paramName = DomUtils.getAttributeValue(paramNode, "name");
            String paramType = DomUtils.getAttributeValue(paramNode, "type");
            String paramValue = DomUtils.getAllText(paramNode, true);
            Parameter paramInstance;

            paramInstance = resourceConfig.setParameter(paramName, paramType, paramValue);
            paramInstance.setXML(paramNode);
        }
    }

    private LocalEntityResolver getDTDEntityResolver() {
        return new LocalDTDEntityResolver();
    }

    public String getCurrentPath() {
        StringBuilder pathBuilder = new StringBuilder();

        for(int i = configStack.size() - 1; i >= 0; i--) {
            pathBuilder.insert(0, "]");
            pathBuilder.insert(0, configStack.get(i).configFile);
            pathBuilder.insert(0, "/[");
        }

        return pathBuilder.toString();
    }

    private void pushConfig(String file) {
        SmooksConfig config = new SmooksConfig(file);

        config.parent = configStack.peek();
        configStack.push(config);
    }

    private void popConfig() {
        configStack.pop();
    }

    private void addConditionEvaluator(String id, ExpressionEvaluator evaluator) {
        assertUniqueConditionId(id);
        configStack.peek().conditionEvaluators.put(id, evaluator);
    }

    private ExpressionEvaluator getConditionEvaluator(String idRef) {
        SmooksConfig smooksConfig = configStack.peek();

        while(smooksConfig != null) {
            ExpressionEvaluator evaluator = smooksConfig.conditionEvaluators.get(idRef);
            if(evaluator != null) {
                return evaluator;
            }
            smooksConfig = smooksConfig.parent;
        }

        throw new SmooksConfigurationException("Unknown condition idRef '" + idRef + "'.");
    }

    private void assertUniqueConditionId(String id) {
        if(configStack.peek().conditionEvaluators.containsKey(id)) {
            throw new SmooksConfigurationException("Duplicate condition ID '" + id + "'.");
        }
    }

    private static class SmooksConfig {

        private SmooksConfig parent;
        private String configFile;
        private Map<String, ExpressionEvaluator> conditionEvaluators = new HashMap<String, ExpressionEvaluator>();

        private SmooksConfig(String configFile) {
            this.configFile = configFile;
        }
    }
}