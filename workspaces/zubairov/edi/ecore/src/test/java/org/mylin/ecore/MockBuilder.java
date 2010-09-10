package org.mylin.ecore;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.XMLReader;

/**
 * 
 * Extension to {@link SAXBuilder} to handle external parser
 * 
 * @author zubairov
 *
 */
public class MockBuilder extends SAXBuilder {

	private XMLReader reader;

	public MockBuilder(XMLReader reader) {
		super();
		this.reader = reader;
	}
	
	@Override
	protected XMLReader createParser() throws JDOMException {
		return reader;
	}
}
