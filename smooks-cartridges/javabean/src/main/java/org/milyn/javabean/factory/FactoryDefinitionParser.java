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

import org.milyn.container.ApplicationContext;
import org.milyn.javabean.BeanPopulator;
import org.milyn.util.ClassUtil;


/**
 * A factory definition string is an expression that instructs how to create a certain object.
 * A {@link FactoryDefinitionParser} can parse the factory definition and create a {@link Factory} object which
 * can create the object according to the definition.
 *
 * A {@link FactoryDefinitionParser} must have a public argumentless constructor. The {@link FactoryDefinitionParser}
 * must be thread safe. The parse method can be called concurrently. If the {@link FactoryDefinitionParser} is created
 * with the {@link FactoryDefinitionParserFactory} then it will be created only once.
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
@SuppressWarnings("deprecation")
public interface FactoryDefinitionParser {

	Factory<?> parse(String factoryDefinition);


	public static class FactoryDefinitionParserFactory {

		private static volatile ConcurrentMap<String, FactoryDefinitionParser> instances = new ConcurrentHashMap<String, FactoryDefinitionParser>();

		public static FactoryDefinitionParser getInstance(ApplicationContext applicationContext) {

    		String className = applicationContext.getStore().getGlobalParams().getStringParameter(BeanPopulator.GLOBAL_DEFAULT_FACTORY_DEFINITION_PARSER_CLASS, BeanPopulator.DEFAULT_FACTORY_DEFINITION_PARSER_CLASS);

    		FactoryDefinitionParser factoryDefinitionParser = instances.get(className);
    		if(factoryDefinitionParser == null) {

    			try {
					@SuppressWarnings("unchecked")
					Class<FactoryDefinitionParser> factoryDefinitionParserClass = ClassUtil.forName(className, FactoryDefinitionParser.class);

					FactoryDefinitionParser newFactoryDefinitionParser = factoryDefinitionParserClass.newInstance();

					instances.putIfAbsent(className, newFactoryDefinitionParser);

					// We do an extra get to make sure that there is always only one factoryDefinitionParser instance
					factoryDefinitionParser = instances.get(className);

				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException("The FactoryDefinitionParser class can't be found", e);
				} catch (InstantiationException e) {
					throw new IllegalArgumentException("The FactoryDefinitionParser class can't be instantiated. The FactoryDefinitionParser class must have a argumentless public constructor.", e);
				} catch (IllegalAccessException e) {
					throw new IllegalArgumentException("The FactoryDefinitionParser class can't be instantiated.", e);
				}

    		}

    		return factoryDefinitionParser;
		}

	}
}
;