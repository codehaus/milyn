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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.assertion.AssertArgument;
import org.milyn.javabean.dynamic.resolvers.DefaultBindingConfigResolver;
import org.milyn.javabean.dynamic.resolvers.DefaultSchemaResolver;
import org.milyn.payload.JavaResult;
import org.milyn.util.ClassUtil;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Dynamic Model Builder.
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
 * The default descriptor file is "META-INF/services/org/smooks/javabean/dynamic/ns-descriptors.properties",
 * but you can define a different descriptor file in the constructor.  Of course there can be many instances
 * of this file on the classpath i.e. one per module/jar.  This allows you to easily add extensions and updates
 * to your configuration model, without having to define new Java models for the new namespaces (versions) etc.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DynamicModelBuilder {
	
    private static Log logger = LogFactory.getLog(DynamicModelBuilder.class);
	private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	static {
		documentBuilderFactory.setNamespaceAware(true);
	}

	public static final String DEFAULT_MODEL_BUILDER_NS_DESCRIPTOR = "META-INF/services/org/smooks/javabean/dynamic/ns-descriptors.properties";
	
	private EntityResolver schemaResolver;
	private EntityResolver bindingResolver;
	private boolean validate = true;

	public DynamicModelBuilder() {
		this(DynamicModelBuilder.DEFAULT_MODEL_BUILDER_NS_DESCRIPTOR);
	}

	public DynamicModelBuilder(String descriptorPath) {
		AssertArgument.isNotNullAndNotEmpty(descriptorPath, "descriptorPath");
		
		List<Properties> descriptors = loadDescriptors(descriptorPath);
		
		schemaResolver = new DefaultSchemaResolver(descriptors);
		bindingResolver = new DefaultBindingConfigResolver(descriptors);
	}
	
	public DynamicModelBuilder setSchemaResolver(EntityResolver schemaResolver) {
		AssertArgument.isNotNull(schemaResolver, "schemaResolver");
		this.schemaResolver = schemaResolver;
		return this;
	}

	public DynamicModelBuilder setBindingResolver(EntityResolver bindingResolver) {
		AssertArgument.isNotNull(bindingResolver, "bindingResolver");
		this.bindingResolver = bindingResolver;
		return this;
	}
	
	public DynamicModelBuilder validate(boolean validate) {
		this.validate = validate;
		return this;
	}
	
	public boolean isValidating() {
		return validate;
	}

	public <T> T parse(InputStream message, Class<T> returnType) throws SAXException, IOException {
		return parse(new InputStreamReader(message), returnType);
	}
	
	public <T> T parse(Reader message, Class<T> returnType) throws SAXException, IOException {
		JavaResult result = parse(message);
		
		for(Object bean : result.getResultMap().values()) {
			if(bean.getClass() == returnType) {
				return returnType.cast(bean);
			}
		}
		
		return null;
	}

	public JavaResult parse(InputStream message) throws SAXException, IOException {
		return parse(new InputStreamReader(message));
	}

	public JavaResult parse(Reader message) throws SAXException, IOException {
		Document messageDoc = toDocument(message);
		Set<String> namespaces = getNamespaces(messageDoc);		
		
		// Validate the message against the schemas...
		if(validate) {
			validateMessage(messageDoc, namespaces);
		}
		
		// Create the Smooks instance for the specified configuration namespaces...
		Smooks smooks = createSmooks(namespaces);
		
		// Filter the message through Smooks and populate a JavaResult from it...
		JavaResult result = new JavaResult();
		smooks.filterSource(new DOMSource(messageDoc), result);
		
		return result;
	}

	private Smooks createSmooks(Set<String> namespaces) throws SAXException, IOException {
		Smooks smooks = new Smooks();
        
    	for (String namespace : namespaces) {
    		InputSource schemaSource = bindingResolver.resolveEntity(namespace, namespace);
    		
    		if(schemaSource != null) {
	    		if(schemaSource.getByteStream() != null) {
	    			smooks.addConfigurations(schemaSource.getByteStream());
	    		} else {
	    			throw new SAXException("Binding configuration resolver '" + bindingResolver.getClass().getName() + "' failed to resolve binding configuration for namespace '" + namespace + "'.  Resolver must return an InputStream in the InputSource.");
	    		}
    		}
        }

        return smooks;
	}

	public void validateMessage(Document message, Set<String> namespaces) throws SAXException, IOException {
		List<Source> schemas = getSchemas(namespaces);

		try {
			// Create the merged Schema instance and from that, create the Validator instance...
	        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = schemaFactory.newSchema(schemas.toArray(new Source[schemas.size()]));
	        Validator validator = schema.newValidator();
	
	        // Validate the document...
	        validator.validate(new DOMSource(message));
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

	private List<Source> getSchemas(Set<String> namespaces) throws SAXException, IOException {
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
	
	private Set<String> getNamespaces(Document messageDoc) {
		Set<String> namespaces = new HashSet<String>();
		addNamespaces(messageDoc.getDocumentElement(), namespaces);
		return namespaces;
	}

	private void addNamespaces(Element element, Set<String> namespaces) {
		addNamespace(element, namespaces);
		
		NamedNodeMap attributes = element.getAttributes();
		int attribCount = attributes.getLength();
		for (int i = 0; i < attribCount; i++) {
			addNamespace(attributes.item(i), namespaces);
		}		
		
		NodeList children = element.getChildNodes();
		int childCount = children.getLength();
		for (int i = 0; i < childCount; i++) {
			addNamespace(children.item(i), namespaces);
		}		
	}

	private void addNamespace(Node node, Set<String> namespaces) {
		String namespaceURI = node.getNamespaceURI();
		if(namespaceURI != null && !namespaceURI.trim().equals("") && !XmlUtil.isXMLReservedNamespace(namespaceURI)) {
			namespaces.add(namespaceURI);
		}
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
	
	private List<Properties> loadDescriptors(String descriptorPath) {
		List<Properties> descriptorFiles = new ArrayList<Properties>();
		
		try {
			List<URL> resources = ClassUtil.getResources(descriptorPath, getClass());
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
}
