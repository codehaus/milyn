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

package org.milyn.validation.regex;

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.validation.AbstractValidator;
import org.milyn.validation.ValidationFailures;

/**
 * Validator that validates a against a regular expression.
 * <p>
 *
 * Usage:
 * <pre>{@code
 * <resource-config selector="firstName">
 *     <resource>org.milyn.validation.regex.Validator</resource>
 *     <param name="alias">custom</param>
 *     <param name="aliasFile">regex.properties</param>
 *     <param name="failFast">true</param>
 * </resource-config>
 * }</pre>
 * Properties description:
 * <lu>
 *  <li>{@code alias} An alias to regex mapping that can exist in either the 'regex-default.properties' file of the file specified with the 'aliasFile' property. Optional</li>
 *  <li>{@code aliasFile} A properties file that contains alias to regex mappings. This files should be located on the classpath. Optional.</li>
 *  <li>{@code failFast} if {@code true} a SmooksException will be thrown when a validation error occurs. If {@code false} the failures will be stored in the
 *                       ExecutionContext and can be retrieved using {@link ValidationFailures} class</li>
 * </lu>
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class Validator extends AbstractValidator
{
	/**
	 * The regular expression.
	 */
    @ConfigParam (use=Use.OPTIONAL)
    private String regex;

    /**
     * An alias that exists in either the default default-regex-validator.properties file or
     * in the optional reqex-validator.properties file.
     */
    @ConfigParam (use=Use.OPTIONAL)
    private String alias;

    /**
     * Specifies if an exception should be thrown when a patterns does not match.
     * If false then the the result of will be stored in the execution context for
     * later retrieval.
     */
    @ConfigParam (defaultVal = "true", decoder=org.milyn.javabean.decoders.BooleanDecoder.class)
    private boolean failFast;

    /**
     * A properties file that can contain user defined aliases for regular expression.
     * For example:
     * swedish_phone_nr=regular expression
     */
    @ConfigParam (use=Use.OPTIONAL)
    private String aliasFile;

    /**
     * The validator that will perform the actual validation.
     */
    private RegexValidator validator;

    /**
     * Loads the default regular expressions(default-validator-regex.properties) and any user
     * defined regular expression.
     *
     * @throws SmooksConfigurationException Will be thrown if a mis-configuration exists or if the regex pattern cannot be compiled.
     */
    @Initialize
    public final void initialize() throws SmooksConfigurationException
    {
        validator = regex == null ?  new RegexValidator(alias, aliasFile) : new RegexValidator(regex);
    }

    @Override
    public void validate(final String text, final ExecutionContext executionContext)
    {
        if (failFast)
        {
            validator.validate(text);
        }
        else
        {
            validator.validate(text, executionContext);
        }
    }

    @Override
    public String toString()
    {
        return "RegexValidatorVisitor [validator='" + validator + "']";
    }

}
