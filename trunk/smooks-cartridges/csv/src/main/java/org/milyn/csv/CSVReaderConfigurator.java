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
package org.milyn.csv;

import org.milyn.cdr.ReaderConfigurator;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.GenericReaderConfigurator;
import org.milyn.assertion.AssertArgument;

import java.nio.charset.Charset;

/**
 * CSV Reader configurator.
 * <p/>
 * Supports programmatic {@link CSVReader} configuration on a {@link org.milyn.Smooks#setReaderConfig(org.milyn.cdr.ReaderConfigurator) Smooks} instance.
 *
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class CSVReaderConfigurator implements ReaderConfigurator {

    private String csvFields;
    private char separatorChar = ',';
    private char quoteChar = '"';
    private int skipLineCount = 0;
    private Charset encoding = Charset.forName("UTF-8");
    private String rootElementName = "csv-set";
    private String recordElementName = "csv-record";
    private CSVBinding binding;
    private String targetProfile;    

    public CSVReaderConfigurator(String csvFields) {
        AssertArgument.isNotNullAndNotEmpty(csvFields, "csvFields");
        this.csvFields = csvFields;
    }

    public void setSeparatorChar(char separatorChar) {
        AssertArgument.isNotNull(separatorChar, "separatorChar");
        this.separatorChar = separatorChar;
    }

    public void setQuoteChar(char quoteChar) {
        AssertArgument.isNotNull(quoteChar, "quoteChar");
        this.quoteChar = quoteChar;
    }

    public void setSkipLineCount(int skipLineCount) {
        AssertArgument.isNotNull(skipLineCount, "skipLineCount");
        this.skipLineCount = skipLineCount;
    }

    public void setEncoding(Charset encoding) {
        AssertArgument.isNotNull(encoding, "encoding");
        this.encoding = encoding;
    }

    public void setRootElementName(String csvRootElementName) {
        AssertArgument.isNotNullAndNotEmpty(csvRootElementName, "rootElementName");
        this.rootElementName = csvRootElementName;
    }

    public void setRecordElementName(String csvRecordElementName) {
        AssertArgument.isNotNullAndNotEmpty(csvRecordElementName, "recordElementName");
        this.recordElementName = csvRecordElementName;
    }

    public void setBinding(CSVBinding binding) {
        this.binding = binding;
    }

    public void setTargetProfile(String targetProfile) {
        AssertArgument.isNotNullAndNotEmpty(targetProfile, "targetProfile");
        this.targetProfile = targetProfile;
    }

    public SmooksResourceConfiguration toConfig() {
        GenericReaderConfigurator configurator = new GenericReaderConfigurator(CSVReader.class);

        configurator.getParameters().setProperty("fields", csvFields);
        configurator.getParameters().setProperty("separator", Character.toString(separatorChar));
        configurator.getParameters().setProperty("quote-char", Character.toString(quoteChar));
        configurator.getParameters().setProperty("skip-line-count", Integer.toString(skipLineCount));
        configurator.getParameters().setProperty("encoding", encoding.name());
        configurator.getParameters().setProperty("rootElementName", rootElementName);
        configurator.getParameters().setProperty("recordElementName", recordElementName);

        if(binding != null) {
            configurator.getParameters().setProperty("bindBeanId", binding.getBeanId());
            configurator.getParameters().setProperty("bindBeanClass", binding.getBeanClass().getName());
            configurator.getParameters().setProperty("bindBeanAsList", Boolean.toString(binding.isCreateList()));
        }

        configurator.setTargetProfile(targetProfile);

        return configurator.toConfig();
    }
}
