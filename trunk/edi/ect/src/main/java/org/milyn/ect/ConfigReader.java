package org.milyn.ect;

import org.milyn.ect.formats.unedifact.UnEdifactReader;
import org.milyn.edisax.model.internal.Edimap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * ConfigReader.
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
