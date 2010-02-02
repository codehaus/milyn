package org.milyn.ect.formats.unedifact;

import org.milyn.edisax.model.internal.Edimap;
import org.milyn.edisax.model.internal.Import;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.ect.EdimapConfiguration;
import org.milyn.ect.EdiParseException;
import org.milyn.ect.ConfigReader;
import org.milyn.util.ClassUtil;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * UnEdifactReader
 * @author bardl
 */
public class UnEdifactReader implements ConfigReader {
    
    private static final int BUFFER = 2048;
    private static final String INTERCHANGE_DEFINITION = "un-edifact-interchange-definition.xml";

    private boolean useImport;
    private Map<String, byte[]> definitionFiles;
    private Map<String, byte[]> messageFiles;
    private Edimap definitionModel;

    public void initialize(InputStream inputStream, boolean useImport) throws IOException, EdiParseException {
        this.useImport = useImport;

        if (!(inputStream instanceof ZipInputStream)) {
            throw new IOException("InputStream should be a ZipInputStream when parsing UnEdifact specification.");
        }

        ZipInputStream zipInputStream = (ZipInputStream)inputStream;

        definitionFiles = new HashMap<String, byte[]>();
        messageFiles = new HashMap<String, byte[]>();
        readDefinitionEntries(zipInputStream, new ZipDirectoryEntry("eded.", definitionFiles), new ZipDirectoryEntry("edcd.", definitionFiles), new ZipDirectoryEntry("edsd.", definitionFiles), new ZipDirectoryEntry("edmd.", "*", messageFiles));
        List<EdimapConfiguration> edimaps = new ArrayList<EdimapConfiguration>();

        // Read Definition Configuration
        definitionModel = parseEDIDefinitionFiles();

        //Interchange envelope is inserted into the definitions. Handcoded at the moment.
        try {
            EdifactModel interchangeEnvelope = new EdifactModel();
            interchangeEnvelope.parseSequence(Thread.currentThread().getContextClassLoader().getResourceAsStream("org/milyn/ect/formats/unedifact/" + INTERCHANGE_DEFINITION));
            definitionModel.getSegments().getSegments().addAll(interchangeEnvelope.getEdimap().getSegments().getSegments());
        } catch (Exception e) {
            throw new EdiParseException(e.getMessage(), e);
        }


//        Import interchangeEnvImport = new Import();
//        interchangeEnvImport.setNamespace(UnEdifactMessageReader.INTERCHANGE_NAMESPACE);
//        interchangeEnvImport.setResource(new File(interchangeDefinitionResource).toURI().toString());
//
//                              
//
//        // Read Message Configurations
//        File messageDir = new File(decompressedDir + File.separator + "edmd");
//        for (String fileName : messageDir.list()) {
//            if (message.equalsIgnoreCase("ALL") || fileName.toLowerCase().startsWith(message.toLowerCase())) {
//                System.out.println("Parsing message [" + fileName + "]");
//                EdimapConfiguration edimapConfig = parseEDIMessage(messageDir + File.separator + fileName, outDirectory + File.separator + "un-edifact-message-" + fileName + ".xml", new File(definitionResource).toURI().toString(), interchangeEnvImport);
//                if (edimapConfig.getEdimap() != null) {
//                    edimaps.add(edimapConfig);
//                }
//            }
//        }
//
//        // Prepare Edimap for output.
//        definitionEdimap.getSegments().setXmltag(edimaps.get(0).getEdimap().getDescription().getName() + "-Definition");
//        definitionEdimap.setDescription(edimaps.get(0).getEdimap().getDescription());
//        definitionEdimap.setDelimiters(edimaps.get(0).getEdimap().getDelimiters());
//        edimaps.add(new EdimapConfiguration(definitionEdimap, definitionResource));
//
//        edimaps.add(new EdimapConfiguration( interchangeEnvelope.getEdimap(), interchangeDefinitionResource));
//



    }

    public Set<String> getMessageNames() {
        return messageFiles.keySet();
    }

    public Edimap getMappingModelForMessage(String messageName) throws IOException {
        return parseEdiMessage(messageName);
    }

    private Edimap parseEdiMessage(String messageName) throws IOException {
        byte[] message = messageFiles.get(messageName);

        Edimap edimap = null;
        if (message != null) {
            InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(message));
            try {
                edimap = UnEdifactMessageReader.readMessage(reader, useImport, definitionModel);
            } finally {
                reader.close();
            }
        }
        return edimap;
    }

    public Edimap getDefinitionModel() throws IOException {
        if (definitionModel.getDescription() == null) {
            Edimap messageEdimap = parseEdiMessage(messageFiles.keySet().iterator().next());

            // Prepare Edimap for output.
            definitionModel.getSegments().setXmltag(messageEdimap.getDescription().getName() + "-Definition");
            definitionModel.setDescription(messageEdimap.getDescription());
            definitionModel.setDelimiters(messageEdimap.getDelimiters());
        }
        return definitionModel;
    }

//    private static EdimapConfiguration parseEDIMessage(String fileName, String outFile, String definitionResource, Import interchangeEnvImport) throws IOException {
//
//        Edimap edimap;
//        Reader messageISR = null;
//        InputStream messageFIS = null;
//        try {
//            messageFIS = new FileInputStream(fileName);
//            messageISR = new InputStreamReader(messageFIS);
//            edimap = UnEdifactMessageReader.readMessage(messageISR);
//            if (edimap != null) {
//                edimap.getImports().get(0).setResource(definitionResource);
//                edimap.getImports().add(interchangeEnvImport);
//            }
//        } finally {
//            if (messageFIS != null) {
//                messageFIS.close();
//            }
//            if (messageISR != null) {
//                messageISR.close();
//            }
//        }
//        return new EdimapConfiguration(edimap, outFile);
//    }

    private Edimap parseEDIDefinitionFiles() throws IOException, EdiParseException {

        Edimap edifactModel;
        Reader dataISR = null;
        Reader compositeISR = null;
        Reader segmentISR = null;
        try {
            dataISR = new InputStreamReader(new ByteArrayInputStream(definitionFiles.get("eded.")));
            compositeISR = new InputStreamReader(new ByteArrayInputStream(definitionFiles.get("edcd.")));
            segmentISR = new InputStreamReader(new ByteArrayInputStream(definitionFiles.get("edsd.")));

            edifactModel = UnCefactDefinitionReader.parse(dataISR, compositeISR, segmentISR);
        } finally {
            if (dataISR != null) {
                dataISR.close();
            }
            if (compositeISR != null) {
                compositeISR.close();
            }
            if (segmentISR != null) {
                segmentISR.close();
            }
        }
        return edifactModel;




//        Edimap definitionEdimap;
//        FileInputStream dataFIS = null;
//        FileInputStream compositeFIS = null;
//        FileInputStream segmentFIS = null;
//        Reader dataISR = null;
//        Reader compositeISR = null;
//        Reader segmentISR = null;
//        try {
//            dataFIS = new FileInputStream(decompressedDir + File.separator + "eded" + File.separator + "EDED" + fileExtension);
//            dataISR = new InputStreamReader(dataFIS);
//            compositeFIS = new FileInputStream(decompressedDir + File.separator + "edcd" + File.separator + "EDCD" + fileExtension);
//            compositeISR = new InputStreamReader(compositeFIS);
//            segmentFIS = new FileInputStream(decompressedDir + File.separator + "edsd" + File.separator + "EDSD" + fileExtension);
//            segmentISR = new InputStreamReader(segmentFIS);
//
//            definitionEdimap = UnCefactDefinitionReader.test(dataISR, compositeISR, segmentISR);
//        } finally {
//            if (dataISR != null) {
//                dataISR.close();
//            }
//            if (compositeISR != null) {
//                compositeISR.close();
//            }
//            if (segmentISR != null) {
//                segmentISR.close();
//            }
//            if (dataFIS != null) {
//                dataFIS.close();
//            }
//            if (compositeFIS != null) {
//                compositeFIS.close();
//            }
//            if (segmentFIS != null) {
//                segmentFIS.close();
//            }
//        }
//        return definitionEdimap;
    }




    private static void readDefinitionEntries(ZipInputStream folderZip, ZipDirectoryEntry... entries) throws IOException {

        ZipEntry fileEntry = folderZip.getNextEntry();
        while (fileEntry != null) {
            for (ZipDirectoryEntry entry : entries) {
                if (fileEntry.getName().toLowerCase().startsWith(entry.getDirectory())) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    byte[] bytes = new byte[BUFFER];
                    int size = 0;
                      while ((size = folderZip.read(bytes, 0, bytes.length)) != -1) {
                        baos.write(bytes, 0, size);
                      }

                    ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()));
                    readZipEntry(entry.getEntries(), zipInputStream, entry.getFile());
                    zipInputStream.close();
                }
            }
            folderZip.closeEntry();
            fileEntry = folderZip.getNextEntry();
        }
    }

    private static boolean readZipEntry(Map<String, byte[]> files, ZipInputStream folderZip, String entry) throws IOException {

        boolean result = false;

        ZipEntry fileEntry = folderZip.getNextEntry();
        while (fileEntry != null) {
            if (fileEntry.getName().toLowerCase().startsWith(entry) || entry.equals("*")) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] bytes = new byte[2048];
                int size = 0;
                  while ((size = folderZip.read(bytes, 0, bytes.length)) != -1) {
                    baos.write(bytes, 0, size);
                  }

                result = true;
                if (entry.equals("*")) {
                    if (fileEntry.getName().indexOf('_') != -1) {
                        files.put(fileEntry.getName().substring(0, fileEntry.getName().indexOf('_')), baos.toByteArray());
                    }
                } else {
                    files.put(entry, baos.toByteArray());
                    break;
                }
            }
            folderZip.closeEntry();
            fileEntry = folderZip.getNextEntry();
        }

        return result;
    }


    private static class ZipDirectoryEntry {
        private String directory;
        private String file;
        private Map<String, byte[]> entries;

        private ZipDirectoryEntry(String directory, Map<String, byte[]> entries) {
            this(directory, directory, entries);
        }

        public ZipDirectoryEntry(String directory, String file, Map<String, byte[]> entries) {
            this.directory = directory;
            this.file = file;
            this.entries = entries;
        }

        public String getDirectory() {
            return directory;
        }

        public String getFile() {
            return file;
        }

        public Map<String, byte[]> getEntries() {
            return entries;
        }
    }


}