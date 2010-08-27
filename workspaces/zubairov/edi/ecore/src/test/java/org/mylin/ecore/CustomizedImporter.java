package org.mylin.ecore;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.importer.ecore.EcoreImporter;

public class CustomizedImporter extends EcoreImporter {

	@Override
	public ResourceSet createResourceSet() {
		ResourceSet resourceSet = super.createResourceSet();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new EcoreResourceFactoryImpl());
		return resourceSet;
	}
	
}
