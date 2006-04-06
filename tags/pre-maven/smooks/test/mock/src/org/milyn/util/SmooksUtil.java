/*
	Milyn - Copyright (C) 2003

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

package org.milyn.util;

import java.net.URI;

import org.milyn.cdr.CDRConfig;
import org.milyn.cdr.CDRDef;
import org.milyn.cdr.cdrar.CDRArchive;
import org.milyn.cdr.cdrar.CDRArchiveEntry;
import org.milyn.container.MockContainerContext;
import org.milyn.container.MockContainerRequest;
import org.milyn.container.MockContainerResourceLocator;
import org.milyn.delivery.ContentDeliveryConfigImpl;
import org.milyn.delivery.MockContentDeliveryConfig;
import org.milyn.delivery.http.HeaderAction;
import org.milyn.delivery.trans.TransSet;
import org.milyn.delivery.trans.TransUnit;
import org.milyn.device.MockUAContext;

/**
 * Test utility class for working with the CDRStore - loading test cdres instances etc. 
 * @author tfennelly
 */
public class SmooksUtil {

	public MockContainerContext context = new MockContainerContext();
	
	public SmooksUtil() {
	}
	
	/**
	 * Add the supplied entry definition to the CDRStore.
	 * <p/>
	 * Can optionaly include the Content Deliver Resource data associated with the entry. 
	 * @param selector CDRDef selector value.
	 * @param uatarget CDRDef uatarget value.
	 * @param type CDRDef type value.
	 * @param path CDRDef path value.
	 * @param resourceBytes Optional Content Delivery Resource data.
	 * @return The resource definition.
	 */
	public CDRDef addCDRDef(String selector, String uatarget, String path, byte[] resourceBytes) {
		String name = selector + "-" + uatarget + "-" + path;
		CDRConfig archiveDef = new CDRConfig(name);
		CDRArchive cdrar = new CDRArchive(name);
		CDRDef unitDef = new CDRDef(selector, uatarget, path);
		
		archiveDef.addCDRDef(unitDef);
		cdrar.addArchiveDef(archiveDef);
		if(resourceBytes != null) {
			CDRArchiveEntry cdrarEntry = new CDRArchiveEntry(path, resourceBytes);
			cdrar.addEntries(new CDRArchiveEntry[] {cdrarEntry});
		}		
		context.getCdrarStore().load(cdrar);
		
		return unitDef;
	}
	
	/**
	 * Constructs a mock ContainerRequest for the names device.
	 * @param deviceName Device name.
	 * @return Mock ContainerRequest instance.
	 */
	public MockContainerRequest getRequest(String deviceName) {
		MockContainerRequest request;
		MockContainerResourceLocator resourceLocator;
		
		request = new MockContainerRequest();
		request.contextPath = "/";
		request.requestURI = URI.create("http://www.milyn.org");
		request.uaContext = new MockUAContext(deviceName);
		resourceLocator = new MockContainerResourceLocator();
		request.context = context;
		request.context.containerResourceLocator = resourceLocator;
		
		try {
			context.getCdrarStore().getCdrars();
		}catch(IllegalStateException noCdrars) {
			// Just add one to avoid state exceptions
			addCDRDef("*", "*", "org/milyn/delivery/serialize/ReportSerializationUnit.class", null);
		}

		request.deliveryConfig = ContentDeliveryConfigImpl.getInstance(request.uaContext, context);
		
		return request;
	}

	public static void addHeaderAction(String action, String headerName, String headerValue, MockContentDeliveryConfig deliveryConfig) {
		CDRDef cdrDef = new CDRDef("X", "X", "X");
		
		cdrDef.setParameter("action", action);
		cdrDef.setParameter("header-name", headerName);
		cdrDef.setParameter("header-value", headerValue);
		
		deliveryConfig.addObject("http-response-header", new HeaderAction(cdrDef));
	}

	public static void addTransUnit(String targetElement, TransUnit transUnit, MockContentDeliveryConfig deliveryConfig) {
		TransSet transSet = (TransSet)deliveryConfig.transSets.get(targetElement);
		
		if(transSet == null) {
			transSet = new TransSet();
			deliveryConfig.transSets.put(targetElement, transSet);
		}
		transSet.addTransUnit(transUnit);
	}
}
