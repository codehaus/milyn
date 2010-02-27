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
package org.milyn;

import org.milyn.cdr.SmooksResourceConfiguration;

/**
 * Reader configurator.
 * <p/>
 * Implementation are responsible creating the {@link org.milyn.cdr.SmooksResourceConfiguration} for
 * the Reader to be used by a Smooks instance.
 *
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public interface ReaderConfigurator {

    /**
     * Create the {@link org.milyn.cdr.SmooksResourceConfiguration} for the Reader to be used by the Smooks instance.
     * @return The {@link org.milyn.cdr.SmooksResourceConfiguration}.
     */
    SmooksResourceConfiguration toConfig();
}
