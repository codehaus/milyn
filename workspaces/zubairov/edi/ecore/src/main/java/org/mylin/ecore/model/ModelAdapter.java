package org.mylin.ecore.model;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.mylin.ecore.ECoreConversionUtils;

public class ModelAdapter {

	public ModelAdapter() {
		super();
	}

	protected String getAnnotationValue(EModelElement element, String key) {
		EAnnotation annotation = element.getEAnnotation(ECoreConversionUtils.ANNOTATION_TYPE);
		String result = annotation.getDetails().get(key);
		if (result == null) {
			throw new NullPointerException("No annotation value with key " + key + " was found on " + element);
		}
		return result;
	}

}