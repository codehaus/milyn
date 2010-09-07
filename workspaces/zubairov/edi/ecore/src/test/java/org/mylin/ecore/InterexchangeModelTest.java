package org.mylin.ecore;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

public class InterexchangeModelTest extends TestCase {

	public void testInterexchangeModel() throws Exception {
		EPackage ePkg = TestingUtils.loadModel("model/envelope.ecore");
		EPackage cPkg = TestingUtils.loadModel("cuscar.ecore");

		EClass unEdifactClass = (EClass) ePkg.getEClassifier("UnEdifactType");
		EClass imType = (EClass) ePkg.getEClassifier("InterchangeMessageType");
		EStructuralFeature interchangeMessageFeature = unEdifactClass.getEStructuralFeature("interchangeMessage");
		EStructuralFeature msgFeature = imType.getEStructuralFeature("any");
		EClass cdr = (EClass) cPkg.getEClassifier("DocumentRoot");
		assertNotNull("DocumentRoot is not found in CUSCAR package", cdr);
		EStructuralFeature csf = cdr.getEStructuralFeature("content");
		assertNotNull(csf);
		
		
		EObject unEdifact = ePkg.getEFactoryInstance().create(unEdifactClass);
		@SuppressWarnings("unchecked")
		EList<EObject> msgs = (EList<EObject>) unEdifact.eGet(interchangeMessageFeature);
		EObject im = ePkg.getEFactoryInstance().create(imType);
		msgs.add(im);

		EObject cuscar = createCUSCAR(cPkg);
		FeatureMap map = (FeatureMap) im.eGet(msgFeature);
		map.add(csf, cuscar);
		
		TestingUtils.serializeAsXML(unEdifact, System.out);
	}

	private EObject createCUSCAR(EPackage pkg) throws IOException {
		EFactory factory = pkg.getEFactoryInstance();
		EClass cuscarClass = (EClass) pkg.getEClassifier("CUSCAR");
		EObject result = factory.create(cuscarClass);
		EReference feature = (EReference) cuscarClass.getEStructuralFeature("beginningOfMessage");
		EObject bgm = feature.getEReferenceType().getEPackage().getEFactoryInstance().create(feature.getEReferenceType());
		result.eSet(feature, bgm);
		return result;
	}
	
}
