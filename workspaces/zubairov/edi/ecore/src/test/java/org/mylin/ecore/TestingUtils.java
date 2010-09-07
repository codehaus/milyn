package org.mylin.ecore;

import static junit.framework.Assert.assertNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

public class TestingUtils {

	/**
	 * Loading an ecore model
	 * @param fileName TODO
	 * 
	 * @return
	 * @throws IOException
	 */
	public static EPackage loadModel(String fileName) throws IOException {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new EcoreResourceFactoryImpl());
		Resource resource = rs.createResource(URI
				.createFileURI(fileName));
		resource.load(null);
		EPackage pkg = (EPackage) resource.getAllContents().next();
		assertNotNull(pkg);
		return pkg;
	}

	public static void serializeAsXML(EObject root, OutputStream out) {
		ResourceSet resourceSet = new ResourceSetImpl();
		/*
		 * Register XML Factory implementation using DEFAULT_EXTENSION
		 */
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new XMLResourceFactoryImpl());

		/*
		 * Create empty resource with the given URI
		 */
		Resource resource = resourceSet.createResource(URI
				.createURI("./bookStore.xml"));

		/*
		 * Add bookStoreObject to contents list of the resource
		 */
		resource.getContents().add(root);

		try {
			/*
			 * Save the resource
			 */
			Map<String, Boolean> map = new HashMap<String, Boolean>();
			map.put(XMLResource.OPTION_EXTENDED_META_DATA, true);
			resource.save(out, map);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Unexpected IO Exception " + e.getMessage());
		}
	}
}
