package org.milyn.ect;

import org.milyn.ect.formats.unedifact.UnEdifactReader;
import org.milyn.edisax.model.internal.Edimap;
import org.milyn.util.ClassUtil;

import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: bardl
 * Date: 2010-jan-04
 * Time: 11:15:48
 * To change this template use File | Settings | File Templates.
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
