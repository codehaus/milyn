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

import org.xml.sax.XMLReader;
import org.milyn.delivery.AbstractParser;

import java.util.*;

/**
 * Generic reader configurator.
 * <p/>
 * Specific reader implementations can define specialized configurators.
 * 
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class GenericReaderConfigurator implements ReaderConfigurator {

    private Class readerClass;
    private Properties parameters = new Properties();
    private List<String> featuresOn = new ArrayList<String>();
    private List<String> featuresOff = new ArrayList<String>();
    private String targetProfile;

    public GenericReaderConfigurator() {
    }

    public GenericReaderConfigurator(Class<? extends XMLReader> readerClass) {
        this.readerClass = readerClass;
    }

    public Properties getParameters() {
        return parameters;
    }

    public void setParameters(Properties parameters) {
        this.parameters = parameters;
    }

    public void setFeature(String feature, boolean on) {
        if(on) {
            featuresOn.add(feature);
        } else {
            featuresOff.add(feature);
        }
    }

    public List<String> getFeaturesOn() {
        return featuresOn;
    }

    public List<String> getFeaturesOff() {
        return featuresOff;
    }

    public String getTargetProfile() {
        return targetProfile;
    }

    public void setTargetProfile(String targetProfile) {
        this.targetProfile = targetProfile;
    }

    public SmooksResourceConfiguration toConfig() {
        SmooksResourceConfiguration smooksConfig = new SmooksResourceConfiguration();

        if(readerClass != null) {
            smooksConfig.setResource(readerClass.getName());
        }

        if(targetProfile != null) {
            smooksConfig.setTargetProfile(targetProfile);
        }

        // Add the parameters...
        Set<Map.Entry<Object, Object>> entries = parameters.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            smooksConfig.setParameter((String)entry.getKey(), (String)entry.getValue());
        }

        // Add the "on" features...
        for(String featureOn : featuresOn) {
            smooksConfig.setParameter(AbstractParser.FEATURE_ON, featureOn);
        }

        // Add the "off" features...
        for(String featureOff : featuresOff) {
            smooksConfig.setParameter(AbstractParser.FEATURE_OFF, featureOff);
        }

        return smooksConfig;
    }
}
