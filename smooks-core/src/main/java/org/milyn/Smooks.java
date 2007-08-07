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

package org.milyn;

import java.io.*;
import java.util.LinkedHashMap;
import java.net.URISyntaxException;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.standalone.StandaloneApplicationContext;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.SmooksDOMFilter;
import org.milyn.profile.ProfileSet;
import org.milyn.profile.UnknownProfileMemberException;
import org.milyn.assertion.AssertArgument;
import org.milyn.resource.URIResourceLocator;
import org.milyn.net.URIUtil;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Smooks standalone execution class.
 * <p/>
 * Additional configurations can be carried out on the {@link org.milyn.Smooks} instance
 * through the {@link org.milyn.SmooksUtil} class.
 * <p/>
 * The basic usage scenario for this class might be as follows:
 * <ol>
 * <li>Develop (or reuse) an implementation of {@link org.milyn.delivery.dom.DOMElementVisitor} to
 * perform some transformation/analysis operation on a message.  There are a number of prebuilt
 * and reuseable implemntations available as
 * "<a target="new" href="http://milyn.codehaus.org/Smooks#Smooks-smookscartridges">Smooks Cartridges</a>".</li>
 * <li>Write a {@link org.milyn.cdr.SmooksResourceConfiguration resource configuration} to target the {@link org.milyn.delivery.dom.DOMElementVisitor}
 * implementation at the target fragment of the message being processed.</li>
 * <li>Apply the logic as follows:
 * <pre>
 * Smooks smooks = {@link #Smooks(String) new Smooks}("smooks-config.xml");
 * {@link ExecutionContext} execContext;
 * 
 * execContext = smooks.{@link #createExecutionContext createExecutionContext}();
 * smooks.{@link #filter filter}(new {@link StreamSource}(...), new {@link StreamResult}(...), execContext);
 * </pre>
 * </li>
 * </ol>
 * Remember, you can implement and apply multiple {@link org.milyn.delivery.dom.DOMElementVisitor DOMElementVisitors}
 * within the context of a single filtering operation.  You can also target
 * {@link org.milyn.delivery.dom.DOMElementVisitor DOMElementVisitors} based on target profiles, and so use a single
 * configuration to process multiple messages by sharing profiles across your message set.
 * <p/>
 * See <a target="new" href="http://milyn.codehaus.org/Tutorials">Smooks Tutorials</a>.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Smooks {

    private static Log logger = LogFactory.getLog(Smooks.class);
    private StandaloneApplicationContext context;
    private boolean initialised = false;

    /**
     * Public Default Constructor.
     * <p/>
     * Resource configurations can be added through calls to
     * {@link #addConfigurations(String)} or {@link #addConfigurations(String,java.io.InputStream)}.
     */
    public Smooks() {
        context = new StandaloneApplicationContext();
    }

    /**
     * Public constructor.
     * <p/>
     * Adds the set of {@link SmooksResourceConfiguration resources} via the {@link #addConfigurations(String)} method,
     * which resolves the resourceURI parameter using a {@link org.milyn.resource.URIResourceLocator}.
     * <p/>
     * Additional resource configurations can be added through calls to
     * {@link #addConfigurations(String)} or {@link #addConfigurations(String,java.io.InputStream)}.
     *
     * @param resourceURI XML resource configuration stream URI.
     * @throws IOException  Error reading resource stream.
     * @throws SAXException Error parsing the resource stream.
     * @see SmooksResourceConfiguration
     */
    public Smooks(String resourceURI) throws IOException, SAXException {
        this();
        addConfigurations(resourceURI);
    }

    /**
     * Add a set of resource configurations to this Smooks instance.
     * <p/>
     * Uses the {@link org.milyn.resource.URIResourceLocator} class to load the resource.
     * <p/>
     * These configurations do not overwrite previously added configurations.
     * They are added to the list of configurations on this Smooks instance.
     *
     * @param resourceURI The URI string for the resource configuration list. See
     *                    {@link org.milyn.resource.URIResourceLocator}.
     * @throws IOException  Error reading resource stream.
     * @throws SAXException Error parsing the resource stream.
     */
    public void addConfigurations(String resourceURI) throws IOException, SAXException {
        AssertArgument.isNotNullAndNotEmpty(resourceURI, "resourceURI");

        InputStream resourceConfigStream;
        URIResourceLocator resourceLocator = new URIResourceLocator();

        resourceConfigStream = resourceLocator.getResource(resourceURI);
        try {
            URI resourceURIObj = new URI(resourceURI);
            addConfigurations(URIUtil.getParent(resourceURIObj).toString(), resourceConfigStream);
        } catch (URISyntaxException e) {
            logger.error("Failed to load Smooks resource configuration '" + resourceURI + "'.", e);
        } finally {
            resourceConfigStream.close();
        }
    }

    /**
     * Add a set of resource configurations to this Smooks instance.
     * <p/>
     * These configurations do not overwrite previously added configurations.
     * They are added to the list of configurations on this Smooks instance.
     * <p/>
     * The base URI is required for resolving resource imports.  Just specify
     * the location of the resource file.
     *
     * @param baseURI The base URI string for the resource configuration list. See
     *                    {@link org.milyn.resource.URIResourceLocator}.
     * @param resourceConfigStream
     * @throws IOException  Error reading resource stream.
     * @throws SAXException Error parsing the resource stream.
     */
    public void addConfigurations(String baseURI, InputStream resourceConfigStream) throws SAXException, IOException {
        AssertArgument.isNotNullAndNotEmpty(baseURI, "baseURI");
        AssertArgument.isNotNull(resourceConfigStream, "resourceConfigStream");
        try {
            context.getStore().registerResources(baseURI, resourceConfigStream);
        } catch (URISyntaxException e) {
            throw new IOException("Failed to read resource configuration. Invalid 'baseURI'.");
        }
    }

    /**
     * Create a {@link StandaloneExecutionContext} instance for use on this Smooks instance.
     * <p/>
     * The created context is profile agnostic and should be used where profile based targeting is not in use.
     * <p/>
     * The context returned from this method is used in subsequent calls to
     * {@link #filter(javax.xml.transform.Source,javax.xml.transform.Result,org.milyn.container.standalone.StandaloneExecutionContext)}.
     * It allows access to the execution context instance
     * before and after calls on this method.  This means the caller has an opportunity to set and get data
     * {@link org.milyn.container.BoundAttributeStore bound} to the execution context (before and after the calls), providing the
     * caller with a mechanism for interacting with the content {@link SmooksDOMFilter filtering} phases.
     *
     * @return Execution context instance.
     */
    public StandaloneExecutionContext createExecutionContext() {
        return new StandaloneExecutionContext(StandaloneApplicationContext.OPEN_PROFILE_NAME, new LinkedHashMap(), context);
    }

    /**
     * Create a {@link StandaloneExecutionContext} instance for use on this Smooks instance.
     * <p/>
     * The created context is profile aware and should be used where profile based targeting is in use. In this case,
     * the transfromation/analysis resources must be configured with profile targeting information.
     * <p/>
     * The context returned from this method is used in subsequent calls to
     * {@link #filter(javax.xml.transform.Source,javax.xml.transform.Result,org.milyn.container.standalone.StandaloneExecutionContext)}.
     * It allows access to the execution context instance
     * before and after calls on this method.  This means the caller has an opportunity to set and get data
     * {@link org.milyn.container.BoundAttributeStore bound} to the execution context (before and after the calls), providing the
     * caller with a mechanism for interacting with the content {@link SmooksDOMFilter filtering} phases.
     *
     * @param targetProfile The target profile ({@link ProfileSet base profile}) on behalf of whom the filtering/serialisation
     *                      filter is to be executed.
     * @return Execution context instance.
     * @throws UnknownProfileMemberException Unknown target profile.
     */
    public StandaloneExecutionContext createExecutionContext(String targetProfile) throws UnknownProfileMemberException {
        return new StandaloneExecutionContext(targetProfile, new LinkedHashMap(), context);
    }

    /**
     * Filter the content in the supplied {@link javax.xml.transform.Source} instance, outputing the result
     * to the supplied {@link javax.xml.transform.Result} instance.
     * <p/>
     * This method always executes the {@link SmooksDOMFilter visit phases} of content
     * processing.  It will also execute the serialization phase if the
     * supplied result is a {@link javax.xml.transform.stream.StreamResult}.
     * <p/>
     * SAX based Source and Result are not yet supported.
     *
     * @param source           The content Source.
     * @param result           The content Result.  To serialize the result, supply a {@link javax.xml.transform.stream.StreamResult}.
     *                         To have the result returned as a DOM, supply a {@link javax.xml.transform.dom.DOMResult}.
     * @param executionContext The {@link StandaloneExecutionContext} for this filter operation. See
     *                         {@link #createExecutionContext(String)}.
     * @throws SmooksException Failed to filter.
     */
    public void filter(Source source, Result result, StandaloneExecutionContext executionContext) throws SmooksException {
        AssertArgument.isNotNull(source, "source");
        AssertArgument.isNotNull(result, "result");
        AssertArgument.isNotNull(executionContext, "executionContext");

        if (!(source instanceof StreamSource) && !(source instanceof DOMSource)) {
            throw new IllegalArgumentException(source.getClass().getName() + " Source types not yet supported.");
        }
        if (!(result instanceof StreamResult) && !(result instanceof DOMResult)) {
            throw new IllegalArgumentException(result.getClass().getName() + " Result types not yet supported.");
        }

        SmooksDOMFilter smooks = new SmooksDOMFilter(executionContext);
        try {
            Node resultNode;

            // Filter the Source....
            if (source instanceof StreamSource) {
                resultNode = smooks.filter(new InputStreamReader(((StreamSource) source).getInputStream(), executionContext.getContentEncoding()));
            } else {
                Node node = ((DOMSource) source).getNode();
                if (!(node instanceof Document)) {
                    throw new IllegalArgumentException("DOMSource Source types must contain a Document node.");
                }
                resultNode = smooks.filter((Document) node);
            }

            // Populate the Result
            if (result instanceof StreamResult) {
                Writer writer = ((StreamResult) result).getWriter();

                if (writer == null) {
                    writer = new OutputStreamWriter(((StreamResult) result).getOutputStream(), executionContext.getContentEncoding());
                }
                try {
                    smooks.serialize(resultNode, writer);
                } catch (IOException e) {
                    logger.error("Error writing result to output stream.", e);
                }
            } else {
                ((DOMResult) result).setNode(resultNode);
            }
        } catch (UnsupportedEncodingException e) {
            throw new Error("Unexpected exception.  Encoding has already been validated as being unsupported.", e);
        } finally {
            if (source instanceof StreamSource) {
                try {
                    ((StreamSource) source).getInputStream().close();
                } catch (IOException e) {
                    logger.warn("Failed to close input stream.", e);
                }
            }
            if (result instanceof StreamResult) {
                Writer writer = ((StreamResult) result).getWriter();

                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        logger.warn("Failed to close output writer.", e);
                    }
                } else {
                    try {
                        ((StreamResult) result).getOutputStream().close();
                    } catch (IOException e) {
                        logger.warn("Failed to close output stream.", e);
                    }
                }
            }
        }
    }

    /**
     * Get the Smooks {@link org.milyn.container.ApplicationContext} associated with
     * this Smooks instance.
     *
     * @return The Smooks {@link org.milyn.container.ApplicationContext}.
     */
    public StandaloneApplicationContext getApplicationContext() {
        return context;
    }
}
