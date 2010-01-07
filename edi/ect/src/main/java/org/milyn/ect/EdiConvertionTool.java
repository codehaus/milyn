package org.milyn.ect;

import org.milyn.ect.formats.unedifact.UnEdifactReader;
import org.milyn.edisax.model.internal.Edimap;
import org.milyn.util.ClassUtil;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: bardl
 * Date: 2010-jan-04
 * Time: 11:15:48
 * To change this template use File | Settings | File Templates.
 */
public class EdiConvertionTool {

    private ConfigReader configReader;

    public EdiConvertionTool(InputStream inputStream, String configReader) throws EdiParseException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        try {
            this.configReader = ConfigReader.Impls.valueOf(configReader).newInstance();
        } catch (IllegalArgumentException e) {
            Class configReaderType = ClassUtil.forName(configReader, ConfigReader.class);
            if (configReaderType == null) {
                throw new EdiParseException("Illegal name provided for configReader. It should be one of [" + Arrays.toString(ConfigReader.Impls.values()) + "] or a classpath to a class implementing ConfigReader.", e);
            }
            this.configReader = (ConfigReader)configReaderType.newInstance();
        }
        this.configReader.initialize(inputStream, false);
    }

    public EdiConvertionTool(InputStream inputStream, ConfigReader configReader) throws IOException, EdiParseException {
        this.configReader = configReader;
        this.configReader.initialize(inputStream, false);
    }

    public EdiConvertionTool(InputStream inputStream, ConfigReader configReader, boolean useImport) throws IOException, EdiParseException {
        this.configReader = configReader;
        this.configReader.initialize(inputStream, useImport);
    }

    public Set<String> getMessageNames() {
        return configReader.getMessageNames();
    }

    public String getMappingModelForMessage(String messageName) throws IOException {
        return writeEdimap(configReader.getMappingModelForMessage(messageName));
    }

    public String getDefinitionModel() throws IOException {
        return writeEdimap(configReader.getDefinitionModel());
    }

    private String writeEdimap(Edimap edimap) throws IOException {
        ConfigWriter configWriter = new ConfigWriter();

        Writer writer = null;
        try {
            writer = new StringWriter();
            configWriter.generate(writer, edimap);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return writer.toString();
    }

//    public static void convert(InputStream inputStream, String infile, String message) throws IOException, EdiParseException, InstantiationException, IllegalAccessException {
//        File outDirectory = new File(outDir);
//        if (!outDirectory.exists()) {
//            outDirectory.mkdirs();
//        }
//
//        ConfigReader configReader = ConfigReader.Impls.UNEDIFACT.newInstance();
//
//        List<EdimapConfiguration> edimaps = UnEdifactReader.parse(infile, message, outDirectory.getCanonicalPath());
//
//
//        ConfigWriter configWriter = new ConfigWriter();
//        for (EdimapConfiguration conf : edimaps) {
//           OutputStreamWriter writer = null;
//            try {
//                writer = new OutputStreamWriter(new FileOutputStream(conf.getFilename()));
//                configWriter.generate(writer, conf.getEdimap());
//            } finally {
//                if (writer != null) {
//                    writer.close();
//                }
//            }
//        }
//    }

    private static String getParameter(String flag, String[] args) {
        for (int i = 0; i < args.length; i++ ) {
            if (flag.equalsIgnoreCase(args[i]) && i <= args.length - 1) {
                return args[i+1];
            }
        }
        return null;
    }

    private static String writeUsage() {
        return "Usage edi-converter [params]\n\n"+
                "params\n" +
                "\t-outDir\t\t\t\tThe output directory for the configurationfiles.\n" +
                "\t-inFile\t\t\t\tThe file or directory containing specification to parse.\n" +
                "\t-message\t\t\tThe name of the message to parse. Write 'ALL' to parse all messages.";
    }

    public static void main(String[] args) throws IOException, EdiParseException {

        String outDir = getParameter("-outDir", args);
        String infile = getParameter("-inFile", args);
        String message = getParameter("-message", args);

        if (outDir == null || infile == null || message == null) {
            System.out.println(writeUsage());
            return;
        }


        //convert(outDir, infile, message);
    }


}
