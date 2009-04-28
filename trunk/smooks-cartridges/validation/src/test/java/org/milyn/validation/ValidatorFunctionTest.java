/*
 * Milyn - Copyright (C) 2006
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.io.StreamUtils;
import org.milyn.payload.StringSource;
import org.milyn.profile.Profile;
import org.milyn.rules.RuleEvalResult;
import org.xml.sax.SAXException;

/**
 * Function test for {@link Validator}
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class ValidatorFunctionTest
{
    @Test
    public void filter() throws IOException, SAXException
    {
        InputStream config = null;
        try
        {
            config = getSmooksConfig("smooks-validation-config.xml");
            final Smooks smooks = new Smooks(config);

            final String xml = readStringFromFile("validation-test.xml");

            final ExecutionContext context = smooks.createExecutionContext(Profile.DEFAULT_PROFILE);

            smooks.filter(new StringSource(xml), null, context);

            List<RuleEvalResult> warnings = ValidationResults.getWarnings(context);
            System.out.println(warnings);

        }
        finally
        {
            if (config != null)
                config.close();
        }
    }

    private InputStream getSmooksConfig(final String fileName)
    {
        return getClass().getResourceAsStream("/smooks-configs/extended/1.0/" + fileName);
    }

    private String readStringFromFile(final String fileName) throws IOException
    {
        return StreamUtils.readStreamAsString(getClass().getResourceAsStream("/test-input-files/" + fileName));
    }
}
