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

import java.lang.annotation.*;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public abstract @interface VisitIfNot {

    /**
     * The name of the parameter to be tested.
     * @return The name of the resource paramater to be tested.
     */
    public abstract String param();

    /**
     * The value of the parameter to be tested.
     * @return The value of the resource paramater to be tested.
     */
    public abstract String value();


    /**
     * The default value of the parameter to be tested, if not specified.
     * @return The default value of the resource paramater to be tested, if not specified.
     */
    public abstract String defaultVal();

}