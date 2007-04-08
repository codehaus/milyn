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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.standalone.StandaloneApplicationContext;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.delivery.SmooksXML;
import org.milyn.profile.DefaultProfileStore;
import org.milyn.profile.ProfileSet;
import org.milyn.profile.ProfileStore;
import org.milyn.profile.UnknownProfileMemberException;
import org.milyn.resource.URIResourceLocator;
import org.milyn.assertion.AssertArgument;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Smooks standalone execution class.
 * <p/>
 * See <a href="http://milyn.codehaus.org/Tutorials">Smooks Tutorials</a>.
 *
 * @author tfennelly
 */
public class Smooks {

    private static Log logger = LogFactory.getLog(Smooks.class);
    private StandaloneApplicationContext context;

    /**
     * Public Default Constructor.
     */
    public Smooks() {
        URIResourceLocator resourceLocator = new URIResourceLocator();

        resourceLocator.setBaseURI(URI.create(URIResourceLocator.SCHEME_CLASSPATH + ":/"));
        context = new StandaloneApplicationContext(new DefaultProfileStore(), resourceLocator);
    }

    /**
     * Public constructor.
     * <p/>
     * Register the set of resources specified in the supplied XML configuration
     * stream.  Additional resource configurations can be registered through calls to
     * {@link #registerResources(String, java.io.InputStream)}.
     *
     * @param resourceConfigStream XML resource configuration stream.
     * @throws SAXException Error parsing the resource stream.
     * @throws IOException  Error reading resource stream.
     * @see SmooksResourceConfiguration
     */
    public Smooks(InputStream resourceConfigStream) throws IOException, SAXException {
        this();
        registerResources("via-constructor", resourceConfigStream);
    }

    /**
     * Create a {@link StandaloneExecutionContext} instance for use on this Smooks instance.
     * <p/>
     * The created context is profile agnostic and should be used where target profiling is not in use.
     * <p/>
     * The context returned from this method is used in subsequent calls to {@link #filter(StandaloneExecutionContext,InputStream)} and
     * {@link #serialize(StandaloneExecutionContext,Node,Writer)}.  It allows access to the execution context instance
     * before and after calls on these methods.  This means the caller has an opportunity to set and get data
     * {@link org.milyn.container.BoundAttributeStore bound} to the execution context (before and after the calls), providing the
     * caller with a mechanism for interacting with the filtering and serialisation processes.
     * <p/>
     *
     * @return Request instance.
     */
    public StandaloneExecutionContext createExecutionContext() {
        return new StandaloneExecutionContext(StandaloneApplicationContext.OPEN_PROFILE_NAME, new LinkedHashMap(), context);
    }

    /**
     * Create a {@link StandaloneExecutionContext} instance for use on this Smooks instance.
     * <p/>
     * The created context is profile aware and should be used where target profiling is in use. In this case,
     * the transfromation/analysis resources must be configured with profile targeting information.
     * <p/>
     * The context returned from this method is used in subsequent calls to {@link #filter(StandaloneExecutionContext,InputStream)} and
     * {@link #serialize(StandaloneExecutionContext,Node,Writer)}.  It allows access to the execution context instance
     * before and after calls on these methods.  This means the caller has an opportunity to set and get data
     * {@link org.milyn.container.BoundAttributeStore bound} to the execution context (before and after the calls), providing the
     * caller with a mechanism for interacting with the filtering and serialisation processes.
     * <p/>
     *
     * @param targetProfile The target profile ({@link ProfileSet base profile}) on behalf of whom the filtering/serialisation
     *                      process is to be executed.
     *                      {@link #filter(StandaloneExecutionContext,InputStream) filter(2)}, or
     *                      {@link #serialize(StandaloneExecutionContext,Node,Writer)} calls call {@link org.milyn.container.ExecutionContext#getDocumentSource()}.
     * @return Request instance.
     */
    public StandaloneExecutionContext createExecutionContext(String targetProfile) {
        return new StandaloneExecutionContext(targetProfile, new LinkedHashMap(), context);
    }

    /**
     * Filter the content at the specified {@link InputStream} for the useragent associated with the
     * supplied executionContext object.
     * <p/>
     * This method allows access to the container executionContext instance before and after the filtering process.
     * This means the caller has an opportunity to set and get data bound to the executionContext (before and after).
     * <p/>
     * The content of the Node returned is totally dependent on the configured
     * {@link org.milyn.delivery.assemble.AssemblyUnit}, {@link org.milyn.delivery.process.ProcessingUnit}
     * and {@link org.milyn.delivery.serialize.SerializationUnit} implementations.
     *
     * @param executionContext The {@link StandaloneExecutionContext} for this filter operation. See
     *                         {@link #createExecutionContext(String)}.
     * @param stream           Stream to be processed.  Will be closed before returning.
     * @return The Smooks processed content DOM {@link Node}.
     * @throws SmooksException Excepting processing content stream.
     */
    public Node filter(StandaloneExecutionContext executionContext, InputStream stream) throws SmooksException {
        if (executionContext == null) {
            throw new IllegalArgumentException("null 'executionContext' arg in method call.");
        }
        if (stream == null) {
            throw new IllegalArgumentException("null 'stream' arg in method call.");
        }

        SmooksXML smooks = new SmooksXML(executionContext);
        try {
            try {
                return smooks.filter(new InputStreamReader(stream, executionContext.getContentEncoding()));
            } catch (UnsupportedEncodingException e) {
                Error error = new Error("Unexpected exception.  Encoding has already been validated as being unsupported.");
                error.initCause(e);
                throw error;
            }
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                logger.error("Exception closing input stream.", e);
            }
        }
    }

    /**
     * Filter the content at the specified {@link InputStream} for the specified targetProfile
     * and serialise into a String buffer.
     * <p/>
     * The content of the buffer returned is totally dependent on the configured
     * {@link org.milyn.delivery.process.ProcessingUnit} and {@link org.milyn.delivery.serialize.SerializationUnit}
     * implementations.
     *
     * @param executionContext Execution context for the process.
     * @param stream        Stream to be processed.  Will be closed before returning.
     * @return The Smooks processed content buffer.
     * @throws IOException     Exception using or closing the supplied InputStream.
     * @throws SmooksException Excepting processing content stream.
     */
    public String filterAndSerialize(StandaloneExecutionContext executionContext, InputStream stream) throws SmooksException {
        String responseBuf = null;
        CharArrayWriter writer = new CharArrayWriter();
        try {
            Node node;

            node = filter(executionContext, stream);
            serialize(executionContext, node, writer);
            responseBuf = writer.toString();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    new SmooksException("Failed to close stream...", e);
                }
            }
            writer.close();
        }

        return responseBuf;
    }

    /**
     * Serialise the supplied node.
     *
     * @param executionContext The {@link StandaloneExecutionContext} for this serialisation operation. See
     *                         {@link #createExecutionContext(String)}.
     * @param node             Node to be serialised.
     * @param writer           Serialisation output writer.
     * @throws IOException     Unable to write to output writer.
     * @throws SmooksException Unable to serialise due to bad Smooks environment.  Check cause.
     */
    public void serialize(StandaloneExecutionContext executionContext, Node node, Writer writer) throws SmooksException {
        SmooksXML smooks;

        if (node == null) {
            throw new IllegalArgumentException("null 'node' arg in method call.");
        }
        if (writer == null) {
            throw new IllegalArgumentException("null 'writer' arg in method call.");
        }

        smooks = new SmooksXML(executionContext);
        try {
            smooks.serialize(node, writer);
        } catch (IOException e) {
            throw new SmooksException("Serialisation failed...", e);
        }
    }

    /**
     * Manually register a set of profiles.
     * <p/>
     * ProfileSets will typically be registered via the config, but it is useful
     * to be able to perform this task manually.
     *
     * @param profileSet The profile set to be registered.
     */
    public void registerProfileSet(ProfileSet profileSet) {
        AssertArgument.isNotNull(profileSet, "profileSet");

        ProfileStore profileStore = context.getProfileStore();
        try {
            profileStore.getProfileSet(profileSet.getBaseProfile());
            logger.warn("ProfileSet [" + profileSet.getBaseProfile() + "] already registered.  Not registering new profile set.");
        } catch (UnknownProfileMemberException e) {
            // It's an unregistered profileset...
            profileStore.addProfileSet(profileSet.getBaseProfile(), profileSet);
        }
    }

    /**
     * Register a {@link SmooksResourceConfiguration} on this {@link Smooks} instance.
     *
     * @param resourceConfig The Content Delivery Resource definition to be  registered.
     */
    public void registerResource(SmooksResourceConfiguration resourceConfig) {
        if (resourceConfig == null) {
            throw new IllegalArgumentException("null 'resourceConfig' arg in method call.");
        }
        context.getStore().registerResource(resourceConfig);
    }

    /**
     * Register the set of resources specified in the supplied XML configuration
     * stream.
     *
     * @param name                 The name of the resource set.
     * @param resourceConfigStream XML resource configuration stream.
     * @throws SAXException Error parsing the resource stream.
     * @throws IOException  Error reading resource stream.
     * @see SmooksResourceConfiguration
     */
    public void registerResources(String name, InputStream resourceConfigStream) throws SAXException, IOException {
        context.getStore().registerResources(name, resourceConfigStream);
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
