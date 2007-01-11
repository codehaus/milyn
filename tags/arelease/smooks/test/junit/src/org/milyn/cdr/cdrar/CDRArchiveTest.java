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

import java.io.IOException;
import java.util.jar.JarInputStream;

import org.milyn.cdr.cdrar.CDRArchive;
import org.milyn.cdr.cdrar.CDRArchiveEntry;
import org.milyn.cdr.cdrar.InvalidCDRArchiveException;

import junit.framework.TestCase;

public class CDRArchiveTest extends TestCase {

	public CDRArchiveTest(String arg0) {
		super(arg0);
	}

	public void testConstructorOK() {
		CDRArchive cdrar = null;
		try {
			cdrar = new CDRArchive("OK.cdrar", new JarInputStream(getClass().getResourceAsStream("OK.cdrar")));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected exception on valid cdrar: " + e.getMessage());
		}
		assertEquals(5, cdrar.size());
		assertNotNull(cdrar.getEntry("META-INF/cdrar.cdrl"));
		checkForEntry(cdrar, "org/milyn/dom/HtmlDOMParserTest.class");		
		checkForEntry(cdrar, "org/milyn/cdrar/ArchiveDefTest.class");
		checkForEntry(cdrar, "org/milyn/cdrar/TransunitArchiveTest.class");
		checkForEntry(cdrar, "org/milyn/cdrar/TuarTest.class");
	}
	
	private void checkForEntry(CDRArchive cdrar, String path) {
		CDRArchiveEntry entry = cdrar.getEntry(path);
		
		if(entry == null) {
			fail("CDRArchiveEntry [" + path + "] not found.");
		}
	}

	public void testConstructorNotOK() {
		CDRArchive cdrar = null;
		try {
			// MissingCDRLResource.cdrar is missing one of the units define in 
			// the Archive Def file. This is OK - should be in one of 
			// the other .cdrar files.
			cdrar = new CDRArchive("MissingADFResource.cdrar", new JarInputStream(getClass().getResourceAsStream("MissingADFResource.cdrar")));
		} catch (InvalidCDRArchiveException e) {
			fail(".cdrar should have to contain the resource referenced in .cdrl" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
