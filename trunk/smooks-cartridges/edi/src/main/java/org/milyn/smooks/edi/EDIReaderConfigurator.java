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
package org.milyn.smooks.edi;

import org.milyn.cdr.ReaderConfigurator;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.GenericReaderConfigurator;
import org.milyn.cdr.Parameter;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.assertion.AssertArgument;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * EDI Reader configurator.
 * <p/>
 * Supports programmatic {@link SmooksEDIReader} configuration on a {@link org.milyn.Smooks#setReaderConfig(org.milyn.cdr.ReaderConfigurator) Smooks} instance.
 *
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class EDIReaderConfigurator implements ReaderConfigurator {

    private String mappingModel;
    private String targetProfile;

    public EDIReaderConfigurator(String mappingModel) {
        AssertArgument.isNotNullAndNotEmpty(mappingModel, "mappingModel");
        this.mappingModel = mappingModel;
    }

    public void setTargetProfile(String targetProfile) {
        AssertArgument.isNotNullAndNotEmpty(targetProfile, "targetProfile");
        this.targetProfile = targetProfile;
    }

    public SmooksResourceConfiguration toConfig() {
        GenericReaderConfigurator configurator = new GenericReaderConfigurator(SmooksEDIReader.class);

        configurator.getParameters().setProperty(SmooksEDIReader.MODEL_CONFIG_KEY, mappingModel);
        configurator.setTargetProfile(targetProfile);

        return configurator.toConfig();
    }
}