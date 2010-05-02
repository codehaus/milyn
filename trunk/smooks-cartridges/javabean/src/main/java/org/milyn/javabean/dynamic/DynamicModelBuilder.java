/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.javabean.dynamic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.FilterSettings;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksResourceConfigurationList;
import org.milyn.cdr.XMLConfigDigester;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.dynamic.resolvers.DefaultBindingConfigResolver;
import org.milyn.javabean.dynamic.resolvers.DefaultSchemaResolver;
import org.milyn.javabean.lifecycle.BeanContextLifecycleEvent;
import org.milyn.javabean.lifecycle.BeanContextLifecycleObserver;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.payload.JavaResult;
import org.milyn.util.ClassUtil;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Dynamic Model Builder (from XML only).
 * <p/>
 * Useful for constructing configuration models etc.  Allows you to build a config model
 * for a dynamic configuration namespace i.e. a config namespace that is evolving and being 
 * extended all the time.  New namespaces can be easily added or extended.  All that's required
 * is to define the new config XSD and the Smooks Java Binding config to bind the data in the 
 * config namespace into the target Java model.
 * <p/>
 * The namespaces all need to be configured in a "descriptor" .properties file located on the classpath.
 * Here's an example:
 * <pre>
 * mycomp=http://www.acme.com/xsd/mycomp.xsd
 * mycomp.schemaLocation=/META-INF/xsd/mycomp.xsd
 * mycomp.bindingConfigLocation=/META-INF/xsd/mycomp-binding.xml
 * </pre>
 * 
 * You should use a unique descriptor path for a given configuration model.  Of course there can be many instances
 * of this file on the classpath i.e. one per module/jar.  This allows you to easily add extensions and updates
 * to your configuration model, without having to define new Java models for the new namespaces (versions) etc.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DynamicModelBuilder {
	
	public static final String DESCRIPTOR_NAMESPACE_POSTFIX = ".namespace";
	public static final String DESCRIPTOR_SCHEMA_LOCATION_POSTFIX = ".schemaLocation";
	public static final String DESCRIPTOR_BINDING_CONFIG_LOCATION_POSTFIX = ".bindingConfigLocation";
	
	private static Log logger = LogFactory.getLog(DynamicModelBuilder.class);
	private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	static {
		documentBuilderFactory.setNamespaceAware(true);
	}

	private Smooks smooks;
	private Schema schema;
	private boolean validate = true;
	private boolean parseCalled = false;

	public DynamicModelBuilder(String descriptorPath) throws SAXException, IOException {
		AssertArgument.isNotNullAndNotEmpty(descriptorPath, "descriptorPath");
		
		List<Properties> descriptors = loadDescriptors(descriptorPath);				
		intialize(descriptors, new DefaultSchemaResolver(descriptors), new DefaultBindingConfigResolver(descriptors));
	}

	public DynamicModelBuilder(String descriptorPath, EntityResolver schemaResolver, EntityResolver bindingResolver) throws SAXException, IOException {
		AssertArgument.isNotNullAndNotEmpty(descriptorPath, "descriptorPath");
		AssertArgument.isNotNull(schemaResolver, "schemaResolver");
		AssertArgument.isNotNull(bindingResolver, "bindingResolver");

		List<Properties> descriptors = loadDescriptors(descriptorPath);
		intialize(descriptors, schemaResolver, bindingResolver);
	}

	private void intialize(List<Properties> descriptors, EntityResolver schemaResolver, EntityResolver bindingResolver) throws SAXException, IOException {
		this.schema = newSchemaInstance(descriptors, schemaResolver);
		this.smooks = newSmooksInstance(descriptors, bindingResolver);
		configureFilterSettings();
	}
	
	public DynamicModelBuilder validate(boolean validate) {
		this.validate = validate;
		configureFilterSettings();
		return this;
	}
	
	public boolean isValidating() {
		return validate;
	}

	public <T> T parse(InputStream message, Class<T> returnType) throws SAXException, IOException {
		return parse(new InputStreamReader(message), returnType);
	}
	
	public <T> T parse(Reader message, Class<T> returnType) throws SAXException, IOException {
		Model<JavaResult> model = parse(message);		
		return model.getModelRoot().getBean(returnType);
	}

	public Model<JavaResult> parse(InputStream message) throws SAXException, IOException {
		return parse(new InputStreamReader(message));
	}

	public Model<JavaResult> parse(Reader message) throws SAXException, IOException {
		JavaResult result = new JavaResult();
		ExecutionContext executionContext = smooks.createExecutionContext();
		BeanTracker beanTracker = new BeanTracker();

		// Mark the builder as being in use!!
		parseCalled = true;
		
		executionContext.getBeanContext().addObserver(beanTracker);
		
		if(validate) {
			// Validate the message against the schemas...
			Document messageDoc = toDocument(message);

	        // Validate the document and then filter it through smooks...
	        schema.newValidator().validate(new DOMSource(messageDoc));
			smooks.filterSource(executionContext, new DOMSource(messageDoc), result);
		} else {
			smooks.filterSource(executionContext, new StreamSource(message), result);
		}
		
		Model<JavaResult> model = new Model<JavaResult>(result, beanTracker.beans);
		
		return model;
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
		Set<Entry<Object, Object>> properties = descriptor.entrySet();
		for(Entry<Object, Object> property: properties) {
			String key = ((String) property.getKey()).trim();
			if(key.endsWith(DESCRIPTOR_NAMESPACE_POSTFIX)) {
				namespaces.add((String) property.getValue());
			}
		}
	}

	public static String getNamespaceId(String namespaceURI, List<Properties> descriptors) {
		for(Properties descriptor : descriptors) {
			Set<Entry<Object, Object>> properties = descriptor.entrySet();
			for(Entry<Object, Object> property: properties) {
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

	private Document toDocument(Reader message) {
		DocumentBuilder docBuilder;
		
		try {
			docBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new SmooksException("Unable to parse message and dynamically bind into object model.  DOM Parser confguration exception.", e);
		}
		
		try {
			return docBuilder.parse(new InputSource(message));
		} catch (SAXException e) {
			throw new SmooksException("Unable to parse message and dynamically bind into object model.  Message format exception.", e);
		} catch (IOException e) {
			throw new SmooksException("Unable to parse message and dynamically bind into object model.  IO exception.", e);
		} finally {
			try {
				message.close();
			} catch (IOException e) {
				logger.error("Exception closing message reader.", e);
			}
		}
	}
	
	private static List<Properties> loadDescriptors(String descriptorPath) {
		List<Properties> descriptorFiles = new ArrayList<Properties>();
		
		try {
			List<URL> resources = ClassUtil.getResources(descriptorPath, DynamicModelBuilder.class);
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

	private void configureFilterSettings() {
		assertParseNotCalled();
		
		if(validate) {
			smooks.setFilterSettings(FilterSettings.DEFAULT_DOM);
		} else {
			smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
		}
	}
	
	private void assertParseNotCalled() {
		if(parseCalled) {
			throw new IllegalStateException("Invalid operation.  The 'parse' method has been invoked at least once.");
		}		
	}

	private class BeanTracker implements BeanContextLifecycleObserver {
		
		private List<BeanMetadata> beans = new ArrayList<BeanMetadata>();

		public void onBeanLifecycleEvent(BeanContextLifecycleEvent event) {
			if(event.getLifecycle() == BeanLifecycle.BEGIN || event.getLifecycle() == BeanLifecycle.CHANGE) {
				BeanMetadata beanMetadata = new BeanMetadata(event.getBean());				
				beanMetadata.setNamespace(event.getBeanId().getCreateResourceConfiguration().getSelectorNamespaceURI());
				beans.add(beanMetadata);
			}
		}		
	}
}
