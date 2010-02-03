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
package org.milyn.ejc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * {@link EJC} Executor.
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class EJCExecutor {

    private File ediMappingModel;
    private File destDir;
    private String packageName;

    public void execute() throws EJCException {
        assertMandatoryProperty(ediMappingModel, "ediMappingModel");
        assertMandatoryProperty(destDir, "destDir");
        assertMandatoryProperty(packageName, "packageName");

        if(!ediMappingModel.exists()) {
            throw new EJCException("Specified EDI Mapping Model file '" + ediMappingModel.getAbsoluteFile() + "' does not exist.");
        }
        if(ediMappingModel.exists() && ediMappingModel.isDirectory()) {
            throw new EJCException("Specified EDI Mapping Model file '" + ediMappingModel.getAbsoluteFile() + "' exists, but is a directory.  Must be an EDI Mapping Model file.");
        }
        if(destDir.exists() && !destDir.isDirectory()) {
            throw new EJCException("Specified EJC destination directory '" + destDir.getAbsoluteFile() + "' exists, but is not a directory.");
        }

        EJC ejc = new EJC();
        FileInputStream configInputStream;
        try {
            configInputStream = new FileInputStream(ediMappingModel);
        } catch (FileNotFoundException e) {
            throw new EJCException("Error opening EDI Mapping Model InputStream '" + ediMappingModel.getAbsoluteFile() + "'.", e);
        }

        try {
            ejc.compile(configInputStream, ediMappingModel.getAbsolutePath(), packageName, destDir.getAbsolutePath());
        } catch (Exception e) {
            throw new EJCException("Error compiling EDI Mapping Model '" + ediMappingModel.getAbsoluteFile() + "'.", e);
        } finally {
            try {
                configInputStream.close();
            } catch (IOException e) {
                throw new EJCException("Error closing EDI Mapping Model '" + ediMappingModel.getAbsoluteFile() + "'.", e);
            }
        }
    }

    public void setEdiMappingModel(File ediMappingModel) {
        this.ediMappingModel = ediMappingModel;
    }

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private void assertMandatoryProperty(Object obj, String name) {
        if(obj == null) {
            throw new EJCException("Mandatory EJC property '" + name + "' + not specified.");
        }
    }
}