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

package org.milyn.templating.xslt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.ContentDeliveryUnit;
import org.milyn.delivery.ContentDeliveryUnitCreator;
import org.milyn.io.StreamUtils;
import org.milyn.templating.AbstractTemplateProcessingUnit;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * XSL {@link org.milyn.delivery.process.ProcessingUnit} Creator class.
 * <p/>
 * Creates {@link org.milyn.delivery.ContentDeliveryUnit} instances for performing node/element level
 * <a href="http://www.w3.org/Style/XSL/">XSL</a> templating (aka XSLT).
 * 
 * <h3>.cdrl Configuration</h3>
 * Two configurations are required in order to use <a href="http://www.w3.org/Style/XSL/">XSL</a>
 * based templating:
 * <ol>
 *  <li>A Configuration to register this {@link org.milyn.delivery.ContentDeliveryUnitCreator}
 *      implementation.  This configuration basically tells Smooks how to handle ".xsl" files (or restype param "xsl").
 *  </li>
 *  <li>Configurations for targeting the "xsl" templates.
 *  </li>
 * </ol>
 * 
 * <h4>1. Registering XslContentDeliveryUnitCreator to Handle "xsl" resource types</h4>
 * <pre>
 * &lt;smooks-resource path="<b>org.milyn.templating.xslt.XslContentDeliveryUnitCreator</b>" &gt;
 * 
 *  &lt;!-- 
 *      (Mandatory) Specifying the resource type.  This param basically tells Smooks to use the {@link XslContentDeliveryUnitCreator} to
 *      create {@link org.milyn.delivery.process.ProcessingUnit} instances for handling "xsl" resources.
 *  --&gt;
 *  &lt;param name="<b>restype</b>"&gt;xsl&lt;/param&gt;
 * 
 * &lt;/smooks-resource&gt;
 * </pre>
 * <p/>
 * Registration of the {@link XslContentDeliveryUnitCreator} to handle "xsl" resources can also be done by calling 
 * {@link org.milyn.templating.TemplatingUtils#registerCDUCreators(ContainerContext)}.
 * 
 * <h4>2. Targeting "xsl" Templates</h4>
 * XSLTs can be specified in a file or as a "resdata" parameter (i.e. inline in the resource configuration).
 * <p/>
 * <i>File based spec</i>:
 * <pre>
 * &lt;smooks-resource  useragent="<i>useragent/profile</i>" selector="<i>target-element</i>" 
 *  path="<b>/com/acme/AcmeXslTemplate.xsl</b>" &gt;
 *  
 *  &lt;!-- (Optional) The action to be applied on the template content. Should the content 
 *          generated by the template:
 *          1. replace ("replace") the target element, or
 *          2. be added to ("addto") the target element, or
 *          3. be inserted before ("insertbefore") the target element, or
 *          4. be inserted after ("insertafter") the target element.
 *          Default "replace".--&gt;
 *  &lt;param name="<b>action</b>"&gt;<i>replace/addto/insertbefore/insertafter</i>&lt;/param&gt;
 * 
 *  &lt;!-- (Optional) Is this XSL template resource a complete XSL template, or just a <a href="#templatelets">"Template<u>let</u></a>".
 *          Default "true".--&gt;
 *  &lt;param name="<b>is-xslt-templatelet</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 *  &lt;!-- (Optional) Should the template be applied before (true) or 
 *          after (false) Smooks visits the child elements of the target element. 
 *          Default "false".--&gt;
 *  &lt;param name="<b>visitBefore</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 *  &lt;!-- (Optional) Template encoding. 
 *          Default "UTF-8".--&gt;
 *  &lt;param name="<b>encoding</b>"&gt;<i>encoding</i>&lt;/param&gt;
 * 
 * &lt;/smooks-resource&gt;
 * </pre>
 * <p/>
 * <i>Parameter based spec (inline)</i>:
 * <pre>
 * &lt;smooks-resource  useragent="<i>useragent/profile</i>" selector="<i>target-element</i>"&gt;
 * 
 *  &lt;!-- (Mandatory) This parameter tells Smooks how to handle this resource i.e. to use
 *  		 {@link XslContentDeliveryUnitCreator}.  This is required because there's no "path" attribute
 *  		available on this configuration for Smooks to use to determine the resource type. 
 *  		See {@link org.milyn.cdr.SmooksResourceConfiguration#getType()} --&gt;
 *  &lt;param name="<b>restype</b>"&gt;xsl&lt;/param&gt;
 * 
 *  &lt;!-- (Mandatory) This parameter tells Smooks how to handle this resource i.e. to use
 *  		 {@link XslContentDeliveryUnitCreator}. --&gt;
 *  &lt;param name="<b>resdata</b>"&gt;
 *  	&lt;![CDATA[
 *  		<i>XSL Template/<a href="#templatelets">Templatelet</a></i>
 *  	]]&gt;
 *  &lt;/param&gt;
 *  
 *  &lt;!-- (Optional) The action to be applied on the template content. Should the content 
 *          generated by the template:
 *          1. replace ("replace") the target element, or
 *          2. be added to ("addto") the target element, or
 *          3. be inserted before ("insertbefore") the target element, or
 *          4. be inserted after ("insertafter") the target element.
 *          Default "replace".--&gt;
 *  &lt;param name="<b>action</b>"&gt;<i>replace/addto/insertbefore/insertafter</i>&lt;/param&gt;
 * 
 *  &lt;!-- (Optional) Is this XSL template resource a complete XSL template, or just a <a href="#templatelets">"Templatelet"</a>.
 *          Default "true".--&gt;
 *  &lt;param name="<b>is-xslt-templatelet</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 *  &lt;!-- (Optional) Should the template be applied before (true) or 
 *          after (false) Smooks visits the child elements of the target element. 
 *          Default "false".--&gt;
 *  &lt;param name="<b>visitBefore</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 *  &lt;!-- (Optional) Template encoding. 
 *          Default "UTF-8".--&gt;
 *  &lt;param name="<b>encoding</b>"&gt;<i>encoding</i>&lt;/param&gt;
 * 
 * &lt;/smooks-resource&gt;
 * </pre>
 * 
 * <h3 id="templatelets">Templatelets</h3>
 * Templatelets are a convienient way of specifying templates.  When using "Templatelets", this creator 
 * class wraps the templatelet with the necessary XSL template code to make the template a complete XSL template
 * for the targeted DOM node.  It's simply a way of abreviating an xsl template resource.  Use of the Templatelet
 * feature can be disabled by specifying the "is-xslt-templatelet" parameter with a value of "false".
 * <p/>
 * <a href="doc-files/templatelet.xsl" type="text/plain">See the template used to wrap the templatelet</a>.
 * @author tfennelly
 */
public class XslContentDeliveryUnitCreator implements ContentDeliveryUnitCreator {

    /**
     * Public constructor.
     * @param config Configuration details for this ContentDeliveryUnitCreator.
     */
    public XslContentDeliveryUnitCreator(SmooksResourceConfiguration config) {        
    }
    
	/**
	 * Create an XSL based ContentDeliveryUnit instance ie from an XSL byte stream.
     * @param resourceConfig The SmooksResourceConfiguration for the XSL {@link ContentDeliveryUnit}
     * to be created.
     * @return XSL {@link ContentDeliveryUnit} instance.
	 * @see JavaContentDeliveryUnitCreator 
	 */
	public synchronized ContentDeliveryUnit create(SmooksResourceConfiguration resourceConfig) throws InstantiationException {
		try {
			return new XslProcessingUnit(resourceConfig);
		} catch (TransformerConfigurationException e) {
			InstantiationException instanceException = new InstantiationException("XSL ProcessingUnit resource [" + resourceConfig.getPath() + "] not loadable.  XSL resource invalid.");
			instanceException.initCause(e);
			throw instanceException;
		} catch (IOException e) {
			InstantiationException instanceException = new InstantiationException("XSL ProcessingUnit resource [" + resourceConfig.getPath() + "] not loadable.  XSL resource not found.");
			instanceException.initCause(e);
			throw instanceException;
		}
	}

	/**
	 * XSLT template application ProcessingUnit.
	 * @author tfennelly
	 */
	private static class XslProcessingUnit extends AbstractTemplateProcessingUnit {

		/**
		 * XSL template to be applied to the visited element.
		 */
		private Templates xslTemplate;
		
		/**
		 * Constructor.
		 * <p/>
		 * Create the XSL Template from the Content Delivery Resource bytes.
		 * @param resourceConfig Config. 
		 * @throws TransformerConfigurationException Unable to parse XSL.
		 * @throws IOException Failed to read resource data.
		 */
		private XslProcessingUnit(SmooksResourceConfiguration resourceConfig) throws TransformerConfigurationException, IOException {
			super(resourceConfig);
		}

		protected void loadTemplate(SmooksResourceConfiguration resourceConfig) throws IOException, TransformerConfigurationException {
			byte[] xslBytes = resourceConfig.getBytes();
			boolean isTemplatelet = resourceConfig.getBoolParameter("is-xslt-templatelet", true);
			TransformerFactory transformer = TransformerFactory.newInstance();
			StreamSource xslStreamSource;
            String encoding = resourceConfig.getStringParameter("encoding", "UTF-8");

			// If it's not a full XSL template, we need to make it so by wrapping it.
			if(isTemplatelet) {
				String templatelet = new String(StreamUtils.readStream(getClass().getResourceAsStream("doc-files/templatelet.xsl")));
				
				templatelet = templatelet.replaceFirst("@@@templatelet@@@", new String(xslBytes));
				xslBytes = templatelet.getBytes();
			}

			xslStreamSource = new StreamSource(new InputStreamReader(new ByteArrayInputStream(xslBytes), encoding));
			xslTemplate = transformer.newTemplates(xslStreamSource);
		}

		public void visit(Element element, ContainerRequest containerRequest) {
			Node transRes = element.getOwnerDocument().createElement("xsltrans");
			NodeList children = null;
			
			try {
				xslTemplate.newTransformer().transform(new DOMSource(element), new DOMResult(transRes));
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			children = transRes.getChildNodes();
            
            // Process the templating action, supplying the templating result...
            processTemplateAction(element, children);
		}
	}
}
