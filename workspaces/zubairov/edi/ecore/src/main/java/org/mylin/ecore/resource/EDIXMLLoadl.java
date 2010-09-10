package org.mylin.ecore.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLLoadImpl;
import org.milyn.edisax.EDIParser;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser.MappingRegistry;
import org.xml.sax.SAXException;

/**
 * Custom XML Load
 * 
 * @author zubairov
 *
 */
public class EDIXMLLoadl extends XMLLoadImpl implements XMLLoad {

	private MappingRegistry reg;

	public EDIXMLLoadl(XMLHelper helper, UNEdifactInterchangeParser.MappingRegistry reg) {
		super(helper);
		this.reg = reg;
	}

	@Override
	protected SAXParser makeParser() throws ParserConfigurationException,
			SAXException {
		UNEdifactInterchangeParser parser = new UNEdifactInterchangeParser(reg);
		parser.setFeature(EDIParser.FEATURE_IGNORE_NEWLINES, true);
		parser.setFeature(EDIParser.FEATURE_VALIDATE, true);
		return new EDISAXParser(parser);
	}
	
	@Override
	public void load(XMLResource resource, InputStream inputStream,
			Map<?, ?> options) throws IOException {
		this.options = options;
		super.load(resource, inputStream, options);
	}
}
