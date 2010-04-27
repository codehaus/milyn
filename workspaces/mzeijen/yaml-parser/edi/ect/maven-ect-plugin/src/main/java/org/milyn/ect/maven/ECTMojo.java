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
package org.milyn.ect.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jfrog.maven.annomojo.annotations.MojoPhase;
import org.jfrog.maven.annomojo.annotations.MojoRequiresDependencyResolution;
import org.milyn.ect.ECTUnEdifactExecutor;
import org.milyn.ect.EdiParseException;

import java.io.File;

/**
 * ECT Mojo.
 * 
 * @author bardl
 */
@MojoGoal("generate")
@MojoPhase("generate-sources")
@MojoRequiresDependencyResolution
public class ECTMojo extends AbstractMojo {
	
	@MojoParameter(required = true, description = "The name of the message as defined in the message definition file ('src') e.g. 'INVOIC' for UN/EDIFACT.")
	private String messageName;

    @MojoParameter(required = true, description = "The EDI message definition type.  Currently Supports 'UNEDIFACT' only.")
    private String srcType ;

    @MojoParameter(required = true, description = "The message definition file.  Depends on the message definition type ('srcType') e.g. for UN/EDIFACT, this is a ZIP file that can be downloaded from the web.")
    private File src;

    @MojoParameter(required = false)
    private File outFile;
	
    public void execute() throws MojoExecutionException {
    	
        if(!src.exists()) {
        	throw new MojoExecutionException("EDI mapping model '" + src.getAbsolutePath() + "' not found.");
        }
        
        if(srcType.equals("UNEDIFACT")) {
        	// Currently supports UN/EDIFACT only...
        	ECTUnEdifactExecutor ect = new ECTUnEdifactExecutor();
        	
            try {
                ect.setUnEdifactZip(src);
                
                if(outFile == null) {
                	outFile = new File("target/" + srcType + "-" + messageName + "-model.xml");
                }
                
                File outDir = outFile.getParentFile();
                if(outDir != null && !outDir.exists()) {
                	outDir.mkdirs();
                }
                
                ect.setOutFile(outFile);
                ect.setMessageName(messageName);
                ect.execute();
                
                getLog().info("UN/EDIFACT message model for message '" + messageName + "' generated in '" + outFile.getAbsolutePath() + "'.");
            } catch (EdiParseException e) {
                throw new MojoExecutionException("Error Executing EJC Maven Plugin.  See chained cause.", e);
            } 
        } else {
            throw new MojoExecutionException("Unsupported ECT 'srcType' configuration value '" + srcType + "'.  Currently support 'UNEDIFACT' only.");
        }
    }
}
