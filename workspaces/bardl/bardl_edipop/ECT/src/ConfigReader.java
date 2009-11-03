import java.io.IOException;
import java.io.File;
import java.util.List;

/**
 * ConfigReader.
 */
public class ConfigReader {

    public static void main(String[] args) throws IOException, EdiParseException {

        String outDir = getParameter("-outDir", args);
        String version = getParameter("-version", args);
        String message = getParameter("-message", args);

        if (outDir == null || version == null || message == null) {
            System.out.println(writeUsage());
            return;
        }
//        String version = "d08a";
//        String message = "*";

        File outDirectory = new File(outDir);
        if (!outDirectory.exists()) {
            outDirectory.mkdirs();
        }

        List<EdimapConfiguration> edimaps = UnEdifactReader.parse(version, message, outDirectory.getCanonicalPath());
        ConfigWriter writer = new ConfigWriter();
        for (EdimapConfiguration conf : edimaps) {
            writer.generate(conf.getFilename(), conf.getEdimap());
        }
        System.out.println("");
    }

    private static String getParameter(String flag, String[] args) {
        for (int i = 0; i < args.length; i++ ) {
            if (flag.equals(args[i]) && i <= args.length - 1) {
                return args[i+1];
            }
        }
        return null;
    }

    private static String writeUsage() {
        return "Usage edi-converter [params]\n\n"+
                "params\n" + 
                "\t-outDir\t\t\t\tThe output directory for the configurationfiles.\n" +
                "\t-version\t\t\tThe version of the edi specification to parse.\n" +
                "\t-message\t\t\tThe name of the message to parse. Write 'ALL' to parse all messages.";
    }

}
