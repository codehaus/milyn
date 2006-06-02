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

package org.milyn.templating.stringtemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.ContentDeliveryUnit;
import org.milyn.delivery.ContentDeliveryUnitCreator;
import org.milyn.delivery.process.AbstractProcessingUnit;
import org.milyn.dom.DomUtils;
import org.milyn.javabean.BeanAccessor;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * StringTemplate {@link org.milyn.delivery.process.ProcessingUnit} ConcreteCreator class (GoF - Factory Method).
 * <p/>
 * Creates {@link org.milyn.delivery.ContentDeliveryUnit} instances for applying
 * <a href="http://www.stringtemplate.org/">StringTemplate</a> transformations.
 * <p/>
 * This templating solution relies on the <a href="http://milyn.codehaus.org/downloads">Smooks JavaBean Cartridge</a>
 * to perform the JavaBean population that's required by <a href="http://www.stringtemplate.org/">StringTemplate</a>.
 * @author tfennelly
 */
public class StringTemplateContentDeliveryUnitCreator implements ContentDeliveryUnitCreator {

    /**
     * Public constructor.
     * @param config Configuration details for this ContentDeliveryUnitCreator.
     */
    public StringTemplateContentDeliveryUnitCreator(SmooksResourceConfiguration config) {        
    }
    
	/**
	 * Create a StringTemplate based ContentDeliveryUnit.
     * @param resourceConfig The SmooksResourceConfiguration for the StringTemplate.
     * @return The StringTemplate {@link ContentDeliveryUnit} instance.
	 */
	public synchronized ContentDeliveryUnit create(SmooksResourceConfiguration resourceConfig) throws InstantiationException {
        return new StringTemplateProcessingUnit(resourceConfig);
	}

	/**
	 * StringTemplate template application ProcessingUnit.
	 * @author tfennelly
	 */
	private static class StringTemplateProcessingUnit extends AbstractProcessingUnit {

        private boolean visitBefore = false;
        private StringTemplate template;
        
        public StringTemplateProcessingUnit(SmooksResourceConfiguration config) {
            super(config);
            visitBefore = config.getBoolParameter("visitBefore", false);
            
            String path = config.getPath();
            String encoding = config.getStringParameter("encoding", "UTF-8");
            
            if(path.charAt(0) == '/') {
                path = path.substring(1);
            }
            if(path.endsWith(".st")) {
                path = path.substring(0, path.length() - 3);
            }
            
            StringTemplateGroup templateGroup = new StringTemplateGroup(path);
            templateGroup.setFileCharEncoding(encoding);
            template = templateGroup.getInstanceOf(path);        
        }

        public void visit(Element element, ContainerRequest request) {
            // First thing we do is clone the template for this transformation...
            StringTemplate thisTransTemplate = template.getInstanceOf();
            HashMap beans = BeanAccessor.getBeans(request);
            String transformedElementResult;

            // Set the document data beans on the template and apply it...
            thisTransTemplate.setAttributes(beans);
            transformedElementResult = thisTransTemplate.toString();
            
            // Create the replacement DOM text node containing the applied template...            
            Text transformedElement = element.getOwnerDocument().createTextNode(transformedElementResult);
            DomUtils.replaceNode(transformedElement, element);
        }

        public boolean visitBefore() {
            return visitBefore;
        }
	}
}
