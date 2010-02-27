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
package org.milyn.csv.prog;

import org.milyn.Smooks;
import org.milyn.FilterSettings;
import org.milyn.csv.CSVReaderConfigurator;
import org.milyn.csv.CSVBinding;
import org.milyn.csv.CSVBindingType;
import org.milyn.payload.JavaResult;
import org.milyn.assertion.AssertArgument;

import javax.xml.transform.stream.StreamSource;
import java.util.List;
import java.util.UUID;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * CSV {@link List} Binder class.
 * <p/>
 * Simple CSV to Object {@link List} binding class.
 * <p/>
 * Exmaple usage:
 * <pre>
 * public class PeopleBinder {
 *     // Create and cache the binder instance..
 *     private CSVListBinder binder = new CSVListBinder("firstname,lastname,gender,age,country", Person.class);
 *
 *     public List&lt;Person&gt; bind(Reader csvStream) {
 *         return binder.bind(csvStream);
 *     }
 * }
 * </pre>
 *
 *
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class CSVListBinder {

    private String beanId = UUID.randomUUID().toString();
    private Smooks smooks;

    public CSVListBinder(String fields, Class recordType) {
        AssertArgument.isNotNullAndNotEmpty(fields, "fields");
        AssertArgument.isNotNull(recordType, "recordType");

        smooks = new Smooks();
        smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
        smooks.setReaderConfig(new CSVReaderConfigurator(fields)
                .setBinding(new CSVBinding(beanId, recordType, CSVBindingType.LIST)));
    }

    public List bind(Reader csvStream) {
        AssertArgument.isNotNull(csvStream, "csvStream");

        JavaResult javaResult = new JavaResult();

        smooks.filterSource(new StreamSource(csvStream), javaResult);

        return (List) javaResult.getBean(beanId);
    }

    public List bind(InputStream csvStream) {
        return bind(new InputStreamReader(csvStream));
    }
}
