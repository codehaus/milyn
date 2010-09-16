package org.smooks.edi.editor;

import org.eclipse.emf.ecore.presentation.EcoreEditor;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.mylin.ecore.model.envelope.EnvelopeFactory;
import org.mylin.ecore.resource.EDIFactResourceFactoryImpl;

public class EDIEditor extends EcoreEditor {

	public EDIEditor() {
		super();
		editingDomain.getResourceSet().getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("edi", new EDIFactResourceFactoryImpl(new EclipseEDIRegistry()));
		editingDomain.getResourceSet().getLoadOptions().put(EDIFactResourceFactoryImpl.FEATURE_IGNORE_NEWLINES, true);
		editingDomain.getResourceSet().getLoadOptions().put(EDIFactResourceFactoryImpl.FEATURE_VALIDATE, true);
		editingDomain.getResourceSet().getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, true);
		// Initialize Envelope factory
		EnvelopeFactory.eINSTANCE.getEnvelopePackage();
	}

}
