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

package org.smooks.model.csv.v13;

import junit.framework.TestCase;
import org.milyn.javabean.dynamic.Model;
import org.milyn.javabean.dynamic.ModelBuilder;
import org.smooks.model.core.SmooksModel;
import org.smooks.model.csv.CSVReader;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CSVReaderTest extends TestCase {

    public void test_01_read_write() throws IOException, SAXException {
        ModelBuilder modelBuilder = new ModelBuilder(SmooksModel.MODEL_DESCRIPTOR, false);
        Model<SmooksModel> model = modelBuilder.readModel(getClass().getResourceAsStream("csv-config-01.xml"), SmooksModel.class);

        StringWriter modelWriter = new StringWriter();
        model.writeModel(modelWriter);
        System.out.println(modelWriter);
    }

    public void test_01_write() throws IOException, SAXException {
        SmooksModel smooksModel = new SmooksModel();
        ModelBuilder modelBuilder = new ModelBuilder(SmooksModel.MODEL_DESCRIPTOR, false);
        Model<SmooksModel> model = new Model<SmooksModel>(smooksModel, modelBuilder);
        CSVReader csvReader = new CSVReader();

        // Populate it...
        csvReader.setFields("name,address,age");
        csvReader.setRootElementName("people");
        csvReader.setRecordElementName("person");
        csvReader.setIndent(true);

        // Need to register all the "namespace root" bean instances...
        model.registerBean(csvReader);

        // Add it in the appropriate place in the object graph....
        smooksModel.getModelComponents().add(csvReader);

        StringWriter modelWriter = new StringWriter();
        model.writeModel(modelWriter);
        System.out.println(modelWriter);
    }
}
