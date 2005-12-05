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

package org.milyn.magger;

import java.io.IOException;

import org.apache.batik.css.engine.sac.CSSConditionFactory;
import org.apache.batik.css.engine.sac.CSSSelectorFactory;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.flute.parser.Parser;

import junit.framework.TestCase;

public class DocumentHandlerTest extends TestCase {

	public void test_DocumentHandler() {
		/*
		CSSDocumentHandler docHandler = new CSSDocumentHandler();
		Parser flute = new Parser();
		InputSource cssSrc = new InputSource();
		CSSConditionFactory cssConditionFactory = new CSSConditionFactory(null, "class", null, "id");

		
		flute.setDocumentHandler(docHandler);
		flute.setSelectorFactory(CSSSelectorFactory.INSTANCE);
		flute.setConditionFactory(cssConditionFactory);
		cssSrc.setByteStream(getClass().getResourceAsStream("test1.css"));
		try {
			flute.parseStyleSheet(cssSrc);
		} catch (CSSException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
}
