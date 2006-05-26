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

import org.apache.commons.logging.Log;
import org.milyn.dom.DomUtils;
import org.milyn.logging.SmooksLogger;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Digester class for an XML {@link org.milyn.cdr.SmooksResourceConfiguration} file (.cdrl).
 * @author tfennelly
 */
public final class XMLConfigDigester {

    private static Log logger = SmooksLogger.getLog();

    /**
     * Digest the XML Smooks configuration stream.
     * @param name The name of the configuration stream.
     * @param stream The stream.
     * @return A {@link SmooksResourceConfigurationList} containing the list of
     * {@link SmooksResourceConfiguration SmooksResourceConfigurations} defined in the
     * XML configuration.
     * @throws SAXException Error parsing the XML stream.
     * @throws IOException Error reading the XML stream.
     */
	public static SmooksResourceConfigurationList digestConfig(String name, InputStream stream) throws SAXException, IOException {
		Document archiveDefDoc = XmlUtil.parseStream(stream, true, true);
        SmooksResourceConfigurationList list = new SmooksResourceConfigurationList(name);
		int cdrIndex = 1;
		Element currentElement;
		String resourceSelector = null;
		
		currentElement = (Element)XmlUtil.getNode(archiveDefDoc, "/smooks-resource-list");
		String defaultSelector = trimToNull(currentElement.getAttribute("default-selector"));
		String defaultNamespace = trimToNull(currentElement.getAttribute("default-namespace"));
		String defaultUseragent = trimToNull(currentElement.getAttribute("default-useragent"));
		String defaultPath = trimToNull(currentElement.getAttribute("default-path"));
		
		resourceSelector = "/smooks-resource-list/smooks-resource[" + cdrIndex + "]";
		while((currentElement = (Element)XmlUtil.getNode(archiveDefDoc, resourceSelector)) != null) {
			String selector = trimToNull(currentElement.getAttribute("selector"));
			String namespace = trimToNull(currentElement.getAttribute("namespace"));
			String useragents = trimToNull(currentElement.getAttribute("useragent"));
			String path = trimToNull(currentElement.getAttribute("path"));
			SmooksResourceConfiguration resourceConfig;
			
			try {
				resourceConfig = new SmooksResourceConfiguration((selector != null?selector:defaultSelector),
									(namespace != null?namespace:defaultNamespace),
									(useragents != null?useragents:defaultUseragent), 
									(path != null?path:defaultPath));
			} catch(IllegalArgumentException e) {
				throw new SAXException("Invalid unit definition.", e);
			}
			int paramIndex = 1;
			Node paramNode;
			String paramSelector = null; 
			
			paramSelector = "param[" + paramIndex + "]";
			while((paramNode = XmlUtil.getNode(currentElement, paramSelector)) != null) {
				String paramName = XmlUtil.getString(paramNode, "@name");
				String paramType = XmlUtil.getString(paramNode, "@type");
				String paramValue = DomUtils.getAllText((Element)paramNode, true);
				
				resourceConfig.setParameter(paramName, paramType, paramValue);
				paramIndex++;
				paramSelector = "param[" + paramIndex + "]";
			}

            list.add(resourceConfig);
			if(logger.isDebugEnabled()) {
				logger.debug("Adding smooks-resource config from [" + name + "]: " + resourceConfig);
			}
			
			cdrIndex++;
			resourceSelector = "/smooks-resource-list/smooks-resource[" + cdrIndex + "]";
		}
		
		if(list.isEmpty()) {
			throw new SAXException("Invalid Content Delivery Resource archive definition file (.cdrl): 0 Content Delivery Resource definitions.");			
		}
        
        return list;
	}

    
    /**
     * Trim the String, setting to null if empty.
     * @param string String to trim.
     * @return String, null if empty.
     */
    private static String trimToNull(String string) {
        if(string == null) {
            return null;
        }
        
        String retString = string.trim();
        
        if(retString.equals("")) {
            retString = null;
        }
        
        return retString;
    }
}
