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
import org.milyn.io.StreamUtils;
import org.milyn.io.FileUtils;
import org.xml.sax.SAXException;
import org.apache.commons.logging.Log;
import static org.milyn.ejc.EJCLogFactory.Level;
import org.milyn.javabean.gen.ConfigGenerator;

import java.io.*;

/**
 * EJC is the main class parsing parameters and starting the compilation of the edi-mapping-config.
 * The compilation consists of the following steps:
 * <ol>
 * <li>{@link org.milyn.ejc.EdiConfigReader} - parse a edi-mapping-file a creates a {@link org.milyn.ejc.ClassModel}.</li>
 * <li>{@link org.milyn.ejc.BeanWriter} - generates javaimplementation from {@link org.milyn.ejc.ClassModel}.</li>
 * <li>{@link org.milyn.ejc.BindingWriter} - generates a bindingfile from {@link org.milyn.ejc.ClassModel}.</li>
 * </ol>
 *
 * <b>Example of how to execute the EJC:</b><br/>
 * EJC -p "package.name" -d "place/classes/in/directory/"  "path/to/edi-mapping-config"
 *
 * @author bardl  
 */
public class EJC {

    private static Log LOG = EJCLogFactory.getLog(EJC.class);

    private static final String VERISON = "0.1";

    private static final String PARAMETER_BEAN_FOLDER = "-d";
    private static final String PARAMETER_BEAN_PACKAGE = "-p";
    //private static final String PARAMETER_JAR_PATH = "-jar";

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
     * @param configFile the edi-mapping-configuration.
     * @param configName the name of the edi-mapping-config.
     * @param beanPackage the package name of generated java classes.
     * @param beanFolder the folder to place the generated java classes.
     * @throws EDIConfigurationException When edi-mapping-configuration is badly formatted.
     * @throws IOException When unable to read edi-mapping-configuration.
     * @throws SAXException When edi-mapping-configuration is badly formatted.
     * @throws IllegalNameException when name of java-classes is illegal.
     * @throws ClassNotFoundException when error occurs while creating bindingfile.
     */
    public void compile(InputStream configFile, String configName, String beanPackage, String beanFolder) throws EDIConfigurationException, IOException, SAXException, IllegalNameException, ClassNotFoundException {
        String bindingFile = beanFolder + "/" + beanPackage.replace('.', '/') + "/bindingconfig.xml";

        //Read edifact configuration
        Edimap edimap = readEDIConfig(configFile);

        LOG.info("Reading the edi-configuration...");
        EdiConfigReader ediConfigReader = new EdiConfigReader();
        ClassModel model = ediConfigReader.parse(edimap, beanPackage);

        LOG.info("Writing java beans to " + beanFolder + "...");
        BeanWriter.writeBeans(model, beanFolder, bindingFile);

        LOG.info("Creating bindingfile...");
        String bundleConfigPath = "/" + beanPackage.replace('.', '/') + "/edimappingconfig.xml";
        FileUtils.copyFile(configName, beanFolder + bundleConfigPath);

        BindingWriter bindingWriter = new BindingWriter(model);
        bindingWriter.generate(bindingFile);

        LOG.info("-----------------------------------------------------------------------");
        LOG.info(" Compiltation complete.");
        LOG.info("-----------------------------------------------------------------------");
        LOG.info(" Files are located in folder ");
        LOG.info(" " + beanFolder);
        LOG.info("-----------------------------------------------------------------------");

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
     * @throws org.milyn.edisax.EDIConfigurationException when error occurs while reading ediConfiguration.
     * @throws IllegalNameException when xmltag in edi-configuration has a conconflict with reserved java keywords.
     * @throws java.io.IOException when error ocurcurs when reading or writing files.
     * @throws org.xml.sax.SAXException when error occurs while reading ediConfiguration. 
     */
    public static void main(String[] args) throws IOException, EDIConfigurationException, IllegalNameException, SAXException, ClassNotFoundException {
        EJC ejc = new EJC();

        // Should be an odd number of commandline args...
        if(args.length % 2 == 0) {
            System.out.println(writeUsageText());
            return;
        }

        String configFile = args[args.length-1];
        String beanPackage = getParameter(PARAMETER_BEAN_PACKAGE, args);
        String beanFolder = getParameter(PARAMETER_BEAN_FOLDER, args);

        //String jarPath = getParameter(PARAMETER_JAR_PATH, args);
        boolean isVerbose = containsParameter(PARAMETER_VERBOSE, args);
        boolean isQuiet = containsParameter(PARAMETER_QUIET, args);

        if (containsParameter(PARAMETER_HELP, args)) {
            System.out.println(writeAboutText());
            System.out.println(writeUsageText());

            if (args.length == 1) {
                return;
            }
        }

        if (containsParameter(PARAMETER_VERSION, args)) {
            System.out.println(writeVersionText());

            if (args.length == 1) {
                return;
            }
        }

        if (isVerbose && isQuiet) {
            LOG.error("Both 'quiet' and 'verbose' is activated. Only one of these can be active at once.");
            return;
        }

        // Set log-level depending on argument VERBOSE or QUIET.        
        ((EJCLog)LOG).setLevel(isVerbose ? Level.DEBUG : (isQuiet ? Level.ERROR : Level.INFO));

        InputStream configInputStream = null;
        try {
            configInputStream = new ByteArrayInputStream(StreamUtils.readStream(new FileInputStream(configFile)));
            ejc.compile(configInputStream, configFile, beanPackage, beanFolder);

        } finally {
            if (configInputStream != null) {
                configInputStream.close();
            }
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
        return "Usage: " + EJC.class.getName() + " [-options ...] <edi file/URL/dir>\n" +
                "Options:\n" +
                "\n" +
                "  -d <dir>           :  generated files will go into this directory\n" +
                "  -p <pkg>           :  specifies the target package\n" +
                //"  -jar               :  path to generated jar holding both generated classes and bindingfile\n" +
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

        throw new RuntimeException("Mandatory command line parameter '' not specified.\n\n" + writeUsageText());
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
