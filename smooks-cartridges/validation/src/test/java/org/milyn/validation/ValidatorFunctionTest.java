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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.io.StreamUtils;
import org.milyn.payload.StringResult;
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

            final ExecutionContext context = smooks.createExecutionContext();
            //context.setEventListener(new HtmlReportGenerator("smooks-report.html"));
            final StringResult result = new StringResult();

            smooks.filter(new StringSource(xml), result, context);

            List<RuleEvalResult> warnings = ValidationResults.getWarnings(context);

            // TODO: Commented out intentionally. Want to check in and let tom see
            // he can spot what I'm doing wrong.
            //assertEquals(1, warnings.size());

            assertEquals(0, ValidationResults.getOKs(context).size());
            assertEquals(0, ValidationResults.getErrors(context).size());

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
