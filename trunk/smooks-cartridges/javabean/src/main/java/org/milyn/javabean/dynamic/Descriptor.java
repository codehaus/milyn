/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License (version 2.1) as published by the Free Software
 *  Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  See the GNU Lesser General Public License for more details:
 *  http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.javabean.dynamic;

import org.milyn.Smooks;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksResourceConfigurationList;
import org.milyn.cdr.XMLConfigDigester;
import org.milyn.javabean.dynamic.ext.BeanWriterFactory;
import org.milyn.javabean.dynamic.resolvers.DefaultBindingConfigResolver;
import org.milyn.javabean.dynamic.resolvers.DefaultSchemaResolver;
import org.milyn.javabean.dynamic.serialize.BeanWriter;
import org.milyn.util.ClassUtil;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Model Descriptor.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Descriptor {

    public static final String DESCRIPTOR_NAMESPACE_POSTFIX = ".namespace";
    public static final String DESCRIPTOR_SCHEMA_LOCATION_POSTFIX = ".schemaLocation";
    public static final String DESCRIPTOR_BINDING_CONFIG_LOCATION_POSTFIX = ".bindingConfigLocation";

    private Smooks smooks;
    private Schema schema;

    Descriptor(String descriptorPath) throws SAXException, IOException {
		AssertArgument.isNotNullAndNotEmpty(descriptorPath, "descriptorPath");

		List<Properties> descriptors = loadDescriptors(descriptorPath);
		intialize(descriptors, new DefaultSchemaResolver(descriptors), new DefaultBindingConfigResolver(descriptors));
	}

	Descriptor(String descriptorPath, EntityResolver schemaResolver, EntityResolver bindingResolver) throws SAXException, IOException {
		AssertArgument.isNotNullAndNotEmpty(descriptorPath, "descriptorPath");
		AssertArgument.isNotNull(schemaResolver, "schemaResolver");
		AssertArgument.isNotNull(bindingResolver, "bindingResolver");

		List<Properties> descriptors = loadDescriptors(descriptorPath);
		intialize(descriptors, schemaResolver, bindingResolver);
	}

    public Smooks getSmooks() {
        return smooks;
    }

    public Schema getSchema() {
        return schema;
    }

    public Map<Class<?>, Map<String, BeanWriter>> getBeanWriters() {
        return BeanWriterFactory.getBeanWriters(smooks.getApplicationContext());
    }

    private List<Properties> loadDescriptors(String descriptorPath) {
        List<Properties> descriptorFiles = new ArrayList<Properties>();

        try {
            List<URL> resources = ClassUtil.getResources(descriptorPath, ModelBuilder.class);
            for(URL resource : resources) {
                InputStream resStream = resource.openStream();
                try {
                    Properties descriptor = new Properties();
                    descriptor.load(resStream);
                    descriptorFiles.add(descriptor);
                } finally {
                    resStream.close();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IO Exception when reading Dynamic Namespace Descriptor files from classpath.", e);
        }

        return descriptorFiles;
    }


    private void intialize(List<Properties> descriptors, EntityResolver schemaResolver, EntityResolver bindingResolver) throws SAXException, IOException {
		this.schema = newSchemaInstance(descriptors, schemaResolver);
		this.smooks = newSmooksInstance(descriptors, bindingResolver);
	}

    private Schema newSchemaInstance(List<Properties> descriptors, EntityResolver schemaResolver) throws SAXException, IOException {
        Set<String> namespaces = resolveNamespaces(descriptors);
        List<Source> schemas = getSchemas(namespaces, schemaResolver);

        try {
            // Create the merged Schema instance and from that, create the Validator instance...
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            return schemaFactory.newSchema(schemas.toArray(new Source[schemas.size()]));
        } finally {
            for(Source schemaSource : schemas) {
                if(schemaSource instanceof StreamSource) {
                    StreamSource streamSource = (StreamSource)schemaSource;
                    if(streamSource.getInputStream() != null) {
                        streamSource.getInputStream().close();
                    } else if(streamSource.getReader() != null) {
                        streamSource.getReader().close();
                    }
                }
            }
        }
    }

    private List<Source> getSchemas(Set<String> namespaces, EntityResolver schemaResolver) throws SAXException, IOException {
        List<Source> xsdSources = new ArrayList<Source>();

        for (String namespace : namespaces) {
            InputSource schemaSource = schemaResolver.resolveEntity(namespace, namespace);

            if(schemaSource != null) {
                if(schemaSource.getByteStream() != null) {
                    xsdSources.add(new StreamSource(schemaSource.getByteStream()));
                } else if(schemaSource.getCharacterStream() != null) {
                    xsdSources.add(new StreamSource(schemaSource.getCharacterStream()));
                } else {
                    throw new SAXException("Schema resolver '" + schemaResolver.getClass().getName() + "' failed to resolve schema for namespace '" + namespace + "'.  Resolver must return a Reader or InputStream in the InputSource.");
                }
            }
        }

        return xsdSources;
    }

    public static Smooks newSmooksInstance(List<Properties> descriptors, EntityResolver bindingResolver) throws SAXException, IOException, SmooksConfigurationException {
        AssertArgument.isNotNullAndNotEmpty(descriptors, "descriptors");
        AssertArgument.isNotNull(bindingResolver, "bindingResolver");

        Set<String> namespaces = resolveNamespaces(descriptors);

        // Now create a Smooks instance for processing configurations for these namespaces...
        Smooks smooks = new Smooks();
        for (String namespace : namespaces) {
            InputSource bindingSource = bindingResolver.resolveEntity(namespace, namespace);

            if(bindingSource != null) {
                if(bindingSource.getByteStream() != null) {
                    SmooksResourceConfigurationList configList;

                    try {
                        configList = XMLConfigDigester.digestConfig(bindingSource.getByteStream(), "./");
                        for(int i = 0; i < configList.size(); i++) {
                            SmooksResourceConfiguration config = configList.get(i);
                            if(config.getSelectorNamespaceURI() == null) {
                                config.setSelectorNamespaceURI(namespace);
                            }
                        }
                    } catch (URISyntaxException e) {
                        throw new SmooksConfigurationException("Unexpected configuration digest exception.", e);
                    }

                    smooks.getApplicationContext().getStore().addSmooksResourceConfigurationList(configList);
                } else {
                    throw new SAXException("Binding configuration resolver '" + bindingResolver.getClass().getName() + "' failed to resolve binding configuration for namespace '" + namespace + "'.  Resolver must return an InputStream in the InputSource.");
                }
            }
        }

        return smooks;
    }

    private static Set<String> resolveNamespaces(List<Properties> descriptors) {
        Set<String> namespaces = new LinkedHashSet<String>();
        for(Properties descriptor : descriptors) {
            extractNamespaceDecls(descriptor, namespaces);
        }
        return namespaces;
    }

    private static void extractNamespaceDecls(Properties descriptor, Set<String> namespaces) {
        Set<Map.Entry<Object, Object>> properties = descriptor.entrySet();
        for(Map.Entry<Object, Object> property: properties) {
            String key = ((String) property.getKey()).trim();
            if(key.endsWith(DESCRIPTOR_NAMESPACE_POSTFIX)) {
                namespaces.add((String) property.getValue());
            }
        }
    }

    public static String getNamespaceId(String namespaceURI, List<Properties> descriptors) {
        for(Properties descriptor : descriptors) {
            Set<Map.Entry<Object, Object>> properties = descriptor.entrySet();
            for(Map.Entry<Object, Object> property: properties) {
                String key = ((String) property.getKey()).trim();
                String value = ((String) property.getValue()).trim();
                if(key.endsWith(DESCRIPTOR_NAMESPACE_POSTFIX) && value.equals(namespaceURI)) {
                    return key.substring(0, (key.length() - DESCRIPTOR_NAMESPACE_POSTFIX.length()));
                }
            }
        }
        return null;
    }

    public static String getSchemaLocation(String namespaceId, List<Properties> descriptors) {
        return getDescriptorValue(namespaceId + DESCRIPTOR_SCHEMA_LOCATION_POSTFIX, descriptors);
    }

    public static String getBindingConfigLocation(String namespaceId, List<Properties> descriptors) {
        return getDescriptorValue(namespaceId + DESCRIPTOR_BINDING_CONFIG_LOCATION_POSTFIX, descriptors);
    }

    private static String getDescriptorValue(String name, List<Properties> descriptors) {
        for(Properties descriptor : descriptors) {
            String value = descriptor.getProperty(name);
            if(value != null) {
                return value;
            }
        }

        return null;
    }
}
