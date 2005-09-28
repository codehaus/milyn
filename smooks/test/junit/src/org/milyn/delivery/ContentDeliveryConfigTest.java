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

package org.milyn.delivery;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarInputStream;

import org.milyn.container.MockContainerRequest;
import org.milyn.container.MockContainerResourceLocator;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ContentDeliveryConfigImpl;
import org.milyn.delivery.serialize.SerializationUnit;
import org.milyn.delivery.trans.TransSet;
import org.milyn.delivery.trans.TransUnit;
import org.milyn.device.MockUAContext;
import org.milyn.test.FileSysUtils;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class ContentDeliveryConfigTest extends TestCase {

	private MockContainerRequest containerRequest;
	
	/**
	 * Constructor for ContentDeliveryConfigTest.
	 * @param arg0
	 */
	public ContentDeliveryConfigTest(String arg0) {
		super(arg0);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		containerRequest = new MockContainerRequest();
		containerRequest.context.containerResourceLocator.setResource("/deliveryunit-config.xml", getClass().getResourceAsStream("/deliveryunit-config.xml"));
		containerRequest.uaContext = new MockUAContext("deviceX");
		loadTestCdrar();	
	}

	public void testGetInstance_bad_args() {
		try { 
			ContentDeliveryConfigImpl.getInstance(null, containerRequest.getContext());
			fail("Expected IllegalArgumentException on null UAContext.");
		} catch(IllegalArgumentException e) {
			// OK
		}
		try { 
			ContentDeliveryConfigImpl.getInstance(containerRequest.getUseragentContext(), null);
			fail("Expected IllegalArgumentException on null ContainerContext.");
		} catch(IllegalArgumentException e) {
			// OK
		}
	}

	public void testGetInstance_test_multiple_calls() {
		ContentDeliveryConfig table1 = ContentDeliveryConfigImpl.getInstance(containerRequest.getUseragentContext(), containerRequest.getContext());
		if(table1 == null) {
			fail("ContentDeliveryConfigImpl.getInstance returned null - this should never happen!");
		}
		ContentDeliveryConfig table2 = ContentDeliveryConfigImpl.getInstance(containerRequest.getUseragentContext(), containerRequest.getContext());
		if(table2 == null) {
			fail("ContentDeliveryConfigImpl.getInstance returned null - this should never happen!");
		}
		if(table1 != table2) {
			fail("Different ContentDeliveryConfigImpl instances returned in sequence of calls to getInstance with the same UAContext instance.");
		}
	}

	public void loadTestCdrar() {
		try {
			File taurFile = FileSysUtils.getProjectFile("test/testCdrar/testCdrar.cdrar");
			JarInputStream cdrarStream = new JarInputStream(new FileInputStream(taurFile));
			containerRequest.context.getCdrarStore().load(taurFile.toURL().toExternalForm(), cdrarStream);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	/*
		<cdres-list> 
			<cdres selector="a" uatarget="device1,profile1,device2"  	path="test/trans/ATransUnit.class" />
			<cdres selector="b" uatarget="device3,device4,profile2," 	path="test/trans/BTransUnit.class" />
			<cdres selector="c" uatarget="profile3,device5,profile5"  	path="test/trans/CTransUnit.class" />
			<cdres selector="d" uatarget="device7,profile2" 			path="test/trans/DTransUnit.class" />
		
			<cdres selector="d" uatarget="device1,profile1,device2"  	path="test/trans/ATransUnit.class" />
			<cdres selector="c" uatarget="device3,device4,profile2," 	path="test/trans/BTransUnit.class" />
			<cdres selector="b" uatarget="profile3,device5,profile5"  	path="test/trans/CTransUnit.class" />
			<cdres selector="a" uatarget="device7,profile2" 			path="test/trans/DTransUnit.class" />
		
			<cdres selector="c" uatarget="device1,profile1,device2"  	path="test/trans/ATransUnit.class" />
			<cdres selector="d" uatarget="device3,device4,profile2," 	path="test/trans/BTransUnit.class" />
			<cdres selector="a" uatarget="profile3,device5,profile5"  	path="test/trans/CTransUnit.class" />
			<cdres selector="b" uatarget="device7,profile2" 			path="test/trans/DTransUnit.class" />

			<cdres selector="dtd" uatarget="device10" path="test/trans/xhtml1-strict.dtd" />	
			<cdres selector="xmldef:elcspec:empty" uatarget="device10" path="test/trans/ATransUnit.class" />	
			<cdres selector="table" uatarget="device10" path="test/trans/BTransUnit.class" />	
			<cdres selector="xmldef:elcspec:empty" uatarget="device10" path="test/trans/ITransUnit.class" />	
			<cdres selector="input" uatarget="profile10" path="test/trans/CTransUnit.class" />	
			<cdres selector="hr" uatarget="device10" path="test/trans/DTransUnit.class" />	
			<cdres selector="xmldef:elcspec:empty" uatarget="device10" path="test/trans/HTransUnit.class" />	
			<cdres selector="area" uatarget="device10" path="test/trans/FTransUnit.class" />	
			<cdres selector="input" uatarget="device10" path="test/trans/BTransUnit.class" />	
			<cdres selector="xmldef:elcspec:empty" uatarget="device10" path="test/trans/GTransUnit.class" />	
		</cdres-list>
	 */
	public void testGetInstance_table_contents() {
		ContentDeliveryConfig deliveryContext;

		/*
			<cdres selector="a" uatarget="device1,profile1,device2"  	path="test/trans/ATransUnit.class" />
			<cdres selector="a" uatarget="device7,profile2" 		  	path="test/trans/DTransUnit.class" />
			<cdres selector="a" uatarget="profile3,device5,profile5" 	path="test/trans/CTransUnit.class" />
		 */
		deliveryContext = getContentDeliveryContext("device1", new String[] {"profile3", "profile4"});
		checkTagUnits("a", new String[] {}, new String[] {"ATransUnit", "CTransUnit"}, deliveryContext);
		deliveryContext = getContentDeliveryContext("device7", new String[] {});
		checkTagUnits("a", new String[] {}, new String[] {"DTransUnit"}, deliveryContext);
		deliveryContext = getContentDeliveryContext("device5", new String[] {"profile2"});
		checkTagUnits("a", new String[] {}, new String[] {"CTransUnit", "DTransUnit"}, deliveryContext);
		// make sure it doesn't return null
		checkTagUnits("xxx", new String[] {}, new String[] {}, deliveryContext);

		/*
			<cdres selector="b" uatarget="device3,device4,profile2," 	path="test/trans/BTransUnit.class" />
			<cdres selector="b" uatarget="profile3,device5,profile5"  	path="test/trans/CTransUnit.class" />
			<cdres selector="b" uatarget="device7,profile2" 			path="test/trans/DTransUnit.class" />
		*/
		deliveryContext = getContentDeliveryContext("device1", new String[] {"profile3", "profile4"});
		checkTagUnits("b", new String[] {}, new String[] {"CTransUnit"}, deliveryContext);
		deliveryContext = getContentDeliveryContext("device7", new String[] {});
		checkTagUnits("b", new String[] {}, new String[] {"DTransUnit"}, deliveryContext);
		deliveryContext = getContentDeliveryContext("device5", new String[] {"profile2"});
		checkTagUnits("b", new String[] {}, new String[] {"CTransUnit", "BTransUnit", "DTransUnit"}, deliveryContext);
		
		/*
			<cdres selector="c" uatarget="profile3,device5,profile5"  	path="test/trans/CTransUnit.class" />
			<cdres selector="c" uatarget="device3,device4,profile2," 	path="test/trans/BTransUnit.class" />
			<cdres selector="c" uatarget="device1,profile1,device2"  	path="test/trans/ATransUnit.class" />
		*/
		deliveryContext = getContentDeliveryContext("device1", new String[] {"profile3", "profile4"});
		checkTagUnits("c", new String[] {}, new String[] {"ATransUnit", "CTransUnit"}, deliveryContext);
		deliveryContext = getContentDeliveryContext("device7", new String[] {});
		checkTagUnits("c", new String[] {}, new String[] {}, deliveryContext);
		deliveryContext = getContentDeliveryContext("device5", new String[] {"profile2"});
		checkTagUnits("c", new String[] {}, new String[] {"CTransUnit", "BTransUnit"}, deliveryContext);
		
		MockUAContext context = new MockUAContext("device10");
		context.addProfile("profile10");
		containerRequest.uaContext = context;
		deliveryContext = ContentDeliveryConfigImpl.getInstance(context, containerRequest.getContext());
		checkTagUnits("table", new String[] {}, new String[] {"BTransUnit"}, deliveryContext);
		checkTagUnits("input", new String[] {"HTransUnit"}, new String[] {"BTransUnit", "CTransUnit", "ATransUnit", "ITransUnit", "GTransUnit"}, deliveryContext);
		checkTagUnits("hr", new String[] {"HTransUnit"}, new String[] {"DTransUnit", "ATransUnit", "ITransUnit", "GTransUnit"}, deliveryContext);

		Hashtable serializationUnits = deliveryContext.getSerailizationUnits();
		assertEquals("Expected 2 SerializationUnit", 2, serializationUnits.size());
		assertNotNull("Expected SerializationUnit for element 'a'", (SerializationUnit)serializationUnits.get("a"));
		assertNotNull("Expected SerializationUnit for element 'b'", (SerializationUnit)serializationUnits.get("b"));

		Hashtable assemblyUnits = deliveryContext.getAssemblyUnits();
		assertEquals("Expected AssemblyUnits on 1 element - 'b'", 1, assemblyUnits.size());
		Vector bAssemblyUnits = (Vector)assemblyUnits.get("b");
		assertNotNull("Expected AssemblyUnit for element 'b'", bAssemblyUnits);
		assertEquals("Expected 1 AssemblyUnit on 'b'", 1, bAssemblyUnits.size());
	}
	
	private void checkTagUnits(String tag, String[] expectedVisitBeforeTransUnits, String[] expectedVisitAfterTransUnits, ContentDeliveryConfig table) {
		TransSet transSet;
		List visitBefore;
		List visitAfter;

		transSet = table.getTransSet(tag);
		if(transSet == null && (expectedVisitBeforeTransUnits.length > 0 || expectedVisitAfterTransUnits.length > 0)) {
			fail("No TransSet for '" + tag + "'.");
		}
		
		if(transSet != null) {
			visitBefore = transSet.getVisitBeforeTransUnits();
			visitAfter = transSet.getVisitAfterTransUnits();
			
			if(expectedVisitBeforeTransUnits.length > 0 && visitBefore == null) {
				fail("Expected TransSet.getVisitBeforeTransUnits() to return a non-null visit before list.");
			} else if(expectedVisitBeforeTransUnits.length == 0 && visitBefore == null) {
				// OK
			} else {
				assertEquals("Wrong number of visitBefore TransUnits.", expectedVisitBeforeTransUnits.length, visitBefore.size());
				for(int i = 0; i < visitBefore.size(); i++) {
					TransUnit transUnit = (TransUnit)visitBefore.get(i);
					if(!transUnit.getClass().getName().endsWith(expectedVisitBeforeTransUnits[i])) {
						fail("Expected: " + Arrays.asList(expectedVisitBeforeTransUnits) + " visitBefore TransUnits, but was: " + visitBefore);
					}
				}
			}
			
			if(expectedVisitAfterTransUnits.length > 0 && visitAfter == null) {
				fail("Expected TransSet.getVisitAfterTransUnits() to return a non-null visit after list.");
			} else if(expectedVisitAfterTransUnits.length == 0 && visitAfter == null) {
				// OK
			} else {
				assertEquals("Wrong number of visitAfter TransUnits.", expectedVisitAfterTransUnits.length, visitAfter.size());
				for(int i = 0; i < visitAfter.size(); i++) {
					TransUnit transUnit = (TransUnit)visitAfter.get(i);
					if(!transUnit.getClass().getName().endsWith(expectedVisitAfterTransUnits[i])) {
						fail("Expected: " + Arrays.asList(expectedVisitAfterTransUnits) + " visitAfter TransUnits, but was: " + visitAfter);
					}
				}
			}
		}
	}
	
	private ContentDeliveryConfig getContentDeliveryContext(String device, String[] profiles) {
		MockUAContext context = new MockUAContext(device);
		MockContainerResourceLocator resourceLocator = new MockContainerResourceLocator();
		
		context.addProfiles(profiles);
		resourceLocator.setResource("/deliveryunit-config.xml", getClass().getResourceAsStream("/deliveryunit-config.xml"));
		return ContentDeliveryConfigImpl.getInstance(context, containerRequest.getContext());
	}
}
