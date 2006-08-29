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

import org.milyn.container.ContainerContext;
import org.milyn.delivery.ContentDeliveryUnitCreator;
import org.milyn.delivery.UnsupportedContentDeliveryUnitTypeException;
import org.milyn.device.UAContext;
import org.milyn.logging.SmooksLogger;
import org.milyn.resource.ContainerResourceLocator;
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
	 * Table of loaded SmooksResourceConfigurationList objects.
	 */
	private List configLists = new Vector();
    /**
     * Default configuration list.
     */
    private SmooksResourceConfigurationList defaultList = new SmooksResourceConfigurationList("default");
	/**
	 * Container context in which this store lives.
	 */
	private ContainerContext containerContext;
	
	/**
	 * Public constructor.
	 * @param containerContext Container context in which this store lives.
	 */
	public SmooksResourceConfigurationStore(ContainerContext containerContext) {
		if(containerContext == null) {
			throw new IllegalArgumentException("null 'containerContext' arg in constructor call.");
		}
		this.containerContext = containerContext;
        // add the default list to the list.
        configLists.add(defaultList);
        registerInstalledCDUCreators();
	}

    /**
     * Register the pre-installed CDU Creator classes.
     */
    private void registerInstalledCDUCreators() {
        try {
            registerResources("installed-cdu-creators", getClass().getResourceAsStream("installed-cdu-creators.cdrl"));
        } catch (Exception e) {
            IllegalStateException state = new IllegalStateException("Failed to load installed-cdu-creators.cdrl.  Expected to be in the same package as " + getClass().getName());
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
	 */
	public void load(BufferedReader cdrlLoadList) throws IOException {
		String cdrl;
		ContainerResourceLocator resLocator = containerContext.getResourceLocator();
		
		while((cdrl = cdrlLoadList.readLine()) != null) {
			cdrl = cdrl.trim();
			if(cdrl.equals("") || cdrl.charAt(0) == '#') {
				continue;
			}
			
			try {
				InputStream resource = resLocator.getResource(cdrl);
				
				if(cdrl.toLowerCase().endsWith(".cdrl")) {
                    registerResources(cdrl, resource);
				}
				SmooksLogger.getLog().debug("[" + cdrl + "] Loaded.");
			} catch (IllegalArgumentException e) {
				SmooksLogger.getLog().error("[" + cdrl + "] Load failure. " + e.getMessage(), e);
			} catch (IOException e) {
				SmooksLogger.getLog().error("[" + cdrl + "] Load failure. " + e.getMessage(), e);
			} catch (SAXException e) {
				SmooksLogger.getLog().error("[" + cdrl + "] Load failure. " + e.getMessage(), e);
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
	 * @param useragentContext The useragent.
	 * @return All SmooksResourceConfiguration entries for the specified useragent.
	 */
	public SmooksResourceConfiguration[] getSmooksResourceConfigurations(UAContext useragentContext) {
		Vector allSmooksResourceConfigurationsColl = new Vector();
		SmooksResourceConfiguration[] allSmooksResourceConfigurations = null;
		
		// Iterate through each of the loaded CDRArchive files.
		for(int i = 0; i < configLists.size(); i++) {
            SmooksResourceConfigurationList list = (SmooksResourceConfigurationList) configLists.get(i);
			SmooksResourceConfiguration[] resourceConfigs = list.getUseragentConfigurations(useragentContext);
            
			allSmooksResourceConfigurationsColl.addAll(Arrays.asList(resourceConfigs));
		}
		
		allSmooksResourceConfigurations = new SmooksResourceConfiguration[allSmooksResourceConfigurationsColl.size()];
		allSmooksResourceConfigurationsColl.toArray(allSmooksResourceConfigurations);
		
		return allSmooksResourceConfigurations;
	}
	
	/**
	 * Load a Java Object defined by the supplied SmooksResourceConfiguration instance.
	 * <p/>
	 * The class implementation must contain a public constructor
	 * that takes a {@link SmooksResourceConfiguration} parameter.
	 * @param resourceConfig SmooksResourceConfiguration instance.
	 * @return An Object instance from the SmooksResourceConfiguration.
	 */
	public Object getObject(SmooksResourceConfiguration resourceConfig) {
		Object object = null;
        String className = ClasspathUtils.toClassName(resourceConfig.getPath());
		
		try {
			Class classRuntime = Class.forName(className);
			Constructor constructor = classRuntime.getConstructor(new Class[] {SmooksResourceConfiguration.class});
			
			object = constructor.newInstance(new Object[] {resourceConfig});
		} catch (NoSuchMethodException e) {
			IllegalStateException state = new IllegalStateException("Unable to load Java Object [" + resourceConfig.getPath() + "]. Implementation must provide a public constructor that takes a SmooksResourceConfiguration arg.");
			state.initCause(e);
			throw state;
		} catch (Exception e) {
			IllegalStateException state = new IllegalStateException("Error loading Java class: " + className);
			state.initCause(e);
			throw state;
		}
		
		return object;
	}

    /**
     * Get the {@link ContentDeliveryUnitCreator} for a resource based on the
     * supplied resource type.
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
            SmooksResourceConfigurationList list = (SmooksResourceConfigurationList) configLists.get(i);
            
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
