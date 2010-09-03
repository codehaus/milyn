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
		EAnnotation annotation = element.getEAnnotation(ECoreConversionUtils.ANNOTATION_TYPE);
		if (annotation == null) {
			throw new NullPointerException("Can't find annotations of type " + ECoreConversionUtils.ANNOTATION_TYPE + " on " + element);
		}
		String result = annotation.getDetails().get(key);
		if (result == null) {
			throw new NullPointerException("No annotation value with key " + key + " was found on " + element);
		}
		return result;
	}

}
