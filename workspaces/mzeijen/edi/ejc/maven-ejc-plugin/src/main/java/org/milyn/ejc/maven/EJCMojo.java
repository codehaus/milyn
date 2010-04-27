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
package org.milyn.ejc.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.model.Resource;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoPhase;
import org.jfrog.maven.annomojo.annotations.MojoRequiresDependencyResolution;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.milyn.ejc.EJC;
import org.milyn.ejc.EJCExecutor;
import org.milyn.ejc.EJCException;

import java.io.File;

/**
 * EJC Mojo.
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
@MojoGoal("generate")
@MojoPhase("generate-sources")
@MojoRequiresDependencyResolution
public class EJCMojo extends AbstractMojo {

    @MojoParameter(expression = "${project}", required = true, readonly = true)
    private MavenProject project;

    @MojoParameter(expression = "target/ejc", required = false)
    private File destDir;

    @MojoParameter(expression = "src/main/resources/edi-model.xml", required = false)
    private File ediMappingFile;

    @MojoParameter(required = true)
    private String packageName;

    public void execute() throws MojoExecutionException {
        EJCExecutor ejc = new EJCExecutor();

        if(ediMappingFile.exists()) {
            try {
                ejc.setDestDir(destDir);
                ejc.setEdiMappingModel(ediMappingFile);
                ejc.setPackageName(packageName);

                if(destDir.exists()) {
                    destDir.delete();
                }

                ejc.execute();
                project.addCompileSourceRoot(destDir.getPath());

                Resource resource = new Resource();
                resource.setDirectory(destDir.getPath());
                resource.addInclude("**/*.xml");
                project.addResource(resource);
            } catch (EJCException e) {
                throw new MojoExecutionException("Error Executing EJC Maven Plugin.  See chained cause.", e);
            }
        } else {
            throw new MojoExecutionException("EDI mapping model '" + ediMappingFile.getAbsolutePath() + "' not found.");
        }
    }
}
