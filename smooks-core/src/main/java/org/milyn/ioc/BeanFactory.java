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

package org.milyn.ioc;

import java.io.InputStream;

import org.milyn.delivery.ContentDeliveryUnitCreator;
import org.milyn.delivery.UnsupportedContentDeliveryUnitTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.InputStreamResource;

/**
 * IoC bean factory class.
 * <p id="top"/>
 * Current implementation uses Spring.  Loads 2 Spring config files from the root 
 * of the classpath:
 * <ol>
 * 	<li><b>root-ioc.config</b>: This config file is packaged within the Smooks 
 * 		deployment. <b>Do not replace this config - see ext-ioc.config</b>.</li>
 * 	<li><b id="ext-smooks-ioc.config">ext-smooks-ioc.config</b>: Used to extend the IoC config defined 
 *      in root-ioc.config. If, for example, you 
 * 		need to add a configuration for a new {@link org.milyn.delivery.ContentDeliveryUnitCreator},
 * 		create a Spring configuration file named <i>ext-smooks-ioc.config</i>, add your configurations and
 * 		add the file to the root of the classpath.
 * 	</li>
 * </ol>
 * @author tfennelly
 */
public class BeanFactory {

	/**
	 * Spring BeanFactory instance.
	 */
	private static XmlBeanFactory beanFactory;

	/**
	 * Staticlly load the Spring configuration.
	 */
	static {
		try {
			InputStreamResource config = new InputStreamResource(BeanFactory.class.getResourceAsStream("root-ioc.config"));
			beanFactory = new XmlBeanFactory(config);				
		} catch(Exception e) {
			IllegalStateException state = new IllegalStateException("Root Spring configuration load failure [root-ioc.config].  Must be in the root of the classpath. This is an unexpected exception because this config file should be present in the basic Smooks deployment.");
	    	state.initCause(e);
	        throw state;
		}
		InputStream extIocConfig = BeanFactory.class.getResourceAsStream("/ext-smooks-ioc.config");
		if(extIocConfig != null) {
			try {
				InputStreamResource config = new InputStreamResource(extIocConfig);
				beanFactory = new XmlBeanFactory(config, beanFactory);				
			} catch(Exception e) {
				IllegalStateException state = new IllegalStateException("Extended Spring configuration load failure [ext-ioc.config]. See exception cause...");
		    	state.initCause(e);
		        throw state;
			}
		}
	}

	/**
	 * Get the named bean from the IoC configuration.
	 * @param bean Bean name/id.
	 * @return Bean instance.
	 * @throws NoSuchBeanDefinitionException Bean not specified in configuration. 
	 * See <a href="#top">Javadoc</a>.
	 */
	public static Object getBean(String bean) throws NoSuchBeanDefinitionException {
		if(bean == null || bean.trim().equals("")) {
			throw new IllegalArgumentException("null or empty 'bean' arg in method call.");
		}
		return beanFactory.getBean(bean);
	}

	/**
	 * Get the {@link ContentDeliveryUnitCreator} for specified {@link org.milyn.delivery.ContentDeliveryUnit} type.
	 * @param type The {@link org.milyn.delivery.ContentDeliveryUnit} type e.g. "class" (Java class) or "xsl" (XSL transform).
	 * @return {@link ContentDeliveryUnitCreator} instance.
	 * @throws UnsupportedContentDeliveryUnitTypeException The specified type has no IoC configuration defined. See 
	 * <a href="#ext-ioc.config">ext-ioc.config</a>.
	 */
	public static ContentDeliveryUnitCreator getContentDeliveryUnitCreator(String type) throws UnsupportedContentDeliveryUnitTypeException {
		try {
			return (ContentDeliveryUnitCreator)BeanFactory.getBean(type);
		} catch (NoSuchBeanDefinitionException noSuchBean) {
			throw new UnsupportedContentDeliveryUnitTypeException(type);
		}
	}
}
