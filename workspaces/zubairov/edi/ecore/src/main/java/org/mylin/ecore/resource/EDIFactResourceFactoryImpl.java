package org.mylin.ecore.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser.MappingRegistry;

/**
 * EDI-specific resource factory implementation
 * 
 * @author zubairov
 *
 */
public class EDIFactResourceFactoryImpl extends ResourceFactoryImpl {

	private MappingRegistry reg;

	public EDIFactResourceFactoryImpl(UNEdifactInterchangeParser.MappingRegistry registry) {
		this.reg = registry;
	}
	
	@Override
	public Resource createResource(URI uri) {
		return new EDIXMLResource(uri, reg);
	}
	
}
