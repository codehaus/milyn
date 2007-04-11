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

import org.milyn.profile.ProfileSet;
import org.milyn.profile.ProfileStore;
import org.milyn.profile.UnknownProfileMemberException;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.IOException;
import java.io.CharArrayWriter;

/**
 * {@link org.milyn.Smooks} utilities.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class SmooksUtil {

    private static Log logger = LogFactory.getLog(Smooks.class);

    private SmooksUtil() {
    }

    /**
     * Manually register a set of profiles with the profile store associated with the supplied {@link org.milyn.Smooks} instance.
     * <p/>
     * ProfileSets will typically be registered via the config, but it is useful
     * to be able to perform this task manually.
     *
     * @param profileSet The profile set to be registered.
     * @param smooks     The {@link org.milyn.Smooks} instance on which to perform the configuration operation.
     */
    public static void registerProfileSet(ProfileSet profileSet, Smooks smooks) {
        AssertArgument.isNotNull(profileSet, "profileSet");

        ProfileStore profileStore = smooks.getApplicationContext().getProfileStore();
        try {
            profileStore.getProfileSet(profileSet.getBaseProfile());
            logger.warn("ProfileSet [" + profileSet.getBaseProfile() + "] already registered.  Not registering new profile set.");
        } catch (UnknownProfileMemberException e) {
            // It's an unregistered profileset...
            profileStore.addProfileSet(profileSet.getBaseProfile(), profileSet);
        }
    }

    /**
     * Register a {@link org.milyn.cdr.SmooksResourceConfiguration} on the supplied {@link Smooks} instance.
     *
     * @param resourceConfig The Content Delivery Resource definition to be  registered.
     * @param smooks         The {@link org.milyn.Smooks} instance on which to perform the configuration operation.
     */
    public static void registerResource(SmooksResourceConfiguration resourceConfig, Smooks smooks) {
        if (resourceConfig == null) {
            throw new IllegalArgumentException("null 'resourceConfig' arg in method call.");
        }
        smooks.getApplicationContext().getStore().registerResource(resourceConfig);
    }

    /**
     * Register the set of resources specified in the supplied XML configuration
     * stream.
     *
     * @param name                 The name of the resource set.
     * @param resourceConfigStream XML resource configuration stream.
     * @param smooks               The {@link org.milyn.Smooks} instance on which to perform the configuration operation.
     * @throws org.xml.sax.SAXException Error parsing the resource stream.
     * @throws java.io.IOException      Error reading resource stream.
     * @see SmooksResourceConfiguration
     */
    public static void registerResources(String name, InputStream resourceConfigStream, Smooks smooks) throws SAXException, IOException {
        smooks.getApplicationContext().getStore().registerResources(name, resourceConfigStream);
    }

    /**
     * Utility method to filter the content in the specified {@link InputStream} for the specified {@link org.milyn.container.ExecutionContext}.
     * <p/>
     * Useful for testing purposes.  In a real scenario, use
     * {@link Smooks#filter(javax.xml.transform.Source, javax.xml.transform.Result, org.milyn.container.standalone.StandaloneExecutionContext)}.
     * <p/>
     * The content of the returned String is totally dependent on the configured
     * {@link org.milyn.delivery.dom.DOMElementVisitor} and {@link org.milyn.delivery.dom.serialize.SerializationUnit}
     * implementations.
     *
     * @param executionContext Execution context for the filter.
     * @param stream           Stream to be processed.  Will be closed before returning.
     * @param smooks           The {@link org.milyn.Smooks} instance through which to perform the filter and serialize operations.
     * @return The Smooks processed content buffer.
     * @throws IOException     Exception using or closing the supplied InputStream.
     * @throws SmooksException Excepting processing content stream.
     */
    public static String filterAndSerialize(StandaloneExecutionContext executionContext, InputStream stream, Smooks smooks) throws SmooksException {
        String responseBuf = null;
        CharArrayWriter writer = new CharArrayWriter();
        try {
            smooks.filter(new StreamSource(stream), new StreamResult(writer), executionContext);
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
}
