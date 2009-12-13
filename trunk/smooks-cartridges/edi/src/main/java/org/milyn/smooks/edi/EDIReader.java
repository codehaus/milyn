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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.ProfileTargetingExpression;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.Config;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.EDIParser;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.resource.ContainerResourceLocator;
import org.milyn.resource.URIResourceLocator;
import org.milyn.xml.SmooksXMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Smooks EDI Reader.
 * <p/>
 * Hooks the Milyn {@link org.milyn.edisax.EDIParser} into the <a href="http://milyn.codehaus.org/Smooks" target="new">Smooks</a> framework.
 * This adds EDI processing support to Smooks.
 *
 * <h3>Configuration</h3>
 * <pre>
 * &lt;edi:reader mappingModel="edi-to-xml-order-mapping.xml" validate="false"/&gt;
 * </pre>
 * 
 * @author tfennelly
 */
public class EDIReader extends EDIParser implements SmooksXMLReader {

	/**
	 * Logger.
	 */
	private static Log logger = LogFactory.getLog(EDIReader.class);
	/**
	 * Context lookup key for the mapping model table.
	 */
	private static String MAPPING_TABLE_CTX_KEY = EDIReader.class.getName() + "#MAPPING_TABLE_CTX_KEY";
	/**
	 * Model resource configuration key.
	 */
	public static final String MODEL_CONFIG_KEY = "mapping-model";
	/**
	 * The parser configuration.
	 */
    @Config
    private SmooksResourceConfiguration configuration;
    /**
     * Application context.
     */
    @AppContext
    private ApplicationContext applicationContext;

    @ConfigParam(name = MODEL_CONFIG_KEY)
    private String modelConfigData;

    @ConfigParam(defaultVal = "UTF-8")
    private Charset encoding;

    @ConfigParam(defaultVal = "false")
    private Boolean validate;

    public void setExecutionContext(ExecutionContext executionContext) {
	}

	/**
	 * Parse the EDI message.
	 * <p/>
	 * Overridden so as to set the EDI to XML mapping model on the parser.
	 */
	public void parse(InputSource ediSource) throws IOException, SAXException {
		EdifactModel edi2xmlMappingModel = getMappingModel();
		
		setMappingModel(edi2xmlMappingModel);
        setValidateValueNodes();
        super.parse(ediSource);
	}

    /**
     * Activates or deactivates validation of value-nodes in EDIParser.
     * @throws SAXNotSupportedException When the XMLReader recognizes the property name but cannot set the requested value.
     * @throws SAXNotRecognizedException If the property value can't be assigned or retrieved.
     */
    private void setValidateValueNodes() throws SAXNotSupportedException, SAXNotRecognizedException {
        super.setFeature(VALIDATE, validate);        
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
	private EdifactModel getMappingModel() throws IOException, SAXException {
        EdifactModel edifactModel;
        Hashtable mappings = getMappingTable(applicationContext);

		synchronized (configuration) {
            edifactModel = (EdifactModel) mappings.get(configuration);
            if(edifactModel == null) {
				try {
	            	ContainerResourceLocator resourceLocator = applicationContext.getResourceLocator();
	            	
	            	if(resourceLocator instanceof URIResourceLocator) {
	            		// This will resolve config paths relative to the containing smooks config file....
	            		edifactModel = EDIParser.parseMappingModel(modelConfigData, ((URIResourceLocator)resourceLocator).getBaseURI());
	            	} else {
	            		edifactModel = EDIParser.parseMappingModel(modelConfigData, URIResourceLocator.getSystemBaseURI());
	            	}
	    			if(edifactModel == null) {
	    				logger.error("Invalid " + MODEL_CONFIG_KEY + " config value '" + modelConfigData + "'. Failed to locate EDI Mapping Model resource!");
	    			}
				} catch (IOException e) {
                    IOException newE = new IOException("Error parsing EDI mapping model [" + configuration.getStringParameter(MODEL_CONFIG_KEY) + "].  Target Profile(s) " + getTargetProfiles() + ".");
					newE.initCause(e);
					throw newE;
				} catch (SAXException e) {
					throw new SAXException("Error parsing EDI mapping model [" + configuration.getStringParameter(MODEL_CONFIG_KEY) + "].  Target Profile(s) " + getTargetProfiles() + ".", e);
				} catch (EDIConfigurationException e) {
                    throw new SAXException("Error parsing EDI mapping model [" + configuration.getStringParameter(MODEL_CONFIG_KEY) + "].  Target Profile(s) " + getTargetProfiles() + ".", e);
                }
                mappings.put(configuration, edifactModel);
				logger.info("Parsed, validated and cached EDI mapping model [" + edifactModel.getEdimap().getDescription().getName() + ", Version " + edifactModel.getEdimap().getDescription().getVersion() + "].  Target Profile(s) " + getTargetProfiles() + ".");
			} else if(logger.isInfoEnabled()) {
				logger.info("Found EDI mapping model [" + edifactModel.getEdimap().getDescription().getName() + ", Version " + edifactModel.getEdimap().getDescription().getVersion() + "] in the model cache.  Target Profile(s) " + getTargetProfiles() + ".");
			}
		}
		
		return edifactModel;
	}

	/**
	 * Get the mapping model table from the context.
	 * @param context The context from which to extract the mapping table.
	 * @return The mapping model talbe.
	 */
	protected static Hashtable getMappingTable(ApplicationContext context) {
		Hashtable mappingModelTable = (Hashtable) context.getAttribute(MAPPING_TABLE_CTX_KEY);
		
		if(mappingModelTable == null) {
			mappingModelTable = new Hashtable();
			context.setAttribute(MAPPING_TABLE_CTX_KEY, mappingModelTable);
		}
		
		return mappingModelTable;
	}

    private List<ProfileTargetingExpression> getTargetProfiles() {
        return Arrays.asList(configuration.getProfileTargetingExpressions());
    }
}
