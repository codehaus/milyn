package org.mylin.ecore.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

/**
 * EDI-specific resource factory implementation
 * 
 * @author zubairov
 *
 */
public class EDIFactResourceFactoryImpl extends ResourceFactoryImpl {

	@Override
	public Resource createResource(URI uri) {
		return new EDIXMLResource(uri);
	}
	
}
