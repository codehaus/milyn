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

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentHandler;
import org.milyn.delivery.ContentHandlerFactory;
import org.milyn.delivery.annotation.Resource;
import org.milyn.delivery.dom.serialize.GhostElementSerializationUnit;
import org.milyn.event.report.annotation.VisitAfterReport;
import org.milyn.event.report.annotation.VisitBeforeReport;
import org.milyn.io.StreamUtils;
import org.milyn.templating.AbstractTemplateProcessor;
import org.milyn.util.ClassUtil;
import org.milyn.xml.DomUtils;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/**
 * XSL {@link org.milyn.delivery.dom.DOMElementVisitor} Creator class.
 * <p/>
 * Creates {@link org.milyn.delivery.dom.DOMElementVisitor} instances for performing node/element level
 * <a href="http://www.w3.org/Style/XSL/">XSL</a> templating (aka XSLT).
 * <p/>
 * Template application can be done in a synchronized or unsynchronized fashion by setting
 * the system property "org.milyn.templating.xslt.synchronized".  According to the spec,
 * this should not be necessary.  However, Xalan 2.7.0 (for one) has a bug which results in
 * unsynchronized template application causing invalid transforms.
 * <p/>
 * <h2>Targeting "xsl" Templates</h2>
 * The following is the basic configuration specification for XSL resources:
 * <pre>
 * &lt;resource-config selector="<i>target-element</i>"&gt;
 *     &lt;resource&gt;<b>XSL Resource - Inline or {@link org.milyn.resource.URIResourceLocator URI}</b>&lt;/resource&gt;
 *
 *     &lt;!-- (Optional) The action to be applied on the template content. Should the content
 *          generated by the template:
 *          1. replace ("replace") the target element, or
 *          2. be added to ("addto") the target element, or
 *          3. be inserted before ("insertbefore") the target element, or
 *          4. be inserted after ("insertafter") the target element.
 *          5. be bound to ("bindto") a {@link org.milyn.javabean.repository.BeanRepository} variable named by the "bindId" param.
 *          Default "replace".--&gt;
 *     &lt;param name="<b>action</b>"&gt;<i>replace/addto/insertbefore/insertafter/bindto</i>&lt;/param&gt;
 *
 *     &lt;!-- (Optional) Is this XSL template resource a complete XSL template, or just a <a href="#templatelets">"Template<u>let</u></a>".
 *          Only relevant for inlined XSL resources.  URI based resource are always assumed to NOT be templatelets.
 *          Default "false" (for inline resources).--&gt;
 *     &lt;param name="<b>is-xslt-templatelet</b>"&gt;<i>true/false</i>&lt;/param&gt;
 *
 *     &lt;!-- (Optional) Should the template be applied before (true) or
 *             after (false) Smooks visits the child elements of the target element.
 *             Default "false".--&gt;
 *     &lt;param name="<b>applyTemplateBefore</b>"&gt;<i>true/false</i>&lt;/param&gt;
 *
 *     &lt;!-- (Optional) The name of the {@link org.milyn.io.AbstractOutputStreamResource OutputStreamResource}
 *             to which the result should be written. If set, the "action" param is ignored. --&gt;
 *     &lt;param name="<b>outputStreamResource</b>"&gt;<i>xyzResource</i>&lt;/param&gt;
 *
 *     &lt;!-- (Optional) Template encoding.
 *          Default "UTF-8".--&gt;
 *     &lt;param name="<b>encoding</b>"&gt;<i>encoding</i>&lt;/param&gt;
 *
 *     &lt;!-- (Optional) bindId when "action" is "bindto".
 *     &lt;param name="<b>bindId</b>"&gt;<i>xxxx</i>&lt;/param&gt;
 *
 *     &lt;!-- (Optional) Fail on XSL Transformer Warning.
 *          Default "true".--&gt;
 *     &lt;param name="<b>failOnWarning</b>"&gt;false&lt;/param&gt;</b> &lt;!-- Default "true" --&gt;
 *
 * &lt;/resource-config&gt;
 * </pre>
 * <p/>
 * <i><u>Example - URI based XSLT spec</u></i>:
 * <pre>
 * &lt;resource-config selector="<i>target-element</i>"&gt;
 *     &lt;!-- 1. See {@link org.milyn.resource.URIResourceLocator} --&gt;
 *     &lt;resource&gt;/com/acme/order-transform.xsl&lt;/resource&gt;
 * &lt;/resource-config&gt;
 * </pre>
 * <p/>
 * <i><u>Example - Inlined XSLT spec</u></i>:
 * <pre>
 * &lt;resource-config selector="<i>target-element</i>"&gt;
 *     &lt;!-- 1. Note how we have to specify the resource type when it's inlined. --&gt;
 *     &lt;!-- 2. Note how the inlined XSLT is wrapped as an XML Comment. CDATA Section wrapping also works. --&gt;
 *     &lt;!-- 3. Note if the inlined XSLT is a <a href="#templatelets">templatelet</a>, is-xslt-templatelet=true must be specified. --&gt;
 *     &lt;resource <b color="red">type="xsl"</b>&gt;
 *         &lt;!--
 *            <i>Inline XSLT....</i>
 *         --&gt;
 *     &lt;/resource&gt;
 *     <b color="red">&lt;param name="is-xslt-templatelet"&gt;true&lt;/param&gt;</b>
 * &lt;/resource-config&gt;
 * </pre>
 * <p/>
 * <h3 id="templatelets">Templatelets</h3>
 * Templatelets are a convenient way of specifying an XSL Stylesheet.  When using "Templatelets", you simply specify the
 * body of an XSL template. This creator then wraps that body to make a complete XSL Stylesheet with a single template matched to the
 * element targeted by the Smooks resource configuration in question.  This feature only applies
 * to inlined XSL resources and in this case, it's <u>OFF</u> by default.  To use this feature,
 * you must specify the "is-xslt-templatelet" parameter with a value of "true".
 * <p/>
 * This feature will not work in all situations since you'll often need to specify a full stylesheet in order to
 * specify namespaces etc.  It's just here for convenience.
 * <p/>
 * <a href="doc-files/templatelet.xsl" type="text/plain">See the template used to wrap the templatelet</a>.
 * <p/>
 * <h3>JavaBean Support</h3>
 * Support for injection of JavaBean values populated by the
 * <a href="http://milyn.codehaus.org/downloads">Smooks JavaBean Cartridge</a> is supported through the
 * <a href="http://xml.apache.org/xalan-j/">Xalan</a> extension {@link org.milyn.templating.xslt.XalanJavabeanExtension}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Resource(type = "xsl")
public class XslContentHandlerFactory implements ContentHandlerFactory {

    /**
     * Parameter name for templating feature.
     */
    public static final String IS_XSLT_TEMPLATELET = "is-xslt-templatelet";
    /**
     * Logger.
     */
    private static Log logger = LogFactory.getLog(XslContentHandlerFactory.class);
    /**
     * Synchonized template application system property key.
     */
    public static final String ORG_MILYN_TEMPLATING_XSLT_SYNCHRONIZED = "org.milyn.templating.xslt.synchronized";

    @AppContext
    private ApplicationContext applicationContext;
    
    /**
     * Create an XSL based ContentHandler instance ie from an XSL byte streamResult.
     *
     * @param resourceConfig The SmooksResourceConfiguration for the XSL {@link org.milyn.delivery.ContentHandler}
     *                       to be created.
     * @return XSL {@link org.milyn.delivery.ContentHandler} instance.
     * @see org.milyn.delivery.JavaContentHandlerFactory
     */
    public synchronized ContentHandler create(SmooksResourceConfiguration resourceConfig) throws SmooksConfigurationException, InstantiationException {
        try {
            return Configurator.configure(new XslProcessor(), resourceConfig, applicationContext);
        } catch(SmooksConfigurationException e) {
            throw e;
        } catch (Exception e) {
            InstantiationException instanceException = new InstantiationException("XSL ProcessingUnit resource [" + resourceConfig.getResource() + "] not loadable.");
            instanceException.initCause(e);
            throw instanceException;
        }
    }

    /**
     * XSLT template application ProcessingUnit.
     *
     * @author tfennelly
     */
    @VisitBeforeReport(condition = "false")
    @VisitAfterReport(summary = "Applied XSL Template.", detailTemplate = "reporting/XslTemplateProcessor_After.html")
    private static class XslProcessor extends AbstractTemplateProcessor {

        /**
         * XSL template to be applied to the visited element.
         */
        private Templates xslTemplate;
        /**
         * Is this processor processing an XSLT <a href="#templatelets">Templatelet</a>.
         */
        private boolean isTemplatelet;
        /**
         * Is the template application synchronized or not.
         * <p/>
         * Xalan v2.7.0 has/had a threading issue - kick-on effect being that template application
         * must be synchronized.
         */
        private final boolean isSynchronized = Boolean.getBoolean(ORG_MILYN_TEMPLATING_XSLT_SYNCHRONIZED);

        @Override
		protected void loadTemplate(SmooksResourceConfiguration resourceConfig) throws IOException, TransformerConfigurationException {
            byte[] xslBytes = resourceConfig.getBytes();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            StreamSource xslStreamSource;
            boolean isInlineXSL = resourceConfig.isInline();

            // If it's not a full XSL template, we need to make it so by wrapping it.
            isTemplatelet = isTemplatelet(isInlineXSL, resourceConfig, new String(xslBytes));
            if (isTemplatelet) {
                String templateletWrapper = new String(StreamUtils.readStream(ClassUtil.getResourceAsStream("doc-files/templatelet.xsl", getClass())));
                String templatelet = new String(xslBytes);

                templateletWrapper = StringUtils.replace(templateletWrapper, "@@@templatelet@@@", templatelet);
                xslBytes = templateletWrapper.getBytes();
            }

            boolean failOnWarning = resourceConfig.getBoolParameter("failOnWarning", true);

            xslStreamSource = new StreamSource(new InputStreamReader(new ByteArrayInputStream(xslBytes), getEncoding()));
            transformerFactory.setErrorListener(new XslErrorListener(failOnWarning));
            xslTemplate = transformerFactory.newTemplates(xslStreamSource);
        }

        private boolean isTemplatelet(boolean inlineXSL, SmooksResourceConfiguration resourceConfig, String templateCode) {
            boolean isTemplatelet = (inlineXSL && resourceConfig.getBoolParameter(IS_XSLT_TEMPLATELET, false));

            // If it's configured as a templatelet, but the code looks like it's a
            // full template, log a warning...
            if (isTemplatelet && templateCode.indexOf(":stylesheet>") != -1) {
                logger.warn("The following XSL resource is configured as a templatelet, but looks as though it may be a complete stylesheet i.e. not a templatelet. You may want to remove the 'is-xslt-templatelet' parameter. Resource:\n" + resourceConfig);
            }
            // If it's not configured as a templatelet, but the code looks like it's a not
            // full template, log a warning...
            if (!isTemplatelet && templateCode.indexOf(":stylesheet>") == -1) {
                logger.warn("The following XSL resource is NOT configured as a templatelet, but looks as though it may be an incomplete stylesheet i.e. it may be templatelet.  If so, it must be explicitly configured as a templatelet. Resource:\n" + resourceConfig);
            }

            return isTemplatelet;
        }

        @Override
		protected void visit(Element element, ExecutionContext executionContext) throws SmooksException {
            Document ownerDoc = element.getOwnerDocument();
            Element ghostElement = GhostElementSerializationUnit.createElement(ownerDoc);

            try {
                if (isSynchronized) {
                    synchronized (xslTemplate) {
                        performTransform(element, ghostElement, ownerDoc);
                    }
                } else {
                    performTransform(element, ghostElement, ownerDoc);
                }
            } catch (TransformerException e) {
                throw new SmooksException("Error applying XSLT to node [" + executionContext.getDocumentSource() + ":" + DomUtils.getXPath(element) + "]", e);
            }

            if(getOutputStreamResource() != null || getAction() == Action.BIND_TO) {
                // For bindTo or streamTo actions, we need to serialize the content and supply is as a Text DOM node.
                // AbstractTemplateProcessor will look after the rest, by extracting the content from the
                // Text node and attaching it to the ExecutionContext...
                String serializedContent = XmlUtil.serialize(ghostElement.getChildNodes());
                Text textNode = element.getOwnerDocument().createTextNode(serializedContent);

                processTemplateAction(element, textNode, executionContext);
            } else {
                NodeList children = ghostElement.getChildNodes();

                // Process the templating action, supplying the templating result...
                if(children.getLength() == 1 && children.item(0).getNodeType() == Node.ELEMENT_NODE) {
                    processTemplateAction(element, children.item(0), executionContext);
                } else {
                    processTemplateAction(element, ghostElement, executionContext);
                }
            }
        }

        private void performTransform(Element element, Element transRes, Document ownerDoc) throws TransformerException {
            Transformer transformer;
            transformer = xslTemplate.newTransformer();

            if (element == ownerDoc.getDocumentElement()) {
                transformer.transform(new DOMSource(ownerDoc), new DOMResult(transRes));
            } else {
                transformer.transform(new DOMSource(element), new DOMResult(transRes));
            }
        }

        private static class XslErrorListener implements ErrorListener {
            private final boolean failOnWarning;

            public XslErrorListener(boolean failOnWarning) {
                this.failOnWarning = failOnWarning;
            }

            public void warning(TransformerException exception) throws TransformerException {
                if(failOnWarning) {
                    throw exception;
                } else {
                    logger.warn("XSL Warning.", exception);
                }
            }

            public void error(TransformerException exception) throws TransformerException {
                throw exception;
            }

            public void fatalError(TransformerException exception) throws TransformerException {
                throw exception;
            }
        }
    }
}
