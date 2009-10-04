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
package org.milyn.javabean.factory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Creates MVELFactory objects from a factory definition.
 *
 * The MVELFactory is cached so that it is only created once for a definition.
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class MVELFactoryDefinitionParser implements FactoryDefinitionParser {

	public ConcurrentMap<String, MVELFactory<?>> cache = new ConcurrentHashMap<String, MVELFactory<?>>();

	/* (non-Javadoc)
	 * @see org.milyn.javabean.factory.FactoryDefinitionParser#parse(java.lang.String)
	 */
	public Factory<?> parse(String factoryDefinition) {

		MVELFactory<?> factory = cache.get(factoryDefinition);
		if(factory == null) {

			factory = new MVELFactory<Object>(factoryDefinition);

			cache.put(factoryDefinition, factory);
		}

		return factory;
	}

}
