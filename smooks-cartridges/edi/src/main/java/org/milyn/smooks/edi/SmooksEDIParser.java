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

package org.milyn.smooks.edi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ContainerContext;
import org.milyn.container.ContainerRequest;
import org.milyn.edisax.EDIParser;
import org.milyn.resource.URIResourceLocator;
import org.milyn.schema.ediMessageMapping10.EdimapDocument.Edimap;
import org.milyn.xml.SmooksXMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Smooks EDI Parser.
 * <p/>
 * Hooks the Milyn {@link org.milyn.edisax.EDIParser} into the <a href="http://milyn.codehaus.org/Smooks" target="new">Smooks</a> framework.
 * This adds EDI processing support to Smooks.
 * 
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;smooks-resource useragent="<i>&lt;profile&gt;</i>" selector="org.xml.sax.driver" path="org.milyn.smooks.edi.SmooksEDIParser" &gt;
 * 
 *  &lt;!-- 
 *      (Mandatory) {@link org.milyn.edisax.EDIParser Mapping Model}.  Can be a URI specifiying the location of the model (see {@link org.milyn.resource.URIResourceLocator}), 
 *      or can be the model itself (inlined).
 *  --&gt;
 *  &lt;param name="<b>mapping-model</b>"&gt;[{@link java.net.URI} | <i>inlined model</i>]&lt;/param&gt;
 * 
 * &lt;/smooks-resource&gt;
 * </pre>
 * 
 * @author tfennelly
 */
public class SmooksEDIParser extends EDIParser implements SmooksXMLReader {

	/**
	 * Logger.
	 */
	private static Log logger = LogFactory.getLog(SmooksEDIParser.class);
	/**
	 * Context lookup key for the mapping model table.
	 */
	private static String MAPPING_TABLE_CTX_KEY = SmooksEDIParser.class.getName() + "#MAPPING_TABLE_CTX_KEY";
	/**
	 * Model resource configuration key.
	 */
	public static final String MODEL_CONFIG_KEY = "mapping-model";
	/**
	 * URI based mapping locator.
	 */
	private static URIResourceLocator uriMappingLocator = new URIResourceLocator();
	/**
	 * The parser configuration.
	 */
	private SmooksResourceConfiguration configuration;
	/**
	 * The Smooks container request.
	 */
	private ContainerRequest request;
	
	/* (non-Javadoc)
	 * @see org.milyn.xml.SmooksXMLReader#setConfiguration(org.milyn.cdr.SmooksResourceConfiguration)
	 */
	public void setConfiguration(SmooksResourceConfiguration configuration) {
		AssertArgument.isNotNull(configuration, "configuration");
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.milyn.xml.SmooksXMLReader#setRequest(org.milyn.container.ContainerRequest)
	 */
	public void setRequest(ContainerRequest request) {
		AssertArgument.isNotNull(request, "request");
		this.request = request;
	}

	/**
	 * Parse the EDI message.
	 * <p/>
	 * Overridden so as to set the EDI to XML mapping model on the parser.
	 */
	public void parse(InputSource ediSource) throws IOException, SAXException {
		Edimap edi2xmlMappingModel = getMappingModel();
		
		setMappingModel(edi2xmlMappingModel);
		super.parse(ediSource);
	}

	/**
	 * Get the mapping model associated with the supplied SmooksResourceConfiguration.
	 * <p/>
	 * The parsed and validated models are cached in the Smooks container context, keyed
	 * by the SmooksResourceConfiguration instance.
	 * @return The Mapping Model.
	 * @throws IOException Error reading resource configuration data (the mapping model).
	 * @throws SAXException Error parsing mapping model.
	 */
	private Edimap getMappingModel() throws IOException, SAXException {
		Edimap edi2xmlMappingModel;
		Hashtable mappings = getMappingTable(request.getContext());

		synchronized (configuration) {
			edi2xmlMappingModel = (Edimap) mappings.get(configuration);
			if(edi2xmlMappingModel == null) {
				InputStream mappingConfigData = getMappingConfigData();
				
				try {
					edi2xmlMappingModel = EDIParser.parseMappingModel(mappingConfigData);
				} catch (IOException e) {
					IOException newE = new IOException("Error parsing EDI mapping model [" + configuration.getStringParameter(MODEL_CONFIG_KEY) + "].  Target Useragent(s) " + Arrays.asList(configuration.getUseragentExpressions()) + ".");
					newE.initCause(e);
					throw newE;
				} catch (SAXException e) {
					throw new SAXException("Error parsing EDI mapping model [" + configuration.getStringParameter(MODEL_CONFIG_KEY) + "].  Target Useragent(s) " + Arrays.asList(configuration.getUseragentExpressions()) + ".", e);
				}
				mappings.put(configuration, edi2xmlMappingModel);
				logger.info("Parsed, validated and cached EDI mapping model [" + edi2xmlMappingModel.getDescription().getName() + ", Version " + edi2xmlMappingModel.getDescription().getVersion() + "].  Target Useragent(s) " + Arrays.asList(configuration.getUseragentExpressions()) + ".");
			} else if(logger.isInfoEnabled()) {
				logger.info("Found EDI mapping model [" + edi2xmlMappingModel.getDescription().getName() + ", Version " + edi2xmlMappingModel.getDescription().getVersion() + "] in the model cache.  Target Useragent(s) " + Arrays.asList(configuration.getUseragentExpressions()) + ".");
			}
		}
		
		return edi2xmlMappingModel;
	}

	/**
	 * Get the mapping model table from the context.
	 * @param context The context from which to extract the mapping table.
	 * @return The mapping model talbe.
	 */
	protected static Hashtable getMappingTable(ContainerContext context) {
		Hashtable mappingModelTable = (Hashtable) context.getAttribute(MAPPING_TABLE_CTX_KEY);
		
		if(mappingModelTable == null) {
			mappingModelTable = new Hashtable();
			context.setAttribute(MAPPING_TABLE_CTX_KEY, mappingModelTable);
		}
		
		return mappingModelTable;
	}

	/**
	 * Get the actual mapping configuration data (the XML).
	 * <p/>
	 * Attempts to interpret the {@link #MODEL_CONFIG_KEY} config parameter as a URI to access the config.
	 * If this parameter does not specify a URI, it's value will be interpreted as being an inlined 
	 * Mapping Model configuration.
	 * 
	 * @return The mapping configuration data stream.
	 */
	private InputStream getMappingConfigData() {
		InputStream configStream = null;
		String modelConfigData = configuration.getStringParameter(MODEL_CONFIG_KEY);
		
		if(modelConfigData == null) {
			throw new IllegalStateException("Mandatory resource configuration parameter [" + MODEL_CONFIG_KEY + "] not specified for [" + getClass().getName() + "] parser configuration.  Target Useragent(s) " + Arrays.asList(configuration.getUseragentExpressions()) + ".");
		}
		
		try {
			new URI(modelConfigData);
			configStream = uriMappingLocator.getResource(modelConfigData);
			if(configStream == null) {
				logger.error("Invalid " + MODEL_CONFIG_KEY + " config value '" + modelConfigData + "'. Failed to locate resource!");
			}
		} catch (URISyntaxException e) {
			// It's not a URI based specification.  Return the contents under the assumption 
			// that it's an inlined config...
			configStream = new ByteArrayInputStream(modelConfigData.getBytes());
		} catch (IOException e) {
			IllegalStateException state = new IllegalStateException("Invalid EDI mapping model config specified for " + getClass().getName() + ".  Unable to access URI based mapping model [" + modelConfigData + "].  Target Useragent(s) " + Arrays.asList(configuration.getUseragentExpressions()) + ".");
			state.initCause(e);
			throw state;
		}
		
		return configStream;
	}
}
