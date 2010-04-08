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
package org.milyn.smooks.edi.unedifact;

import java.io.IOException;
import java.util.Map;

import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Description;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.milyn.smooks.edi.ModelLoader;
import org.milyn.xml.SmooksXMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * UN/EDIFACT Smooks reader.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNEdifactReader extends UNEdifactInterchangeParser implements SmooksXMLReader  {
	
	@ConfigParam
	private String mappingModel;

	@ConfigParam(defaultVal = "false")
	private boolean validate;
    
	@ConfigParam(defaultVal = "false")
	private boolean ignoreNewLines;

    @AppContext
    private ApplicationContext applicationContext;
    
	public void setExecutionContext(ExecutionContext executionContext) {		
	}

	@Override
	public void parse(InputSource unedifactInterchange) throws IOException, SAXException {
		Map<Description, EdifactModel> mappingModelMap;
		
		try {
			mappingModelMap = ModelLoader.getMappingModels(mappingModel, applicationContext);
		} catch (EDIConfigurationException e) {
			throw (IOException)(new IOException("Failed to load mapping model(s) '" + mappingModel + "'.").initCause(e));
		}
		
		setMappingModels(mappingModelMap);
		ignoreNewLines(ignoreNewLines);
		validate(validate);
		
		super.parse(unedifactInterchange);
	}
}
