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
package example;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.io.StreamUtils;
import org.milyn.container.ExecutionContext;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.ejc.EJC;
import org.milyn.ejc.IllegalNameException;
import org.milyn.payload.JavaResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URLDecoder;

/**
 * Simple example of how to use EJC.
 *
 * @author bardl
 */
public class Main {

    private String folder;
    private String packageName;


    public Main() {
        try {
            folder = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("").toString().replace("file:/", ""), "utf-8");
        } catch (UnsupportedEncodingException e) {
            folder = null;
        }
        packageName = "test.packageName";
    }

    public String getFolder() {
        return folder;
    }

    public String getPackageName() {
        return packageName;
    }

    /**
     * Tests running the EJC compiler on edi-configuration: edi-to-xml-order-mapping.xml. After
     * compilation the generated classes and binding-file is tested with the original
     * edi-configuration and a valid edi-file.
     * @throws IOException when unable to read input files.
     * @throws SAXException when unable to read configuration-files.
     * @throws org.milyn.edisax.EDIConfigurationException when edi-configuration is wrong.
     * @throws InterruptedException when error occurs during compilation of generated classes.
     * @throws org.milyn.ejc.IllegalNameException when error occurs during compilation in EJC.
     * @throws org.milyn.SmooksException when error occurs during filtering.
     * @param args no arguments are need.
     */
    public static void main(String[] args) throws IOException, SAXException, SmooksException, EDIConfigurationException, IllegalNameException, InterruptedException {
        Main main = new Main();

        writeEdiConfiguration(main.getFolder());

        pause("Press any key to compile the generated java classes...");

        writeEDIInputFile(main.getFolder());

        pause("Press any key to continue...");

        main.performSmooksFiltering();

        pause("And that's it!  Press 'enter' to finish...");
    }

    private static void writeEdiConfiguration(String folder) throws IOException {
        System.out.println("\n\n*************************************************************************************\n");
        System.out.println("* The original edi-configuration.\n");
        System.out.println("*************************************************************************************\n\n");
        System.out.println(new String(getEDIConfiguration(folder)));
        System.out.println("*************************************************************************************\n\n");
    }

    private static void writeEDIInputFile(Object folder) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(folder + "../../" + "input-message.edi");
            byte[] ediFile = new byte[inputStream.available()];
            inputStream.read(ediFile);
            System.out.println("\n\n*************************************************************************************\n");
            System.out.println("* The edi input file.\n");
            System.out.println("*************************************************************************************\n\n");
            System.out.println(new String(ediFile));
            System.out.println("*************************************************************************************\n\n");
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static void pause(String message) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("> " + message);
            in.readLine();
        } catch (IOException e) {
            //Do nothing
        }
        System.out.println("\n");
    }

    private void performSmooksFiltering() throws IOException, SAXException, InterruptedException, EDIConfigurationException, IllegalNameException {

        System.out.println("\n\n*************************************************************************************\n");
        System.out.println("* This step consists of two parts:\n");
        System.out.println("* \n");
        System.out.println("* EJC - compile edi-configuration.\n");
        System.out.println("* In this step the EJC compile method is invoked with the edi-to-xml-order-mapping.xml\n");
        System.out.println("* as the edi-configuration. The pakage name of the generated javaclasses is set to be \n");
        System.out.println("* packageName and the outputfolder is [" + folder + "].\n");
        System.out.println("* \n");
        System.out.println("* Smooks - read EDI input file and bind to java classes.\n");
        System.out.println("* In this step the generated java classes is compiled and populated with the\n");
        System.out.println("* generated binding-configuration.\n");
        System.out.println("* The resulting Order class is presented below.\n");
        System.out.println("*************************************************************************************\n\n");
        System.out.println(runEJCTest());
        System.out.println("\n*************************************************************************************\n\n");

    }

    protected String runEJCTest() throws EDIConfigurationException, IllegalNameException, IOException, SAXException, InterruptedException {
        InputStream inputStream = null;
        try {
            EJC ejc = new EJC();

            InputStream configFile = new ByteArrayInputStream(getResource("edi-to-xml-order-mapping.xml"));
            ejc.compile(configFile, packageName, folder, folder + "binding-config.xml");

            compileSourceFile(folder,  packageName, "Order");

            Smooks smooks = new Smooks();
            smooks.addConfigurations(new ByteArrayInputStream(getResource("edi-config.xml")));
            smooks.addConfigurations(new FileInputStream(folder + "binding-config.xml"));
            ExecutionContext context = smooks.createExecutionContext();

            JavaResult result = new JavaResult();
            inputStream = new FileInputStream(folder + "../../" + "input-message.edi");
            StreamSource source = new StreamSource(inputStream);
            smooks.filter(source, result, context);

            com.thoughtworks.xstream.XStream xstream = new com.thoughtworks.xstream.XStream();

            return xstream.toXML(result.getBean("order"));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    private static byte[] getEDIConfiguration(String folder) throws IOException {
        FileInputStream input = null;
        try {
            input = new FileInputStream(folder + "../../" + "edi-to-xml-order-mapping.xml");
            byte[] ediconfig = new byte[input.available()];
            input.read(ediconfig);
            return ediconfig;            
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    private static byte[] getResource(String resourceName) throws IOException {
        return StreamUtils.readStream(new FileInputStream(resourceName));
    }

    private static void compileSourceFile(String path, String packageName, String className) throws InterruptedException, IOException {
        try {
            Runtime _runtime = Runtime.getRuntime();
            String[] _cmd = getCommand( path, packageName, className );
            final Process _proc = _runtime.exec(_cmd);
            int _exitVal = _proc.waitFor();
            if (_exitVal != 0) {
                StringBuilder msg = new StringBuilder();
                msg.append("Failed to compile java sourcefiles. Command [");
                for (String s : _cmd) {
                    msg.append("_cmd[i] = ").append(s);
                }
                msg.append("].");
            }

        } catch (InterruptedException e) {
            assert false : e;
        } catch (IOException e) {
            assert false : e;
        }
    }
    private static String[] getCommand(String path, String packageName, String className) {
        String classFile = path + packageName.replace('.', '/') + "/" + className + ".java";
        String _osName = System.getProperty("os.name");
        if (_osName.equals("Linux")) {
            return new String[] { "javac", "-classpath " + "\"" + path + "\" " + "\"" + classFile + "\""};
        } else if (_osName.equals("Windows 95") || _osName.equals("Windows 98")) {
            return new String[] {"command.com", "/C", "start /wait /min javac -classpath " + "\"" + path + "\" " + "\"" + classFile + "\""};
        } else {
            return new String[] {"cmd.exe", "/C", "start /wait /min javac -classpath " + "\"" + path + "\" " + "\"" + classFile + "\""};
        }
    }
}
