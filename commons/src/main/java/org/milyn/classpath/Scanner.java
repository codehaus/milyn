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
package org.milyn.classpath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.assertion.AssertArgument;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.File;

/**
 * Classpath scanner.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Scanner {

    private static Log logger = LogFactory.getLog(Scanner.class);
    private Filter filter;

    public Scanner(Filter filter) {
        AssertArgument.isNotNull(filter, "filter");
        this.filter = filter;
    }

    public void scanClasspath(ClassLoader classLoader) throws IOException {

        if (!(classLoader instanceof URLClassLoader)) {
            logger.warn("Not scanning classpath for ClassLoader '" + classLoader.getClass().getName() + "'.  ClassLoader must implement '" + URLClassLoader.class.getName() + "'.");
            return;
        }

        URL[] urls = ((URLClassLoader) classLoader).getURLs();

        for (URL url : urls) {
            String urlPath = url.getFile();

            urlPath = URLDecoder.decode(urlPath, "UTF-8");
            if (urlPath.startsWith("file:")) {
                urlPath = urlPath.substring(5);
            }

            if (urlPath.indexOf('!') > 0) {
                urlPath = urlPath.substring(0, urlPath.indexOf('!'));
            }

            File file = new File(urlPath);
            if (file.isDirectory()) {
                if(logger.isDebugEnabled()) {
                    logger.debug("Scanning directory: " + file.getAbsolutePath());
                }
                handleDirectory(file, null);
            } else {
                if(logger.isDebugEnabled()) {
                    logger.debug("Scanning archive: " + file.getAbsolutePath());
                }
                handleArchive(file);
            }
        }
    }

    private void handleArchive(File file) throws IOException {
        if(filter.isIgnorable(file.getName())) {
            if(logger.isDebugEnabled()) {
                logger.debug("Ignoring classpath archive: " + file);
            }
            return;
        }

        ZipFile zip = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zip.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            filter.filter(name);
        }
    }

    private void handleDirectory(File file, String path) {
        if(path != null && filter.isIgnorable(path)) {
            if(logger.isDebugEnabled()) {
                logger.debug("Ignoring classpath directory (and subdirectories): " + path);
            }
            return;
        }

        for (File child : file.listFiles()) {
            String newPath = path == null?child.getName() : path + '/' + child.getName();

            if (child.isDirectory()) {
                handleDirectory(child, newPath);
            } else {
                filter.filter(newPath);
            }
        }
    }

}