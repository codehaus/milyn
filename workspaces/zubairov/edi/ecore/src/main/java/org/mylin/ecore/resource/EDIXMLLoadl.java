package org.mylin.ecore.resource;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.impl.XMLLoadImpl;
import org.milyn.edisax.EDIParser;
import org.xml.sax.SAXException;

/**
 * Custom XML Load
 * 
 * @author zubairov
 *
 */
public class EDIXMLLoadl extends XMLLoadImpl implements XMLLoad {

	public EDIXMLLoadl(XMLHelper helper) {
		super(helper);
	}

	@Override
	protected SAXParser makeParser() throws ParserConfigurationException,
			SAXException {
		return new EDISAXParser(new EDIParser());
	}
}
