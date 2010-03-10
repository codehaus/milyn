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

	protected InputSource resolve(String systemId, String resourceName) throws SAXException {
		String prefix = getNamespacePrefix(systemId);
		String resourceLocation = getDescriptorValue(prefix + "." + resourceName);
		
		if(resourceLocation == null) {
			throw new SAXException("Failed to resolve " + resourceName + " for namespace '" + systemId + "'.");
		}
		
		InputStream stream = ClassUtil.getResourceAsStream(resourceLocation, getClass());

		if(stream == null) {
			throw new SAXException(resourceName + " '" + resourceLocation + "' for namespace '" + systemId + "' does not resolve to a Classpath resource.");
		}
		
		return new InputSource(stream);
	}
	
	private String getNamespacePrefix(String systemId) {
		for(Properties descriptor : descriptors) {
			Set<Entry<Object, Object>> entries = descriptor.entrySet();
			for(Entry<Object, Object> entry : entries) {
				if(systemId.equals(entry.getValue())) {
					return entry.getKey().toString();
				}
			}
		}
		
		return null;
	}

	protected String getDescriptorValue(String name) {
		for(Properties descriptor : descriptors) {
			String value = descriptor.getProperty(name);
			if(value != null) {
				return value;
			}
		}
		
		return null;
	}
}
