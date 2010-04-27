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
package org.milyn.ect;

import org.milyn.ect.formats.unedifact.UnEdifactReader;
import org.milyn.edisax.model.internal.Edimap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * ConfigReader
 * @author bardl
 */
public interface ConfigReader {
    enum Impls {
        UNEDIFACT(UnEdifactReader.class);

        private Class<? extends ConfigReader> configReader;
        
        Impls(Class<? extends ConfigReader> configReader) {
            this.configReader = configReader;
        }

        public ConfigReader newInstance() throws IllegalAccessException, InstantiationException {
            return configReader.newInstance();
        }
    }

    void initialize(InputStream inputStream, boolean useImport) throws IOException, EdiParseException;
    Set<String> getMessageNames();
    Edimap getMappingModelForMessage(String messageName) throws IOException;
    Edimap getDefinitionModel() throws IOException;
}
