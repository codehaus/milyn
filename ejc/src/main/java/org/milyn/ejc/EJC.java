/*
	Milyn - Copyright (C) 2006

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

import org.milyn.edisax.model.internal.Edimap;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.EDIConfigurationException;
import org.xml.sax.SAXException;

import java.io.*;

/**
 * EJC is the main class parsing parameters and starting the compilation of the edi-mapping-config.
 * The compilation consists of the following steps:
 * 1. {@link org.milyn.ejc.EdiConfigReader} - parse a edi-mapping-file a creates a {@link org.milyn.ejc.ClassModel}.
 * 2. {@link org.milyn.ejc.BeanWriter} - generates javaimplementation from {@link org.milyn.ejc.ClassModel}.
 * 3. {@link org.milyn.ejc.BindingWriter} - generates a bindingfile from {@link org.milyn.ejc.ClassModel}.
 *
 * Example of how to use the EJC:
 * EJC -b "pacth/to/binding-config" -p package.name -d "place/classes/in/directory/"  "path/to/edi-mapping-config"
 *
 * @author bardl  
 */
public class EJC {

    private static final String VERISON = "0.1";

    private static final String PARAMETER_BEAN_FOLDER = "-d";
    private static final String PARAMETER_BEAN_PACKAGE = "-p";
    private static final String PARAMETER_BINDING_FILE = "-b";

    private static final String PARAMETER_VERBOSE = "-version";
    private static final String PARAMETER_QUIET = "-quiet";
    private static final String PARAMETER_HELP = "-help";
    private static final String PARAMETER_VERSION = "-version";

    /**
     * Compiles a edi-mapping-configuration and generates java implementation and
     * bindingfile.
     *
     * The compilation is performed in the following order:
     * 1. {@link org.milyn.ejc.EdiConfigReader} - parse a edi-mapping-file a creates a {@link org.milyn.ejc.ClassModel}.
     * 2. {@link org.milyn.ejc.BeanWriter} - generates javaimplementation from {@link org.milyn.ejc.ClassModel}.
     * 3. {@link org.milyn.ejc.BindingWriter} - generates a bindingfile from {@link org.milyn.ejc.ClassModel}.
     * @param inputfile the edi-mapping-configuration.
     * @param beanPackage the package name of generated java classes.
     * @param beanFolder the folder to place the generated java classes.
     * @param bindingFile the path and name to place the generated binding-file.
     * @throws EDIConfigurationException When edi-mapping-configuration is badly formatted.
     * @throws IOException When unable to read edi-mapping-configuration.
     * @throws SAXException When edi-mapping-configuration is badly formatted.
     */
    public void compile(String inputfile, String beanPackage, String beanFolder, String bindingFile) throws EDIConfigurationException, IOException, SAXException, IllegalNameException {

        InputStream inputStream = null;
        try {
            
            //Read edifact configuration
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(inputfile);
            Edimap edimap = readEDIConfig(inputStream);

            EdiConfigReader _ediConfigReader = new EdiConfigReader();
            ClassModel model = _ediConfigReader.parse(edimap, beanPackage);

            BeanWriter.writeBeans(model, beanFolder);

            BindingWriter.parse(model, bindingFile );
            
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

    }

    /**
     * Returns the Edimap for a given edi-mapping inputstream.
     * @param inputStream the edi-mapping.
     * @return the Edimap.
     * @throws EDIConfigurationException When edi-mapping-configuration is badly formatted.
     * @throws IOException When unable to read edi-mapping-configuration.
     * @throws SAXException When edi-mapping-configuration is badly formatted.
     */
    private Edimap readEDIConfig(InputStream inputStream) throws EDIConfigurationException, IOException, SAXException {
        EdifactModel edifactModel = new EdifactModel();
        edifactModel.parseSequence(inputStream);
        return edifactModel.getEdimap();
    }

    /**
     * The main method parsing in-parameters and invoking the compile method.
     * @param args the arguments
     */
    public static void main(String[] args) {
        EJC ejc = new EJC();

        String configFile = args[args.length-1];
        String beanPackage = getParameter(PARAMETER_BEAN_PACKAGE, args);
        String beanFolder = getParameter(PARAMETER_BEAN_FOLDER, args);
        String bindingFile = getParameter(PARAMETER_BINDING_FILE, args);

        try {
            ejc.compile(configFile, beanPackage, beanFolder, bindingFile);

        //TODO :: Take care of exceptions    
        } catch (EDIConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IllegalNameException e) {
            e.printStackTrace();
        }

        if (containsParameter(PARAMETER_HELP, args)) {
            System.out.println(writeAboutText());
            System.out.println(writeUsageText());
        }

        if (containsParameter(PARAMETER_VERSION, args)) {
            System.out.println(writeVersionText());
        }
    }

    /**
     * Returns the about-text for EJC.
     * @return the about text.
     */
    private static String writeAboutText() {
        return "\n\n" +
                "***********************************************************************\n" +
                "* A toolkit for compiling an edi-mapping-file into a class-structure  *\n" +
                "* with a corresponding binding-config-file.                           *\n" +
                "***********************************************************************\n";
    }

    /**
     * Returns the usage info for EJC.
     * @return the usage info.
     */
    private static String writeUsageText() {
        return "Usage: ejc [-options ...] <edi file/URL/dir>\n" +
                "Options:\n" +
                "\n" +
                "  -d <dir>           :  generated files will go into this directory\n" +
                "  -p <pkg>           :  specifies the target package\n" +
                "  -b <bindingfile>   :  generated bindingfile will go inte this directory\n" +
                "\n" +
                "  -verbose           :  be extra verbose\n" +
                "  -quiet             :  suppress compiler output\n" +
                "  -help              :  display this help message\n" +
                "  -version           :  display version information\n\n\n";
    }

    /**
     * Returns the version-text for EJC.
     * @return the version text.
     */
    private static String writeVersionText() {
        return "ejc version " + VERISON + "\nMilyn Smooks toolkit for binding edi.";
    }

    /**
     * Returns the parameter value following a given flag.
     * @param flag the flag to search for.
     * @param args the arguments.
     * @return the value following a flag if flag exists in arguments, otherwise it returns null.
     */
    private static String getParameter(String flag, String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase(flag) && i+1 < args.length) {
                return args[i+1];
            }
        }
        return null;
    }

    /**
     * Checks if parameter exists in arguments
     * @param flag the flag to search for.
     * @param args the argumenst to look in.
     * @return true if flag exists in argumenst, otherwise return false. 
     */
    private static boolean containsParameter(String flag, String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase(flag)) {
                return true;
            }
        }
        return false;
    }
}
