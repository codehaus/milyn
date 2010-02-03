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
import org.apache.maven.project.MavenProject;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jfrog.maven.annomojo.annotations.MojoPhase;
import org.jfrog.maven.annomojo.annotations.MojoRequiresDependencyResolution;
import org.milyn.ect.ECTUnEdifactExecutor;
import org.milyn.ect.EdiParseException;

import java.io.File;

/**
 * ECT Un/Edifact Mojo.
 * @author bardl
 */
@MojoGoal("generate")
@MojoPhase("generate-sources")
@MojoRequiresDependencyResolution
public class ECTUnEdifactMojo extends AbstractMojo {

    @MojoParameter(expression = "${project}", required = true, readonly = true)
    private MavenProject project;

    @MojoParameter(required = true)
    private File unEdifactZip;

    @MojoParameter(required = true)
    private File outFile;

    @MojoParameter(required = true)
    private String messageName;

    public void execute() throws MojoExecutionException {
        ECTUnEdifactExecutor ect = new ECTUnEdifactExecutor();

        if(unEdifactZip.exists()) {
            try {
                ect.setUnEdifactZip(unEdifactZip);
                ect.setOutFile(outFile);
                ect.setMessageName(messageName);
                ect.execute();
            } catch (EdiParseException e) {
                throw new MojoExecutionException("Error Executing EJC Maven Plugin.  See chained cause.", e);
            } 
        } else {
            throw new MojoExecutionException("EDI mapping model '" + unEdifactZip.getAbsolutePath() + "' not found.");
        }
    }
}
