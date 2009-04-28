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
package org.milyn.validation.regex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ExecutionContext;
import org.milyn.io.StreamUtils;
import org.milyn.payload.StringSource;
import org.milyn.profile.Profile;
import org.milyn.validation.ValidationException;
import org.milyn.validation.ValidationResults;
import org.xml.sax.SAXException;

/**
 * Test for {@link Validator}.
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class ValidatorTest
{
    @Test
	public void filterWithRegex() throws IOException, SAXException
	{
        //filter("smooks-config.xml", "regex-test.xml");
	}

    @Test
	public void filterWithDefaultAlias() throws IOException, SAXException
	{
        //filter("smooks-config-with-alias.xml", "regex-test.xml");
	}

    @Test
	public void filterWithAlias() throws IOException, SAXException
	{
        //filter("smooks-config-with-custom-alias.xml", "regex-test.xml");
	}

    //@Test (expected = SmooksConfigurationException.class)
	public void filterWithMissingAlias() throws IOException, SAXException
	{
        //filter("smooks-config-with-missing-alias.xml", "regex-test.xml");
	}

    @Test
    public void filterWithRegexLogFailures() throws IOException, SAXException
    {
        //final ExecutionContext context = filter("smooks-config.xml", "regex-failure-test.xml");

        //final List<ValidationException> failures = ValidationFailures.getAll(context);
        //assertEquals(1, failures.size());
    }

    private ExecutionContext filter(final String smooksConfig, final String testFile) throws IOException, SAXException
    {
        final Smooks smooks = createSmooks(smooksConfig);
        final String xml = readStringFromFile(testFile);
        final ExecutionContext context = smooks.createExecutionContext(Profile.DEFAULT_PROFILE);
        smooks.filter(new StringSource(xml), null, context);
        return context;
    }

    private Smooks createSmooks(final String config) throws IOException, SAXException
    {
        return new Smooks(getClass().getResourceAsStream("/smooks-configs/" + config));
    }

    private String readStringFromFile(final String fileName) throws IOException
    {
        return StreamUtils.readStreamAsString(getClass().getResourceAsStream("/test-input-files/" + fileName));
    }

}
