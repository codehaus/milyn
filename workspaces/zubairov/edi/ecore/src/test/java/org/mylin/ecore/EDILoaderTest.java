package org.mylin.ecore;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.mylin.ecore.model.envelope.InterchangeMessageType;
import org.mylin.ecore.model.envelope.UNBType;
import org.mylin.ecore.model.envelope.UNEdifact;


public class EDILoaderTest extends TestCase {

	@SuppressWarnings("unchecked")
	public void testEDILoader() throws Exception {
		UNEdifact envelope = EDILoader.INSTANCE.load(getClass()
				.getResourceAsStream("/99a_cuscar.edi"));
		UNBType unb = envelope.getUNB();
		assertEquals("XXXXXLCTA", unb.getRecipient().getId());
		assertEquals("1918", unb.getControlRef());
		assertEquals("SENDER",unb.getSender().getId());
		assertEquals("100421", unb.getDateTime().getDate());
		assertEquals(2, envelope.getMessages().size());
		// Let's check header
		InterchangeMessageType msg1 = envelope.getMessages().get(0);
		InterchangeMessageType msg2 = envelope.getMessages().get(1);
		assertEquals("163477", msg1.getUNH().getMessageRefNum());
		assertEquals("163478", msg2.getUNH().getMessageRefNum());
		// Let's check first message content
		Entry entry = msg1.getMessage().get(0);
		EStructuralFeature cusRef = entry.getEStructuralFeature();
		EObject value = (EObject) entry.getValue();
		assertEquals("CUSCAR", cusRef.getEType().getName());
		EClass cuscar = (EClass) cusRef.getEType();
		assertEquals("CUSCAR", value.eClass().getName());
		// Not let's check
		// RFF+ACE::OHNE'
		// RFF+ABE:400190754417'
		EReference sg1 = (EReference) cuscar.getEStructuralFeature("segmentGroup1");
		assertNotNull(sg1);
		List<EObject> values = (List<EObject>) value.eGet(sg1);
		assertEquals("We have two segment group 1", 2, values.size());
		EClass sg1Type = (EClass) sg1.getEType();
		EStructuralFeature rffType = sg1Type.getEStructuralFeature("RFF");
		EStructuralFeature dtmType = sg1Type.getEStructuralFeature("DTM");
		
		// Both sg1 instances has only RFF elements
		for (EObject sg : values) {
			assertTrue(sg.eIsSet(rffType));
			assertFalse(sg.eIsSet(dtmType));
		}
		// Now let's check each RFF
		EStructuralFeature c506feature = ((EClass)rffType.getEType()).getEStructuralFeature("reference");
		EStructuralFeature rfqFeature = ((EClass)c506feature.getEType()).getEStructuralFeature("referenceQualifier");
		
		// RFF+ACE::OHNE'
		EObject rff1 = (EObject) values.get(0).eGet(rffType);
		assertEquals("ACE", ((EObject)rff1.eGet(c506feature)).eGet(rfqFeature));
		
		// RFF+ABE:400190754417'
		EObject rff2 = (EObject) values.get(1).eGet(rffType);
		assertEquals("ABE", ((EObject)rff2.eGet(c506feature)).eGet(rfqFeature));
	}
	
}
