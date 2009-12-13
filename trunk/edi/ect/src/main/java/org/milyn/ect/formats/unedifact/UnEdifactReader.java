package org.milyn.ect.formats.unedifact;

import org.milyn.edisax.model.internal.Edimap;
import org.milyn.edisax.model.internal.Import;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.ect.EdimapConfiguration;
import org.milyn.ect.EdiParseException;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * UnEdifactReader
 * @author bardl
 */
public class UnEdifactReader {
    private static final int BUFFER = 2048;
    private static final String INTERCHANGE_DEFINITION = "un-edifact-interchange-definition.xml";

    public static List<EdimapConfiguration> parse(String infile, String message, String outDirectory) throws IOException, EdiParseException {

        File zipfile =  new File(infile);
        String version = zipfile.getName().substring(0, zipfile.getName().lastIndexOf('.'));

        String definitionResource = outDirectory + File.separator + "un-edifact-definition-" + version + ".xml";
        File decompressedDir = unzpipAll(zipfile);                                                     
        String fileExtension = getFileExtension(decompressedDir + File.separator + "eded");

        List<EdimapConfiguration> edimaps = new ArrayList<EdimapConfiguration>();

        //Interchange envelope. Handcoded at the moment.
        String interchangeDefinitionResource = outDirectory + File.separator + INTERCHANGE_DEFINITION;
        EdifactModel interchangeEnvelope = new EdifactModel();
        try {
            interchangeEnvelope.parseSequence(Thread.currentThread().getContextClassLoader().getResourceAsStream("org/milyn/ect/formats/unedifact/" + INTERCHANGE_DEFINITION));
        } catch (Exception e) {
            throw new EdiParseException(e.getMessage(), e);
        }

        Import interchangeEnvImport = new Import();
        interchangeEnvImport.setNamespace(UnEdifactMessageReader.INTERCHANGE_NAMESPACE);
        interchangeEnvImport.setResource(new File(interchangeDefinitionResource).toURI().toString());

        // Read Definition Configuration
        Edimap definitionEdimap = parseEDIDefinitionFiles(decompressedDir, fileExtension);

        // Read Message Configurations
        File messageDir = new File(decompressedDir + File.separator + "edmd");
        for (String fileName : messageDir.list()) {
            if (message.equalsIgnoreCase("ALL") || fileName.toLowerCase().startsWith(message.toLowerCase())) {
                System.out.println("Parsing message [" + fileName + "]");
                EdimapConfiguration edimapConfig = parseEDIMessage(messageDir + File.separator + fileName, outDirectory + File.separator + "un-edifact-message-" + fileName + ".xml", new File(definitionResource).toURI().toString(), interchangeEnvImport);
                if (edimapConfig.getEdimap() != null) {
                    edimaps.add(edimapConfig);
                }
            }
        }

        // Prepare Edimap for output.
        definitionEdimap.getSegments().setXmltag(edimaps.get(0).getEdimap().getDescription().getName() + "-Definition");
        definitionEdimap.setDescription(edimaps.get(0).getEdimap().getDescription());
        definitionEdimap.setDelimiters(edimaps.get(0).getEdimap().getDelimiters());
        edimaps.add(new EdimapConfiguration(definitionEdimap, definitionResource));

        edimaps.add(new EdimapConfiguration( interchangeEnvelope.getEdimap(), interchangeDefinitionResource));



        return edimaps;
    }

    private static EdimapConfiguration parseEDIMessage(String fileName, String outFile, String definitionResource, Import interchangeEnvImport) throws IOException {

        Edimap edimap;
        Reader messageISR = null;
        InputStream messageFIS = null;
        try {
            messageFIS = new FileInputStream(fileName);
            messageISR = new InputStreamReader(messageFIS);
            edimap = UnEdifactMessageReader.readMessage(messageISR);
            if (edimap != null) {
                edimap.getImports().get(0).setResource(definitionResource);
                edimap.getImports().add(interchangeEnvImport);
            }
        } finally {
            if (messageFIS != null) {
                messageFIS.close();
            }
            if (messageISR != null) {
                messageISR.close();
            }            
        }
        return new EdimapConfiguration(edimap, outFile);
    }

    private static Edimap parseEDIDefinitionFiles(File decompressedDir, String fileExtension) throws IOException, EdiParseException {
        Edimap definitionEdimap;
        FileInputStream dataFIS = null;
        FileInputStream compositeFIS = null;
        FileInputStream segmentFIS = null;
        Reader dataISR = null;
        Reader compositeISR = null;
        Reader segmentISR = null;
        try {
            dataFIS = new FileInputStream(decompressedDir + File.separator + "eded" + File.separator + "EDED" + fileExtension);
            dataISR = new InputStreamReader(dataFIS);
            compositeFIS = new FileInputStream(decompressedDir + File.separator + "edcd" + File.separator + "EDCD" + fileExtension);
            compositeISR = new InputStreamReader(compositeFIS);
            segmentFIS = new FileInputStream(decompressedDir + File.separator + "edsd" + File.separator + "EDSD" + fileExtension);
            segmentISR = new InputStreamReader(segmentFIS);

            definitionEdimap = UnCefactDefinitionReader.test(dataISR, compositeISR, segmentISR);
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
            if (dataFIS != null) {
                dataFIS.close();
            }
            if (compositeFIS != null) {
                compositeFIS.close();
            }
            if (segmentFIS != null) {
                segmentFIS.close();
            }
        }
        return definitionEdimap;
    }

    private static String getFileExtension(String path) throws EdiParseException {
        File file = new File(path);
        String[] files = file.list();
        if (files.length > 0) {
            return files[0].substring(files[0].lastIndexOf('.'), files[0].length());
        } else {
            throw new EdiParseException("Could not decide file extension in edifact specification.");
        }
    }

    private static File unzpipAll(File file) throws IOException {
        File decompressedDir = unzpip(file);

        FilenameFilter filter = new FilenameFilter(){
            public boolean accept(File dir, String name) {
                return name.matches(".*\\.zip");
            }};
        for (File zipFile : decompressedDir.listFiles(filter)) {
            unzpip(zipFile);
        }

        return decompressedDir;
    }

    private static File unzpip(File file) throws IOException {
        ZipInputStream zis = null;
        try {
            File unzipDirectory = new File(file.getCanonicalPath().substring(0, file.getCanonicalPath().length() - 4));
            BufferedOutputStream dest;
            FileInputStream fis = new FileInputStream(file);
            zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
                int count;
                byte data[] = new byte[BUFFER];

                // write the files to the disk
                if (!unzipDirectory.exists()) {
                    unzipDirectory.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(new File(unzipDirectory.getCanonicalPath() + File.separator + entry.getName()));                
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
                fos.close();
            }
            return unzipDirectory;
        } finally {
            if (zis != null) {
                zis.close();
            }
        }
    }
}