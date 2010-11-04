/*
 * Milyn - Copyright (C) 2006 - 2010
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
package org.milyn;

import java.io.IOException;

import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

public class SmooksOSGIFactory
{
    private final Bundle bundle;

    public SmooksOSGIFactory(Bundle bundle)
    {
        this.bundle = bundle;
    }
    
    public Smooks createSmooksInstance() throws IOException, SAXException
    {
        Smooks smooks = new Smooks();
        String config = (String) bundle.getHeaders().get("Smooks-Config");
        System.out.println("SmooksOSGIFactory [" + config + "]");
        smooks.setClassLoader(new BundleClassLoaderDelegator(bundle, getClass().getClassLoader()));
        smooks.addConfigurations(config);
        return smooks;
    }

}
