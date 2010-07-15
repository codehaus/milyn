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

import org.milyn.edisax.util.EDIUtils;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Description;
import org.milyn.resource.URIResourceLocator;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@link EJC} Executor.
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class EJCExecutor {

    private String ediMappingModel;
    private File destDir;
    private String packageName;

    public void execute() throws EJCException, IOException, SAXException, IllegalNameException, ClassNotFoundException {
        assertMandatoryProperty(ediMappingModel, "ediMappingModel");
        assertMandatoryProperty(destDir, "destDir");
        assertMandatoryProperty(packageName, "packageName");

        if(destDir.exists() && !destDir.isDirectory()) {
            throw new EJCException("Specified EJC destination directory '" + destDir.getAbsoluteFile() + "' exists, but is not a directory.");
        }

        Map<Description, EdifactModel> mappingModels = new LinkedHashMap<Description, EdifactModel>();
        EDIUtils.loadMappingModels(ediMappingModel, mappingModels, URIResourceLocator.DEFAULT_BASE_URI);

        EdifactModel definitionsModel = mappingModels.get(EDIUtils.MODEL_SET_DEFINITIONS_DESCRIPTION);
        String commonsPackageName = packageName + ".common";
        ClassModel definitionsClassModel = null;

        if(definitionsModel != null) {
            EJC ejc = new EJC();
            definitionsClassModel = ejc.compile(definitionsModel.getEdimap(), commonsPackageName, destDir.getAbsolutePath());
        }

        Set<Map.Entry<Description, EdifactModel>> modelSet = mappingModels.entrySet();
        for(Map.Entry<Description, EdifactModel> model : modelSet) {
            Description description = model.getKey();

            if(description.equals(EDIUtils.MODEL_SET_DEFINITIONS_DESCRIPTION)) {
                // Already done (above).  Skip it...
                continue;
            }

            EJC ejc = new EJC();
            
            ejc.include(commonsPackageName);
            ejc.addEDIMessageAnnotation(true);
            if(definitionsClassModel != null) {
                String messagePackageName = packageName + "." + description.getName();
                ejc.compile(model.getValue().getEdimap(), messagePackageName, destDir.getAbsolutePath(), definitionsClassModel.getClassesByNode());
            } else {
                ejc.compile(model.getValue().getEdimap(), packageName, destDir.getAbsolutePath());
            }
        }
    }

    public void setEdiMappingModel(String ediMappingModel) {
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