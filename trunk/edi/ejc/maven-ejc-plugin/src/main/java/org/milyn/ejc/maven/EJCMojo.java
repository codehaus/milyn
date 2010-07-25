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
import org.milyn.ejc.IllegalNameException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

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
    private String ediMappingFile;

    @MojoParameter(required = false)
    private String messages;

    @MojoParameter(required = false)
    private String packageName;

    public void execute() throws MojoExecutionException {
        EJCExecutor ejc = new EJCExecutor();

        try {
            if(ediMappingFile.startsWith("urn:")) {
                if(packageName != null) {
                    throw new MojoExecutionException("Invalid EJC configuration.  'packageName' must not be configured for 'urn' mapping model configurations.");
                }

                String urn = ediMappingFile.substring(4).trim();
                String[] urnTokens;

                urn = urn.replace("-", "_");
                urnTokens = urn.split(":");

                if(urnTokens.length != 3) {
                    throw new MojoExecutionException("'ediMappingFile' urn value must have a minimum of 3 colon separated tokens (4 tokens if including the leading 'urn' token).");
                }

                String directoryMapping = urnTokens[1];
                if(directoryMapping.endsWith("_mapping")) {
                    directoryMapping = directoryMapping.substring(0, directoryMapping.length() - "_mapping".length());
                }

                packageName = urnTokens[0] + "." + directoryMapping;
            } else if(packageName == null) {
                throw new MojoExecutionException("Invalid EJC configuration.  'packageName' must be configured for non 'urn' mapping model configurations.");
            } else {
                File mappingFileObj = new File(project.getBasedir(), ediMappingFile);
                if(mappingFileObj.exists()) {
                    ediMappingFile = mappingFileObj.toURI().toString();
                }
            }

            ejc.setMessages(messages);
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
        } catch (SAXException e) {
            throw new MojoExecutionException("Error Executing EJC Maven Plugin.  See chained cause.", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Error Executing EJC Maven Plugin.  See chained cause.", e);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Error Executing EJC Maven Plugin.  See chained cause.", e);
        } catch (IllegalNameException e) {
            throw new MojoExecutionException("Error Executing EJC Maven Plugin.  See chained cause.", e);
        }
    }
}