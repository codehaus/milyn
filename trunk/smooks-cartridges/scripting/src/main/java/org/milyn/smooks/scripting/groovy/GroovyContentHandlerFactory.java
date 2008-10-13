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
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.delivery.ContentHandler;
import org.milyn.delivery.ContentHandlerFactory;
import org.milyn.delivery.Visitor;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.annotation.Resource;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.io.StreamUtils;
import org.milyn.util.FreeMarkerTemplate;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link DOMElementVisitor} for the <a href="http://groovy.codehaus.org/">Groovy</a> scripting language.
 * <p/>
 * The Groovy script must implement one (and only one) of the following interfaces:
 * <ol>
 * 	<li>{@link DOMElementVisitor}, or</li>
 * 	<li>{@link org.milyn.delivery.dom.serialize.SerializationUnit}.</li>
 * </ol>
 * Since Groovy v1.0 doesn't support annotations, you'll need to set the "visit phase" for all
 * {@link DOMElementVisitor} implementations via the "VisitPhase" parameter.  See
 * the {@link DOMElementVisitor} javadoc.  Alternatively, you can try using Groovy 1.1, which is supposed
 * to support annotations. 
 * 
 * <h2>Registering GroovyContentHandlerFactory to Handle "groovy" Resources</h2>
 * So this configuration tells Smooks how to handle "groovy" resource configuration types i.e. resources with
 * a restype of "groovy", or resources with a ".groovy" file extension in their path.
 * <pre>
 * &lt;resource-config selector="cdu-creator"&gt;
 *     &lt;resource&gt;<b>org.milyn.smooks.scripting.GroovyContentHandlerFactory</b>&lt;/resource&gt;
 *     &lt;param name="<b>restype</b>"&gt;groovy&lt;/param&gt;
 * &lt;/resource-config&gt;
 * </pre>
 * 
 * <h4>2. Targeting "groovy" Resources</h4>
 * <a href="http://groovy.codehaus.org/">Groovy</a> scripts can be defined and target in either
 * of 2 ways:
 * <ol>
 *  <li>By specifying the ".groovy" script in the resource path attribute (a {@link org.milyn.resource.URIResourceLocator URI}).</li>
 *  <li>By inlining the groovy script directly into the resource configuration.</li>
 * </ol>
 * <p/>
 * <u>URI based Groovy Scripting:</u><br/>
 * Note, the groovy resource specified in the path attribute must have a file extension of ".groovy".
 * <pre>
 * &lt;smooks-resource  selector="<i>target-element</i>"&gt;
 *     &lt;resource&gt;<b>{@link org.milyn.resource.URIResourceLocator URI}</b>&lt;/resource&gt;
 *     &lt;!-- (Optional)  Zero or more &lt;param&gt; instances to be supplied to the Groovy script through
 *                      the setConfiguration()
 *                      method. --&gt;
 * &lt;/smooks-resource&gt;
 * </pre>
 * <p/>
 * <u>Inlined Groovy Scripting:</u>
 * <pre>
 * &lt;smooks-resource  selector="<i>target-element</i>"&gt;
 *     &lt;resource <b>type="groovy"</b>&gt;
 *         <b><i>inlined groovy script, optionally wrapped in XML Comment or CDATA sections...</i></b>
 *     &lt;/resource&gt;
 *     &lt;!-- (Optional)  Zero or more &lt;param&gt; instances to be supplied to the Groovy script through
 *                      the setConfiguration()
 *                      method. --&gt;
 * &lt;/smooks-resource&gt;
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
