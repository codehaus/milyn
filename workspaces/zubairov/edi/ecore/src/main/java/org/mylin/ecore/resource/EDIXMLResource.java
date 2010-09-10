package org.mylin.ecore.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser.MappingRegistry;
import org.xml.sax.InputSource;

/**
 * An extension to {@link XMLResourceImpl} that could be used
 * for parsing
 * 
 * @author zubairov
 *
 */
public class EDIXMLResource extends XMLResourceImpl {

	private MappingRegistry reg;


	public EDIXMLResource() {
		super();
	}
	
	public EDIXMLResource(URI uri, UNEdifactInterchangeParser.MappingRegistry reg) {
		super(uri);
		this.reg = reg;
	}
	
	
	@Override
	protected XMLLoad createXMLLoad() {
		return new EDIXMLLoadl(createXMLHelper(), reg);
	}

	@Override
	public void doLoad(InputSource inputSource, Map<?, ?> options)
			throws IOException {
		
		super.doLoad(inputSource, options);
	}
}
