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

package org.milyn.device.ident;

import java.io.IOException;
import java.io.InputStream;

import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
		Document identDoc = XmlUtil.parseStream(input, false, true);
		int deviceIndex = 1;
		String deviceSelector = null; 

		// While there are device ident definitions.
		deviceSelector = "/device-ident/device[" + deviceIndex + "]";
		while(!XmlUtil.getString(identDoc, deviceSelector).equals("")) {
			IdentSet identSet = new IdentSet();
			String name = XmlUtil.getString(identDoc, deviceSelector + "/@name"); 
			String match = XmlUtil.getString(identDoc, deviceSelector + "/@match");
			int identUnitIndex = 1; 
			String identUnitSelector = null;
			String identUnitQName = null;

			if(name == null || name.equals("")) {
				throw new SAXException("\"name\" attribute not specified.  Unable to specify exact device.");
			}
			identSet.setDeviceName(XmlUtil.removeEntities(name));
			if(match != null && !match.equals("")) {
				identSet.setMatch(XmlUtil.removeEntities(match));
			}
			deviceIdent.addIdentSet(identSet);
			
			// Extract the IdentUnits now...
			identUnitSelector = deviceSelector + "/*[" + identUnitIndex + "]";
			while((identUnitQName = XmlUtil.getString(identDoc, identUnitSelector + "/name()")) != null && !identUnitQName.equals("")) {
				String id = XmlUtil.getString(identDoc, identUnitSelector + "/@id");
				String identName = XmlUtil.getString(identDoc, identUnitSelector + "/@name");
				String value = XmlUtil.getString(identDoc, identUnitSelector + "/@value");
				IdentUnit identUnit = null;
				
				// Create the ident unit from the attributes
				identUnit = createIdentUnit(identUnitQName, id, identName, value);
				// And add it to the current ident set.
				identSet.addIdentUnit(identUnit);
				
				identUnitIndex++;
				identUnitSelector = deviceSelector + "/*[" + identUnitIndex + "]";
			}
			
			deviceIndex++;
			deviceSelector = "/device-ident/device[" + deviceIndex + "]";
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
