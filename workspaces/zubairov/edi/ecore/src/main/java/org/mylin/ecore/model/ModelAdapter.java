package org.mylin.ecore.model;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.milyn.edisax.model.internal.IEdimap;
import org.mylin.ecore.ECoreConversionUtils;

public class ModelAdapter {

	public ModelAdapter() {
		super();
	}

	protected String getAnnotationValue(EModelElement element, String key) {
		EAnnotation annotation = element.getEAnnotation(ECoreConversionUtils.ANNOTATION_TYPE);
		return annotation.getDetails().get(key);
	}

}