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
package org.milyn.delivery.executionlifecyclecleanup;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.delivery.StringSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ExecutionLifecycleCleanupTest extends TestCase {

    public void test_dom() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("dom-config.xml"));

        smooks.filter(new StringSource("<a><b/><c/><d/><e/></a>"), null);
        assertTrue(DomAssemblyBefore.cleaned);
        assertTrue(DomAssemblyAfter.cleaned);
        assertTrue(DomAssemblyAfterWithException.cleaned);
        assertTrue(DomProcessingBefore.cleaned);
        assertTrue(DomProcessingAfter.cleaned);
    }

    public void test_SAX() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("sax-config.xml"));

        smooks.filter(new StringSource("<a><b/></a>"), null);
        assertTrue(SaxVisitBefore.cleaned);
        assertTrue(SaxVisitAfter.cleaned);
    }
}
