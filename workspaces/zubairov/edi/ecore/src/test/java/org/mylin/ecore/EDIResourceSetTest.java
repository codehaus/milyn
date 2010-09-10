package org.mylin.ecore;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.mylin.ecore.resource.EDIFactResourceFactoryImpl;
import org.mylin.ecore.resource.EDIPackageRegistry;

public class EDIResourceSetTest extends TestCase {

	public void testEDILoading() throws Exception {
		EDIPackageRegistry registry = new EDIPackageRegistry();
		ResourceSetImpl rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new EDIFactResourceFactoryImpl(registry));
		Resource resource = rs.createResource(URI.createURI("test.edi"));
		resource.load(getClass().getResourceAsStream("/99a_cuscar.edi"), null);
	}

}
