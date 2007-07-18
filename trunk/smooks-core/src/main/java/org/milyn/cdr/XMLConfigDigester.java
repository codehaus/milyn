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

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.xml.*;
import org.milyn.profile.DefaultProfileSet;
import org.milyn.io.StreamUtils;
import org.milyn.util.ClassUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;

/**
 * Digester class for an XML {@link org.milyn.cdr.SmooksResourceConfiguration} file (.cdrl).
 *
 * @author tfennelly
 */
public final class XMLConfigDigester {

    private static final String DTD_V10 = "http://www.milyn.org/dtd/smooksres-list-1.0.dtd";
    private static final String XSD_V10 = "http://www.milyn.org/xsd/smooks-1.0.xsd";

    private static Log logger = LogFactory.getLog(XMLConfigDigester.class);

    /**
     * Digest the XML Smooks configuration stream.
     *
     * @param name   The name of the configuration stream.
     * @param stream The stream.
     * @return A {@link SmooksResourceConfigurationList} containing the list of
     *         {@link SmooksResourceConfiguration SmooksResourceConfigurations} defined in the
     *         XML configuration.
     * @throws SAXException Error parsing the XML stream.
     * @throws IOException  Error reading the XML stream.
     */
    public static SmooksResourceConfigurationList digestConfig(String name, InputStream stream) throws SAXException, IOException {
        byte[] streamBuffer = StreamUtils.readStream(stream);
        LocalEntityResolver entityResolver = null;
        Document archiveDefDoc = null;
        String docType = null;
        SmooksResourceConfigurationList list = null;

        try {
            entityResolver = getDTDEntityResolver();
            archiveDefDoc = XmlUtil.parseStream(new ByteArrayInputStream(streamBuffer), entityResolver, XmlUtil.VALIDATION_TYPE.DTD, true);
        } catch(Exception e) {
            entityResolver = getXSDEntityResolver();
            archiveDefDoc = XmlUtil.parseStream(new ByteArrayInputStream(streamBuffer), entityResolver, XmlUtil.VALIDATION_TYPE.XSD, true);
        }
        docType = entityResolver.getDocType();
        list = new SmooksResourceConfigurationList(name);

        if (DTD_V10.equals(docType)) {
            logger.warn("Using a deprecated Smooks configuration DTD '" + DTD_V10 + "'.  Update configuration to use XSD '" + XSD_V10 + "'.");
            digestV10DTDValidatedConfig(archiveDefDoc, list);
            logger.warn("Using a deprecated Smooks configuration DTD '" + DTD_V10 + "'.  Update configuration to use XSD '" + XSD_V10 + "'.");
        } else if (XSD_V10.equals(docType)) {
            digestV10XSDValidatedConfig(archiveDefDoc, list);
        } else {
            throw new SAXException("Invalid Content Delivery Resource archive definition file: Must be validated against one of - '" + DTD_V10 + "' or '" + XSD_V10 + "'.");
        }

        if (list.isEmpty()) {
            throw new SAXException("Invalid Content Delivery Resource archive definition file: 0 Content Delivery Resource definitions.");
        }

        return list;
    }

    private static LocalEntityResolver getDTDEntityResolver() {
        return new LocalDTDEntityResolver();
    }

    private static LocalEntityResolver getXSDEntityResolver() {
        Source schemaSource = new StreamSource(ClassUtil.getResourceAsStream("/org/milyn/xsd/smooks-1.0.xsd", XMLConfigDigester.class), XSD_V10);
        return new LocalXSDEntityResolver(new Source[] {schemaSource});
    }

    private static void digestV10DTDValidatedConfig(Document archiveDefDoc, SmooksResourceConfigurationList list) throws SAXException {
        int cdrIndex = 1;
        Element currentElement;
        String resourceSelector = null;

        currentElement = (Element) XmlUtil.getNode(archiveDefDoc, "/smooks-resource-list");
        String defaultSelector = DomUtils.getAttributeValue(currentElement, "default-selector");
        String defaultNamespace = DomUtils.getAttributeValue(currentElement, "default-namespace");
        String defaultUseragent = DomUtils.getAttributeValue(currentElement, "default-useragent");
        String defaultPath = DomUtils.getAttributeValue(currentElement, "default-path");

        resourceSelector = "/smooks-resource-list/smooks-resource[" + cdrIndex + "]";
        while ((currentElement = (Element) XmlUtil.getNode(archiveDefDoc, resourceSelector)) != null) {
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

    private static void digestV10XSDValidatedConfig(Document archiveDefDoc, SmooksResourceConfigurationList list) throws SAXException {
        int cdrIndex = 1;
        Element currentElement;
        String resourceSelector = null;

        currentElement = (Element) XmlUtil.getNode(archiveDefDoc, "/smooks-resource-list");
        String defaultSelector = DomUtils.getAttributeValue(currentElement, "default-selector");
        String defaultNamespace = DomUtils.getAttributeValue(currentElement, "default-selector-namespace");
        String defaultProfile = DomUtils.getAttributeValue(currentElement, "default-target-profile");

        currentElement = (Element) XmlUtil.getNode(archiveDefDoc, "/smooks-resource-list/profiles");
        if(currentElement != null) {
            digestProfiles(currentElement, list);
        }

        resourceSelector = "/smooks-resource-list/resource-config[" + cdrIndex + "]";
        while ((currentElement = (Element) XmlUtil.getNode(archiveDefDoc, resourceSelector)) != null) {
            String selector = DomUtils.getAttributeValue(currentElement, "selector");
            String namespace = DomUtils.getAttributeValue(currentElement, "selector-namespace");
            String profiles = DomUtils.getAttributeValue(currentElement, "target-profile");
            Element resourceElement = (Element) XmlUtil.getNode(currentElement, "resource");
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
            } catch (IllegalArgumentException e) {
                throw new SAXException("Invalid unit definition.", e);
            }

            // Add the parameters...
            digestParameters(currentElement, resourceConfig);

            list.add(resourceConfig);
            if(resource == null) {
                if(resourceConfig.getParameters(SmooksResourceConfiguration.PARAM_RESDATA) != null) {
                    logger.warn("Resource 'null' for resource config: " + resourceConfig + ".  This is probably an error because the configuration contains a 'resdata' param, which suggests it is following the old DTD based configuration model.  The new model requires the resource to be specified in the <resource> element.");
                } else {
                    logger.debug("Resource 'null' for resource config: " + resourceConfig + ". This is not invalid!");
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Adding smooks-resource config from [" + list.getName() + "]: " + resourceConfig);
            }

            cdrIndex++;
            resourceSelector = "/smooks-resource-list/resource-config[" + cdrIndex + "]";
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

            if(subProfiles != null) {
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
