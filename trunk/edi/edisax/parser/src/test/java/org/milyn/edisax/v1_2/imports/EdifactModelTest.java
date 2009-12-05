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

package org.milyn.edisax.v1_2.imports;

import junit.framework.TestCase;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Segment;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.io.StreamUtils;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Validates that the EdifactModel's logic works as expected.
 * @author bardl
 */
public class EdifactModelTest extends TestCase {

    public void testImport_truncatableSegmentsExists() throws IOException, EDIConfigurationException, SAXException {
        EdifactModel ediModel = new EdifactModel();
        InputStream input = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream("edi-config-truncatableSegmentsExists.xml")));
        ediModel.parseSequence(input);

        assertTrue("The truncatableSegments attribute should exist in Import element.", ediModel.getEdimap().getImport().get(0).isTruncatableSegments());
        assertTrue("The truncatable attribute should have value [true] in Segment.", ((Segment)ediModel.getEdimap().getSegments().getSegments().get(0).getSegments().get(0)).isTruncatable());
    }

    public void testImport_truncatableSegmentsNotExists() throws IOException, EDIConfigurationException, SAXException {
        EdifactModel ediModel = new EdifactModel();
        InputStream input = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream("edi-config-truncatableSegmentsNotExists.xml")));
        ediModel.parseSequence(input);

        assertTrue("The truncatableSegments attribute should not exist in Import element.", ediModel.getEdimap().getImport().get(0).isTruncatableSegments() == null);
        assertTrue("The truncatable attribute should have value [true] in Segment.", !((Segment)ediModel.getEdimap().getSegments().getSegments().get(0).getSegments().get(0)).isTruncatable());
    }        
}