/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License (version 2.1) as published by the Free Software
 *  Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  See the GNU Lesser General Public License for more details:
 *  http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.ect;

import org.milyn.archive.Archive;
import org.milyn.assertion.AssertArgument;
import org.milyn.ect.formats.unedifact.UnEdifactSpecificationReader;
import org.milyn.edisax.EDIUtils;
import org.milyn.edisax.model.internal.Edimap;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * EDI Mapping Model configuration set writer.
 * <p/>
 * Takes the set of messages from an {@link EdiSpecificationReader} and generates
 * a Smooks EDI Mapping Model zip file.  This zip can be used with an
 * appropriate EDI Reader and can also be used as input to EJC for generating a
 * Java binding object model.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ModelConfigSetWriter {

    /**
     * Write an EDI Mapping Model configuration set from a UN/EDIFACT
     * specification.
     *
     * @param specification The UN/EDIFACT specification zip file.
     * @param modelSetOutStream The output zip stream for the generated EDI Mapping Model configuration set.
     * @throws IOException Error writing Mapping Model configuration set.
     */
    public static void fromUnEdifactSpec(ZipInputStream specification, ZipOutputStream modelSetOutStream, String pathPrefix) throws IOException {
        try {
            fromSpec(new UnEdifactSpecificationReader(specification, true), modelSetOutStream, pathPrefix);
        } finally {
            specification.close();
        }
    }

    /**
     * Write an EDI Mapping Model configuration set from the specified EDI Specification Reader.
     * @param ediSpecificationReader The configuration reader for the EDI interchange configuration set.
     * @param modelSetOutStream The EDI Mapping Model output Stream.
     * @throws IOException Error writing Mapping Model configuration set.
     */
    public static void fromSpec(EdiSpecificationReader ediSpecificationReader, ZipOutputStream modelSetOutStream, String pathPrefix) throws IOException {
        AssertArgument.isNotNull(ediSpecificationReader, "ediSpecificationReader");
        AssertArgument.isNotNull(modelSetOutStream, "modelSetOutStream");

        try {
            Archive archive = createArchive(ediSpecificationReader, pathPrefix);

            // Now output the generated archive...
            archive.toOutputStream(modelSetOutStream);
        } finally {
            modelSetOutStream.close();
        }
    }

    /**
     * Write an EDI Mapping Model configuration set from a UN/EDIFACT
     * specification.
     *
     * @param specification The UN/EDIFACT specification zip file.
     * @param modelSetOutFolder The output folder for the generated EDI Mapping Model configuration set.
     * @throws IOException Error writing Mapping Model configuration set.
     */
    public static void fromUnEdifactSpec(ZipInputStream specification, File modelSetOutFolder, String pathPrefix) throws IOException {
        try {
            fromSpec(new UnEdifactSpecificationReader(specification, true), modelSetOutFolder, pathPrefix);
        } finally {
            specification.close();
        }
    }

    /**
     * Write an EDI Mapping Model configuration set from the specified EDI Specification Reader.
     * @param ediSpecificationReader The configuration reader for the EDI interchange configuration set.
     * @param modelSetOutFolder The output folder for the generated EDI Mapping Model configuration set.
     * @throws IOException Error writing Mapping Model configuration set.
     */
    public static void fromSpec(EdiSpecificationReader ediSpecificationReader, File modelSetOutFolder, String pathPrefix) throws IOException {
        AssertArgument.isNotNull(ediSpecificationReader, "ediSpecificationReader");
        AssertArgument.isNotNull(modelSetOutFolder, "modelSetOutFolder");

        Archive archive = createArchive(ediSpecificationReader, pathPrefix);

        // Now output the generated archive...
        archive.toFileSystem(modelSetOutFolder);
    }

    private static Archive createArchive(EdiSpecificationReader ediSpecificationReader, String pathPrefix) throws IOException {
        Archive archive = new Archive();
        StringBuilder modelListBuilder = new StringBuilder();
        ConfigWriter configWriter = new ConfigWriter();
        Set<String> messages = ediSpecificationReader.getMessageNames();
        StringWriter messageEntryWriter = new StringWriter();

        for(String message : messages) {
            Edimap model = ediSpecificationReader.getMappingModel(message);
            String messageEntryPath = pathPrefix + "/" + message + ".xml";

            // Generate the mapping model for this message...
            messageEntryWriter.getBuffer().setLength(0);
            configWriter.generate(messageEntryWriter, model);
            messageEntryWriter.flush();

            // Add the generated mapping model to the archive...
            archive.addEntry(messageEntryPath, messageEntryWriter.toString());

            // Add this messages archive entry to the mapping model list file...
            modelListBuilder.append("/" + messageEntryPath + "\n");
        }

        // Add the generated mapping model to the archive...
        archive.addEntry(EDIUtils.EDI_MAPPING_MODEL_ZIP_LIST_FILE, modelListBuilder.toString());
        
        return archive;
    }
}
