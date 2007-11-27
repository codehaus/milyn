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
package org.milyn.cdr.annotation;

import org.milyn.javabean.DataDecoder;

import java.lang.annotation.*;

/**
 * Configuration paramater field annotation.
 * <p/>
 * Helps supports reflective injection of {@link org.milyn.delivery.ContentHandler} paramaters
 * from its {@link org.milyn.cdr.SmooksResourceConfiguration} instance.  To inject the whole
 * {@link org.milyn.cdr.SmooksResourceConfiguration} instance, use the {@link @org.milyn.cdr.annotation.Config}
 * annotation.
 *
 * <h3>Usage</h3>
 * Where the paramater name is the same as the field name:
 * <pre>
 *     &#64;ConfigParam(decoder={@link org.milyn.javabean.decoders.IntegerDecoder}.class)
 *     private int maxDigits;
 * </pre>
 * Where the paramater name is NOT the same as the field name:
 * <pre>
 *     &#64;ConfigParam(name="max-digits", decoder={@link org.milyn.javabean.decoders.IntegerDecoder}.class)
 *     private int maxDigits;
 * </pre>
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @see Configurator
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ConfigParam {

    /**
     * The paramater name as defined in the resource configuration.  If not defined,
     * the name defaults to the name of the field.
     * @return The paramater name.
     */
    public String name() default NULL;

    /**
     * Paramater required or optional.
     * <p/>
     * Defaults to required.
     *
     * @return Paramater usage.
     */
    public Use use() default Use.REQUIRED;

    /**
     * The default paramater value.
     * <p/>
     * Only relevant when use=OPTIONAL and the paramater is not defined on the configuration..
     *
     * @return The default paramater value (un-decoded).
     */
    public String defaultVal() default UNASSIGNED;

    /**
     * Paramater choice values.
     *
     * @return List of valid choices (un-decoded).
     */
    public String[] choice() default NULL;

    /**
     * The {@link DataDecoder} class to use when decoding the paramater value.
     * @return The {@link DataDecoder} class.
     */
    public Class<? extends DataDecoder> decoder() default DataDecoder.class;

    /**
     * Configuration paramater use.
     */
    public static enum Use {
        /**
         * Parameter is required.
         */
        REQUIRED,

        /**
         * Parameter is optional.
         */
        OPTIONAL,
    }

    public static final String NULL = "##NULL";
    public static final String UNASSIGNED = "org.milyn.cdr.annotation.ConfigParam##UNASSIGNED";
}
