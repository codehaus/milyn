package org.mylin.ecore.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

/**
 * An extension to {@link XMLResourceImpl} that could be used
 * for parsing
 * 
 * @author zubairov
 *
 */
public class EDIXMLResource extends XMLResourceImpl {

	public EDIXMLResource() {
		super();
	}
	
	public EDIXMLResource(URI uri) {
		super(uri);
	}
	
	
	@Override
	protected XMLLoad createXMLLoad() {
		return new EDIXMLLoadl(createXMLHelper());
	}
}
