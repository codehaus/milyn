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
package org.milyn.smooks.scripting;

import org.milyn.util.ClassUtil;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.Smooks;
import org.milyn.SmooksUtil;

import java.io.InputStream;

/**
 * Utility class for the Scripting Cartridge.
 * @author tfennelly
 * @deprecated No need to do this anymore.  The classpath is scanned.
 */
public class ScriptingUtils {

    private static final String SCRIPTING_CDU_CREATORS_CDRL = "scripting-cdu-creators.cdrl";

    /**
     * Register the Scripting CDU creator configurations.
     * @param smooks The Smooks instance with which the registrations are to be made.
     */
    public static void registerCDUCreators(Smooks smooks) {
        InputStream stream = ClassUtil.getResourceAsStream(ScriptingUtils.SCRIPTING_CDU_CREATORS_CDRL, ScriptingUtils.class);
        try {
            smooks.addConfigurations(SCRIPTING_CDU_CREATORS_CDRL, stream);
        } catch (Exception e) {
            throw new SmooksConfigurationException("Failed to load classpath resource file: " + ScriptingUtils.SCRIPTING_CDU_CREATORS_CDRL + ".  Should be in package: " + ScriptingUtils.class.getPackage().getName(), e);
        }
    }
}
