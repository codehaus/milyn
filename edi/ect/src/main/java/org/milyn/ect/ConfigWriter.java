package org.milyn.ect;

import org.milyn.edisax.model.internal.Edimap;
import org.milyn.util.FreeMarkerTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * ConfigWriter
 * @author bardl
 */
public class ConfigWriter {

    private FreeMarkerTemplate template = new FreeMarkerTemplate("template/definition-configuration.ftl", ConfigWriter.class);

    public void generate(Writer writer, Edimap edimap) throws IOException {
        Map<String, Object> templatingContextObject = new HashMap<String, Object>();

        Map<String, String> configuration = new HashMap<String,String>();
        configuration.put("version", "1.3");

        templatingContextObject.put("edimap", edimap);
        templatingContextObject.put("configuration", configuration);
        writer.write(template.apply(templatingContextObject));

    }    
}
