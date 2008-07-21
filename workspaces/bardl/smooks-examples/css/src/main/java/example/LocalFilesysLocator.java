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
package example;

import org.milyn.resource.ContainerResourceLocator;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class LocalFilesysLocator implements ContainerResourceLocator {

    public InputStream getResource(String configName, String defaultUri) throws IllegalArgumentException, IOException {
        return getResource(defaultUri);
    }

    public InputStream getResource(String uri) throws IllegalArgumentException, IOException {
        return new FileInputStream(uri);
    }
}
