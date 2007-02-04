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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.useragent.UAContext;

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
     * {@link org.milyn.cdr.SmooksResourceConfiguration} list.
     */
    private List list = new Vector();
    
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
        if(config == null) {
            throw new IllegalArgumentException("null 'config' arg in method call.");
        }
        list.add(config);
        logger.debug("Smooks ResourceConfiguration [" + config + "] added to list [" + name + "].");
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
     * @param index
     * @return The {@link SmooksResourceConfiguration} instance at the specified index.
     * @throws ArrayIndexOutOfBoundsException The specified index is out of bounds.
     */
    public SmooksResourceConfiguration get(int index) throws ArrayIndexOutOfBoundsException {
        return (SmooksResourceConfiguration) list.get(index);
    }
    
    /**
     * Get all SmooksResourceConfiguration entries for the specified useragent from list. 
     * @param useragentContext The useragent recoginition context.
     * @return All SmooksResourceConfiguration entries for the specified useragent.
     */
    public SmooksResourceConfiguration[] getUseragentConfigurations(UAContext useragentContext) {
        Vector<SmooksResourceConfiguration> matchingSmooksResourceConfigurationsColl = new Vector<SmooksResourceConfiguration>();
        SmooksResourceConfiguration[] matchingSmooksResourceConfigurations = null;
        
        // Iterate over the SmooksResourceConfigurations defined on this list.
        for(int i = 0; i < size(); i++) {
            SmooksResourceConfiguration resourceConfig = get(i);
            UseragentExpression[] useragentExpressions = resourceConfig.getUseragentExpressions();
            
            for(int expIndex = 0; expIndex < useragentExpressions.length; expIndex++) {
                UseragentExpression expression = useragentExpressions[expIndex];
                
                if(expression.isMatchingDevice(useragentContext)) {
                    matchingSmooksResourceConfigurationsColl.addElement(resourceConfig);
                    break;
                } else {
            		logger.debug("Resource [" + resourceConfig + "] not targeted at useragent [" + useragentContext.getCommonName() + "].  Profiles: [" + useragentContext.getProfileSet() + "]");
                }
            }
        }

        matchingSmooksResourceConfigurations = new SmooksResourceConfiguration[matchingSmooksResourceConfigurationsColl.size()];
        matchingSmooksResourceConfigurationsColl.toArray(matchingSmooksResourceConfigurations);
        
        return matchingSmooksResourceConfigurations;
    }
}
