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

package org.milyn.smooks.scripting.groovy;

import groovy.lang.GroovyClassLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.control.CompilationFailedException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.delivery.ContentHandler;
import org.milyn.delivery.ContentHandlerFactory;
import org.milyn.delivery.Visitor;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.annotation.Resource;
import org.milyn.io.StreamUtils;
import org.milyn.util.FreeMarkerTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Visitor} Factory class for the <a href="http://groovy.codehaus.org/">Groovy</a> scripting language.
 * <p/>
 * Supports Groovy classes and scripts:
 * <ol>
 *  <li><b>Class</b>: You can implement a Groovy class that implements one or all of the {@link Visitor} interfaces.</li> 
 *  <li><b>Script</b>: You can implement a simple Groovy script.  This factory will apply a template to the script,
 *                      creating a Groovy class to be executed by Smooks.</li> 
 * </ol>
 *
 * <h2>Example Configuration</h2>
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;smooks-resource-list xmlns="http://www.milyn.org/xsd/smooks-1.1.xsd" xmlns:g="http://www.milyn.org/xsd/smooks/groovy-1.1.xsd"&gt;
 *
 *     &lt;g:groovy executeOnElement="c"&gt;
 *         &lt;g:script&gt;
 *             element = DomUtils.renameElement(element, "xxx", true, true);
 *             element.setAttribute("newElementAttribute", "1234");
 *         &lt;/g:script&gt;
 *     &lt;/g:groovy&gt;
 *
 * &lt;/smooks-resource-list&gt;
 * </pre>
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Resource(type="groovy")
public class GroovyContentHandlerFactory implements ContentHandlerFactory {

    private static Log logger = LogFactory.getLog(GroovyContentHandlerFactory.class);

    private FreeMarkerTemplate classTemplate;
    private volatile int classGenCount = 1;

    @Initialize
    public void initialize() throws IOException {
        String templateText = StreamUtils.readStreamAsString(getClass().getResourceAsStream("ScriptedGroovy.ftl"));
        classTemplate = new FreeMarkerTemplate(templateText);
    }

    /* (non-Javadoc)
	 * @see org.milyn.delivery.ContentHandlerFactory#create(org.milyn.cdr.SmooksResourceConfiguration)
	 */
	public ContentHandler create(SmooksResourceConfiguration configuration) throws SmooksConfigurationException, InstantiationException {

        try {
			byte[] groovyScriptBytes = configuration.getBytes();
            String groovyScript = new String(groovyScriptBytes, "UTF-8");

            if(groovyScriptBytes == null) {
				throw new InstantiationException("No resource specified in either the resource path or resource 'resdata'.");
			}

            Object groovyObject;

            GroovyClassLoader groovyClassLoader = new GroovyClassLoader(getClass().getClassLoader());
            try {
                Class groovyClass = groovyClassLoader.parseClass(groovyScript);
                groovyObject = groovyClass.newInstance();
            } catch(CompilationFailedException e) {
                logger.debug("Failed to create Visitor class instance directly from script:\n==========================\n" + groovyScript + "\n==========================\n Will try applying Visitor template to script.", e);
                groovyObject = null;
            }

            if(!(groovyObject instanceof Visitor)) {
                groovyObject = createFromTemplate(groovyScript, configuration);
            }

            ContentHandler groovyResource = (ContentHandler) groovyObject;
            Configurator.configure(groovyResource, configuration);

            return groovyResource;
        } catch (Exception e) {
			throw new SmooksConfigurationException("Error constructing class from Groovy script " + configuration.getResource(), e);
        }
    }

    private Object createFromTemplate(String groovyScript, SmooksResourceConfiguration configuration) throws InstantiationException, IllegalAccessException {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(getClass().getClassLoader());
        Map templateVars = new HashMap();
        String imports = configuration.getStringParameter("imports", "");

        templateVars.put("imports", cleanImportsConfig(imports));
        templateVars.put("visitorName", createClassName());
        templateVars.put("elementName", getElementName(configuration));        
        templateVars.put("visitBefore", configuration.getBoolParameter("executeBefore", false));
        templateVars.put("visitorScript", groovyScript);

        String templatedClass = classTemplate.apply(templateVars);

        try {
            Class groovyClass = groovyClassLoader.parseClass(templatedClass);
            return groovyClass.newInstance();
        } catch(CompilationFailedException e) {
            throw new SmooksConfigurationException("Failed to compile Groovy scripted Visitor class:\n==========================\n" + templatedClass + "\n==========================\n", e);
        }
    }

    private Object cleanImportsConfig(String imports) {
        try {
            StringBuffer importsBuffer = StreamUtils.trimLines(new StringReader(imports));
            imports = importsBuffer.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException reading String.", e);
        }

        return imports.replace("import ", "\nimport ");
    }

    private synchronized String createClassName() {
        StringBuilder className = new StringBuilder();

        className.append("SmooksVisitor_");
        className.append(System.identityHashCode(this));
        className.append("_");
        className.append(classGenCount++);

        return className.toString();
    }

    private String getElementName(SmooksResourceConfiguration configuration) {
        String elementName = configuration.getTargetElement();

        for (int i = 0; i < elementName.length(); i++) {
            if(!Character.isLetterOrDigit(elementName.charAt(i))) {
                return elementName + "_Mangled";
            }
        }

        return elementName;
    }
}
