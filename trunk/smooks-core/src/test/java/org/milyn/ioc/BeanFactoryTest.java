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

import org.milyn.container.MockContainerResourceLocator;
import org.milyn.resource.ContainerResourceLocator;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import junit.framework.TestCase;

public class BeanFactoryTest extends TestCase {

	public void testGetBean_badargs() {
		try {
			BeanFactory.getBean(null);
			fail("Expected IllegalArgumentException on null bean name arg.");
		} catch(IllegalArgumentException e) {
			// OK
		}
		try {
			BeanFactory.getBean(" ");
			fail("Expected IllegalArgumentException on empty bean name arg.");
		} catch(IllegalArgumentException e) {
			// OK
		}
	}

	public void testGetBean() {
		// Test loading of undefined bean.
		try {
			BeanFactory.getBean("notdefined");
			fail("Expected NoSuchBeanDefinitionException");
		} catch(NoSuchBeanDefinitionException e) {
			//OK
		}

		// Test loading from root-ioc.config
		BeanFactory.getBean("class");
		BeanFactory.getBean("xsl");

		// Test loading from the test root-ioc.config - just making sure the test classpath is OK!
		ContainerResourceLocator resLocator = (ContainerResourceLocator)BeanFactory.getBean("standaloneResourceLocator");
		if(!(resLocator instanceof MockContainerResourceLocator)) {
//			fail("Loading IoC config from wrong classpath location.  Make sure the test configuration is at the top of the classpath.");
		}
		
		// Test loading from ext-ioc.config
		BeanFactory.getBean("xxxyyy");
	}
}
