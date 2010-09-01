package org.mylin.ecore;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.MockContentHandler;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.IEdimap;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.mylin.ecore.model.ECOREEdimap;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MappingTest extends TestCase {

	public void testParser() throws IOException, SAXException,
			EDIConfigurationException {
		ResourceSet rs = new ResourceSetImpl();
		rs
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new EcoreResourceFactoryImpl());
		Resource resource = rs.createResource(URI.createFileURI("./CUSCAR.ecore"));
		resource.load(null);
		EPackage pkg = (EPackage) resource.getAllContents().next();
		IEdimap edimap = new ECOREEdimap(pkg);
		UNEdifactInterchangeParser parser = new UNEdifactInterchangeParser();
		parser.addMappingModel(new EdifactModel(edimap));
		parser.ignoreNewLines(true);

		MockContentHandler handler;

		// Test message 01 - no UNA segment...
		handler = new MockContentHandler();
		parser.setContentHandler(handler);
		parser.parse(new InputSource(getClass().getResourceAsStream(
				"/unedifact-msg-01.edi")));
		System.out.println(handler.xmlMapping);
	}

}
