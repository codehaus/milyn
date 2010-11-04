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

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * 
 * @author Daniel Bevenius
 *
 */
public class SmooksServiceFactory implements ServiceFactory
{
    public Object getService(Bundle bundle, ServiceRegistration registration)
    {
        Smooks smooks = null;
        try
        {
	        SmooksOSGIFactory factory = new SmooksOSGIFactory(bundle);
	        smooks = factory.createSmooksInstance();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        return smooks;
    }

    public void ungetService(Bundle bundle, ServiceRegistration registration, Object service)
    {
        System.out.println("SmooksServiceFactory ungetService " + service);
        Smooks smooks = (Smooks) service;
        if (smooks != null)
        {
            smooks.close();
        }
    }

}
