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
package org.milyn.templating.freemarker;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.io.AbstractOutputStreamResource;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.serialize.ContextObjectSerializationUnit;
import org.milyn.delivery.sax.*;
import org.milyn.event.report.annotation.VisitAfterReport;
import org.milyn.event.report.annotation.VisitBeforeReport;
import org.milyn.javabean.BeanAccessor;
import org.milyn.templating.AbstractTemplateProcessor;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.*;
import java.net.URL;
import java.util.Map;

/**
 * <a href="http://freemarker.org/">FreeMarker</a> template application ProcessingUnit.
 * <p/>
 * See {@link org.milyn.templating.freemarker.FreeMarkerContentHandlerFactory}.
 *
 * @author tfennelly
 */
@VisitBeforeReport(condition = "false")
@VisitAfterReport(summary = "Applied FreeMarker Template.", detailTemplate = "reporting/FreeMarkerTemplateProcessor_After.html")
public class FreeMarkerTemplateProcessor extends AbstractTemplateProcessor implements SAXElementVisitor {

    private static Log logger = LogFactory.getLog(FreeMarkerTemplateProcessor.class);

    private Template template;
    private SmooksResourceConfiguration config;
    private DefaultSAXElementSerializer targetWriter;

    protected void loadTemplate(SmooksResourceConfiguration config) throws IOException {
        this.config = config;

        if (config.isInline()) {
            byte[] templateBytes = config.getBytes();
            Reader templateReader = new InputStreamReader(new ByteArrayInputStream(templateBytes), getEncoding());

            try {
                template = new Template("free-marker-template", templateReader, new Configuration());
            } finally {
                templateReader.close();
            }
        } else {
            Configuration configuration = new Configuration();
            TemplateLoader[] loaders = new TemplateLoader[]{new FileTemplateLoader(), new ContextClassLoaderTemplateLoader()};
            MultiTemplateLoader multiLoader = new MultiTemplateLoader(loaders);

            configuration.setTemplateLoader(multiLoader);
            template = configuration.getTemplate(config.getResource());
        }

        // We'll use the DefaultSAXElementSerializer to write out the targeted element
        // where the action is not "replace" or "bindto".
        targetWriter = new DefaultSAXElementSerializer();
        targetWriter.setWriterOwner(this);
    }

    /**
     * Apply the template for DOM.
     *
     * @param element          The targeted DOM Element.
     * @param executionContext The Smooks execution context.
     * @throws org.milyn.SmooksException Failed to apply template. See cause.
     */
    protected void visit(Element element, ExecutionContext executionContext) throws SmooksException {
        // Apply the template...
        String templatingResult;
        try {
            Writer writer = new StringWriter();
            Map beans = BeanAccessor.getBeanMap(executionContext);

            template.process(beans, writer);
            writer.flush();
            templatingResult = writer.toString();
        } catch (TemplateException e) {
            throw new SmooksException("Failed to apply FreeMarker template to fragment '" + DomUtils.getXPath(element) + "'.  Resource: " + config, e);
        } catch (IOException e) {
            throw new SmooksException("Failed to apply FreeMarker template to fragment '" + DomUtils.getXPath(element) + "'.  Resource: " + config, e);
        }

        Node resultNode;
        if (getAction() != Action.ADDTO && element == element.getOwnerDocument().getDocumentElement()) {
            // We can't replace the root node with a text node (or insert before/after), so we need
            // to replace the root node with a <context-object key="xxx" /> element and bind the result to the
            // execution context under the specified key. The ContextObjectSerializationUnit will take
            // care of the rest.
            String key = "FreeMarkerObject:" + DomUtils.getXPath(element);
            executionContext.setAttribute(key, templatingResult);
            resultNode = ContextObjectSerializationUnit.createElement(element.getOwnerDocument(), key);
        } else {
            // Create the replacement DOM text node containing the applied template...
            resultNode = element.getOwnerDocument().createTextNode(templatingResult);
        }

        // Process the templating action, supplying the templating result...
        processTemplateAction(element, resultNode, executionContext);
    }

    /* ------------------------------------------------------------------------------------------------------------------------------------------
    SAX Processing methods.
    ------------------------------------------------------------------------------------------------------------------------------------------ */

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        String outputStreamResourceName = getOutputStreamResource();
        if(outputStreamResourceName != null) {
            if(applyTemplateBefore()) {
                applyTemplateToOutputStream(element, outputStreamResourceName, executionContext);
            }
        } else {
            if (getAction() == Action.INSERT_BEFORE) {
                // apply the template...
                applyTemplate(element, executionContext);
                // write the start of the element...
                if (executionContext.getDeliveryConfig().isDefaultSerializationOn()) {
                    targetWriter.visitBefore(element, executionContext);
                }
            } else if (getAction() != Action.REPLACE && getAction() != Action.BIND_TO) {
                // write the start of the element...
                if (executionContext.getDeliveryConfig().isDefaultSerializationOn()) {
                    targetWriter.visitBefore(element, executionContext);
                }
            } else {
                // Just acquire ownership of the writer...
                if (executionContext.getDeliveryConfig().isDefaultSerializationOn()) {
                    element.getWriter(this);
                }
            }
        }
    }

    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException {
        if(getOutputStreamResource() == null) {
            if (getAction() != Action.REPLACE && getAction() != Action.BIND_TO) {
                if (executionContext.getDeliveryConfig().isDefaultSerializationOn()) {
                    targetWriter.onChildText(element, childText, executionContext);
                }
            }
        }
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
        if(getOutputStreamResource() == null) {
            if (getAction() != Action.REPLACE && getAction() != Action.BIND_TO) {
                if (executionContext.getDeliveryConfig().isDefaultSerializationOn()) {
                    targetWriter.onChildElement(element, childElement, executionContext);
                }
            }
        }
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        String outputStreamResourceName = getOutputStreamResource();
        if(outputStreamResourceName != null) {
            if(!applyTemplateBefore()) {
                applyTemplateToOutputStream(element, outputStreamResourceName, executionContext);
            }
        } else {
            if (getAction() == Action.ADDTO) {
                if (!targetWriter.isStartWritten(element)) {
                    if (executionContext.getDeliveryConfig().isDefaultSerializationOn()) {
                        targetWriter.writeStartElement(element);
                    }
                }
                // apply the template...
                applyTemplate(element, executionContext);
                // write the end of the element...
                if (executionContext.getDeliveryConfig().isDefaultSerializationOn()) {
                    targetWriter.visitAfter(element, executionContext);
                }
            } else if (getAction() == Action.INSERT_BEFORE) {
                // write the end of the element...
                if (executionContext.getDeliveryConfig().isDefaultSerializationOn()) {
                    targetWriter.visitAfter(element, executionContext);
                }
            } else if (getAction() == Action.INSERT_AFTER) {
                // write the end of the element...
                if (executionContext.getDeliveryConfig().isDefaultSerializationOn()) {
                    targetWriter.visitAfter(element, executionContext);
                }
                // apply the template...
                applyTemplate(element, executionContext);
            } else if (getAction() == Action.REPLACE || getAction() == Action.BIND_TO) {
                // just apply the template...
                applyTemplate(element, executionContext);
            }
        }
    }

    private void applyTemplateToOutputStream(SAXElement element, String outputStreamResourceName, ExecutionContext executionContext) {
        Writer writer = AbstractOutputStreamResource.getOutputWriter(outputStreamResourceName, executionContext);
        applyTemplate(element, executionContext, writer);
    }

    private void applyTemplate(SAXElement element, ExecutionContext executionContext) throws SmooksException {
        if (getAction() == Action.BIND_TO) {
            String bindId = getBindId();

            if (bindId == null) {
                throw new SmooksConfigurationException("'bindto' templating action configurations must also specify a 'bindId' configuration for the Id under which the result is bound to the ExecutionContext");
            }
            Writer writer = new StringWriter();
            applyTemplate(element, executionContext, writer);
            BeanAccessor.addBean(executionContext, bindId, writer.toString());
        } else {
            Writer writer = element.getWriter(this);
            applyTemplate(element, executionContext, writer);
        }
    }

    private void applyTemplate(SAXElement element, ExecutionContext executionContext, Writer writer) throws SmooksException {
        try {
            Map beans = BeanAccessor.getBeanMap(executionContext);
            template.process(beans, writer);
            writer.flush();
        } catch (TemplateException e) {
            throw new SmooksException("Failed to apply FreeMarker template to fragment '" + SAXUtil.getXPath(element) + "'.  Resource: " + config, e);
        } catch (IOException e) {
            throw new SmooksException("Failed to apply FreeMarker template to fragment '" + SAXUtil.getXPath(element) + "'.  Resource: " + config, e);
        }
    }

    private static class ContextClassLoaderTemplateLoader extends URLTemplateLoader {
        protected URL getURL(String name) {
            return Thread.currentThread().getContextClassLoader().getResource(name);
        }
    }
}