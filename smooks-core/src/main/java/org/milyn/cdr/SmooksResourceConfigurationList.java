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

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.profile.ProfileSet;
import org.milyn.assertion.AssertArgument;

/**
 * {@link org.milyn.cdr.SmooksResourceConfiguration} list.
 * @author tfennelly
 */
public class SmooksResourceConfigurationList {

	/**
	 * Logger.
	 */
	private static Log logger = LogFactory.getLog(SmooksResourceConfigurationList.class);
    /**
     * List name.
     */
    private String name;
    /**
     * {@link ProfileSet} list.
     */
    private List<ProfileSet> profiles = new Vector<ProfileSet>();
    /**
     * {@link org.milyn.cdr.SmooksResourceConfiguration} list.
     */
    private List<SmooksResourceConfiguration> list = new Vector<SmooksResourceConfiguration>();
    /**
     * List of loaded resource URIs.
     */
    private List<URI> loadedResources = new ArrayList<URI>();

    /**
     * Public constructor.
     * @param name The name of this instance.
     */
    public SmooksResourceConfigurationList(String name) {
        if(name == null || (name = name.trim()).equals("")) {
            throw new IllegalArgumentException("null or empty 'name' arg in constructor call.");
        }
        this.name = name;
        logger.debug("Smooks ResourceConfiguration List [" + name + "] created.");
    }
    
    /**
     * Add a {@link SmooksResourceConfiguration} instance to this list.
     * @param config {@link SmooksResourceConfiguration} instance to add.
     */
    public void add(SmooksResourceConfiguration config) {
        AssertArgument.isNotNull(config, "config");
        String[] selectors = config.getSelector().split(",");

        for(String selector : selectors) {
            SmooksResourceConfiguration clone = (SmooksResourceConfiguration) config.clone();

            clone.setSelector(selector.trim());
            list.add(clone);
            logger.debug("Smooks ResourceConfiguration [" + clone + "] added to list [" + name + "].");
        }
    }

    /**
     * Add a {@link ProfileSet} instance to this list.
     * @param profileSet {@link ProfileSet} instance to add.
     */
    public void add(ProfileSet profileSet) {
        AssertArgument.isNotNull(profileSet, "profileSet");
        profiles.add(profileSet);
        logger.debug("ProfileSet [" + profileSet.getBaseProfile() + "] added to list Smooks configuration [" + name + "].");
    }

    /**
     * Get the name of this list instance.
     * @return List name.
     */
    public String getName() {
        return name;
    }

    /**
     * Is this list instance empty.
     * @return True if this list instance is empty, otherwise false.
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Get the size of this list.
     * @return The size of te list i.e. number of entries.
     */
    public int size() {
        return list.size();
    }

    /**
     * Get the {@link SmooksResourceConfiguration} instance at the specified index.
     * @param index Resource index.
     * @return The {@link SmooksResourceConfiguration} instance at the specified index.
     * @throws ArrayIndexOutOfBoundsException The specified index is out of bounds.
     */
    public SmooksResourceConfiguration get(int index) throws ArrayIndexOutOfBoundsException {
        return list.get(index);
    }
    
    /**
     * Get all SmooksResourceConfiguration entries targeted at the specified profile set. 
     * @param profileSet The profile set to searh against.
     * @return All SmooksResourceConfiguration entries targeted at the specified profile set.
     */
    public SmooksResourceConfiguration[] getTargetConfigurations(ProfileSet profileSet) {
        Vector<SmooksResourceConfiguration> matchingSmooksResourceConfigurationsColl = new Vector<SmooksResourceConfiguration>();
        SmooksResourceConfiguration[] matchingSmooksResourceConfigurations;
        
        // Iterate over the SmooksResourceConfigurations defined on this list.
        for(int i = 0; i < size(); i++) {
            SmooksResourceConfiguration resourceConfig = get(i);
            ProfileTargetingExpression[] profileTargetingExpressions = resourceConfig.getProfileTargetingExpressions();
            
            for(int expIndex = 0; expIndex < profileTargetingExpressions.length; expIndex++) {
                ProfileTargetingExpression expression = profileTargetingExpressions[expIndex];

                if(expression.isMatch(profileSet)) {
                    matchingSmooksResourceConfigurationsColl.addElement(resourceConfig);
                    break;
                } else {
            		logger.debug("Resource [" + resourceConfig + "] not targeted at profile [" + profileSet.getBaseProfile() + "].  Sub Profiles: [" + profileSet + "]");
                }
            }
        }

        matchingSmooksResourceConfigurations = new SmooksResourceConfiguration[matchingSmooksResourceConfigurationsColl.size()];
        matchingSmooksResourceConfigurationsColl.toArray(matchingSmooksResourceConfigurations);
        
        return matchingSmooksResourceConfigurations;
    }

    /**
     * Get the list of profiles configured on this resource configuration list.
     * @return List of profiles.
     */
    public List<ProfileSet> getProfiles() {
        return profiles;
    }

    protected boolean addSourceResourceURI(URI resource) {
        AssertArgument.isNotNull(resource, "resource");

        if(loadedResources.contains(resource)) {
            URI lastLoaded = loadedResources.get(loadedResources.size() - 1);

            logger.info("Not adding resource config import '" + resource + "'.  This resource is already loaded on this list.");

            return false;
        }
        
        loadedResources.add(resource);
        return true;
    }
}
