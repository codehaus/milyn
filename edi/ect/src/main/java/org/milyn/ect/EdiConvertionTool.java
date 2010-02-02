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
import org.milyn.util.ClassUtil;

import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 * EdiConvertionTool
 * @author bardl
 */
public class EdiConvertionTool {    

    public static void convertUnEdifact(ZipInputStream inputStream, String messageName, Writer writer) throws InstantiationException, IllegalAccessException, IOException, EdiParseException {
        ConfigReader configReader = ConfigReader.Impls.UNEDIFACT.newInstance();
        configReader.initialize(inputStream, false);
        Edimap edimap = configReader.getMappingModelForMessage(messageName);

        ConfigWriter configWriter = new ConfigWriter();
        configWriter.generate(writer, edimap);        
    }

}
