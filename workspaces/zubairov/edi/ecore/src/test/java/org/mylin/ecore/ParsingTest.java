package org.mylin.ecore;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.EDIParser;
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
	 * @throws JDOMException 
	 */
	public void testParser() throws IOException, SAXException,
			EDIConfigurationException, JDOMException {
		EPackage pkg = TestingUtils.loadModel("cuscar.ecore");
		IEdimap edimap = new EdimapAdapter(pkg);
		UNEdifactInterchangeParser parser = new UNEdifactInterchangeParser();
		parser.setFeature(EDIParser.FEATURE_VALIDATE, true);
		parser.addMappingModel(new EdifactModel(edimap));
		parser.ignoreNewLines(true);

		// Test message 01 - no UNA segment...
		SAXBuilder builder = new MockBuilder(parser);
		Document document = builder.build(new InputSource(getClass().getResourceAsStream(
				"/99a_cuscar.edi")));
		new XMLOutputter(Format.getPrettyFormat()).output(document, System.out);
	}

	/**
	 * We need to test that component definitions are in the right order
	 * 
	 * @throws Exception
	 */
	public void testComponentOrder() throws Exception {
		EPackage pkg = TestingUtils.loadModel("cuscar.ecore");
		EClass root = (EClass) pkg.getEClassifier("CUSCAR");
		assertNotNull(root);
		EReference feature = (EReference) root
				.getEStructuralFeature("LOC");
		assertNotNull(feature);
		EClass clazz = feature.getEReferenceType();
		String[] expected = new String[] { "placeLocationQualifier",
				"locationIdentification",
				"relatedLocationOneIdentification",
				"relatedLocationTwoIdentification", "relationCoded" };
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
	
}
