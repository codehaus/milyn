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

package org.milyn.cdr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.container.ApplicationContext;
import org.milyn.delivery.ContentDeliveryUnit;
import org.milyn.delivery.ContentDeliveryUnitCreator;
import org.milyn.delivery.UnsupportedContentDeliveryUnitTypeException;
import org.milyn.resource.ContainerResourceLocator;
import org.milyn.util.ClassUtil;
import org.milyn.profile.ProfileSet;
import org.xml.sax.SAXException;


/**
 * {@link org.milyn.cdr.SmooksResourceConfiguration} context store.
 * <p/>
 * Stores the {@link org.milyn.cdr.SmooksResourceConfiguration SmooksResourceConfigurations}
 * for a given container context in the for of 
 * {@link org.milyn.cdr.SmooksResourceConfigurationList} entries.  Also maintains
 * a "default" config list for the context.
 * @author tfennelly
 */
public class SmooksResourceConfigurationStore {
	
	/**
	 * Logger.
	 */
	private static Log logger = LogFactory.getLog(SmooksResourceConfigurationStore.class);
	/**
	 * Table of loaded SmooksResourceConfigurationList objects.
	 */
	private List<SmooksResourceConfigurationList> configLists = new Vector<SmooksResourceConfigurationList>();
    /**
     * Default configuration list.
     */
    private SmooksResourceConfigurationList defaultList = new SmooksResourceConfigurationList("default");
	/**
	 * Container context in which this store lives.
	 */
	private ApplicationContext applicationContext;
	
	/**
	 * Public constructor.
	 * @param applicationContext Container context in which this store lives.
	 */
	public SmooksResourceConfigurationStore(ApplicationContext applicationContext) {
		if(applicationContext == null) {
			throw new IllegalArgumentException("null 'applicationContext' arg in constructor call.");
		}
		this.applicationContext = applicationContext;
        
		// add the default list to the list.
        configLists.add(defaultList);
        
        registerInstalledResources("installed-cdu-creators.cdrl");
        registerInstalledResources("installed-param-decoders.cdrl");
	}

    /**
     * Register the pre-installed CDU Creator classes.
     * @param resourceFile Installed (internal) resource config file.
     */
    private void registerInstalledResources(String resourceFile) {
        try {
            registerResources(resourceFile, ClassUtil.getResourceAsStream(resourceFile, getClass()));
        } catch (Exception e) {
            IllegalStateException state = new IllegalStateException("Failed to load " + resourceFile + ".  Expected to be in the same package as " + getClass().getName());
            state.initCause(e);
            throw state;
        }
    }

	/**
	 * Load all .cdrl files listed in the BufferedReader stream.
	 * <p/>
	 * Because this method uses the ContainerResourceLocator it may be possible
	 * to load external cdrl files.  If the ContainerResourceLocator is a 
	 * ServletResourceLocator the lines in the BufferedReader param can contain
	 * external URLs.
	 * @param cdrlLoadList BufferedReader cdrl list - one cdrl def per line.
     * @throws java.io.IOException Error reading list buffer.
	 */
	public void load(BufferedReader cdrlLoadList) throws IOException {
		String cdrl;
		ContainerResourceLocator resLocator = applicationContext.getResourceLocator();
		
		while((cdrl = cdrlLoadList.readLine()) != null) {
			cdrl = cdrl.trim();
			if(cdrl.equals("") || cdrl.charAt(0) == '#') {
				continue;
			}
			
			try {
				InputStream resource = resLocator.getResource(cdrl);
				
				logger.info("Loading Smooks Resources from file [" + cdrl + "].");
                registerResources(cdrl, resource);
				logger.debug("[" + cdrl + "] Loaded.");
			} catch (IllegalArgumentException e) {
				logger.error("[" + cdrl + "] Load failure. " + e.getMessage(), e);
			} catch (IOException e) {
				logger.error("[" + cdrl + "] Load failure. " + e.getMessage(), e);
			} catch (SAXException e) {
				logger.error("[" + cdrl + "] Load failure. " + e.getMessage(), e);
			}
		}
	}

    /**
     * Register the set of resources specified in the supplied XML configuration
     * stream.
     * @param name The name of the resource set.
     * @param resourceConfigStream XML resource configuration stream.
     * @throws SAXException Error parsing the resource stream.
     * @throws IOException Error reading resource stream.
     * @see SmooksResourceConfiguration
     */
    public void registerResources(String name, InputStream resourceConfigStream) throws SAXException, IOException {
        SmooksResourceConfigurationList configList;
        
        if(name == null || name.trim().equals("")) {
            throw new IllegalArgumentException("null or empty 'name' arg in method call.");
        }
        if(resourceConfigStream == null) {
            throw new IllegalArgumentException("null 'resourceConfigStream' arg in method call.");
        }
        
        configList = XMLConfigDigester.digestConfig(name, resourceConfigStream);
        configLists.add(configList);
    }    

    /**
     * Register a {@link SmooksResourceConfiguration} on this context store.
     * <p/>
     * The config gets added to the default resource list.
     * @param resourceConfig The Content Delivery Resource definition to be registered.
     */
    public void registerResource(SmooksResourceConfiguration resourceConfig) {
        if(resourceConfig == null) {
            throw new IllegalArgumentException("null 'resourceConfig' arg in method call.");
        }
        defaultList.add(resourceConfig);
    }
	
	/**
	 * Get all the SmooksResourceConfiguration entries registered on this context store 
     * for the specified useragent. 
	 * @param profileSet Useragent profile set.
	 * @return All SmooksResourceConfiguration entries for the specified useragent.
	 */
	public SmooksResourceConfiguration[] getSmooksResourceConfigurations(ProfileSet profileSet) {
		Vector allSmooksResourceConfigurationsColl = new Vector();
		SmooksResourceConfiguration[] allSmooksResourceConfigurations;
		
		// Iterate through each of the loaded SmooksResourceConfigurationLists.
		for(int i = 0; i < configLists.size(); i++) {
            SmooksResourceConfigurationList list = configLists.get(i);
			SmooksResourceConfiguration[] resourceConfigs = list.getUseragentConfigurations(profileSet);
            
			allSmooksResourceConfigurationsColl.addAll(Arrays.asList(resourceConfigs));
		}
		
		allSmooksResourceConfigurations = new SmooksResourceConfiguration[allSmooksResourceConfigurationsColl.size()];
		allSmooksResourceConfigurationsColl.toArray(allSmooksResourceConfigurations);
		
		return allSmooksResourceConfigurations;
	}
	
	/**
	 * Load a Java Object defined by the supplied SmooksResourceConfiguration instance.
	 * @param resourceConfig SmooksResourceConfiguration instance.
	 * @return An Object instance from the SmooksResourceConfiguration.
	 */
	public Object getObject(SmooksResourceConfiguration resourceConfig) {
		Object object = null;
        String className = ClasspathUtils.toClassName(resourceConfig.getPath());

        // Load the runtime class... 
		Class classRuntime;
		try {
			classRuntime = ClassUtil.forName(className, getClass());
		} catch (ClassNotFoundException e) {
			IllegalStateException state = new IllegalStateException("Error loading Java class: " + className);
			state.initCause(e);
			throw state;
		}
		
		// Try constructing via a SmooksResourceConfiguration constructor...
		Constructor constructor;
		try {
			constructor = classRuntime.getConstructor(new Class[] {SmooksResourceConfiguration.class});
			object = constructor.newInstance(new Object[] {resourceConfig});
		} catch (NoSuchMethodException e) {
			// OK, we'll try a default constructor later...
		} catch (Exception e) {
			IllegalStateException state = new IllegalStateException("Error loading Java class: " + className);
			state.initCause(e);
			throw state;
		}
		
		// If we still don't have an object, try constructing via the default construtor...
		if(object == null) {
			try {
				object = classRuntime.newInstance();
			} catch (Exception e) {
				IllegalStateException state = new IllegalStateException("Java class " + className + " must contain a default constructor if it does not contain a constructor that takes an instance of " + SmooksResourceConfiguration.class.getName() + ".");
				state.initCause(e);
				throw state;
			}
		}

		if(object instanceof ContentDeliveryUnit) {
			((ContentDeliveryUnit)object).setConfiguration(resourceConfig);
		}
		
		return object;
	}

    /**
     * Get the {@link ContentDeliveryUnitCreator} for a resource based on the
     * supplied resource type.
     * <p/>
     * Note that {@link ContentDeliveryUnitCreator} implementations must be  configured under a selector value of "cdu-creator". 
     * @param type {@link ContentDeliveryUnitCreator} type e.g. "class", "xsl" etc.
     * @return {@link ContentDeliveryUnitCreator} for the resource.
     * @throws UnsupportedContentDeliveryUnitTypeException No {@link ContentDeliveryUnitCreator}
     * registered for the specified resource type.
     */
    public ContentDeliveryUnitCreator getContentDeliveryUnitCreator(String type) throws UnsupportedContentDeliveryUnitTypeException {
        if(type == null) {
            throw new IllegalArgumentException("null 'resourceExtension' arg in method call.");
        }
        
        for(int i = 0; i < configLists.size(); i++) {
            SmooksResourceConfigurationList list = configLists.get(i);
            
            for(int ii = 0; ii < list.size(); ii++) {
                SmooksResourceConfiguration config = list.get(ii);
                String selector = config.getSelector();
                
                if("cdu-creator".equals(selector) && 
                        type.equals(config.getStringParameter("restype"))) {
                    return (ContentDeliveryUnitCreator) getObject(config);
                }
            }
        }
        
        throw new UnsupportedContentDeliveryUnitTypeException(type);
    }
}
