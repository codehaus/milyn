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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.container.standalone.StandaloneApplicationContext;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.delivery.Filter;
import org.milyn.delivery.Visitor;
import org.milyn.delivery.VisitorConfigMap;
import org.milyn.event.ExecutionEventListener;
import org.milyn.event.types.FilterLifecycleEvent;
import org.milyn.net.URIUtil;
import org.milyn.payload.FilterResult;
import org.milyn.payload.FilterSource;
import org.milyn.profile.Profile;
import org.milyn.profile.ProfileSet;
import org.milyn.profile.UnknownProfileMemberException;
import org.milyn.resource.URIResourceLocator;
import org.xml.sax.SAXException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Smooks executor class.
 * <p/>
 * Additional configurations can be carried out on the {@link org.milyn.Smooks} instance
 * through the {@link org.milyn.SmooksUtil} class.
 * <p/>
 * The basic usage scenario for this class might be as follows:
 * <ol>
 * <li>Develop (or reuse) an implementation of {@link org.milyn.delivery.dom.DOMElementVisitor}/{@link org.milyn.delivery.sax.SAXElementVisitor} to
 * perform some transformation/analysis operation on a message.  There are a number of prebuilt
 * and reuseable implemntations available as
 * "<a target="new" href="http://milyn.codehaus.org/Smooks#Smooks-smookscartridges">Smooks Cartridges</a>".</li>
 * <li>Write a {@link org.milyn.cdr.SmooksResourceConfiguration resource configuration} to target the {@link org.milyn.delivery.dom.DOMElementVisitor}/{@link org.milyn.delivery.sax.SAXElementVisitor}
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
 * Remember, you can implement and apply multiple {@link org.milyn.delivery.dom.DOMElementVisitor DOMElementVisitors}/{@link org.milyn.delivery.sax.SAXElementVisitor}
 * within the context of a single filtering operation.  You can also target
 * {@link org.milyn.delivery.dom.DOMElementVisitor DOMElementVisitors}/{@link org.milyn.delivery.sax.SAXElementVisitor} based on target profiles, and so use a single
 * configuration to process multiple messages by sharing profiles across your message set.
 * <p/>
 * See <a target="new" href="http://milyn.codehaus.org/Tutorials">Smooks Tutorials</a>.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Smooks {

    private static Log logger = LogFactory.getLog(Smooks.class);
    private StandaloneApplicationContext context;
    private ClassLoader classLoader;
    /**
     * Manually added visitors.  In contract to those that are constructed and configured dynamically from
     * an XML configuration stream.
     */
    private VisitorConfigMap visitors = new VisitorConfigMap();
    /**
     * Flag indicating whether or not the Smooks instance is configurable.  It becomes unconfigurable
     * after the first execution context has been created.
     */
    private volatile boolean isConfigurable = true;

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
     * Public constructor.
     * <p/>
     * Adds the set of {@link SmooksResourceConfiguration resources} via the {@link #addConfigurations(java.io.InputStream)}.
     * <p/>
     * Additional resource configurations can be added through calls to
     * <code>addConfigurations</code> method set.
     *
     * @param resourceConfigStream XML resource configuration stream.
     * @throws IOException  Error reading resource stream.
     * @throws SAXException Error parsing the resource stream.
     * @see SmooksResourceConfiguration
     */
    public Smooks(InputStream resourceConfigStream) throws IOException, SAXException {
        this();
        addConfigurations(resourceConfigStream);
    }

    /**
     * Get the ClassLoader associated with this Smooks instance.
     * @return The ClassLoader instance.
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Set the ClassLoader associated with this Smooks instance.
     * @param classLoader The ClassLoader instance.
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Set the default stream filter type on this Smooks instance.
     * @param filterType The filter type.
     */
    public void setFilterType(Filter.StreamFilterType filterType) {
        assertIsConfigurable();
        Filter.setFilterType(this, filterType);
    }

    /**
     * Add a visitor instance to <code>this</code> Smooks instance.
     *
     * @param targetSelector The message fragment target selector.
     * @param visitor The visitor implementation.
     */
    public SmooksResourceConfiguration addVisitor(String targetSelector, Visitor visitor) {
        return addVisitor(visitor, targetSelector, null);
    }

    /**
     * Add a visitor instance to <code>this</code> Smooks instance.
     *
     * @param visitor The visitor implementation.
     * @param targetSelector The message fragment target selector.
     * @param targetSelectorNS The message fragment target selector namespace.
     */
    public SmooksResourceConfiguration addVisitor(Visitor visitor, String targetSelector, String targetSelectorNS) {
        assertIsConfigurable();
        AssertArgument.isNotNull(visitor, "visitor");
        AssertArgument.isNotNull(targetSelector, "targetSelector");

        SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration(targetSelector, visitor.getClass().getName());

        resourceConfig.setSelectorNamespaceURI(targetSelectorNS);

        // And configure/initialize the instance...
        Configurator.processFieldContextAnnotation(visitor, context);
        Configurator.initialise(visitor);

        visitors.addVisitor(resourceConfig.getTargetElement(), resourceConfig, visitor);

        return resourceConfig;
    }

    /**
     * Add a resource configuration to this Smooks instance.
     * <p/>
     * These configurations do not overwrite previously added configurations.
     * They are added to the list of configurations on this Smooks instance.
     *
     * @param resourceConfig The resource configuration to be added.
     */
    public void addConfiguration(SmooksResourceConfiguration resourceConfig) {
        AssertArgument.isNotNull(resourceConfig, "resourceConfig");
        assertIsConfigurable();
        context.getStore().registerResource(resourceConfig);
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
     * @param resourceConfigStream The resource configuration stream.
     * @throws IOException  Error reading resource stream.
     * @throws SAXException Error parsing the resource stream.
     */
    public void addConfigurations(String baseURI, InputStream resourceConfigStream) throws SAXException, IOException {
        assertIsConfigurable();
        AssertArgument.isNotNullAndNotEmpty(baseURI, "baseURI");
        AssertArgument.isNotNull(resourceConfigStream, "resourceConfigStream");
        try {
            context.getStore().registerResources(baseURI, resourceConfigStream);
        } catch (URISyntaxException e) {
            throw new IOException("Failed to read resource configuration. Invalid 'baseURI'.");
        }
    }

    /**
     * Add a set of resource configurations to this Smooks instance.
     * <p/>
     * Calls {@link #addConfigurations(String, java.io.InputStream)} with a baseURI of "./",
     * which is the default base URI on all {@link org.milyn.resource.URIResourceLocator}
     * instances.
     *
     * @param resourceConfigStream The resource configuration stream.
     * @throws IOException  Error reading resource stream.
     * @throws SAXException Error parsing the resource stream.
     */
    public void addConfigurations(InputStream resourceConfigStream) throws SAXException, IOException {
        addConfigurations("./", resourceConfigStream);
    }

    /**
     * Create a {@link ExecutionContext} instance for use on this Smooks instance.
     * <p/>
     * The created context is profile agnostic and should be used where profile based targeting is not in use.
     * <p/>
     * The context returned from this method is used in subsequent calls to
     * {@link #filter(javax.xml.transform.Source,javax.xml.transform.Result,org.milyn.container.ExecutionContext)}.
     * It allows access to the execution context instance
     * before and after calls on this method.  This means the caller has an opportunity to set and get data
     * {@link org.milyn.container.BoundAttributeStore bound} to the execution context (before and after the calls), providing the
     * caller with a mechanism for interacting with the content {@link org.milyn.delivery.dom.SmooksDOMFilter filtering} phases.
     *
     * @return Execution context instance.
     */
    public ExecutionContext createExecutionContext() {
        return createExecutionContext(Profile.DEFAULT_PROFILE);
    }

    /**
     * Create a {@link ExecutionContext} instance for use on this Smooks instance.
     * <p/>
     * The created context is profile aware and should be used where profile based targeting is in use. In this case,
     * the transfromation/analysis resources must be configured with profile targeting information.
     * <p/>
     * The context returned from this method is used in subsequent calls to
     * {@link #filter(javax.xml.transform.Source,javax.xml.transform.Result,org.milyn.container.ExecutionContext)}.
     * It allows access to the execution context instance
     * before and after calls on this method.  This means the caller has an opportunity to set and get data
     * {@link org.milyn.container.BoundAttributeStore bound} to the execution context (before and after the calls), providing the
     * caller with a mechanism for interacting with the content {@link org.milyn.delivery.dom.SmooksDOMFilter filtering} phases.
     *
     * @param targetProfile The target profile ({@link ProfileSet base profile}) on behalf of whom the filtering/serialisation
     *                      filter is to be executed.
     * @return Execution context instance.
     * @throws UnknownProfileMemberException Unknown target profile.
     */
    public ExecutionContext createExecutionContext(String targetProfile) throws UnknownProfileMemberException {
        isConfigurable = false;
        if(classLoader != null) {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            try {
                return new StandaloneExecutionContext(targetProfile, context, visitors);
            } finally {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        } else {
            return new StandaloneExecutionContext(targetProfile, context, visitors);
        }
    }

    /**
     * Filter the content in the supplied {@link javax.xml.transform.Source} instance.
     * <p/>
     * Not producing a {@link Result}.
     *
     * @param source           The content Source.
     * @throws SmooksException Failed to filter.
     */
    public void filter(Source source) throws SmooksException {
        filter(source, null, createExecutionContext());
    }

    /**
     * Filter the content in the supplied {@link javax.xml.transform.Source} instance, outputing the result
     * to the supplied {@link javax.xml.transform.Result} instance.
     *
     * @param source           The content Source.
     * @param result           The content Result.  To serialize the result, supply a {@link javax.xml.transform.stream.StreamResult}.
     *                         To have the result returned as a DOM, supply a {@link javax.xml.transform.dom.DOMResult}.
     * @throws SmooksException Failed to filter.
     */
    public void filter(Source source, Result result) throws SmooksException {
        filter(source, result, createExecutionContext());
    }

    /**
     * Filter the content in the supplied {@link javax.xml.transform.Source} instance, outputing the result
     * to the supplied {@link javax.xml.transform.Result} instance.
     *
     * @param source           The content Source.
     * @param result           The content Result.  To serialize the result, supply a {@link javax.xml.transform.stream.StreamResult}.
     *                         To have the result returned as a DOM, supply a {@link javax.xml.transform.dom.DOMResult}.
     * @param executionContext The {@link ExecutionContext} for this filter operation. See
     *                         {@link #createExecutionContext(String)}.
     * @throws SmooksException Failed to filter.
     */
    public void filter(Source source, Result result, ExecutionContext executionContext) throws SmooksException {
        AssertArgument.isNotNull(source, "source");
        AssertArgument.isNotNull(executionContext, "executionContext");

        if(classLoader != null) {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            try {
                _filter(source, result, executionContext);
            } finally {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        } else {
            _filter(source, result, executionContext);
        }
    }

    private void _filter(Source source, Result result, ExecutionContext executionContext) {
        ExecutionEventListener eventListener = executionContext.getEventListener();

        try {
            Filter.setCurrentExecutionContext(executionContext);
            try {
                if(eventListener != null) {
                    eventListener.onEvent(new FilterLifecycleEvent(FilterLifecycleEvent.EventType.STARTED));
                }

                Filter messageFilter = executionContext.getDeliveryConfig().newFilter(executionContext);

                Filter.setFilter(messageFilter);
                try {
                    // Attach the source and result to the context...
                    FilterSource.setSource(source, executionContext);
                    FilterResult.setResult(result, executionContext);

                    messageFilter.doFilter(source, result);
                } catch(SmooksException e) {
                    executionContext.setTerminationError(e);
                    throw e;
                } catch (Throwable t) {
                    executionContext.setTerminationError(t);
                    throw new SmooksException("Smooks Filtering operation failed.", t);
                } finally {
                    messageFilter.cleanup();
                    Filter.removeCurrentFilter();
                }
            } finally {
                Filter.removeCurrentExecutionContext();
            }
        } finally {
            if(eventListener != null) {
                eventListener.onEvent(new FilterLifecycleEvent(FilterLifecycleEvent.EventType.FINISHED));
            }
        }
    }

    /**
     * Get the Smooks {@link org.milyn.container.ApplicationContext} associated with
     * this Smooks instance.
     *
     * @return The Smooks {@link org.milyn.container.ApplicationContext}.
     */
    public ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * Close this Smooks instance and all associated resources.
     * <p/>
     * Should result in the {@link org.milyn.delivery.annotation.Uninitialize uninitialization}
     * of all allocated {@link org.milyn.delivery.ContentHandler} instances.
     */
    public void close() {
        context.getStore().close();
    }

    /**
     * Assert that the instance is configurable, throwing an exception if it is not.
     */
    private void assertIsConfigurable() {
        if(!isConfigurable) {
            throw new UnsupportedOperationException("Unsupported call to Smooks instance configuration method after Smooks instance has created an ExecutionContext.");
        }
    }
}