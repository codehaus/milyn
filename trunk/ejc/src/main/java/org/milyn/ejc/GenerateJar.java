package org.milyn.ejc;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: bardl
 * Date: 2009-maj-27
 * Time: 07:40:39
 * To change this template use File | Settings | File Templates.
 */
public class GenerateJar {

    public static void generateJar(String path, String packageName, String className, String bindingFile, String jarFile) throws IOException, InterruptedException {
        compileSourceFile(path, packageName, className);
        moveBindingFile(path+ convertToFolderName(packageName) + new File(bindingFile).getName(), bindingFile);
        invokeJarCommand(path, packageName, className);
    }

    private static  void moveBindingFile(String destinationPath, String bindingFile) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(bindingFile);
            out = new FileOutputStream(destinationPath);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private static void compileSourceFile(String path, String packageName, String className) throws InterruptedException, IOException {
        String classFile = path + convertToFolderName(packageName) + className + ".java";
        String[] parameters = {"-classpath", getClassPath(path), classFile};

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            com.sun.tools.javac.Main.compile(parameters, pw);
        } catch (Exception e) {
            assert false : sw.toString();
        } finally {
            pw.close();
            sw.close();
        }
    }

    private static String convertToFolderName(String packageName) {
        return packageName.replace('.', '/') + "/";
    }

    private static String getClassPath(String path) throws IOException {
        StringBuilder result  = new StringBuilder();
        result.append(path);

        String currentDir = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        File dependencyDir = new File(currentDir + "../dependencies");
        for (File file : dependencyDir.listFiles()) {
            if (file.getName().startsWith("milyn-smooks-core-") || file.getName().startsWith("milyn-commons-")) {
                result.append(";");
                result.append(file.getCanonicalPath());
            }
        }
        return result.toString();
    }

    private static void invokeJarCommand(String path, String packageName, String className) throws InterruptedException, IOException {
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
        //String packagePath
        String _osName = System.getProperty("os.name");
        if (_osName.equals("Windows 95") || _osName.equals("Windows 98")) {
            return new String[] {"command.com", "/C", "start /wait /min jar cvf test.jar " + path + packageName.split("\\.")[0] };
        } else if (_osName.startsWith("Windows")) {
            return new String[] {"cmd.exe", "/C", "start /wait /min jar cvf test.jar " + packageName.split("\\.")[0]};
        } else {
            return new String[] { "javac", "-classpath " + "\"" + path + "\" " + "\"" + classFile + "\""};
        }
    }
}
