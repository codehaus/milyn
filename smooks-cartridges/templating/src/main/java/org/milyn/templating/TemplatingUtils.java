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

package org.milyn.templating;

import java.io.InputStream;

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ContainerContext;

/**
 * Utility class for the templating Cartridge.
 * @author tfennelly
 */
public class TemplatingUtils {
    
    private static final String TEMPLATING_CDU_CREATORS_CDRL = "templating-cdu-creators-0.1.cdrl";

    /**
     * Register the Templating {@link org.milyn.delivery.ContentDeliveryUnitCreator}
     * implementations defined by this Cartrdge with the suppied Smooks
     * {@link ContainerContext} store.
     * <p/>
     * This method can be used as a convienient way of registering these resources
     * when using e.g. {@link org.milyn.SmooksStandalone}. Example:
     * <pre>
     * SmooksStandalone smooks = new SmooksStandalone("UTF-8");
     * 
     * TemplatingUtils.registerCDUCreators(smooks.{@link org.milyn.SmooksStandalone#getContext() getContext()}); 
     * </pre>
     * <p/>
     * See {@link org.milyn.templating.stringtemplate.StringTemplateContentDeliveryUnitCreator}
     * and {@link org.milyn.templating.xslt.XslContentDeliveryUnitCreator} as examples. 
     * @param context The context on which the {@link org.milyn.delivery.ContentDeliveryUnitCreator}
     * are to be registered.
     */
    public static void registerCDUCreators(ContainerContext context) {
        InputStream stream = TemplatingUtils.class.getResourceAsStream(TEMPLATING_CDU_CREATORS_CDRL);
        try {
            context.getStore().registerResources(TEMPLATING_CDU_CREATORS_CDRL, stream);
        } catch (Exception e) {
            throw new SmooksConfigurationException("Failed to load classpath resource file: " + TEMPLATING_CDU_CREATORS_CDRL + ".  Should be in package: " + TemplatingUtils.class.getPackage().getName(), e);
        }
    }
}
