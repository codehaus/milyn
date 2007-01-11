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

package org.milyn.delivery;

import org.milyn.resource.ContainerResourceLocator;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.InputStreamResource;

/**
 * ContentDeliveryUnitCreator Factory class.
 * <p/>
 * Creates {@link org.milyn.delivery.ContentDeliveryUnitCreator} implementations based on the file 
 * extension of the &lt;cdres&gt; element "path" attribute in the definition defined in the 
 * in the .cdrl files (see {@link org.milyn.cdr.CDRConfig} and 
 * {@link org.milyn.cdr.CDRDef}).  
 * <p/>
 * The {@link org.milyn.delivery.ContentDeliveryUnitCreator}
 * instances are instanciated using the Spring IoC Container with the bean id attribute values mapping to the
 * file extension as outlined above.  The Spring bean definitions file is called "deliveryunit-config.xml" and
 * is loaded from "/deliveryunit-config.xml" through the {@link org.milyn.resource.ContainerResourceLocator} implementation 
 * for the container in which Smooks is running e.g. {@link org.milyn.resource.ServletResourceLocator} in the Servlet 
 * container.
 * @author tfennelly
 */
public class ContentDeliveryUnitCreatorFactory {
		
	/**
	 * ContentDeliveryUnitCreator beans factory.
	 */
	private XmlBeanFactory beanFactory;
	
	/**
	 * Public constructor.
	 * @param resourceLocator Container resource locator
	 */
	public ContentDeliveryUnitCreatorFactory(ContainerResourceLocator resourceLocator) {
		if(resourceLocator == null) {
			throw new IllegalArgumentException("null 'resourceLocator' arg in constructor call.");
		}
		try {
			InputStreamResource config = new InputStreamResource(resourceLocator.getResource("/deliveryunit-config.xml"));
			beanFactory = new XmlBeanFactory(config);				
		} catch(Exception e) {
			IllegalStateException state = new IllegalStateException("Spring beans configuration load failure [deliveryunit-config.xml].");
        	state.initCause(e);
            throw state;
		}
	}

	/**
	 * Get Instance factory method.
	 * @param type TransUnit type (e.g. java, xsl).
	 * @return ContentDeliveryUnitCreator instance.
	 */
	public ContentDeliveryUnitCreator getInstance(String type) throws UnsupportedContentDeliveryUnitTypeException {
		try {
			return (ContentDeliveryUnitCreator)beanFactory.getBean(type);
		} catch (NoSuchBeanDefinitionException noSuchBean) {
			throw new UnsupportedContentDeliveryUnitTypeException(type);
		}
	}
}
