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
package org.milyn.ejc.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.milyn.ejc.EJC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * {@link org.milyn.ejc.EJC} Ant task.
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class EJCAntTask extends Task {

    private File ediMappingModel;
    private File destDir;
    private String packageName;

    public void execute() throws BuildException {
        assertMandatoryProperty(ediMappingModel, "ediMappingModel");
        assertMandatoryProperty(destDir, "destDir");
        assertMandatoryProperty(packageName, "packageName");

        if(!ediMappingModel.exists()) {
            throw new BuildException("Specified EDI Mapping Model file '" + ediMappingModel.getAbsoluteFile() + "' does not exist.");
        }
        if(ediMappingModel.exists() && ediMappingModel.isDirectory()) {
            throw new BuildException("Specified EDI Mapping Model file '" + ediMappingModel.getAbsoluteFile() + "' exists, but is a directory.  Must be an EDI Mapping Model file.");
        }
        if(destDir.exists() && !destDir.isDirectory()) {
            throw new BuildException("Specified EJC destination directory '" + destDir.getAbsoluteFile() + "' exists, but is not a directory.");
        }

        EJC ejc = new EJC();
        FileInputStream configInputStream;
        try {
            configInputStream = new FileInputStream(ediMappingModel);
        } catch (FileNotFoundException e) {
            throw new BuildException("Error opening EDI Mapping Model InputStream '" + ediMappingModel.getAbsoluteFile() + "'.", e);
        }

        try {
            ejc.compile(configInputStream, ediMappingModel.getAbsolutePath(), packageName, destDir.getAbsolutePath());
        } catch (Exception e) {
            throw new BuildException("Error compiling EDI Mapping Model '" + ediMappingModel.getAbsoluteFile() + "'.", e);
        } finally {
            try {
                configInputStream.close();
            } catch (IOException e) {
                throw new BuildException("Error closing EDI Mapping Model '" + ediMappingModel.getAbsoluteFile() + "'.", e);
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
            throw new BuildException("Mandatory EJC property '" + name + "' + not specified in ant task.");
        }
    }
}
