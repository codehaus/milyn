/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2005-2006, JBoss Inc.
 */
package org.milyn.ect;

import java.io.*;
import java.util.zip.ZipInputStream;

/**
 * {@link org.milyn.ect.EdiConvertionTool} Executor specific for Un/Edifact format.
 * @author bardl
 */
public class ECTUnEdifactExecutor {

    private File unEdifactZip;
    private File outFile;
    private String messageName;

    public void execute() throws EdiParseException {
        assertMandatoryProperty(unEdifactZip, "unEdifactZip");
        assertMandatoryProperty(outFile, "outFile");
        assertMandatoryProperty(messageName, "messageName");

        if(!unEdifactZip.exists()) {
            throw new EdiParseException("Specified EDI Mapping Model file '" + unEdifactZip.getAbsoluteFile() + "' does not exist.");
        }
        if(unEdifactZip.exists() && unEdifactZip.isDirectory()) {
            throw new EdiParseException("Specified EDI Mapping Model file '" + unEdifactZip.getAbsoluteFile() + "' exists, but is a directory.  Must be an EDI Mapping Model file.");
        }

        ZipInputStream zipInputStream;
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(unEdifactZip);
            zipInputStream = new ZipInputStream(fileInputStream);
        } catch (FileNotFoundException e) {
            throw new EdiParseException("Error opening zip file containing the Un/Edifact specification '" + unEdifactZip.getAbsoluteFile() + "'.", e);
        }

        Writer writer;
        try {
            writer = new FileWriter(outFile);
        } catch (IOException e) {
            throw new EdiParseException("Error opening writer for outFile '" + outFile.getAbsoluteFile() + "'.", e);
        }
        try {
            EdiConvertionTool.convertUnEdifact(zipInputStream, messageName, writer);
        } catch (Exception e) {
            throw new EdiParseException("Error parsing the Un/Edifact specification '" + unEdifactZip.getAbsoluteFile() + "'.", e);
        } finally {
            try {                                
                zipInputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                throw new EdiParseException("Error closing the zip file containing the Un/Edifact specification '" + unEdifactZip.getAbsoluteFile() + "'.", e);
            }

            try {
                writer.close();
            } catch (IOException e) {
                throw new EdiParseException("Error closing the outFile '" + outFile.getAbsoluteFile() + "'.", e);
            }
        }
    }

    public void setUnEdifactZip(File unEdifactZip) {
        this.unEdifactZip = unEdifactZip;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    private void assertMandatoryProperty(Object obj, String name) {
        if(obj == null) {
            throw new EdiParseException("Mandatory EJC property '" + name + "' + not specified.");
        }
    }
}