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

package org.milyn.ect;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UnEdifact_ModelConfigSetWriterTest extends TestCase {

    public void test_D08A() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("D08A.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        File modelSetFile = new File("./target/D08A-mapping-model.zip");

        modelSetFile.delete();

        ModelConfigSetWriter.fromUnEdifactSpec(zipInputStream, new ZipOutputStream(new FileOutputStream(modelSetFile)), "org/smooks/edi/unedifact/d08a");
    }
}
