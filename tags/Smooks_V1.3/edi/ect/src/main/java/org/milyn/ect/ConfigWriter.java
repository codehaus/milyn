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
