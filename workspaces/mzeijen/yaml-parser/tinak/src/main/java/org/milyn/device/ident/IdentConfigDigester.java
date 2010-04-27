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

package org.milyn.device.ident;

import org.milyn.xml.DomUtils;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Device Ident configuration XML digester. 
 * <p/>
 * Uses XPath to parse the XML and construct the DeviceIdent instance.
 * @author tfennelly
 */
public class IdentConfigDigester {

	/**
	 * Parse the device identification XML.
	 * @param input Input stream containing the XML data to be parsed
	 * @return The DeviceIdent
	 * @exception SAXException if a parsing exception occurs
	 * @exception IOException if an input/output error occurs
	 */
	public DeviceIdent parse(InputStream input) throws SAXException, IOException {
		DeviceIdent deviceIdent = new DeviceIdent();
		Document identDoc = XmlUtil.parseStream(input, XmlUtil.VALIDATION_TYPE.DTD, true);
		int deviceIndex = 1;
        NodeList devices = identDoc.getElementsByTagName("device");

        // While there are device ident definitions.
		for (int i = 0; i < devices.getLength(); i++) {
            IdentSet identSet = new IdentSet();
            Element device = (Element) devices.item(i);
			String name = device.getAttribute("name");
			String match = device.getAttribute("match");
			int identUnitIndex = 1; 

			if(name == null || name.equals("")) {
				throw new SAXException("\"name\" attribute not specified.  Unable to specify exact device.");
			}
			identSet.setDeviceName(XmlUtil.removeEntities(name));
			if(match != null && !match.equals("")) {
				identSet.setMatch(XmlUtil.removeEntities(match));
			}
			deviceIdent.addIdentSet(identSet);
			
			// Extract the IdentUnits now...
            NodeList deviceNodeList = device.getChildNodes();
            for (int ii = 0; ii < deviceNodeList.getLength(); ii++) {
                Node child = deviceNodeList.item(ii);

                if(child.getNodeType() == Node.ELEMENT_NODE) {
                    Element identUnitEl = (Element) child;
                    String identUnitQName = DomUtils.getName(identUnitEl);
                    String id = identUnitEl.getAttribute("id");
                    String identName = identUnitEl.getAttribute("name");
                    String value = identUnitEl.getAttribute("value");
                    IdentUnit identUnit;

                    // Create the ident unit from the attributes
                    identUnit = createIdentUnit(identUnitQName, id, identName, value);
                    // And add it to the current ident set.
                    identSet.addIdentUnit(identUnit);
                }
            }
		}

		// Prepare it before we return it.
		deviceIdent.prepare();

		return deviceIdent;
	}
	
	/**
	 * Create an Ident Unit based on attributes read from the ident config.
     * @param type The type of ident unit - the ident element name e.g. http-req-header or http-req-param.
	 * @param id The id attribute value.
	 * @param name The name value.
	 * @param value The value value.
	 * @return An IdentUnit instance based on the supplied parameters.
	 * @throws SAXException A problem with the attributes supplied. 
	 */
	private IdentUnit createIdentUnit(String type, String id, String name, String value) throws SAXException {
		IdentUnit identUnit = null;

		if(type.equals("http-req-header")) {
			identUnit = new HttpIdentUnit(HttpIdentUnit.HEADER_UNIT);
		} else if(type.equals("http-req-param")) {
			identUnit = new HttpIdentUnit(HttpIdentUnit.PARAM_UNIT);
		} else {
			throw new SAXException("Unknown Identification unit type: " + type);
		}

		if(id != null && !id.equals("")) {
			identUnit.setId(XmlUtil.removeEntities(id));
		}

		if(name == null || name.equals("")) {
			throw new SAXException("\"name\" attribute not specified.  Unable to specify exact device.");
		}
		if(value == null || value.equals("")) {
			throw new SAXException("\"value\" attribute not specified on ident unit - name: " + name + ".");
		}
		identUnit.setName(XmlUtil.removeEntities(name));
		identUnit.setValue(XmlUtil.removeEntities(value));
		
		return identUnit;
	}

}
