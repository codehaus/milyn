/*
	Milyn - Copyright (C) 2003

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
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.milyn.dom.DomUtils;
import org.milyn.logging.SmooksLogger;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Disgester class for the Content Delivery Resource definition data found in an
 * Archive Definition File (.cdrl).
 * <p/>
 * Archive definition files (.cdrl) are located in the "/META-INF" folder of a 
 * .cdrar or can be manually located and loaded from other locations e.g. from the 
 * "/WEB-INF/cdr" folder in Servlet context. 
 * @author tfennelly
 */
public final class CDRConfig {
	
	/**
	 * The name of the archive definition file.
	 */
	private String name = null;
	/**
	 * Content Delivery Resource definitions for this archive definition.
	 */
	private Vector cdrDefinitions = new Vector();
	private static Log logger = SmooksLogger.getLog();

	/**
	 * Public Constrctor.
	 * @param name The name of the archive definition file.
	 */
	public CDRConfig(String name) {
		if(name == null) {
			throw new IllegalArgumentException("null 'name' arg in constructor call."); 
		}
		this.name = name;
	}

	/**
	 * Public Constructor.
	 * <p/>
	 * Create an CDRConfig instance by "digesting" a .cdrl file.
	 * @param name The name of the archive definition file.
	 * @param stream The XML stream from which the Archive Definition is constructed.
	 */
	public CDRConfig(String name, InputStream stream) throws SAXException, IOException {
		if(name == null) {
			throw new IllegalArgumentException("null 'name' arg in constructor call."); 
		}
		if(stream == null) {
			throw new IllegalArgumentException("null 'stream' arg in constructor call."); 
		}
		this.name = name;
		digestStream(stream);
	}

	/**
	 * Add the supplied CDRDef instance to this CDRConfig.
	 * @param unitDef The CDRDef instance to be added.
	 */
	public void addCDRDef(CDRDef unitDef) {
		if(unitDef == null) {
			throw new IllegalArgumentException("null 'unitDef' arg in method call."); 
		}
		cdrDefinitions.addElement(unitDef);	
	}
	
	/**
	 * Parse the XML definition input stream.
	 * @param stream
	 */
	private void digestStream(InputStream stream) throws SAXException, IOException {
		Document archiveDefDoc = XmlUtil.parseStream(stream, true);
		int cdrIndex = 1;
		Node cdresNode;
		String cdresSelector = null;
		String defaultSelector = trimToNull(XmlUtil.getString(archiveDefDoc, "/cdres-list/@default-selector"));
		String defaultUatarget = trimToNull(XmlUtil.getString(archiveDefDoc, "/cdres-list/@default-uatarget"));
		String defaultPath = trimToNull(XmlUtil.getString(archiveDefDoc, "/cdres-list/@default-path"));
		
		cdresSelector = "/cdres-list/cdres[" + cdrIndex + "]";
		while((cdresNode = XmlUtil.getNode(archiveDefDoc, cdresSelector)) != null) {
			String selector = trimToNull(XmlUtil.getString(cdresNode, "@selector"));
			String uatargets = trimToNull(XmlUtil.getString(cdresNode, "@uatarget"));
			String path = trimToNull(XmlUtil.getString(cdresNode, "@path"));
			CDRDef cdrDef;
			
			try {
				cdrDef = new CDRDef((selector != null?selector:defaultSelector), 
									(uatargets != null?uatargets:defaultUatarget), 
									(path != null?path:defaultPath));
			} catch(IllegalArgumentException e) {
				throw new SAXException("Invalid unit definition.", e);
			}
			int paramIndex = 1;
			Node paramNode;
			String paramSelector = null; 
			
			paramSelector = "param[" + paramIndex + "]";
			while((paramNode = XmlUtil.getNode(cdresNode, paramSelector)) != null) {
				String paramName = XmlUtil.getString(paramNode, "@name");
				String paramType = XmlUtil.getString(paramNode, "@type");
				String paramValue = DomUtils.getAllText((Element)paramNode, true);
				
				cdrDef.setParameter(paramName, paramType, paramValue);
				paramIndex++;
				paramSelector = "param[" + paramIndex + "]";
			}

			cdrDefinitions.addElement(cdrDef);
			if(logger.isDebugEnabled()) {
				logger.debug("Adding cdres config from [" + name + "]: " + cdrDef);
			}
			
			cdrIndex++;
			cdresSelector = "/cdres-list/cdres[" + cdrIndex + "]";
		}
		
		if(cdrDefinitions.size() == 0) {
			throw new SAXException("Invalid Content Delivery Resource archive definition file (.cdrl): 0 Content Delivery Resource definitions.");			
		}
	}
	
	/**
	 * Trim the String, setting to null if empty.
	 * @param string String to trim.
	 * @return String, null if empty.
	 */
	private String trimToNull(String string) {
		if(string == null) {
			return null;
		}
		
		String retString = string.trim();
		
		if(retString.equals("")) {
			retString = null;
		}
		
		return retString;
	}

	/**
	 * Get the name of the archive definition file.
	 * @return The name of the archive definition file.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the CDR definitions defined in this archive.
	 * @return This archives unit definitions array.
	 */
	public CDRDef[] getCDRDefs() {
		CDRDef[] unitDefinitionsArray;
		
		if(cdrDefinitions.isEmpty()) {
			throw new IllegalStateException("Call to getCDRDefs() before any CDRDef instances have been added.");
		}
		unitDefinitionsArray = new CDRDef[cdrDefinitions.size()];
		cdrDefinitions.toArray(unitDefinitionsArray);

		return unitDefinitionsArray;
	}

}
