package org.mylin.ecore;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;

/**
 * Helper class to store EMF and Smooks specific methods
 * 
 * @author zubairov
 *
 */
public class EMFHelper {

	public static final String getAnnotationValue(EModelElement element, String key) {
		EAnnotation annotation = getSmooksAnnotation(element);
		String result = annotation.getDetails().get(key);
		if (result == null) {
			throw new NullPointerException("No annotation value with key " + key + " was found on " + element);
		}
		return result;
	}

	public static EAnnotation getSmooksAnnotation(EModelElement element) {
		EAnnotation annotation = element.getEAnnotation(SmooksMetadata.ANNOTATION_TYPE);
		if (annotation == null) {
			throw new NullPointerException("Can't find annotations of type " + SmooksMetadata.ANNOTATION_TYPE + " on " + element);
		}
		return annotation;
	}

}
