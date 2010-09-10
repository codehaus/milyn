package org.mylin.ecore;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.mylin.ecore.model.envelope.DocumentRoot;
import org.mylin.ecore.model.envelope.UnEdifactType;
import org.mylin.ecore.resource.EDIFactResourceFactoryImpl;
import org.mylin.ecore.resource.EDIPackageRegistry;

public class EDIResourceSetTest extends TestCase {

	public void testEDILoading() throws Exception {
		//String fileName = "/99a_cuscar_out.xml";
		String fileName = "/99a_cuscar.edi";
		//String fileName = "/test.xml";
		EDIPackageRegistry registry = new EDIPackageRegistry();
		ResourceSetImpl rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("edi", new EDIFactResourceFactoryImpl(registry));
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("xml", new XMLResourceFactoryImpl());
		rs.setPackageRegistry(registry);
		Resource resource = rs.createResource(URI.createURI(fileName));
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put(XMLResource.OPTION_EXTENDED_META_DATA, true);
		resource.load(getClass().getResourceAsStream(fileName),
				map);
		DocumentRoot root = (DocumentRoot) resource.getContents().get(0);
		UnEdifactType unEdifact = root.getUnEdifact();
		assertEquals("XXXXXLCTA", unEdifact.getUNB().getRecipient().getId());
		assertEquals(2, unEdifact.getMessages().size());
	}

}
