package org.mylin.ecore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.EDIParser;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.IEdimap;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.mylin.ecore.ECoreBindingHandler.MessageHanlder;
import org.mylin.ecore.model.EdimapAdapter;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test that we could parse and bind model based on ECORE
 * 
 * @author zubairov
 */
public class BindingTest extends TestCase implements MessageHanlder {

	List<EObject> testContent = new ArrayList<EObject>();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testContent.clear();
	}

	/**
	 * Parsing CUSCAR 99A CUSCAR message with validation
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws EDIConfigurationException
	 */
	public void testParser() throws IOException, SAXException,
			EDIConfigurationException {
		parseEDI();
		EObject one = testContent.get(0);
		EObject two = testContent.get(1);
		EObject bgm1 = (EObject) get(one, "BGM");
		EObject bgm2 = (EObject) get(two, "BGM");
		EObject docid1 = (EObject) get(bgm1, "documentMessageIdentification");
		EObject docid2 = (EObject) get(bgm2, "documentMessageIdentification");
		assertEquals("MOL-EU2-HFA-012W-XXXX8896514-01",
				get(docid1, "documentMessageNumber"));
		assertEquals("MOL-EU2-HFA-012W-XXXX5086746-01",
				get(docid2, "documentMessageNumber"));
	}

	private void parseEDI() throws IOException, SAXException {
		EPackage pkg = loadCUSCARModel();
		IEdimap edimap = new EdimapAdapter(pkg);
		UNEdifactInterchangeParser parser = new UNEdifactInterchangeParser();
		parser.setFeature(EDIParser.FEATURE_VALIDATE, true);
		parser.addMappingModel(new EdifactModel(edimap));
		parser.ignoreNewLines(true);

		ContentHandler handler;

		assertEquals(0, testContent.size());
		handler = new ECoreBindingHandler(pkg, this);
		parser.setContentHandler(handler);
		parser.parse(new InputSource(getClass().getResourceAsStream(
				"/99a_cuscar.edi")));
		assertEquals(2, testContent.size());
	}

	private Object get(EObject one, String string) {
		EClass clazz = one.eClass();
		EStructuralFeature feature = clazz.getEStructuralFeature(string);
		assertNotNull("Can't find feature " + string, feature);
		Object object = one.eGet(feature);
		assertNotNull("Field " + string + " is null", object);
		return object;
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
		Resource resource = rs
				.createResource(URI.createFileURI("cuscar.ecore"));
		resource.load(null);
		EPackage pkg = (EPackage) resource.getAllContents().next();
		assertNotNull(pkg);
		return pkg;
	}

	public void messageElement(EObject message) {
		testContent.add(message);
	}

	public void testModelOutput() throws Exception {
		parseEDI();
		TestingUtils.serializeAsXML(testContent.iterator().next(), System.out);
	}

}
