
import org.milyn.edisax.model.internal.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

/**
 * UnEdifactMessageReader
 * @author bardl
 */
public class UnEdifactMessageReader {

    /**
     * Marks the start of the Message Definition section.
     */
    private static final String MESSAGE_DEFINITION = "[\\d\\. ]*MESSAGE DEFINITION";

    /**
     * Extracts description from start of segment documentation.
     * Group1 = id
     * Group2 = documentation
     */
    private static final String MESSAGE_DEFINITION_START = "^(\\d{4}) *(.*)";

    /**
     * Marks the end of the Message Definition section.
     */
    private static final String MESSAGE_DEFINITION_END = "([\\d\\.]* *Segment index .*)";


    /**
     * Extracts the value for Message type, version, release and agency.
     */
    private static final Pattern MESSAGE_TYPE = Pattern.compile(".*Message Type *: *(\\w*)");
    private static final Pattern MESSAGE_RELEASE = Pattern.compile(".*Release *: *(\\w*)");
    private static final Pattern MESSAGE_AGENCY = Pattern.compile(".*Contr. Agency *: *(\\w*)");
    private static final Pattern MESSAGE_VERSION = Pattern.compile(".*Version *: *(\\w*)");

    /**
     * Marks the start of the Segment table section.
     */
    private static final String SEGMENT_TABLE = "[\\d\\. ]*Segment table";
    private static final String SEGMENT_TABLE_HEADER = "Pos *Tag *Name *S *R.*";

    /**
     * Extracts information from Regular segment definition.
     * Group1 = id
     * Group2 = segcode
     * Group3 = description
     * Group4 = isMandatory
     * Group5 = max occurance
     */
    private static String SEGMENT_REGULAR = "(\\d{4}) *(\\w{3}) *([\\w /]*) *(M|C) *(\\d*)[ \\|]*";

    /**
     * Matches and extracts information from start of segment group.
     * Group1 = id
     * Group2 = name
     * Group4 = isMandatory
     * Group5 = max occurance 
     */
    private static String SEGMENT_GROUP_START = "(\\d{4}) *-* *(Segment group \\d*) *-* *(C|M) *(\\d*)[-+|]*";

    /**
     * Matches and extracts information from segment at end of segment group.
     * Group1 = id
     * Group2 = segcode
     * Group3 = description
     * Group4 = isMandatory
     * Group5 = max occurance
     * Group6 = nrOfClosedGroups
     */
    private static String SEGMENT_GROUP_END = "(\\d{4}) *(\\w{3}) *([\\w /]*) *(C|M) *(\\d*)([-|\\+]*)";

    /**
     * Newline character applied between documentation lines.
     */
    private static final String NEW_LINE = "\n";

    /**
     * A message must match the LEGAL_MESSAGE pattern. Otherwise it may be an index file located in the message folder.
     */
    private static final String LEGAL_MESSAGE = " *UN/EDIFACT";

    /**
     * Default settings for UN/EDIFACT.
     */
    private static final String MESSAGE_NAME = "UN-EDIFACT";
    private static final String DELIMITER_SEGMENT = "&#39;";
    private static final String DELIMITER_COMPOSITE = "+";
    private static final String DELIMITER_SUB_COMPOSITE = "~";
    private static final String DELIMITER_DATA = ":";
    private static final String ESCAPE = "?";

    public static Edimap readMessage(Reader reader) throws IOException {

        Edimap edimap = null;
        BufferedReader breader = null;
        try {
            breader = new BufferedReader(reader);
            if (!legalMessage(breader)) {
                return null;
            }

            String type = getValue(breader, MESSAGE_TYPE);
            String version = getValue(breader, MESSAGE_VERSION);
            String release = getValue(breader, MESSAGE_RELEASE);
            String agency = getValue(breader, MESSAGE_AGENCY);

            edimap = new Edimap();
            SegmentGroup rootGroup = new SegmentGroup();
            rootGroup.setXmltag(type);
            edimap.setSegments(rootGroup);            

            Delimiters delimiters = new Delimiters();
            delimiters.setSegment(DELIMITER_SEGMENT);
            delimiters.setField(DELIMITER_COMPOSITE);
            delimiters.setComponent(DELIMITER_SUB_COMPOSITE);
            delimiters.setSubComponent(DELIMITER_DATA);
            delimiters.setEscape(ESCAPE);
            edimap.setDelimiters(delimiters);

            edimap.setDescription(new Description());
            edimap.getDescription().setName(MESSAGE_NAME);
            edimap.getDescription().setVersion(version + release);

            Import ediImport = new Import();
            ediImport.setNamespace(agency);
            edimap.getImport().add(ediImport);

            Map<String, String> definitions = parseMessageDefinition(breader);

            parseMessageStructure(breader, rootGroup, definitions);

        } finally {
            if (breader != null) {
                breader.close();
            }
        }

        return edimap;
    }

    private static boolean legalMessage(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return line.matches(LEGAL_MESSAGE);
    }

    private static void parseMessageStructure(BufferedReader reader, SegmentGroup group, Map<String, String> definitions) throws IOException {
        String line = reader.readLine();
        while (!line.matches(SEGMENT_TABLE)) {
            line = reader.readLine();
        }

        while (!line.matches(SEGMENT_TABLE_HEADER)) {
            line = reader.readLine();
        }
        parseNextSegment(reader, group, definitions);
    }

    private static Map<String, String> parseMessageDefinition(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (!line.matches(MESSAGE_DEFINITION)) {
            line = reader.readLine();
        }

        while (!line.matches(MESSAGE_DEFINITION_START)) {
            line = reader.readLine();
        }

        Map<String, String> definitions = new HashMap<String, String>();
        while (!line.matches(MESSAGE_DEFINITION_END)) {
            if (line.matches(MESSAGE_DEFINITION_START)) {
                Pattern pattern = Pattern.compile(MESSAGE_DEFINITION_START);
                Matcher matcher = pattern.matcher(line);
                matcher.matches();

                String id = matcher.group(1);
                StringBuilder definition = new StringBuilder();
                definition.append(matcher.group(2)).append(NEW_LINE);
                line = reader.readLine();

                while (!line.matches(MESSAGE_DEFINITION_START) && !line.matches(MESSAGE_DEFINITION_END)) {
                    definition.append(line).append(NEW_LINE);
                    line = reader.readLine();
                }
                definitions.put(id, definition.toString());
            } else {
                line = reader.readLine();
            }

        }
        return definitions;
    }

    private static int parseNextSegment(BufferedReader reader, SegmentGroup parentGroup, Map<String, String> definitions) throws IOException {
        String line = reader.readLine();
        while (line != null) {
            if (line.matches(SEGMENT_REGULAR)) {
                Matcher matcher = Pattern.compile(SEGMENT_REGULAR).matcher(line);
                matcher.matches();
                Segment segment = createSegment(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5), definitions);
                parentGroup.getSegments().add(segment);
            } else if (line.matches(SEGMENT_GROUP_START)) {
                Matcher matcher = Pattern.compile(SEGMENT_GROUP_START).matcher(line);
                matcher.matches();
                SegmentGroup group = createGroup(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), definitions);
                parentGroup.getSegments().add(group);

                int result = parseNextSegment(reader, group, definitions);
                if (result != 0) {
                    return result - 1;
                }

            } else if (line.matches(SEGMENT_GROUP_END)) {
                Matcher matcher = Pattern.compile(SEGMENT_GROUP_END).matcher(line);
                matcher.matches();
                Segment segment = createSegment(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5), definitions);
                parentGroup.getSegments().add(segment);
                return extractPlusCharacter(matcher.group(6)).length() - 1;
            }
            
            line = reader.readLine();
        }
        return 0;
    }

    private static String extractPlusCharacter(String value) {
        return value.replaceAll("[^\\+]", "");
    }

    private static SegmentGroup createGroup(String id, String name, String mandatory, String maxOccurance, Map<String, String> definitions) {
        SegmentGroup group = new SegmentGroup();
        group.setXmltag(name.trim());
        group.setDocumentation(definitions.get(id).trim());
        group.setMinOccurs(mandatory.equals("M") ? 1 : 0);
        group.setMaxOccurs(Integer.valueOf(maxOccurance));
        return group;
    }

    private static Segment createSegment(String id, String segcode, String description, String mandatory, String maxOccurance, Map<String, String> definitions) {
        Segment segment = new Segment();
        segment.setSegcode(segcode);
        segment.setXmltag(description.trim());
        segment.setDocumentation(definitions.get(id).trim());
        segment.setMinOccurs(mandatory.equals("M") ? 1 : 0);
        segment.setMaxOccurs(Integer.valueOf(maxOccurance));
        return segment;
    }

    private static String getValue(BufferedReader reader, Pattern pattern) throws IOException {
        String line = reader.readLine();
        Matcher matcher = pattern.matcher(line);
        while (!matcher.matches()) {
            line = reader.readLine();
            matcher = pattern.matcher(line);
        }
        return matcher.group(1);
    }



    public static Edimap test() throws IOException {
        FileInputStream in = null;
        Reader reader = null;
        try {
            in = new FileInputStream("C:\\Documents and Settings\\bardl\\Skrivbord\\d08a\\edmd\\INVOIC_D.08A");
            reader = new InputStreamReader(in);            
            return UnEdifactMessageReader.readMessage(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }
}
