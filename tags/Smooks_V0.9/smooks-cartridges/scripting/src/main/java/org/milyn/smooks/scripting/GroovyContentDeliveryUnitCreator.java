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

package org.milyn.smooks.scripting;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import groovy.lang.GroovyClassLoader;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.ContentDeliveryUnit;
import org.milyn.delivery.ContentDeliveryUnitCreator;
import org.milyn.delivery.dom.serialize.SerializationUnit;
import org.milyn.delivery.dom.DOMElementVisitor;

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
 * <h3>Resource Configuration</h3>
 * Two configurations are required in order to use <a href="http://groovy.codehaus.org/">Groovy</a>
 * based scripting in Smooks:
 * <ol>
 *  <li>A Configuration to register this {@link org.milyn.delivery.ContentDeliveryUnitCreator}
 *      implementation.  This configuration basically tells Smooks how to handle the "groovy" 
 *      resource type i.e. resources with a restype of "groovy", or resources with a ".groovy" file
 *      extension in their path.
 *  </li>
 *  <li>One or more configurations for targeting Groovy resources at one or more 
 *  	document/message fragments.
 *  </li>
 * </ol>
 * 
 * <h4>1. Registering GroovyContentDeliveryUnitCreator to Handle "groovy" Resources</h4>
 * So this configuration tells Smooks how to handle "groovy" resource configuration types i.e. resources with
 * a restype of "groovy", or resources with a ".groovy" file extension in their path.
 * <pre>
 * &lt;resource-config selector="cdu-creator"&gt;
 *     &lt;resource&gt;<b>org.milyn.smooks.scripting.GroovyContentDeliveryUnitCreator</b>&lt;/resource&gt;
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
 *                      the {@link org.milyn.delivery.ContentDeliveryUnit#setConfiguration(org.milyn.cdr.SmooksResourceConfiguration)}
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
 *                      the {@link org.milyn.delivery.ContentDeliveryUnit#setConfiguration(org.milyn.cdr.SmooksResourceConfiguration)}
 *                      method. --&gt;
 * &lt;/smooks-resource&gt;
 * </pre>
 * 
 * @author tfennelly
 */
public class GroovyContentDeliveryUnitCreator implements ContentDeliveryUnitCreator {

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryUnitCreator#create(org.milyn.cdr.SmooksResourceConfiguration)
	 */
	public ContentDeliveryUnit create(SmooksResourceConfiguration configuration) throws InstantiationException {
		GroovyClassLoader groovyClassLoader = new GroovyClassLoader(getClass().getClassLoader());
		
		try {
			byte[] groovyScriptData = configuration.getBytes();
			
			if(groovyScriptData == null) {
				throw new InstantiationException("No resource specified in either the resource path or resource 'resdata'.");
			}
			
			Class groovyClass = groovyClassLoader.parseClass(new String(groovyScriptData, "UTF-8"));
			Object groovyObject = groovyClass.newInstance();
			
			if(groovyObject instanceof DOMElementVisitor || groovyObject instanceof SerializationUnit) {
				ContentDeliveryUnit groovyResource = (ContentDeliveryUnit)groovyObject;
				groovyResource.setConfiguration(configuration);
				
				return groovyResource;
			} else {
				throw new InstantiationException("Invalid Groovy script " + configuration.getResource() + ".  Must implement one of the following Smooks interfaces:\n\t\t1. " + DOMElementVisitor.class.getName() + ", or\n\t\t2. " + SerializationUnit.class.getName() + ".");
			}
		} catch (IllegalAccessException e) {
			InstantiationException initE = new InstantiationException("Error constructing class from Groovy script " + configuration.getResource());
			initE.initCause(e);
			throw initE;
		} catch (UnsupportedEncodingException e) {
            InstantiationException initE = new InstantiationException("Error decoding Groovy script " + configuration.getResource());
            initE.initCause(e);
            throw initE;
        }
    }
}
