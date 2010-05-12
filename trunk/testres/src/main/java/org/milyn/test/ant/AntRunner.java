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

package org.milyn.test.ant;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import java.io.*;

/**
 * AntRunner test utility.
 * 
 * <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class AntRunner {

    private Project project = new Project();

    public AntRunner(InputStream antScript, String... properties) throws IOException {
        if(antScript == null) {
            throw new IllegalArgumentException("null 'antScript' argument.");
        }

        try {
            project.init();

            DefaultLogger antLogger = new DefaultLogger();
            antLogger.setErrorPrintStream(System.err);
            antLogger.setOutputPrintStream(System.out);
            antLogger.setMessageOutputLevel(Project.MSG_INFO);

            project.addBuildListener(antLogger);

            File executeScript = new File("./target/ant-exec.xml");
            FileOutputStream fileOs = new FileOutputStream(executeScript);

            try {
                byte[] readBuf = new byte[254];
                int readCount = 0;

                while((readCount = antScript.read(readBuf)) != -1) {
                    fileOs.write(readBuf, 0, readCount);
                }
            } finally {
                fileOs.flush();
                fileOs.close();
            }

            ProjectHelper.configureProject(project, executeScript);

            if(properties != null) {
                for(String property : properties) {
                    int eqIndex = property.indexOf('=');

                    if(eqIndex == -1 || eqIndex + 1 == property.length()) {
                        throw new RuntimeException("Invalid AntExecute property '" + property + "'.  No value.");
                    }

                    String key = property.substring(0, eqIndex);
                    String value = property.substring(eqIndex + 1, property.length());

                    project.setProperty(key, value);
                }
            }
        } finally {
            antScript.close();
        }
    }

    public AntRunner run(String target) {
        if(target == null) {
            throw new IllegalArgumentException("null 'target' argument.");
        }
        project.executeTarget(target);
        return this;
    }
}
