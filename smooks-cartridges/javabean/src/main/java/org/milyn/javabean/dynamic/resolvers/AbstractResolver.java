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
package org.milyn.javabean.dynamic.resolvers;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.milyn.javabean.dynamic.DynamicModelBuilder;
import org.milyn.util.ClassUtil;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Abstract descriptor resource resolver. 
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
abstract class AbstractResolver implements EntityResolver {

	private List<Properties> descriptors;

	protected AbstractResolver(List<Properties> descriptors) {
		this.descriptors = descriptors;
	}

	protected List<Properties> getDescriptors() {
		return descriptors;
	}

	protected InputSource resolveSchemaLocation(String systemId) throws SAXException {
		String namespaceId = DynamicModelBuilder.getNamespaceId(systemId, descriptors);
		
		if(namespaceId != null) {
			String schemaLocation = DynamicModelBuilder.getSchemaLocation(namespaceId, descriptors);
			
			if(schemaLocation == null) {
				throw new SAXException("Failed to resolve schemaLocation for namespace '" + systemId + "'.");
			}
			
			InputStream stream = ClassUtil.getResourceAsStream(schemaLocation, getClass());
	
			if(stream == null) {
				throw new SAXException("schemaLocation '" + schemaLocation + "' for namespace '" + systemId + "' does not resolve to a Classpath resource.");
			}
			
			return new InputSource(stream);
		} else {
			return null;
		}
	}

	protected InputSource resolveBindingConfigLocation(String systemId) throws SAXException {
		String namespaceId = DynamicModelBuilder.getNamespaceId(systemId, descriptors);
		
		if(namespaceId != null) {
			String bindingConfigLocation = DynamicModelBuilder.getBindingConfigLocation(namespaceId, descriptors);
			
			if(bindingConfigLocation == null) {
				throw new SAXException("Failed to resolve bindingConfigLocation for namespace '" + systemId + "'.");
			}
			
			InputStream stream = ClassUtil.getResourceAsStream(bindingConfigLocation, getClass());
	
			if(stream == null) {
				throw new SAXException("bindingConfigLocation '" + bindingConfigLocation + "' for namespace '" + systemId + "' does not resolve to a Classpath resource.");
			}
			
			return new InputSource(stream);
		} else {
			return null;
		}
	}
}
