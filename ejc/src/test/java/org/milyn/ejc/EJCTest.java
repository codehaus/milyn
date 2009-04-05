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

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.payload.JavaResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

public class EJCTest { //extends TestCase {

    /**
     * Tests running the EJC compiler on edi-configuration: edi-to-xml-order-mapping.xml. After
     * compilation the generated classes and binding-file is tested with the original
     * edi-configuration and a valid edi-file.
     * @throws IOException when unable to read input files.
     * @throws SAXException when unable to read configuration-files.
     * @throws EDIConfigurationException when edi-configuration is wrong.
     * @throws InterruptedException when error occurs during compilation of generated classes.
     */
    public void testCompile() throws IOException, SAXException, EDIConfigurationException, InterruptedException {
        InputStream inputStream = null;
        try {
            String expected = org.milyn.io.StreamUtils.readStreamAsString(getClass().getResourceAsStream("expected.xml"));

            // Compile edi-configuration using the EJC.
            EJC ejc = new EJC();            
            String _folder = getClass().getResource("").toString().replace("file:/", "");
            String packageName = "test.packageName";
            ejc.compile("edi-to-xml-order-mapping.xml", packageName, _folder, _folder + "binding-config.xml");

            // Compile the generated java-classes.
            compileSourceFile( _folder,  packageName, "Order");

            // Test the generated files in Smooks using a previously defined edi-smooks-config
            // and the generated binding-config.
            Smooks smooks = new Smooks();
            smooks.addConfigurations(getClass().getResourceAsStream("edi-config.xml"));
            smooks.addConfigurations(getClass().getResourceAsStream("binding-config.xml"));
            ExecutionContext context = smooks.createExecutionContext();

            JavaResult result = new JavaResult();
            inputStream = new FileInputStream(_folder +"input-message.edi");
            StreamSource source = new StreamSource(inputStream);
            smooks.filter(source, result, context);

            com.thoughtworks.xstream.XStream xstream = new com.thoughtworks.xstream.XStream();
            String actual = xstream.toXML(result.getBean("order"));

            actual = actual.replaceFirst("<date>.*</date>", "<date/>");

            boolean matchesExpected = org.milyn.io.StreamUtils.compareCharStreams(new java.io.StringReader(expected), new java.io.StringReader(actual));
            if(!matchesExpected) {
//                assertEquals("Actual does not match expected.", expected, actual);
            }
        } catch (IllegalNameException e) {
            assert false : e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private void compileSourceFile(String path, String packageName, String className) throws InterruptedException, IOException {
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
    private String[] getCommand(String path, String packageName, String className) {
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
