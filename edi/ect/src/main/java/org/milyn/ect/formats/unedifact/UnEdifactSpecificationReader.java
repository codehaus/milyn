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
package org.milyn.ect.formats.unedifact;

import org.milyn.ect.EdiSpecificationReader;
import org.milyn.ect.EdiParseException;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Description;
import org.milyn.edisax.model.internal.Edimap;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.milyn.util.ClassUtil;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * UN/EDIFACT Specification Reader.
 * 
 * @author bardl
 */
public class UnEdifactSpecificationReader implements EdiSpecificationReader {
    
    private static final int BUFFER = 2048;
    private static final String INTERCHANGE_DEFINITION = "un-edifact-interchange-definition.xml";

    private boolean useImport;
    private Map<String, byte[]> definitionFiles;
    private Map<String, byte[]> messageFiles;
    private Edimap definitionModel;

    public UnEdifactSpecificationReader(ZipInputStream specificationInStream, boolean useImport) throws IOException {
        this.useImport = useImport;

        if (!(specificationInStream instanceof ZipInputStream)) {
            throw new IOException("InputStream should be a ZipInputStream when parsing UnEdifact specification.");
        }

        ZipInputStream zipInputStream = (ZipInputStream)specificationInStream;

        definitionFiles = new HashMap<String, byte[]>();
        messageFiles = new HashMap<String, byte[]>();
        readDefinitionEntries(zipInputStream, new ZipDirectoryEntry("eded.", definitionFiles), new ZipDirectoryEntry("edcd.", definitionFiles), new ZipDirectoryEntry("edsd.", definitionFiles), new ZipDirectoryEntry("edmd.", "*", messageFiles));

        // Read Definition Configuration
        definitionModel = parseEDIDefinitionFiles();

        //Interchange envelope is inserted into the definitions. Handcoded at the moment.
        try {
            EdifactModel interchangeEnvelope = new EdifactModel();
            interchangeEnvelope.parseSequence(ClassUtil.getResourceAsStream(INTERCHANGE_DEFINITION, this.getClass()));
            definitionModel.getSegments().getSegments().addAll(interchangeEnvelope.getEdimap().getSegments().getSegments());
        } catch (Exception e) {
            throw new EdiParseException(e.getMessage(), e);
        }

    }

    public Set<String> getMessageNames() {
        Set<String> names = new HashSet<String>(messageFiles.keySet());
        names.add(definitionModel.getDescription().getName());
        return names;
    }

    public Edimap getMappingModel(String messageName) throws IOException {
        if(messageName.equals(definitionModel.getDescription().getName())) {
            return definitionModel;
        } else {
            return parseEdiMessage(messageName);
        }
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
        return definitionModel;
    }

    private Edimap parseEDIDefinitionFiles() throws IOException, EdiParseException {

        Edimap edifactModel;
        Reader dataISR = null;
        Reader compositeISR = null;
        Reader segmentISR = null;
        try {
            dataISR = new InputStreamReader(new ByteArrayInputStream(definitionFiles.get("eded.")));
            compositeISR = new InputStreamReader(new ByteArrayInputStream(definitionFiles.get("edcd.")));
            segmentISR = new InputStreamReader(new ByteArrayInputStream(definitionFiles.get("edsd.")));

            edifactModel = UnEdifactDefinitionReader.parse(dataISR, compositeISR, segmentISR);
            edifactModel.setDescription(new Description().setName("_UnEdifactDefinitionMap").setVersion("local"));
            edifactModel.getSegments().setXmltag("DefinitionMap");
            edifactModel.setDelimiters(UNEdifactInterchangeParser.defaultUNEdifactDelimiters);
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

    }




    private static void readDefinitionEntries(ZipInputStream folderZip, ZipDirectoryEntry... entries) throws IOException {

        ZipEntry fileEntry = folderZip.getNextEntry();
        while (fileEntry != null) {
            String fName = new File(fileEntry.getName().toLowerCase()).getName().replaceFirst("tr", "ed");
            for (ZipDirectoryEntry entry : entries) {
                if (fName.startsWith(entry.getDirectory())) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    byte[] bytes = new byte[BUFFER];
                    int size;
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
            String fName = new File(fileEntry.getName().toLowerCase()).getName().replaceFirst("tr", "ed");
            if (fName.startsWith(entry) || entry.equals("*")) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] bytes = new byte[2048];
                int size;
                  while ((size = folderZip.read(bytes, 0, bytes.length)) != -1) {
                    translatePseudoGraph(bytes);
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

    private static void translatePseudoGraph(byte[] bytes)
    {
	for (int i=0, l=bytes.length; i<l; i++)
	{
	    switch(bytes[i])
	    {
		case (byte)0xC4:
		    bytes[i] = (byte)'-';
		    break;

		case (byte)0xC1:
		case (byte)0xBF:
		case (byte)0xD9:
		    bytes[i] = (byte)'+';
		    break;

		case (byte)0xB3:
		    bytes[i] = (byte)'|';
		    break;
	    }
	}
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
