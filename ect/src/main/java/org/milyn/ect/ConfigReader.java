package org.milyn.ect;

import org.milyn.ect.formats.unedifact.UnEdifactReader;

import java.io.IOException;
import java.io.File;
import java.util.List;

/**
 * ConfigReader.
 */
public class ConfigReader {

    public static void main(String[] args) throws IOException, EdiParseException {

        String outDir = getParameter("-outDir", args);
        String infile = getParameter("-inFile", args);
        String message = getParameter("-message", args);

        if (outDir == null || infile == null || message == null) {
            System.out.println(writeUsage());
            return;
        }

        convert(outDir, infile, message);
    }

    public static void convert(String outDir, String infile, String message) throws IOException, EdiParseException {
        File outDirectory = new File(outDir);
        if (!outDirectory.exists()) {
            outDirectory.mkdirs();
        }

        List<EdimapConfiguration> edimaps = UnEdifactReader.parse(infile, message, outDirectory.getCanonicalPath());
        ConfigWriter writer = new ConfigWriter();
        for (EdimapConfiguration conf : edimaps) {
            writer.generate(conf.getFilename(), conf.getEdimap());
        }
    }

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

}
