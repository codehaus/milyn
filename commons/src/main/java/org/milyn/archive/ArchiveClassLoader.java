/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 * 	This library is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU Lesser General Public
 * 	License (version 2.1) as published by the Free Software
 * 	Foundation.
 *
 * 	This library is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * 	See the GNU Lesser General Public License for more details:
 * 	http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.archive;

import org.milyn.assertion.AssertArgument;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * {@link Archive} based {@link ClassLoader}.
 * 
 * <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ArchiveClassLoader extends ClassLoader {

    private Archive archive;

    public ArchiveClassLoader(Archive archive) {
        this(Thread.currentThread().getContextClassLoader(), archive);
    }

    public ArchiveClassLoader(ClassLoader parent, Archive archive) {
        super(parent);
        AssertArgument.isNotNull(archive, "archive");
        this.archive = archive;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String resName = name.replace('.', '/') + ".class";
        byte[] classBytes = archive.getEntries().get(resName);

        if(classBytes != null) {
            return defineClass(name, classBytes, 0, classBytes.length);
        } else {
            return super.findClass(name);
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        byte[] bytes = archive.getEntries().get(name);
        if (bytes != null) {
            return new ByteArrayInputStream(bytes);
        } else {
            return super.getResourceAsStream(name);
        }
    }
}
