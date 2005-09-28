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

package org.milyn.cdr.cdrar;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarInputStream;

import org.milyn.cdr.CDRStore;
import org.milyn.cdr.cdrar.CDRArchive;
import org.milyn.cdr.cdrar.CDRArchiveEntry;
import org.milyn.container.MockContainerContext;
import org.milyn.test.FileSysUtils;

import junit.framework.TestCase;

public class CDRStoreTest extends TestCase {

	private CDRStore cdrarStore;

	public CDRStoreTest(String arg0) {
		super(arg0);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		cdrarStore = new CDRStore(new MockContainerContext());
	}

	public void testLoad_File() {
		try {
			cdrarStore.load("cdrar1", new JarInputStream(getClass().getResourceAsStream("CDRClassLoaderTest.cdrar")));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertEquals(5, getCDRStoreCount());
	}

	public void testLoad_File_exceptions() {
		try {
			cdrarStore.load(null, null);
			fail("no exception on null 'name' arg");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			cdrarStore.load("", null);
			fail("no exception on empty 'name' arg");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			cdrarStore.load("cdrar", null);
			fail("no exception on null 'cdrarStream' arg");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private int getCDRStoreCount() {
		CDRArchive[] cdrarEnum = cdrarStore.getCdrars();
		int entryCount = 0;

		for(int i = 0; i < cdrarEnum.length; i++) {
			Enumeration entries = cdrarEnum[i].getEntries();
			
			while(entries.hasMoreElements()) {
				CDRArchiveEntry entry = (CDRArchiveEntry)entries.nextElement();
				entryCount++;
			}
		}
		return entryCount;
	}

	public void testLoad_CDRArchive_1() {
		try {
			cdrarStore.load((CDRArchive)null);
			fail("expecting arg exception on null param");
		} catch(IllegalArgumentException arg){ 			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testLoad_Folder_1() {
		try {
			cdrarStore.load((File)null);
			fail("expecting arg exception on null param");
		} catch(IllegalArgumentException arg){ 			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testLoad_Folder_2() {
		try {
			File cdrarFolder = new File(FileSysUtils.getProjectRootDir(), "test/junit/src/org/milyn/cdr/cdrar/CDRStoreTest.java");
			cdrarStore.load(cdrarFolder);
			// This covers 2 tests: non-existance and not-a-dir.
			fail("Expecting IOException on non Dir file");
		} catch(IOException io){ 			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testLoad_Folder_3() {
		try {
			File cdrarFolder = new File(FileSysUtils.getProjectRootDir(), "test");
			cdrarStore.load(cdrarFolder);
			fail("Expecting IOException on folder with no cdrar files.");
		} catch(IOException io){ 			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testLoad_Folder_4() {
		try {
			File cdrarFolder = new File(FileSysUtils.getProjectRootDir(), "test/junit/src/org/milyn/cdr/cdrar");
			cdrarStore.load(cdrarFolder);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertEquals(14, getCDRStoreCount());
	}
}





