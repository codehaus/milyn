package org.mylin.ecore;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.EDIParser;
import org.milyn.edisax.MockContentHandler;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.IEdimap;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.mylin.ecore.model.EdimapAdapter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test that we can consume ECORE model to feed into EcoreParser (UN EDIFACT parser)
 * 
 * @author zubairov
 *
 */
public class ParsingTest extends TestCase {

	/**
	 * Parsing CUSCAR 99A CUSCAR message with validation
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws EDIConfigurationException
	 */
	public void testParser() throws IOException, SAXException,
			EDIConfigurationException {
		EPackage pkg = loadCUSCARModel();
		IEdimap edimap = new EdimapAdapter(pkg);
		UNEdifactInterchangeParser parser = new UNEdifactInterchangeParser();
		parser.setFeature(EDIParser.FEATURE_VALIDATE, true);
		parser.addMappingModel(new EdifactModel(edimap));
		parser.ignoreNewLines(true);

		MockContentHandler handler;

		// Test message 01 - no UNA segment...
		handler = new MockContentHandler();
		parser.setContentHandler(handler);
		parser.parse(new InputSource(getClass().getResourceAsStream(
				"/99a_cuscar.edi")));
		System.out.println(handler.xmlMapping);
	}

	/**
	 * We need to test that component definitions are in the right order
	 * 
	 * @throws Exception
	 */
	public void testComponentOrder() throws Exception {
		EPackage pkg = loadCUSCARModel();
		EClass root = (EClass) pkg.getEClassifier("CUSCAR");
		assertNotNull(root);
		EReference feature = (EReference) root
				.getEStructuralFeature("Place_location_identification");
		assertNotNull(feature);
		EClass clazz = feature.getEReferenceType();
		String[] expected = new String[] { "Place_location_qualifier",
				"LOCATION_IDENTIFICATION",
				"RELATED_LOCATION_ONE_IDENTIFICATION",
				"RELATED_LOCATION_TWO_IDENTIFICATION", "Relation__coded" };
		String[] parsed = new String[clazz.getEStructuralFeatures().size()];
		for (int i = 0; i < clazz.getEStructuralFeatures().size(); i++) {
			String name = clazz.getEStructuralFeatures().get(i).getName();
			//System.err.println("Parsed sequence: " + name);
			parsed[i] = name;
		}
		for (int i = 0; i < parsed.length; i++) {
			assertEquals(expected[i], parsed[i]);
		}
	}
	
	/**
	 * Loading CUSCAR ecore model
	 * 
	 * @return
	 * @throws IOException
	 */
	private EPackage loadCUSCARModel() throws IOException {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new EcoreResourceFactoryImpl());
		Resource resource = rs.createResource(URI
				.createFileURI("cuscar.ecore"));
		resource.load(null);
		EPackage pkg = (EPackage) resource.getAllContents().next();
		assertNotNull(pkg);
		return pkg;
	}
}
